package com.nguyendat.shopee_be.controllers;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/testMinh")

@Tag(name = "Addresses", description = "Manage user addresses")
public class TestMinhController {
    
    @GetMapping
    @Operation(summary = "print hello world")
    public ResponseEntity<Map<String, Object>> getHelloWorld() {
        Map<String, Object> response = new HashMap<>();
        response.put("helloworld", "Hello world!");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/helloMinh")
    @Operation(summary = "print hello minh")
    public ResponseEntity<Map<String, Object>> getHelloMinh() {
        Map<String, Object> response = new HashMap<>();
        response.put("hellominh", "Hello Minh!");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}


    