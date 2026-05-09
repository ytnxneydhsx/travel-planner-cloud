package org.example.common.web;

import org.springframework.http.HttpStatusCode;

public class BusinessException extends RuntimeException {

    private final HttpStatusCode statusCode;

    public BusinessException(HttpStatusCode statusCode, String message) {
        super(message);
        this.statusCode = statusCode;
    }

    public HttpStatusCode getStatusCode() {
        return statusCode;
    }
}
