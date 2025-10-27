package com.nguyendat.shopee_be.services;

import com.nguyendat.shopee_be.entities.Cart;
import java.util.List;
import java.util.UUID;

public interface CartService {
    List<Cart> findAll();
    Cart findById(UUID id);
    Cart create(Cart cart);
    Cart update(UUID id, Cart cart);
    void deleteById(UUID id);
}
