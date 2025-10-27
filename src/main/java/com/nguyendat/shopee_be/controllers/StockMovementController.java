package com.nguyendat.shopee_be.controllers;

import com.nguyendat.shopee_be.entities.StockMovement;
import com.nguyendat.shopee_be.services.StockMovementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/stock-movements")
@CrossOrigin
public class StockMovementController {

    @Autowired
    private StockMovementService service;

    @GetMapping
    public ResponseEntity<List<StockMovement>> getAll() {
        return new ResponseEntity<>(service.findAll(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<StockMovement> getById(@PathVariable UUID id) {
        return new ResponseEntity<>(service.findById(id), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<StockMovement> create(@RequestBody StockMovement entity) {
        StockMovement created = service.create(entity);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<StockMovement> update(@PathVariable UUID id, @RequestBody StockMovement entity) {
        StockMovement updated = service.update(id, entity);
        return new ResponseEntity<>(updated, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
