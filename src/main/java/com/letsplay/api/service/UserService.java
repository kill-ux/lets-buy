package com.letsplay.api.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.letsplay.api.dto.UserResponseDTO;
import com.letsplay.api.model.User;
import com.letsplay.api.repository.UserRepository;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public List<UserResponseDTO> findAllSafe() {
        return userRepository.findAll()
                .stream()
                .map(UserResponseDTO::fromEntity)
                .toList();
    }

    public Optional<User> findById(String id) {
        return userRepository.findById(id);
    }

    public Optional<UserResponseDTO> findByIdSafe(String id) {
        return userRepository.findById(id).map(UserResponseDTO::fromEntity);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public User register(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public Optional<User> update(String id, User updated) {
        return userRepository.findById(id)
                .map(existing -> {
                    updated.setId(existing.getId());
                    updated.setPassword(passwordEncoder.encode(updated.getPassword()));
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