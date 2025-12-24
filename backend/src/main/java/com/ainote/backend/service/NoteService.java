package com.ainote.backend.service;

import com.ainote.backend.dto.CleanRequest;
import com.ainote.backend.dto.CleanResponse;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * Service for processing and cleaning notes.
 * Delegates to AiCleaningService for AI-powered content transformation.
 */
@Service
public class NoteService {

    private final AiCleaningService aiCleaningService;

    public NoteService(AiCleaningService aiCleaningService) {
        this.aiCleaningService = aiCleaningService;
    }

    /**
     * Cleans the provided note content and formats it according to the requested output format.
     *
     * @param request the clean request containing content and output format
     * @return CleanResponse with original and cleaned content
     */
    public CleanResponse cleanNote(CleanRequest request) {
        String original = request.getContent();
        String outputFormat = request.getOutputFormat();

        String cleaned = aiCleaningService.cleanContent(original, outputFormat);

        return new CleanResponse(original, cleaned, outputFormat, LocalDateTime.now());
    }
}
