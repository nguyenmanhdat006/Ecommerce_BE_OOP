package com.nguyendat.shopee_be.services;

import com.nguyendat.shopee_be.dto.ChatUserDto;
import com.nguyendat.shopee_be.dto.MessageDto;
import com.nguyendat.shopee_be.dto.UnreadCountDto;

import java.util.List;
import java.util.UUID;

public interface MessageService {
    /**
     * Get list of users who have chatted with the current user (admin)
     */
    List<ChatUserDto> getChatUsers(UUID currentUserId);
    
    /**
     * Get chat history between current user and a specific user
     */
    List<MessageDto> getChatHistory(UUID currentUserId, UUID otherUserId);
    
    /**
     * Get unread message count for current user
     */
    UnreadCountDto getUnreadCount(UUID currentUserId);
    
    /**
     * Mark messages from a specific sender as read
     */
    void markAsRead(UUID currentUserId, UUID senderId);
}

