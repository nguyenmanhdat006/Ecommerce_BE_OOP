package com.nguyendat.shopee_be.controllers;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.nguyendat.shopee_be.dto.VnpayRequest;
import com.nguyendat.shopee_be.services.OrderService;

@RestController
@RequestMapping("/api/vnpay")
public class VnpayController {

    @Autowired
    private OrderService orderService;

    // Tạo URL thanh toán VNPAY
    @PostMapping("/create-payment")
    public ResponseEntity<String> createPayment(@RequestBody VnpayRequest request) {
        String paymentUrl = orderService.createVnpayUrl(request.getOrderId(), request.getAmount());
        return ResponseEntity.ok(paymentUrl);
    }

    // Callback từ VNPAY sau khi thanh toán xong
    @GetMapping("/return")
    public ResponseEntity<String> vnpayReturn(@RequestParam Map<String,String> params) {
        boolean success = orderService.processVnpayReturned(params);
        if(success) {
            return ResponseEntity.ok("Thanh toán thành công!");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Thanh toán thất bại!");
        }
    }
}
