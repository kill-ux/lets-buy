package com.letsplay.api.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.letsplay.api.exeptions.ResourceNotFoundException;
import com.letsplay.api.model.Product;
import com.letsplay.api.service.ProductService;

import jakarta.annotation.security.PermitAll;
import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@RequestMapping("/products")
public class ProductController {
    private final ProductService productService;

    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    // @PermitAll 
    @GetMapping
    public List<Product> getAllProducts() {
        return productService.findAll();
    }

    @PermitAll
    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable String id) {
        return productService.findById(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
    }

    @PostMapping
    public ResponseEntity<Product> createProduct(@Valid @RequestBody Product product) {
        Product saved = productService.save(product);
        return ResponseEntity.status(201).body(saved);
    }

    @PutMapping("/{id}")
    @PreAuthorize("@productSecurity.isOwnerOrAdmin(#id)")
    public ResponseEntity<Product> updateProduct(@PathVariable String id, @Valid @RequestBody Product updated) {
        return productService.update(id, updated)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("@productSecurity.isOwnerOrAdmin(#id)")
    public ResponseEntity<Void> deleteProduct(@PathVariable String id) {
        if (!productService.existsById(id)) {
            throw new ResourceNotFoundException("Product not found with id: " + id);
        }
        productService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
