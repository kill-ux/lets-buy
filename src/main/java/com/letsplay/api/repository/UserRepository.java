package com.letsplay.api.repository;

import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;
import com.letsplay.api.model.User;

/**
 * UserRepository
 */
public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);
}
