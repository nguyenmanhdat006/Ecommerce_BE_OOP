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
public class ChatUserDto {
    private UUID userId;
    private String firstName;
    private String lastName;
    private String email;
    private String avatar;
    private String phoneNumber;
    private Date lastMessageAt;
    private String lastMessageContent;
    private long unreadCount;
}

