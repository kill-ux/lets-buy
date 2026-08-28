package com.letsplay.api.repository;

import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import com.letsplay.api.model.Product;

/**
 * ProductRepository
 */
public interface ProductRepository extends MongoRepository<Product, String> {
    List<Product> findByUserId(String userId);
}
