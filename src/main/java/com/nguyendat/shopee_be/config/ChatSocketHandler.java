package com.nguyendat.shopee_be.config;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.web.socket.CloseStatus;

import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class ChatSocketHandler extends TextWebSocketHandler {

    private static final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        sessions.put(session.getId(), session);
        System.out.println("✅ Client connected: " + session.getId());
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        // Parse JSON
        Map<String, Object> json = mapper.readValue(message.getPayload(), Map.class);
        System.out.println("📩 Received JSON: " + json);

        // Add server timestamp
        json.put("serverTime", System.currentTimeMillis());

        // Convert back to string JSON
        String response = mapper.writeValueAsString(json);

        // Broadcast to all clients
        for (WebSocketSession ws : sessions.values()) {
            if (ws.isOpen()) {
                ws.sendMessage(new TextMessage(response));
            }
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        sessions.remove(session.getId());
        System.out.println("❌ Client disconnected: " + session.getId());
    }
}
