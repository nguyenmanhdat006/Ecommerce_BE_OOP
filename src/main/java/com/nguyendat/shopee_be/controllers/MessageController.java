package com.nguyendat.shopee_be.controllers;

import com.nguyendat.shopee_be.dto.ChatUserDto;
import com.nguyendat.shopee_be.dto.MessageDto;
import com.nguyendat.shopee_be.dto.UnreadCountDto;
import com.nguyendat.shopee_be.services.MessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

import com.nguyendat.shopee_be.auth.entities.User;

@RestController
@RequestMapping("/api/messages")
public class MessageController {

    private static final Logger logger = LoggerFactory.getLogger(MessageController.class);

    @Autowired
    private MessageService messageService;

    @Autowired
    private UserDetailsService userDetailsService;

    /**
     * Get list of users who have chatted with admin
     * GET /api/messages/users
     */
    @GetMapping("/users")
    public ResponseEntity<List<ChatUserDto>> getChatUsers(Principal principal) {
        try {
            User currentUser = getCurrentUser(principal);
            List<ChatUserDto> chatUsers = messageService.getChatUsers(currentUser.getId());
            return new ResponseEntity<>(chatUsers, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error getting chat users", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Get chat history between admin and a specific user
     * GET /api/messages/history/{userId}
     */
    @GetMapping("/history/{userId}")
    public ResponseEntity<List<MessageDto>> getChatHistory(
            @PathVariable UUID userId,
            Principal principal) {
        try {
            User currentUser = getCurrentUser(principal);
            List<MessageDto> messages = messageService.getChatHistory(currentUser.getId(), userId);
            return new ResponseEntity<>(messages, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error getting chat history", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Get unread message count
     * GET /api/messages/unread-count
     */
    @GetMapping("/unread-count")
    public ResponseEntity<UnreadCountDto> getUnreadCount(Principal principal) {
        try {
            User currentUser = getCurrentUser(principal);
            UnreadCountDto unreadCount = messageService.getUnreadCount(currentUser.getId());
            return new ResponseEntity<>(unreadCount, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error getting unread count", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Mark messages as read
     * PUT /api/messages/mark-read/{senderId}
     */
    @PutMapping("/mark-read/{senderId}")
    public ResponseEntity<Void> markAsRead(
            @PathVariable UUID senderId,
            Principal principal) {
        try {
            User currentUser = getCurrentUser(principal);
            messageService.markAsRead(currentUser.getId(), senderId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            logger.error("Error marking messages as read", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private User getCurrentUser(Principal principal) {
        if (principal == null) {
            throw new RuntimeException("User not authenticated");
        }
        return (User) userDetailsService.loadUserByUsername(principal.getName());
    }
}

