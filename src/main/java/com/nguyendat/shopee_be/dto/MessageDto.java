package com.nguyendat.shopee_be.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MessageDto {
    private UUID id;
    private UUID senderId;
    private String senderName;
    private String senderEmail;
    private String senderAvatar;
    private UUID receiverId;
    private String receiverName;
    private String receiverEmail;
    private String receiverAvatar;
    private String content;
    private boolean read;
    private Date createdAt;
}

