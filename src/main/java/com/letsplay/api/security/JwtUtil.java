
package com.letsplay.api.security;

import java.util.Date;
import java.util.Optional;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.letsplay.api.model.Role;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

/**
 * JwtUtil
 */
@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration-ms}")
    private long expirationMs;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    public String generateToken(String userId, Role role) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + expirationMs);

        return Jwts.builder()
                .subject(userId)
                .claim("role", role.name())
                .issuedAt(now)
                .expiration(expiry)
                .signWith(getSigningKey())
                .compact();
    }

    public String extractUserId(Claims claims) {
        return claims.getSubject();
    }

    public long extractIssuedAtEpochSeconds(Claims claims) {
        return claims.getIssuedAt().toInstant().getEpochSecond();
    }

    public Role extractRole(Claims claims) {
        String roleStr = claims.get("role", String.class);
        return Role.valueOf(roleStr);
    }

    public Optional<Claims> isTokenValid(String token) {
        try {
            return Optional.of(parseClaims(token));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}