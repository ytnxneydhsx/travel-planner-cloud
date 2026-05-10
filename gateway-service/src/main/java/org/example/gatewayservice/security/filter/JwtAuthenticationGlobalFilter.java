package org.example.gatewayservice.security.filter;

import com.auth0.jwt.exceptions.JWTVerificationException;
import java.util.List;
import org.example.gatewayservice.security.access.AccessControlProperties;
import org.example.gatewayservice.security.authentication.AuthenticatedUser;
import org.example.gatewayservice.security.authentication.AuthenticationConstants;
import org.example.gatewayservice.security.authentication.JwtTokenVerifier;
import org.example.gatewayservice.security.response.UnauthorizedResponseWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class JwtAuthenticationGlobalFilter implements GlobalFilter, Ordered {

    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationGlobalFilter.class);

    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    private final AccessControlProperties accessControlProperties;

    private final JwtTokenVerifier jwtTokenVerifier;

    private final UnauthorizedResponseWriter unauthorizedResponseWriter;

    public JwtAuthenticationGlobalFilter(
            AccessControlProperties accessControlProperties,
            JwtTokenVerifier jwtTokenVerifier,
            UnauthorizedResponseWriter unauthorizedResponseWriter) {
        this.accessControlProperties = accessControlProperties;
        this.jwtTokenVerifier = jwtTokenVerifier;
        this.unauthorizedResponseWriter = unauthorizedResponseWriter;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        if (shouldSkipAuthentication(exchange)) {
            return chain.filter(exchange);
        }

        String authorization = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (!StringUtils.hasText(authorization)
                || !authorization.startsWith(AuthenticationConstants.AUTHORIZATION_PREFIX)) {
            log.warn("Gateway rejected request due to missing bearer token: path={}",
                    exchange.getRequest().getPath().value());
            return unauthorizedResponseWriter.write(exchange, "Unauthorized.");
        }

        String token = authorization.substring(AuthenticationConstants.AUTHORIZATION_PREFIX.length());
        try {
            AuthenticatedUser authenticatedUser = jwtTokenVerifier.verify(token);
            exchange.getAttributes().put(
                    AuthenticationConstants.AUTHENTICATED_USER_ATTRIBUTE,
                    authenticatedUser);
            return chain.filter(exchange);
        } catch (JWTVerificationException exception) {
            log.warn("Gateway rejected request due to invalid token: path={}",
                    exchange.getRequest().getPath().value());
            return unauthorizedResponseWriter.write(exchange, "Unauthorized.");
        }
    }

    @Override
    public int getOrder() {
        return -100;
    }

    private boolean shouldSkipAuthentication(ServerWebExchange exchange) {
        if (HttpMethod.OPTIONS.equals(exchange.getRequest().getMethod())) {
            return true;
        }

        String requestPath = exchange.getRequest().getPath().value();
        List<String> whitelistPaths = accessControlProperties.getWhitelistPaths();
        if (whitelistPaths == null || whitelistPaths.isEmpty()) {
            return false;
        }

        return whitelistPaths.stream()
                .anyMatch(pattern -> pathMatcher.match(pattern, requestPath));
    }
}
