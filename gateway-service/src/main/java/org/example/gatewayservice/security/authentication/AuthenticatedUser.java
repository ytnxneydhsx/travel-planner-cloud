package org.example.gatewayservice.security.authentication;

public record AuthenticatedUser(
        Long userId,
        String username,
        String nickname) {
}
