package com.nguyendat.shopee_be.controllers;

import com.nguyendat.shopee_be.entities.ProductStatus;
import com.nguyendat.shopee_be.services.ProductStatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/product-statuses")
@CrossOrigin
public class ProductStatusController {

    @Autowired
    private ProductStatusService service;

    @GetMapping
    public ResponseEntity<List<ProductStatus>> getAll() {
        return new ResponseEntity<>(service.findAll(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductStatus> getById(@PathVariable UUID id) {
        return new ResponseEntity<>(service.findById(id), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<ProductStatus> create(@RequestBody ProductStatus entity) {
        ProductStatus created = service.create(entity);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductStatus> update(@PathVariable UUID id, @RequestBody ProductStatus entity) {
        ProductStatus updated = service.update(id, entity);
        return new ResponseEntity<>(updated, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
