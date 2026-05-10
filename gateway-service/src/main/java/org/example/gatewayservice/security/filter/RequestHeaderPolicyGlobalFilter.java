package org.example.gatewayservice.security.filter;

import java.util.List;
import org.example.gatewayservice.security.access.RequestHeaderPolicyProperties;
import org.example.gatewayservice.security.authentication.AuthenticatedUser;
import org.example.gatewayservice.security.authentication.AuthenticationConstants;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class RequestHeaderPolicyGlobalFilter implements GlobalFilter, Ordered {

    private final RequestHeaderPolicyProperties requestHeaderPolicyProperties;

    public RequestHeaderPolicyGlobalFilter(RequestHeaderPolicyProperties requestHeaderPolicyProperties) {
        this.requestHeaderPolicyProperties = requestHeaderPolicyProperties;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        AuthenticatedUser authenticatedUser = exchange.getAttribute(
                AuthenticationConstants.AUTHENTICATED_USER_ATTRIBUTE);

        ServerWebExchange mutatedExchange = exchange.mutate()
                .request(request -> request.headers(headers -> {
                    stripConfiguredHeaders(headers);
                    relayAuthenticatedUserHeaders(headers, authenticatedUser);
                }))
                .build();

        return chain.filter(mutatedExchange);
    }

    @Override
    public int getOrder() {
        return -90;
    }

    private void stripConfiguredHeaders(HttpHeaders headers) {
        List<String> stripRequestHeaders = requestHeaderPolicyProperties.getStripRequestHeaders();
        if (stripRequestHeaders == null || stripRequestHeaders.isEmpty()) {
            return;
        }

        stripRequestHeaders.stream()
                .filter(StringUtils::hasText)
                .forEach(headers::remove);
    }

    private void relayAuthenticatedUserHeaders(
            HttpHeaders headers,
            AuthenticatedUser authenticatedUser) {
        if (authenticatedUser == null || authenticatedUser.userId() == null) {
            return;
        }

        headers.set(
                AuthenticationConstants.CURRENT_USER_ID_HEADER,
                String.valueOf(authenticatedUser.userId()));
    }
}
