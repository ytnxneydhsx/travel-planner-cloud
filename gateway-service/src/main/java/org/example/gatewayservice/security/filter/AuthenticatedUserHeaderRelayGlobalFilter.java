package org.example.gatewayservice.security.filter;

import org.example.gatewayservice.security.authentication.AuthenticatedUser;
import org.example.gatewayservice.security.authentication.AuthenticationConstants;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class AuthenticatedUserHeaderRelayGlobalFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        AuthenticatedUser authenticatedUser = exchange.getAttribute(
                AuthenticationConstants.AUTHENTICATED_USER_ATTRIBUTE);

        ServerWebExchange mutatedExchange = exchange.mutate()
                .request(request -> request.headers(headers -> {
                    headers.remove(AuthenticationConstants.CURRENT_USER_ID_HEADER);
                    if (authenticatedUser != null && authenticatedUser.userId() != null) {
                        headers.set(
                                AuthenticationConstants.CURRENT_USER_ID_HEADER,
                                String.valueOf(authenticatedUser.userId()));
                    }
                }))
                .build();

        return chain.filter(mutatedExchange);
    }

    @Override
    public int getOrder() {
        return -90;
    }
}
