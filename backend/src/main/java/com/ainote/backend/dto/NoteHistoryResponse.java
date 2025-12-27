package com.ainote.backend.dto;

import com.ainote.backend.model.NoteHistory;
import java.time.LocalDateTime;

/**
 * Response DTO for note history items.
 * Separates API contract from JPA entity.
 */
public class NoteHistoryResponse {

    private Long id;
    private String originalContent;
    private String cleanedContent;
    private String outputFormat;
    private LocalDateTime createdAt;

    public NoteHistoryResponse() {
    }

    /**
     * Factory method to convert entity to DTO.
     */
    public static NoteHistoryResponse fromEntity(NoteHistory entity) {
        NoteHistoryResponse dto = new NoteHistoryResponse();
        dto.setId(entity.getId());
        dto.setOriginalContent(entity.getOriginalContent());
        dto.setCleanedContent(entity.getCleanedContent());
        dto.setOutputFormat(entity.getOutputFormat());
        dto.setCreatedAt(entity.getCreatedAt());
        return dto;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOriginalContent() {
        return originalContent;
    }

    public void setOriginalContent(String originalContent) {
        this.originalContent = originalContent;
    }

    public String getCleanedContent() {
        return cleanedContent;
    }

    public void setCleanedContent(String cleanedContent) {
        this.cleanedContent = cleanedContent;
    }

    public String getOutputFormat() {
        return outputFormat;
    }

    public void setOutputFormat(String outputFormat) {
        this.outputFormat = outputFormat;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}