package com.letsplay.api.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.letsplay.api.dto.UserResponseDTO;
import com.letsplay.api.model.User;
import com.letsplay.api.repository.UserRepository;
import com.letsplay.api.security.TokenBlacklistService;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenBlacklistService tokenBlacklistService;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, TokenBlacklistService tokenBlacklistService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenBlacklistService = tokenBlacklistService;
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
                    if (!existing.getEmail().equals(updated.getEmail()) && userRepository.existsByEmail(updated.getEmail())) {
                        throw new IllegalStateException("Email already in use");
                    }
                    boolean roleChanged = existing.getRole() != updated.getRole();
                    boolean passwordChanged = !passwordEncoder.matches(updated.getPassword(), existing.getPassword());

                    updated.setId(existing.getId());
                    updated.setPassword(passwordEncoder.encode(updated.getPassword()));

                    if (roleChanged || passwordChanged) {
                        tokenBlacklistService.revokeAllTokensForUser(id);
                    }

                    return userRepository.save(updated);
                });
    }

    public boolean existsById(String id) {
        return userRepository.existsById(id);
    }

    public void deleteById(String id) {
        userRepository.deleteById(id);
        tokenBlacklistService.revokeAllTokensForUser(id);
    }
}