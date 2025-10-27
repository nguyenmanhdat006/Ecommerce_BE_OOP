package com.nguyendat.shopee_be.controllers;

import com.nguyendat.shopee_be.entities.Cart;
import com.nguyendat.shopee_be.services.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/carts")
@CrossOrigin
public class CartController {

    @Autowired
    private CartService cartService;

    @GetMapping
    public ResponseEntity<List<Cart>> getAll() {
        return new ResponseEntity<>(cartService.findAll(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Cart> getById(@PathVariable UUID id) {
        return new ResponseEntity<>(cartService.findById(id), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Cart> create(@RequestBody Cart cart) {
        Cart created = cartService.create(cart);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Cart> update(@PathVariable UUID id, @RequestBody Cart cart) {
        Cart updated = cartService.update(id, cart);
        return new ResponseEntity<>(updated, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        cartService.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
