package com.ainote.backend.dto;

import java.time.LocalDateTime;

/**
 * Response DTO for the note cleaning endpoint.
 */
public class CleanResponse {

    private String original;
    private String cleaned;
    private String outputFormat;
    private LocalDateTime timestamp;

    public CleanResponse() {
    }

    public CleanResponse(String original, String cleaned, String outputFormat, LocalDateTime timestamp) {
        this.original = original;
        this.cleaned = cleaned;
        this.outputFormat = outputFormat;
        this.timestamp = timestamp;
    }

    public String getOriginal() {
        return original;
    }

    public void setOriginal(String original) {
        this.original = original;
    }

    public String getCleaned() {
        return cleaned;
    }

    public void setCleaned(String cleaned) {
        this.cleaned = cleaned;
    }

    public String getOutputFormat() {
        return outputFormat;
    }

    public void setOutputFormat(String outputFormat) {
        this.outputFormat = outputFormat;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
