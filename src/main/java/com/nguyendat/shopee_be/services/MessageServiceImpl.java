package com.nguyendat.shopee_be.services;

import com.nguyendat.shopee_be.dto.ChatUserDto;
import com.nguyendat.shopee_be.dto.MessageDto;
import com.nguyendat.shopee_be.dto.UnreadCountDto;
import com.nguyendat.shopee_be.entities.Message;
import com.nguyendat.shopee_be.auth.entities.User;
import com.nguyendat.shopee_be.auth.repositories.UserDetailRepository;
import com.nguyendat.shopee_be.exceptions.ResourceNotFoundEx;
import com.nguyendat.shopee_be.repositories.MessageRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class MessageServiceImpl implements MessageService {

    private static final Logger logger = LoggerFactory.getLogger(MessageServiceImpl.class);

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private UserDetailRepository userDetailRepository;

    @Override
    public List<ChatUserDto> getChatUsers(UUID currentUserId) {
        User currentUser = userDetailRepository.findById(currentUserId)
                .orElseThrow(() -> new ResourceNotFoundEx("User not found with id: " + currentUserId));

        // Get distinct users who have chatted with current user
        // Combine users who sent messages to current user and users who received messages from current user
        List<User> receivers = messageRepository.findDistinctReceivers(currentUser);
        List<User> senders = messageRepository.findDistinctSenders(currentUser);
        
        // Combine and remove duplicates using a Set
        Set<User> chatUsersSet = new HashSet<>();
        chatUsersSet.addAll(receivers);
        chatUsersSet.addAll(senders);
        List<User> chatUsers = new ArrayList<>(chatUsersSet);

        return chatUsers.stream().map(user -> {
            // Get last message between current user and this user
            List<Message> messages = messageRepository.findConversationBetweenUsers(currentUser, user);
            Message lastMessage = messages.isEmpty() ? null : messages.get(messages.size() - 1);

            // Count unread messages from this user
            long unreadCount = messageRepository.countByReceiverAndSenderAndReadFalse(currentUser, user);

            return ChatUserDto.builder()
                    .userId(user.getId())
                    .firstName(user.getFirstName())
                    .lastName(user.getLastName())
                    .email(user.getEmail())
                    .avatar(user.getAvatar())
                    .phoneNumber(user.getPhoneNumber())
                    .lastMessageAt(lastMessage != null ? lastMessage.getCreatedAt() : null)
                    .lastMessageContent(lastMessage != null ? lastMessage.getContent() : null)
                    .unreadCount(unreadCount)
                    .build();
        })
        .sorted((a, b) -> {
            // Sort by last message time (most recent first)
            if (a.getLastMessageAt() == null && b.getLastMessageAt() == null) return 0;
            if (a.getLastMessageAt() == null) return 1;
            if (b.getLastMessageAt() == null) return -1;
            return b.getLastMessageAt().compareTo(a.getLastMessageAt());
        })
        .collect(Collectors.toList());
    }

    @Override
    public List<MessageDto> getChatHistory(UUID currentUserId, UUID otherUserId) {
        User currentUser = userDetailRepository.findById(currentUserId)
                .orElseThrow(() -> new ResourceNotFoundEx("User not found with id: " + currentUserId));
        
        User otherUser = userDetailRepository.findById(otherUserId)
                .orElseThrow(() -> new ResourceNotFoundEx("User not found with id: " + otherUserId));

        List<Message> messages = messageRepository.findConversationBetweenUsers(currentUser, otherUser);

        return messages.stream().map(message -> {
            User sender = message.getSender();
            User receiver = message.getReceiver();

            return MessageDto.builder()
                    .id(message.getId())
                    .senderId(sender.getId())
                    .senderName(getFullName(sender))
                    .senderEmail(sender.getEmail())
                    .senderAvatar(sender.getAvatar())
                    .receiverId(receiver.getId())
                    .receiverName(getFullName(receiver))
                    .receiverEmail(receiver.getEmail())
                    .receiverAvatar(receiver.getAvatar())
                    .content(message.getContent())
                    .read(message.isRead())
                    .createdAt(message.getCreatedAt())
                    .build();
        }).collect(Collectors.toList());
    }

    @Override
    public UnreadCountDto getUnreadCount(UUID currentUserId) {
        User currentUser = userDetailRepository.findById(currentUserId)
                .orElseThrow(() -> new ResourceNotFoundEx("User not found with id: " + currentUserId));

        long unreadCount = messageRepository.countByReceiverAndReadFalse(currentUser);

        return UnreadCountDto.builder()
                .totalUnread(unreadCount)
                .build();
    }

    @Override
    @Transactional
    public void markAsRead(UUID currentUserId, UUID senderId) {
        User currentUser = userDetailRepository.findById(currentUserId)
                .orElseThrow(() -> new ResourceNotFoundEx("User not found with id: " + currentUserId));
        
        User sender = userDetailRepository.findById(senderId)
                .orElseThrow(() -> new ResourceNotFoundEx("User not found with id: " + senderId));

        int updated = messageRepository.markMessagesAsRead(currentUser, sender);
        logger.info("Marked {} messages as read from sender {} to receiver {}", updated, senderId, currentUserId);
    }

    private String getFullName(User user) {
        if (user == null) return "";
        String firstName = user.getFirstName() != null ? user.getFirstName() : "";
        String lastName = user.getLastName() != null ? user.getLastName() : "";
        return (firstName + " " + lastName).trim();
    }
}

