
package com.letsplay.api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.letsplay.api.dto.AuthResponse;
import com.letsplay.api.dto.LoginRequest;
import com.letsplay.api.dto.RegisterRequest;
import com.letsplay.api.model.Role;
import com.letsplay.api.model.User;
import com.letsplay.api.security.JwtUtil;

/**
 * AuthService
 */
@Service
public class AuthService {
    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AuthService(UserService userService, JwtUtil jwtUtil, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
    }

    public AuthResponse register(RegisterRequest request) {
        if (userService.existsByEmail(request.email())) {
            throw new IllegalStateException("Email already in use");
        }

        User user = new User(request);
        user.setRole(Role.USER);

        User saved = userService.register(user);
        return authResponse(saved.getId(), user.getRole());
    }

    public AuthResponse login(LoginRequest request) {
        User user = userService.findByEmail(request.email())
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password"));

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new IllegalArgumentException("Invalid email or password");
        }

        return authResponse(user.getId(), user.getRole());
    }

    private AuthResponse authResponse(String id, Role role) {
        String token = jwtUtil.generateToken(id, role);
        return new AuthResponse(token, role);
    }

}