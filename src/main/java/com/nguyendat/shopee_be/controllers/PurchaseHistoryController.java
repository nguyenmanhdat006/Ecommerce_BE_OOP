package com.nguyendat.shopee_be.controllers;

import com.nguyendat.shopee_be.entities.PurchaseHistory;
import com.nguyendat.shopee_be.services.PurchaseHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/purchase-history")
@CrossOrigin
public class PurchaseHistoryController {

    @Autowired
    private PurchaseHistoryService service;

    @GetMapping
    public ResponseEntity<List<PurchaseHistory>> getAll() {
        return new ResponseEntity<>(service.findAll(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PurchaseHistory> getById(@PathVariable UUID id) {
        return new ResponseEntity<>(service.findById(id), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<PurchaseHistory> create(@RequestBody PurchaseHistory entity) {
        PurchaseHistory created = service.create(entity);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PurchaseHistory> update(@PathVariable UUID id, @RequestBody PurchaseHistory entity) {
        PurchaseHistory updated = service.update(id, entity);
        return new ResponseEntity<>(updated, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
