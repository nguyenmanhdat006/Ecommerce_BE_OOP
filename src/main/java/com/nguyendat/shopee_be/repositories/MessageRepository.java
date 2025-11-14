package com.nguyendat.shopee_be.repositories;

import com.nguyendat.shopee_be.entities.Message;
import com.nguyendat.shopee_be.auth.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface MessageRepository extends JpaRepository<Message, UUID> {
    List<Message> findByReceiverOrderByCreatedAtDesc(User receiver);
    List<Message> findBySenderOrderByCreatedAtDesc(User sender);
    
    // Get messages between two users
    @Query("SELECT m FROM Message m WHERE (m.sender = :user1 AND m.receiver = :user2) OR (m.sender = :user2 AND m.receiver = :user1) ORDER BY m.createdAt ASC")
    List<Message> findConversationBetweenUsers(@Param("user1") User user1, @Param("user2") User user2);
    
    // Get distinct users who have chatted with a specific user (as sender)
    @Query("SELECT DISTINCT m.receiver FROM Message m WHERE m.sender = :user")
    List<User> findDistinctReceivers(@Param("user") User user);
    
    // Get distinct users who have chatted with a specific user (as receiver)
    @Query("SELECT DISTINCT m.sender FROM Message m WHERE m.receiver = :user")
    List<User> findDistinctSenders(@Param("user") User user);
    
    // Count unread messages for a receiver
    long countByReceiverAndReadFalse(User receiver);
    
    // Count unread messages from a specific sender
    long countByReceiverAndSenderAndReadFalse(User receiver, User sender);
    
    // Mark messages as read
    @Modifying
    @Query("UPDATE Message m SET m.read = true WHERE m.receiver = :receiver AND m.sender = :sender AND m.read = false")
    int markMessagesAsRead(@Param("receiver") User receiver, @Param("sender") User sender);
}
