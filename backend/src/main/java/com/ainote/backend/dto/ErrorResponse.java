package com.ainote.backend.dto;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Error response DTO for validation and other errors.
 */
public class ErrorResponse {

    private String error;
    private List<FieldError> details;
    private LocalDateTime timestamp;

    public ErrorResponse() {
    }

    public ErrorResponse(String error, List<FieldError> details, LocalDateTime timestamp) {
        this.error = error;
        this.details = details;
        this.timestamp = timestamp;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public List<FieldError> getDetails() {
        return details;
    }

    public void setDetails(List<FieldError> details) {
        this.details = details;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * Represents a field-specific validation error.
     */
    public static class FieldError {

        private String field;
        private String message;

        public FieldError() {
        }

        public FieldError(String field, String message) {
            this.field = field;
            this.message = message;
        }

        public String getField() {
            return field;
        }

        public void setField(String field) {
            this.field = field;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}
