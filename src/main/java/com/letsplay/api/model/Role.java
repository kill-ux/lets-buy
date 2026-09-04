package com.letsplay.api.model;

/**
 * Role
 */
public enum Role {
    USER, ADMIN;

    public String toAuthority() {
        return "ROLE_" + this;
    }
}
