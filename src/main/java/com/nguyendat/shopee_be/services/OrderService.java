package com.nguyendat.shopee_be.services;

import com.nguyendat.shopee_be.dto.OrderRequest;
import com.nguyendat.shopee_be.entities.Order;
import java.util.List;
import java.util.UUID;

public interface OrderService {
    List<Order> findAll();
    Order findById(UUID id);
    Order create(OrderRequest request);
    Order update(UUID id, OrderRequest request);
    void deleteById(UUID id);
}
