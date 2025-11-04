package com.nguyendat.shopee_be.services;

import com.nguyendat.shopee_be.dto.OrderRequest;
import com.nguyendat.shopee_be.entities.Order;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface OrderService {
    List<Order> findAll();
    Order findById(UUID id);
    Order create(OrderRequest request);
    Order update(UUID id, OrderRequest request);
    void deleteById(UUID id);

    // Thanh toán VNPay
    String createVnpayUrl(UUID orderId, BigDecimal amount);
    boolean processVnpayReturned(Map<String, String> params);

    // Cập nhật trạng thái đơn hàng
    Order updateStatus(UUID orderId, String newStatus);
}
