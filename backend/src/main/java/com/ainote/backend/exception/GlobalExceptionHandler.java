package com.ainote.backend.exception;

import com.ainote.backend.dto.ErrorResponse;
import com.ainote.backend.dto.ErrorResponse.FieldError;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Global exception handler for REST controllers.
 * Converts exceptions into consistent ErrorResponse format.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles validation errors from @Valid annotation.
     * Returns 400 Bad Request with field-level error details.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex) {
        List<FieldError> fieldErrors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> new FieldError(error.getField(), error.getDefaultMessage()))
                .collect(Collectors.toList());

        ErrorResponse response = new ErrorResponse(
                "Validation failed",
                fieldErrors,
                LocalDateTime.now()
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /**
     * Handles AI service failures.
     * Returns appropriate status based on the underlying cause:
     * - 503 Service Unavailable: Ollama not running (connection refused)
     * - 504 Gateway Timeout: AI request timed out
     * - 500 Internal Server Error: Other AI failures
     */
    @ExceptionHandler(AiServiceException.class)
    public ResponseEntity<ErrorResponse> handleAiServiceException(AiServiceException ex) {
        HttpStatus status;
        String message;

        Throwable cause = findRootCause(ex);

        if (cause instanceof ConnectException) {
            status = HttpStatus.SERVICE_UNAVAILABLE;
            message = "AI service is unavailable. Please ensure Ollama is running.";
        } else if (cause instanceof SocketTimeoutException) {
            status = HttpStatus.GATEWAY_TIMEOUT;
            message = "AI service request timed out. Please try again.";
        } else {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
            message = "An error occurred while processing your request.";
        }

        ErrorResponse response = new ErrorResponse(message, null, LocalDateTime.now());
        return ResponseEntity.status(status).body(response);
    }

    /**
     * Handles all other unexpected exceptions.
     * Returns 500 Internal Server Error with generic message.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        ErrorResponse response = new ErrorResponse(
                "An unexpected error occurred",
                null,
                LocalDateTime.now()
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    /**
     * Traverses the exception chain to find the root cause.
     */
    private Throwable findRootCause(Throwable throwable) {
        Throwable cause = throwable;
        while (cause.getCause() != null && cause.getCause() != cause) {
            cause = cause.getCause();
        }
        return cause;
    }
}
