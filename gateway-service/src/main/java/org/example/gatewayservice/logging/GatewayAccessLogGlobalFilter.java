package org.example.gatewayservice.logging;

import java.net.URI;
import java.util.concurrent.TimeUnit;
import org.example.gatewayservice.security.authentication.AuthenticatedUser;
import org.example.gatewayservice.security.authentication.AuthenticationConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class GatewayAccessLogGlobalFilter implements GlobalFilter, Ordered {

    private static final Logger log = LoggerFactory.getLogger(GatewayAccessLogGlobalFilter.class);

    private static final String UNKNOWN_VALUE = "-";

    private final GatewayAccessLogProperties gatewayAccessLogProperties;

    public GatewayAccessLogGlobalFilter(GatewayAccessLogProperties gatewayAccessLogProperties) {
        this.gatewayAccessLogProperties = gatewayAccessLogProperties;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        if (!gatewayAccessLogProperties.isEnabled()) {
            return chain.filter(exchange);
        }

        long startTime = System.nanoTime();
        return chain.filter(exchange)
                .doOnSuccess(unused -> logCompletedRequest(exchange, startTime))
                .doOnError(exception -> logFailedRequest(exchange, startTime, exception));
    }

    @Override
    public int getOrder() {
        return -200;
    }

    private void logCompletedRequest(ServerWebExchange exchange, long startTime) {
        HttpStatusCode statusCode = exchange.getResponse().getStatusCode();
        int status = statusCode == null ? 0 : statusCode.value();
        String traceId = resolveTraceId(exchange);
        String requestId = resolveRequestId(exchange);

        withTraceMdc(traceId, requestId, () -> log.info(
                "Gateway request completed: requestId={}, traceId={}, method={}, path={}, status={}, routeId={}, targetUri={}, userId={}, durationMs={}",
                requestId,
                traceId,
                exchange.getRequest().getMethod(),
                exchange.getRequest().getPath().value(),
                status,
                resolveRouteId(exchange),
                resolveTargetUri(exchange),
                resolveUserId(exchange),
                calculateDurationMillis(startTime)));
    }

    private void logFailedRequest(ServerWebExchange exchange, long startTime, Throwable exception) {
        String traceId = resolveTraceId(exchange);
        String requestId = resolveRequestId(exchange);

        withTraceMdc(traceId, requestId, () -> log.error(
                "Gateway request failed: requestId={}, traceId={}, method={}, path={}, routeId={}, targetUri={}, userId={}, durationMs={}",
                requestId,
                traceId,
                exchange.getRequest().getMethod(),
                exchange.getRequest().getPath().value(),
                resolveRouteId(exchange),
                resolveTargetUri(exchange),
                resolveUserId(exchange),
                calculateDurationMillis(startTime),
                exception));
    }

    private String resolveRouteId(ServerWebExchange exchange) {
        Route route = exchange.getAttribute(ServerWebExchangeUtils.GATEWAY_ROUTE_ATTR);
        if (route == null || !StringUtils.hasText(route.getId())) {
            return UNKNOWN_VALUE;
        }

        return route.getId();
    }

    private String resolveTargetUri(ServerWebExchange exchange) {
        URI targetUri = exchange.getAttribute(ServerWebExchangeUtils.GATEWAY_REQUEST_URL_ATTR);
        if (targetUri == null) {
            return UNKNOWN_VALUE;
        }

        return targetUri.toString();
    }

    private String resolveUserId(ServerWebExchange exchange) {
        AuthenticatedUser authenticatedUser = exchange.getAttribute(
                AuthenticationConstants.AUTHENTICATED_USER_ATTRIBUTE);
        if (authenticatedUser == null || authenticatedUser.userId() == null) {
            return UNKNOWN_VALUE;
        }

        return String.valueOf(authenticatedUser.userId());
    }

    private String resolveTraceId(ServerWebExchange exchange) {
        String traceId = exchange.getAttribute(TraceContextConstants.TRACE_ID_ATTRIBUTE);
        if (StringUtils.hasText(traceId)) {
            return traceId;
        }

        traceId = exchange.getRequest().getHeaders().getFirst(TraceContextConstants.TRACE_ID_HEADER);
        if (!StringUtils.hasText(traceId)) {
            return UNKNOWN_VALUE;
        }

        return traceId;
    }

    private String resolveRequestId(ServerWebExchange exchange) {
        String requestId = exchange.getAttribute(TraceContextConstants.REQUEST_ID_ATTRIBUTE);
        if (StringUtils.hasText(requestId)) {
            return requestId;
        }

        requestId = exchange.getRequest().getHeaders().getFirst(TraceContextConstants.REQUEST_ID_HEADER);
        if (!StringUtils.hasText(requestId)) {
            return UNKNOWN_VALUE;
        }

        return requestId;
    }

    private void withTraceMdc(String traceId, String requestId, Runnable logAction) {
        String previousTraceId = MDC.get(TraceContextConstants.TRACE_ID_MDC_KEY);
        String previousRequestId = MDC.get(TraceContextConstants.REQUEST_ID_MDC_KEY);
        putMdcValue(TraceContextConstants.TRACE_ID_MDC_KEY, traceId);
        putMdcValue(TraceContextConstants.REQUEST_ID_MDC_KEY, requestId);
        try {
            logAction.run();
        } finally {
            restoreMdcValue(TraceContextConstants.TRACE_ID_MDC_KEY, previousTraceId);
            restoreMdcValue(TraceContextConstants.REQUEST_ID_MDC_KEY, previousRequestId);
        }
    }

    private void putMdcValue(String key, String value) {
        if (StringUtils.hasText(value) && !UNKNOWN_VALUE.equals(value)) {
            MDC.put(key, value);
            return;
        }

        MDC.remove(key);
    }

    private void restoreMdcValue(String key, String value) {
        if (StringUtils.hasText(value)) {
            MDC.put(key, value);
            return;
        }

        MDC.remove(key);
    }

    private long calculateDurationMillis(long startTime) {
        return TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime);
    }
}
