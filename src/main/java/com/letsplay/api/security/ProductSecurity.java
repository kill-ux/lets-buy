package com.letsplay.api.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.letsplay.api.repository.ProductRepository;

/**
 * ProductSecurity
 */
@Component
public class ProductSecurity {
    private final ProductRepository productRepository;

    @Autowired
    public ProductSecurity(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public boolean isOwner(String productId) {
        String currentUserId = SecurityUtils.getCurrentUserId();
        return productRepository.findById(productId)
                .map(product -> product.getUserId().equals(currentUserId))
                .orElse(false);
    }

    public boolean isOwnerOrAdmin(String productId) {
        return isOwner(productId) || SecurityUtils.isAdmin();
    }
}