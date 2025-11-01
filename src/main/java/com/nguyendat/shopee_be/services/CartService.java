package com.nguyendat.shopee_be.services;

import com.nguyendat.shopee_be.dto.CartDto;
import java.util.List;
import java.util.UUID;

public interface CartService {
    List<CartDto> findAll();
    CartDto findById(UUID id);
    CartDto create(CartDto cartDto);
    CartDto update(UUID id, CartDto cartDto);
    void deleteById(UUID id);
}
