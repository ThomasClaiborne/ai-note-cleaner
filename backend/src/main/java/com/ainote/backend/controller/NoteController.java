package com.ainote.backend.controller;

import com.ainote.backend.dto.CleanRequest;
import com.ainote.backend.dto.CleanResponse;
import com.ainote.backend.dto.NoteHistoryResponse;
import com.ainote.backend.service.NoteService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for note cleaning operations.
 */
@RestController
@RequestMapping("/api/notes")
@CrossOrigin(origins = "http://localhost:5173")
public class NoteController {

    private final NoteService noteService;

    public NoteController(NoteService noteService) {
        this.noteService = noteService;
    }

    /**
     * Cleans and formats the provided note content.
     */
    @PostMapping("/clean")
    public ResponseEntity<CleanResponse> cleanNote(@Valid @RequestBody CleanRequest request) {
        CleanResponse response = noteService.cleanNote(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Retrieves the history of note transformations.
     */
    @GetMapping("/history")
    public ResponseEntity<List<NoteHistoryResponse>> getHistory() {
        List<NoteHistoryResponse> history = noteService.getHistory();
        return ResponseEntity.ok(history);
    }
}