package com.ainote.backend.service;

import com.ainote.backend.dto.CleanRequest;
import com.ainote.backend.dto.CleanResponse;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * Service for processing and cleaning notes.
 * Phase 1: Placeholder logic that echoes input with format prefix.
 * Phase 2: Will integrate with AI for actual cleaning.
 */
@Service
public class NoteService {

    /**
     * Cleans the provided note content and formats it according to the requested output format.
     *
     * @param request the clean request containing content and output format
     * @return CleanResponse with original and cleaned content
     */
    public CleanResponse cleanNote(CleanRequest request) {
        String original = request.getContent();
        String outputFormat = request.getOutputFormat();

        // Phase 1: Placeholder logic - prefix with format indicator
        // Phase 2: This will call AI service for actual cleaning
        String cleaned = formatPlaceholder(original, outputFormat);

        return new CleanResponse(original, cleaned, outputFormat, LocalDateTime.now());
    }

    /**
     * Placeholder formatting logic for Phase 1.
     * Simply prefixes the content with the format type.
     */
    private String formatPlaceholder(String content, String format) {
        return "[" + format.toUpperCase() + "] " + content;
    }
}
