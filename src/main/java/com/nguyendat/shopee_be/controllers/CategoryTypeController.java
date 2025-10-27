package com.nguyendat.shopee_be.controllers;

import com.nguyendat.shopee_be.entities.CategoryType;
import com.nguyendat.shopee_be.services.CategoryTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/category-types")
@CrossOrigin
public class CategoryTypeController {

    @Autowired
    private CategoryTypeService categoryTypeService;

    @GetMapping
    public ResponseEntity<List<CategoryType>> getAll() {
        return new ResponseEntity<>(categoryTypeService.findAll(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryType> getById(@PathVariable UUID id) {
        return new ResponseEntity<>(categoryTypeService.findById(id), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<CategoryType> create(@RequestBody CategoryType entity) {
        CategoryType created = categoryTypeService.create(entity);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoryType> update(@PathVariable UUID id, @RequestBody CategoryType entity) {
        CategoryType updated = categoryTypeService.update(id, entity);
        return new ResponseEntity<>(updated, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        categoryTypeService.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
