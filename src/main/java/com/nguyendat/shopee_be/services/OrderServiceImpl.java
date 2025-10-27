package com.nguyendat.shopee_be.services;

import com.nguyendat.shopee_be.entities.Order;
import com.nguyendat.shopee_be.exceptions.ResourceNotFoundEx;
import com.nguyendat.shopee_be.repositories.OrderRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class OrderServiceImpl implements OrderService {
    private final OrderRepository repository;

    public OrderServiceImpl(OrderRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<Order> findAll() {
        return repository.findAll();
    }

    @Override
    public Order findById(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundEx("Order not found with id: " + id));
    }

    @Override
    public Order create(Order entity) {
        return repository.save(entity);
    }

    @Override
    public Order update(UUID id, Order entity) {
        Order existing = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundEx("Order not found with id: " + id));
        BeanUtils.copyProperties(entity, existing, "id");
        return repository.save(existing);
    }

    @Override
    public void deleteById(UUID id) {
        repository.deleteById(id);
    }
}
