package org.example.gatewayservice.security.response;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class UnauthorizedResponseWriter {

    public Mono<Void> write(ServerWebExchange exchange, String message) {
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);

        UnauthorizedResponse response = new UnauthorizedResponse(
                false,
                HttpStatus.UNAUTHORIZED.value(),
                message,
                exchange.getRequest().getPath().value(),
                Instant.now());

        byte[] responseBody = serialize(response).getBytes(StandardCharsets.UTF_8);
        return exchange.getResponse().writeWith(Mono.just(exchange.getResponse()
                .bufferFactory()
                .wrap(responseBody)));
    }

    private String serialize(UnauthorizedResponse response) {
        return "{"
                + "\"success\":" + response.success() + ","
                + "\"status\":" + response.status() + ","
                + "\"message\":\"" + escape(response.message()) + "\","
                + "\"path\":\"" + escape(response.path()) + "\","
                + "\"timestamp\":\"" + response.timestamp() + "\""
                + "}";
    }

    private String escape(String value) {
        if (value == null) {
            return "";
        }

        return value.replace("\\", "\\\\")
                .replace("\"", "\\\"");
    }
}
