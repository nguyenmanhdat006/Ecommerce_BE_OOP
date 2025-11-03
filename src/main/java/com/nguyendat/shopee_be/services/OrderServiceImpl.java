package com.nguyendat.shopee_be.services;

import com.nguyendat.shopee_be.auth.repositories.UserDetailRepository;
// import com.nguyendat.shopee_be.dto.OrderItemRequest;
import com.nguyendat.shopee_be.dto.OrderRequest;
import com.nguyendat.shopee_be.entities.*;
import com.nguyendat.shopee_be.exceptions.ResourceNotFoundEx;
import com.nguyendat.shopee_be.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.nguyendat.shopee_be.auth.entities.User;


import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserDetailRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductVariantRepository productVariantRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Override
    public List<Order> findAll() {
        return orderRepository.findAll();
    }

    @Override
    public Order findById(UUID id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundEx("Order not found with id: " + id));
    }

    @Override
    public Order create(OrderRequest request) {
        User customer = userRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundEx("User not found with id: " + request.getCustomerId()));

        Order order = new Order();
        order.setOrderNumber(request.getOrderNumber());
        order.setOrderDate(new Date());
        order.setTotalAmount(request.getTotalAmount());
        order.setStatus(request.getStatus());
        order.setPaymentMethod(request.getPaymentMethod());
        order.setShippingAddress(request.getShippingAddress());
        order.setNotes(request.getNotes());
        order.setCustomer(customer);

        Order savedOrder = orderRepository.save(order);

        List<OrderItem> orderItems = request.getOrderItems().stream().map(itemReq -> {
            OrderItem item = new OrderItem();
            item.setOrder(savedOrder);
            item.setQuantity(itemReq.getQuantity());
            item.setUnitPrice(itemReq.getUnitPrice());
            item.setTotalPrice(itemReq.getTotalPrice());

            Product product = productRepository.findById(itemReq.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundEx("Product not found with id: " + itemReq.getProductId()));

            ProductVariant variant = productVariantRepository.findById(itemReq.getProductVariantId())
                    .orElseThrow(() -> new ResourceNotFoundEx("Product variant not found with id: " + itemReq.getProductVariantId()));

            item.setProduct(product);
            item.setProductVariant(variant);

            return orderItemRepository.save(item);
        }).collect(Collectors.toList());

        savedOrder.setOrderItems(orderItems);
        return orderRepository.save(savedOrder);
    }

    // @Override
    // public Order update(UUID id, OrderRequest request) {
    //     Order existing = orderRepository.findById(id)
    //             .orElseThrow(() -> new ResourceNotFoundEx("Order not found with id: " + id));

    //     existing.setStatus(request.getStatus());
    //     existing.setNotes(request.getNotes());
    //     existing.setPaymentMethod(request.getPaymentMethod());
    //     return orderRepository.save(existing);
    // }

    @Override
    public void deleteById(UUID id) {
        orderRepository.deleteById(id);
    }

    @Override
    public Order update(UUID id, OrderRequest request) {
        throw new UnsupportedOperationException("Unimplemented method 'update'");
    }
}
