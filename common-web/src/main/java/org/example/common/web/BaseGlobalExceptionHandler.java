package org.example.common.web;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;

public abstract class BaseGlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<List<String>>> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException exception) {
        List<String> errorMessages = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(this::toFieldMessage)
                .toList();

        return ResponseEntity.badRequest()
                .body(ApiResponse.failure("Request validation failed.", errorMessages));
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Void>> handleBusinessException(BusinessException exception) {
        return ResponseEntity.status(exception.getStatusCode())
                .body(ApiResponse.failure(exception.getMessage()));
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ApiResponse<Void>> handleResponseStatusException(ResponseStatusException exception) {
        return ResponseEntity.status(exception.getStatusCode())
                .body(ApiResponse.failure(exception.getReason()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleException(Exception exception) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.failure("Internal server error."));
    }

    protected String toFieldMessage(FieldError fieldError) {
        return fieldError.getField() + ": " + fieldError.getDefaultMessage();
    }
}
