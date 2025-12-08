package com.nguyendat.shopee_be.dto;

import com.nguyendat.shopee_be.auth.dto.UserDto;
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
public class ReviewResponse {
    private UUID id;
    private Integer rating;
    private String comment;
    private Date createdAt;
    private Date updatedAt;
    private UserDto user;
}
