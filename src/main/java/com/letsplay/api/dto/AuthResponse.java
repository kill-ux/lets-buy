package com.letsplay.api.dto;

import com.letsplay.api.model.Role;

/**
 * AuthResponse
 */
public record AuthResponse(String token, Role role) {
}