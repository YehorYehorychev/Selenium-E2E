package com.yehorychev.selenium.helpers.shopping;

/**
 * Immutable representation of an authenticated shopping session retrieved via API.
 */
public record ShoppingSession(String token, String userId, String userEmail, String message) {
    public ShoppingSession {
        if (token == null || token.isBlank()) {
            throw new IllegalArgumentException("Token must not be null or blank");
        }
        if (userId == null || userId.isBlank()) {
            throw new IllegalArgumentException("UserId must not be null or blank");
        }
    }

    // Backward compatibility getters
    public String getToken() {
        return token;
    }

    public String getUserId() {
        return userId;
    }
}

