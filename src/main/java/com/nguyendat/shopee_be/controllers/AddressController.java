package com.nguyendat.shopee_be.controllers;

import com.nguyendat.shopee_be.entities.Address;
import com.nguyendat.shopee_be.services.AddressService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/addresses")
@CrossOrigin
@Tag(name = "Addresses", description = "Manage user addresses")
public class AddressController {

    @Autowired
    private AddressService addressService;

    @GetMapping
    @Operation(summary = "Get all addresses")
    public ResponseEntity<List<Address>> getAll() {
        return new ResponseEntity<>(addressService.findAll(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get address by id")
    public ResponseEntity<Address> getById(@PathVariable UUID id) {
        return new ResponseEntity<>(addressService.findById(id), HttpStatus.OK);
    }

    @PostMapping
    @Operation(summary = "Create address")
    public ResponseEntity<Address> create(@RequestBody Address address) {
        Address created = addressService.create(address);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update address")
    public ResponseEntity<Address> update(@PathVariable UUID id, @RequestBody Address address) {
        Address updated = addressService.update(id, address);
        return new ResponseEntity<>(updated, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete address")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        addressService.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
