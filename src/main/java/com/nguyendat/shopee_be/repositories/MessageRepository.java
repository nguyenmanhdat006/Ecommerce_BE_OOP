package com.nguyendat.shopee_be.repositories;

import com.nguyendat.shopee_be.entities.Message;
import com.nguyendat.shopee_be.auth.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface MessageRepository extends JpaRepository<Message, UUID> {
    List<Message> findByReceiverOrderByCreatedAtDesc(User receiver);
    List<Message> findBySenderOrderByCreatedAtDesc(User sender);
}
