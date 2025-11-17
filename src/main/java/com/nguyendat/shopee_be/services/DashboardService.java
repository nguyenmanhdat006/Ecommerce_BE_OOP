package com.nguyendat.shopee_be.services;

import com.nguyendat.shopee_be.config.DashboardSocketHandler;
import com.nguyendat.shopee_be.entities.OrderStatus;
import com.nguyendat.shopee_be.repositories.OrderRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.*;

@Slf4j
@Service
public class DashboardService {
    
    @Autowired 
    private OrderRepository orderRepository;

    @Autowired 
    private DashboardSocketHandler socketHandler;

    // Đếm số đơn đang xử lý (chưa hoàn thành)
    public int getProcessingOrders() {
        int pending = orderRepository.countByStatus(OrderStatus.PENDING);
        int shipping = orderRepository.countByStatus(OrderStatus.SHIPPING);
        int waitDeliver = orderRepository.countByStatus(OrderStatus.WAIT_DELIVER);
        
        return pending + shipping + waitDeliver;
    }

    public double getTodayRevenue() {
        return orderRepository.sumTotalRevenueByStatus(OrderStatus.PAID);
    }
    

    // Lấy tất cả KPI

    public Map<String, Object> getFullKPI() {
        Map<String, Object> kpi = new HashMap<>();
        kpi.put("processingOrders", getProcessingOrders());
        kpi.put("todayRevenue", getTodayRevenue()); 
        kpi.put("timestamp", System.currentTimeMillis());
        return kpi;
    }


    // Push KPI update realtime

    public void pushKpiUpdate() {
        Map<String, Object> payload = new HashMap<>();
        payload.put("processingOrders", getProcessingOrders());
        payload.put("todayRevenue", getTodayRevenue());
        
        socketHandler.sendEvent("KPI_UPDATE", payload);
        log.info("📊 Pushed KPI update");
    }



    // Push new order event
    public void pushNewOrderEvent(String orderId, String customerName, double total) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("orderId", orderId);
        payload.put("customerName", customerName);
        payload.put("total", total);
        
        socketHandler.sendEvent("NEW_ORDER", payload);
        log.info("🛒 Pushed NEW_ORDER: {}", orderId);
        
        // Trigger KPI update (đơn chưa xử lý tăng)
        pushKpiUpdate();
    }

    // Push order status change event
    public void pushOrderStatusChanged(String orderId, String oldStatus, String newStatus) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("orderId", orderId);
        payload.put("oldStatus", oldStatus);
        payload.put("newStatus", newStatus);
        
        socketHandler.sendEvent("ORDER_STATUS_CHANGED", payload);
        log.info("📦 Pushed ORDER_STATUS_CHANGED: {} -> {}", oldStatus, newStatus);
        
        // NẾU CHUYỂN SANG PAID → UPDATE REVENUE
        if ("PAID".equals(newStatus)) {
            pushRevenueUpdate();
        }
        
        // Update KPI (đơn đang xử lý thay đổi)
        pushKpiUpdate();
    }

    // Push revenue update
    public void pushRevenueUpdate() {
        Map<String, Object> payload = new HashMap<>();
        payload.put("todayRevenue", getTodayRevenue()); // Tổng tất cả PAID
        
        socketHandler.sendEvent("REVENUE_UPDATED", payload);
        log.info("💰 Pushed REVENUE_UPDATED");
    }

    // Push hourly revenue (cho line chart - chỉ đơn PAID hôm nay)
    public void pushHourlyRevenue() {
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = startOfDay.plusDays(1);
        
        List<Object[]> data = orderRepository.getHourlyRevenue(startOfDay, endOfDay, OrderStatus.PAID);
        log.debug("Hourly revenue raw rows: {}", data);

        Map<Integer, Double> hourToRevenue = new HashMap<>();
        for (Object[] row : data) {
            if (row == null || row.length < 2) continue;
            Integer hour = null;
            Double revenue = 0.0;
            try {
                if (row[0] instanceof Number) hour = ((Number) row[0]).intValue();
                else hour = Integer.parseInt(row[0].toString());

                if (row[1] instanceof Number) revenue = ((Number) row[1]).doubleValue();
                else revenue = Double.parseDouble(row[1].toString());
            } catch (Exception e) {
                log.warn("Failed to parse hourly row {}, skipping: {}", row, e.getMessage());
                continue;
            }
            hourToRevenue.put(hour, revenue);
        }

        List<Map<String, Object>> chartData = new ArrayList<>();
        for (int h = 0; h < 24; h++) {
            Map<String, Object> point = new HashMap<>();
            point.put("hour", h);
            point.put("revenue", hourToRevenue.getOrDefault(h, 0.0));
            chartData.add(point);
        }

        // Final log to ensure payload non-empty
        log.debug("HOURLY_REVENUE payload size: {}", chartData.size());

        socketHandler.sendEvent("HOURLY_REVENUE", chartData);
        log.info("📈 Pushed HOURLY_REVENUE");
    }

     //Push order status distribution (cho bar chart - đơn hôm nay)
    public void pushOrderStatusDistribution() {
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = startOfDay.plusDays(1);
        
        List<Object[]> data = orderRepository.countOrdersByStatus(startOfDay, endOfDay);
        
        Map<String, Integer> distribution = new HashMap<>();
        for (Object[] row : data) {
            distribution.put(row[0].toString(), ((Long) row[1]).intValue());
        }
        
        socketHandler.sendEvent("ORDER_STATUS_DISTRIBUTION", distribution);
        log.info("📊 Pushed ORDER_STATUS_DISTRIBUTION");
    }


    @Scheduled(fixedRate = 5000)
    public void autoPushDashboard() {

        int clients = socketHandler.getConnectedClients();

        if (clients == 0) {
            // log.debug("⏳ No active dashboard clients → skip push");
            return;
        }

        try {
            pushKpiUpdate();
            pushRevenueUpdate();
            pushHourlyRevenue();
            pushOrderStatusDistribution();

            log.info("📡 Dashboard data pushed to {} clients", clients);

        } catch (Exception e) {
            log.error("❌ Failed auto push dashboard: {}", e.getMessage());
        }
    }
}