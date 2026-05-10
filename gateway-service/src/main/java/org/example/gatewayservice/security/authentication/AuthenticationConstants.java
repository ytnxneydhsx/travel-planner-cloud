package org.example.gatewayservice.security.authentication;

public final class AuthenticationConstants {

    public static final String AUTHENTICATED_USER_ATTRIBUTE = "authenticatedUser";

    public static final String CURRENT_USER_ID_HEADER = "X-User-Id";

    public static final String AUTHORIZATION_TOKEN_TYPE = "Bearer";

    public static final String AUTHORIZATION_PREFIX = AUTHORIZATION_TOKEN_TYPE + " ";

    private AuthenticationConstants() {
    }
}
