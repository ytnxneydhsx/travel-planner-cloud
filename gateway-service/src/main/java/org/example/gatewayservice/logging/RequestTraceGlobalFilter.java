package org.example.gatewayservice.logging;

import java.util.UUID;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class RequestTraceGlobalFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String traceId = resolveTraceId(exchange);

        ServerWebExchange mutatedExchange = exchange.mutate()
                .request(request -> request.headers(headers ->
                        headers.set(TraceContextConstants.TRACE_ID_HEADER, traceId)))
                .build();
        mutatedExchange.getAttributes().put(TraceContextConstants.TRACE_ID_ATTRIBUTE, traceId);
        mutatedExchange.getResponse().getHeaders().set(TraceContextConstants.TRACE_ID_HEADER, traceId);

        return chain.filter(mutatedExchange);
    }

    @Override
    public int getOrder() {
        return -300;
    }

    private String resolveTraceId(ServerWebExchange exchange) {
        String traceId = exchange.getRequest().getHeaders().getFirst(TraceContextConstants.TRACE_ID_HEADER);
        if (StringUtils.hasText(traceId)) {
            return traceId;
        }

        return UUID.randomUUID().toString().replace("-", "");
    }
}
