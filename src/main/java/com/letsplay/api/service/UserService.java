package com.letsplay.api.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.letsplay.api.model.User;
import com.letsplay.api.repository.UserRepository;

@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public Optional<User> findById(String id) {
        return userRepository.findById(id);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public User save(User user) {
        return userRepository.save(user);
    }

    public Optional<User> update(String id, User updated) {
        return userRepository.findById(id)
                .map(existing -> {
                    updated.setId(existing.getId());
                    return userRepository.save(updated);
                });
    }

    public boolean existsById(String id) {
        return userRepository.existsById(id);
    }

    public void deleteById(String id) {
        userRepository.deleteById(id);
    }
}