package com.ainote.backend.service;

import com.ainote.backend.dto.CleanRequest;
import com.ainote.backend.dto.CleanResponse;
import com.ainote.backend.dto.NoteHistoryResponse;
import com.ainote.backend.model.NoteHistory;
import com.ainote.backend.repository.NoteHistoryRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for processing and cleaning notes.
 * Delegates to AiCleaningService and persists results.
 */
@Service
public class NoteService {

    private final AiCleaningService aiCleaningService;
    private final NoteHistoryRepository noteHistoryRepository;

    public NoteService(AiCleaningService aiCleaningService, 
                       NoteHistoryRepository noteHistoryRepository) {
        this.aiCleaningService = aiCleaningService;
        this.noteHistoryRepository = noteHistoryRepository;
    }

    /**
     * Cleans the provided note content, saves to history, and returns result.
     */
    public CleanResponse cleanNote(CleanRequest request) {
        String original = request.getContent();
        String outputFormat = request.getOutputFormat();

        // Get AI-cleaned content
        String cleaned = aiCleaningService.cleanContent(original, outputFormat);

        // Save to database
        NoteHistory history = new NoteHistory(original, cleaned, outputFormat);
        noteHistoryRepository.save(history);

        return new CleanResponse(original, cleaned, outputFormat, LocalDateTime.now());
    }

    /**
     * Retrieves the 50 most recent note transformations.
     */
    public List<NoteHistoryResponse> getHistory() {
        return noteHistoryRepository.findTop50ByOrderByCreatedAtDesc()
                .stream()
                .map(NoteHistoryResponse::fromEntity)
                .collect(Collectors.toList());
    }
}