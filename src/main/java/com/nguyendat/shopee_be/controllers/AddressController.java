package com.nguyendat.shopee_be.controllers;

import com.nguyendat.shopee_be.entities.Address;
import com.nguyendat.shopee_be.services.AddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/addresses")
@CrossOrigin
public class AddressController {

    @Autowired
    private AddressService addressService;

    @GetMapping
    public ResponseEntity<List<Address>> getAll() {
        return new ResponseEntity<>(addressService.findAll(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Address> getById(@PathVariable UUID id) {
        return new ResponseEntity<>(addressService.findById(id), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Address> create(@RequestBody Address address) {
        Address created = addressService.create(address);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Address> update(@PathVariable UUID id, @RequestBody Address address) {
        Address updated = addressService.update(id, address);
        return new ResponseEntity<>(updated, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        addressService.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
