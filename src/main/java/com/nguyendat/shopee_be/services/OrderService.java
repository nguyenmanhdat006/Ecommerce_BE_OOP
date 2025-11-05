package com.nguyendat.shopee_be.services;

import com.nguyendat.shopee_be.dto.OrderRequest;
import com.nguyendat.shopee_be.entities.Order;
import com.nguyendat.shopee_be.entities.OrderStatus;

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
    /**
     * @param orderId ID đơn hàng
     * @param newStatus trạng thái mới (Enum OrderStatus)
     * @param changedBy username hoặc role của người thao tác
     */
    Order updateStatus(UUID orderId, OrderStatus newStatus, String changedBy);
}
