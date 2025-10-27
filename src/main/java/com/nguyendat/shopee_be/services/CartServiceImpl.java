package com.nguyendat.shopee_be.services;

import com.nguyendat.shopee_be.entities.Cart;
import com.nguyendat.shopee_be.exceptions.ResourceNotFoundEx;
import com.nguyendat.shopee_be.repositories.CartRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class CartServiceImpl implements CartService {
    private final CartRepository repository;

    public CartServiceImpl(CartRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<Cart> findAll() {
        return repository.findAll();
    }

    @Override
    public Cart findById(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundEx("Cart not found with id: " + id));
    }

    @Override
    public Cart create(Cart cart) {
        return repository.save(cart);
    }

    @Override
    public Cart update(UUID id, Cart cart) {
        Cart existing = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundEx("Cart not found with id: " + id));
        BeanUtils.copyProperties(cart, existing, "id");
        return repository.save(existing);
    }

    @Override
    public void deleteById(UUID id) {
        repository.deleteById(id);
    }
}
