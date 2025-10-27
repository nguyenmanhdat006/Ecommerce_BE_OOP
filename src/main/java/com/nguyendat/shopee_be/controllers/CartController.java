package com.nguyendat.shopee_be.controllers;

import com.nguyendat.shopee_be.entities.Cart;
import com.nguyendat.shopee_be.services.CartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/carts")
@CrossOrigin
@Tag(name = "Carts", description = "Manage shopping carts")
public class CartController {

    @Autowired
    private CartService cartService;

    @GetMapping
    @Operation(summary = "Get all carts")
    public ResponseEntity<List<Cart>> getAll() {
        return new ResponseEntity<>(cartService.findAll(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get cart by id")
    public ResponseEntity<Cart> getById(@PathVariable UUID id) {
        return new ResponseEntity<>(cartService.findById(id), HttpStatus.OK);
    }

    @PostMapping
    @Operation(summary = "Create cart entry")
    public ResponseEntity<Cart> create(@RequestBody Cart cart) {
        Cart created = cartService.create(cart);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update cart entry")
    public ResponseEntity<Cart> update(@PathVariable UUID id, @RequestBody Cart cart) {
        Cart updated = cartService.update(id, cart);
        return new ResponseEntity<>(updated, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete cart entry")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        cartService.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
