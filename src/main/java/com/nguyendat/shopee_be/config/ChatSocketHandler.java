package com.nguyendat.shopee_be.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nguyendat.shopee_be.auth.config.JWTTokenHelper;
import com.nguyendat.shopee_be.auth.entities.Authority;
import com.nguyendat.shopee_be.auth.entities.User;
import com.nguyendat.shopee_be.auth.repositories.UserDetailRepository;
import com.nguyendat.shopee_be.entities.Message;
import com.nguyendat.shopee_be.repositories.MessageRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ChatSocketHandler extends TextWebSocketHandler {

    private static final Logger logger = LoggerFactory.getLogger(ChatSocketHandler.class);
    private static final String ADMIN_ROLE = "ROLE_ADMIN";

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private UserDetailRepository userDetailRepository;

    @Autowired
    private JWTTokenHelper jwtTokenHelper;

    private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void afterConnectionEstablished(@NonNull WebSocketSession session) throws Exception {
        try {
            String userId = getUserIdFromSession(session);
            if (userId == null) {
                logger.warn("Failed to authenticate WebSocket connection. Closing session.");
                session.close(CloseStatus.NOT_ACCEPTABLE.withReason("Authentication failed"));
                return;
            }

            sessions.put(userId, session);
            session.getAttributes().put("userId", userId);
            logger.info("WebSocket connection established for user: {}", userId);
        } catch (Exception e) {
            logger.error("Error establishing WebSocket connection", e);
            session.close(CloseStatus.SERVER_ERROR.withReason("Connection error"));
        }
    }

    @Override
    protected void handleTextMessage(@NonNull WebSocketSession session, @NonNull TextMessage message) throws Exception {
        try {
            String senderId = (String) session.getAttributes().get("userId");
            if (senderId == null) {
                session.sendMessage(new TextMessage(objectMapper.writeValueAsString(
                    Map.of("error", "Unauthorized", "message", "User not authenticated"))));
                return;
            }

            // Parse message payload
            Map<String, String> payload = objectMapper.readValue(
                message.getPayload(), 
                new TypeReference<Map<String, String>>() {}
            );
            String receiverId = payload.get("receiverId");
            String content = payload.get("content");

            // Validate input
            if (receiverId == null || receiverId.trim().isEmpty()) {
                session.sendMessage(new TextMessage(objectMapper.writeValueAsString(
                    Map.of("error", "Invalid request", "message", "Receiver ID is required"))));
                return;
            }

            if (content == null || content.trim().isEmpty()) {
                session.sendMessage(new TextMessage(objectMapper.writeValueAsString(
                    Map.of("error", "Invalid request", "message", "Message content is required"))));
                return;
            }

            // Get sender and receiver
            User sender = getUser(senderId);
            User receiver = getUser(receiverId);

            if (sender == null) {
                session.sendMessage(new TextMessage(objectMapper.writeValueAsString(
                    Map.of("error", "Not found", "message", "Sender not found"))));
                return;
            }

            if (receiver == null) {
                session.sendMessage(new TextMessage(objectMapper.writeValueAsString(
                    Map.of("error", "Not found", "message", "Receiver not found"))));
                return;
            }

            // Kiểm tra quyền: chỉ admin mới gửi cho user khác
            boolean isAdmin = checkAdmin(sender);
            boolean receiverIsAdmin = checkAdmin(receiver);


            if (!isAdmin && !receiverIsAdmin) {
                session.sendMessage(new TextMessage(objectMapper.writeValueAsString(
                    Map.of("error", "Permission denied", "message", "Only admin can send messages to other users"))));
                return;
            }

            // Create and save message
            Message msg = Message.builder()
                    .content(content)
                    .sender(sender)
                    .receiver(receiver)
                    .createdAt(new Date())
                    .build();
            messageRepository.save(msg);
            logger.info("Message saved: sender={}, receiver={}", senderId, receiverId);

            // Send message to receiver if online
            WebSocketSession receiverSession = sessions.get(receiverId);
            if (receiverSession != null && receiverSession.isOpen()) {
                Map<String, Object> response = new HashMap<>();
                response.put("id", msg.getId());
                response.put("content", msg.getContent());
                response.put("senderId", sender.getId().toString());
                response.put("receiverId", receiver.getId().toString());
                response.put("createdAt", msg.getCreatedAt());
                response.put("read", msg.isRead());
                
                receiverSession.sendMessage(new TextMessage(objectMapper.writeValueAsString(response)));
                logger.info("Message delivered to receiver: {}", receiverId);
            } else {
                logger.info("Receiver {} is not online. Message saved for later retrieval.", receiverId);
            }

            // Send confirmation to sender
            session.sendMessage(new TextMessage(objectMapper.writeValueAsString(
                Map.of("status", "success", "messageId", msg.getId().toString()))));

        } catch (Exception e) {
            logger.error("Error handling text message", e);
            try {
                session.sendMessage(new TextMessage(objectMapper.writeValueAsString(
                    Map.of("error", "Internal error", "message", "Failed to process message"))));
            } catch (Exception ex) {
                logger.error("Error sending error message", ex);
            }
        }
    }

    @Override
    public void afterConnectionClosed(@NonNull WebSocketSession session, @NonNull CloseStatus status) {
        String userId = (String) session.getAttributes().get("userId");
        if (userId != null) {
            sessions.remove(userId);
            logger.info("WebSocket connection closed for user: {}, status: {}", userId, status);
        }
    }

    @Override
    public void handleTransportError(@NonNull WebSocketSession session, @NonNull Throwable exception) throws Exception {
        logger.error("WebSocket transport error for session: {}", session.getId(), exception);
        String userId = (String) session.getAttributes().get("userId");
        if (userId != null) {
            sessions.remove(userId);
        }
    }

    /**
     * Extract user ID from WebSocket session by decoding JWT token from handshake headers
     */
    private String getUserIdFromSession(WebSocketSession session) {
        try {
            // Get Authorization header from handshake
            String query = session.getUri().getQuery(); // ví dụ: "token=abc"
            if (query == null || !query.startsWith("token=")) {
                logger.warn("No token found in WebSocket query param");
                return null;
            }

            String token = query.substring(6); // bỏ "token="
            if (token == null || token.trim().isEmpty()) {
                logger.warn("Empty token in query param");
                return null;
            }

            // Get username from token
            String username = jwtTokenHelper.getUserNameFromToken(token);
            if (username == null) {
                logger.warn("Failed to extract username from token");
                return null;
            }

            // Get user from database
            User user = userDetailRepository.findByEmail(username);
            if (user == null) {
                logger.warn("User not found for email: {}", username);
                return null;
            }

            // Validate token
            if (!jwtTokenHelper.validateToken(token, user)) {
                logger.warn("Token validation failed for user: {}", username);
                return null;
            }

            return user.getId().toString();
        } catch (Exception e) {
            logger.error("Error extracting user ID from session", e);
            return null;
        }
    }

    /**
     * Check if user has ADMIN role
     */
    private boolean checkAdmin(User user) {
        if (user == null || user.getAuthorities() == null) {
            return false;
        }

        return user.getAuthorities().stream()
                .filter(Authority.class::isInstance)
                .map(Authority.class::cast)
                .map(Authority::getRoleCode)
                .anyMatch(roleCode -> ADMIN_ROLE.equalsIgnoreCase(roleCode));
    }

    /**
     * Fetch user from database by ID
     */
    private User getUser(String userId) {
        try {
            if (userId == null || userId.trim().isEmpty()) {
                return null;
            }

            UUID id = UUID.fromString(userId);
            return userDetailRepository.findById(id).orElse(null);
        } catch (IllegalArgumentException e) {
            logger.error("Invalid UUID format: {}", userId, e);
            return null;
        } catch (Exception e) {
            logger.error("Error fetching user with ID: {}", userId, e);
            return null;
        }
    }
}
