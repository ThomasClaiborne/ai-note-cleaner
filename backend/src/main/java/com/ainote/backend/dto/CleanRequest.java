package com.ainote.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Request DTO for the note cleaning endpoint.
 */
public class CleanRequest {

    @NotBlank(message = "Content cannot be empty")
    @Size(max = 10000, message = "Content cannot exceed 10000 characters")
    private String content;

    @NotBlank(message = "Output format is required")
    @Pattern(regexp = "^(bullets|paragraphs|numbered)$",
             message = "outputFormat must be: bullets, paragraphs, or numbered")
    private String outputFormat;

    public CleanRequest() {
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getOutputFormat() {
        return outputFormat;
    }

    public void setOutputFormat(String outputFormat) {
        this.outputFormat = outputFormat;
    }
}
