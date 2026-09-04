package com.letsplay.api.service;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.letsplay.api.model.Product;
import com.letsplay.api.repository.ProductRepository;
import com.letsplay.api.security.SecurityUtils;

@Service
public class ProductService {
    private final ProductRepository productRepository;

    @Autowired
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<Product> findAll() {
        return productRepository.findAll();
    }

    public Optional<Product> findById(String id) {
        return productRepository.findById(id);
    }

    public Product save(Product product) {
        product.setUserId(SecurityUtils.getCurrentUserId());
        return productRepository.save(product);
    }

    public Optional<Product> update(String id, Product updated) {
        return productRepository.findById(id)
                .map(existing -> {
                    updated.setId(existing.getId());
                    updated.setUserId(existing.getUserId());
                    return productRepository.save(updated);
                });
    }

    public boolean existsById(String id) {
        return productRepository.existsById(id);
    }

    public void deleteById(String id) {
        productRepository.deleteById(id);
    }

}
