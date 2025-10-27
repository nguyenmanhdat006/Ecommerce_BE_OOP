package com.nguyendat.shopee_be.services;

import com.nguyendat.shopee_be.entities.Order;
import java.util.List;
import java.util.UUID;

public interface OrderService {
    List<Order> findAll();
    Order findById(UUID id);
    Order create(Order entity);
    Order update(UUID id, Order entity);
    void deleteById(UUID id);
}
