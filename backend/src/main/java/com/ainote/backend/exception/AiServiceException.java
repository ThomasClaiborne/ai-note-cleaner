package com.ainote.backend.exception;

/**
 * Exception thrown when AI service communication fails.
 * Wraps connection errors, timeouts, and other AI-related failures.
 */
public class AiServiceException extends RuntimeException {

    public AiServiceException(String message) {
        super(message);
    }

    public AiServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
