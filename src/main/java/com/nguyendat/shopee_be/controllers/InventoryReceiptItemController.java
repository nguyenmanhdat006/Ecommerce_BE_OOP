package com.nguyendat.shopee_be.controllers;

import com.nguyendat.shopee_be.entities.InventoryReceiptItem;
import com.nguyendat.shopee_be.services.InventoryReceiptItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/inventory-receipt-items")
@CrossOrigin
public class InventoryReceiptItemController {

    @Autowired
    private InventoryReceiptItemService service;

    @GetMapping
    public ResponseEntity<List<InventoryReceiptItem>> getAll() {
        return new ResponseEntity<>(service.findAll(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<InventoryReceiptItem> getById(@PathVariable UUID id) {
        return new ResponseEntity<>(service.findById(id), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<InventoryReceiptItem> create(@RequestBody InventoryReceiptItem entity) {
        InventoryReceiptItem created = service.create(entity);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<InventoryReceiptItem> update(@PathVariable UUID id, @RequestBody InventoryReceiptItem entity) {
        InventoryReceiptItem updated = service.update(id, entity);
        return new ResponseEntity<>(updated, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
