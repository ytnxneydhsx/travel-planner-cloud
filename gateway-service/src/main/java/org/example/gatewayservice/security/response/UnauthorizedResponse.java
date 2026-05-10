package org.example.gatewayservice.security.response;

import java.time.Instant;

public record UnauthorizedResponse(
        boolean success,
        int status,
        String message,
        String path,
        Instant timestamp) {
}
