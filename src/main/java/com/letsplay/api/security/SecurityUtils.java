
package com.letsplay.api.security;

import org.springframework.security.core.context.SecurityContextHolder;

import com.letsplay.api.model.Role;

/**
 * SecurityUtils
 */
public class SecurityUtils {

    public static String getCurrentUserId() {
        return (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    public static boolean isAdmin() {
        return SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals(Role.ADMIN.toAuthority()));
    }
}