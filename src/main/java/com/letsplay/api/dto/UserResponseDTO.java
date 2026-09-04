package com.letsplay.api.dto;

import com.letsplay.api.model.Role;
import com.letsplay.api.model.User;

public record UserResponseDTO(String id,
        String name,
        String email,
        Role role) {

    public static UserResponseDTO fromEntity(User user) {
        return new UserResponseDTO(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole());
    }
}
