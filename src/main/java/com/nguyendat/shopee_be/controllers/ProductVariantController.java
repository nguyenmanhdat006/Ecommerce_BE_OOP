package com.nguyendat.shopee_be.controllers;

import com.nguyendat.shopee_be.entities.ProductVariant;
import com.nguyendat.shopee_be.services.ProductVariantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/product-variants")
@CrossOrigin
public class ProductVariantController {

    @Autowired
    private ProductVariantService service;

    @GetMapping
    public ResponseEntity<List<ProductVariant>> getAll() {
        return new ResponseEntity<>(service.findAll(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductVariant> getById(@PathVariable UUID id) {
        return new ResponseEntity<>(service.findById(id), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<ProductVariant> create(@RequestBody ProductVariant entity) {
        ProductVariant created = service.create(entity);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductVariant> update(@PathVariable UUID id, @RequestBody ProductVariant entity) {
        ProductVariant updated = service.update(id, entity);
        return new ResponseEntity<>(updated, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
