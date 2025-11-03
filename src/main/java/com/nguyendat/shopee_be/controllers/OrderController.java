package com.nguyendat.shopee_be.controllers;

import com.nguyendat.shopee_be.dto.OrderRequest;
import com.nguyendat.shopee_be.entities.Order;
import com.nguyendat.shopee_be.services.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin
@Tag(name = "Orders", description = "Manage customer orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @GetMapping
    @Operation(summary = "Get all orders")
    public ResponseEntity<List<Order>> getAll() {
        return new ResponseEntity<>(orderService.findAll(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get order by id")
    public ResponseEntity<Order> getById(@PathVariable UUID id) {
        return new ResponseEntity<>(orderService.findById(id), HttpStatus.OK);
    }

    @PostMapping
    @Operation(summary = "Create order")
    public ResponseEntity<Order> create(@RequestBody OrderRequest request) {
        Order created = orderService.create(request);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
}

    @PutMapping("/{id}")
    @Operation(summary = "Update order")
    public ResponseEntity<Order> update(@PathVariable UUID id, @RequestBody OrderRequest request) {
        Order updated = orderService.update(id, request);
        return new ResponseEntity<>(updated, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete order")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        orderService.deleteById(id);
        return ResponseEntity.ok().build();
    }
    @PutMapping("/{id}/status")
    @Operation(summary = "Update order status")
    public ResponseEntity<Order> updateStatus(@PathVariable UUID id, @RequestParam String status) {
        Order updated = orderService.updateStatus(id, status);
        return new ResponseEntity<>(updated, HttpStatus.OK);
    }

}
