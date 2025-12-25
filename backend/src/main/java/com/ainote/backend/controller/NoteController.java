package com.ainote.backend.controller;

import com.ainote.backend.dto.CleanRequest;
import com.ainote.backend.dto.CleanResponse;
import com.ainote.backend.service.NoteService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
     *
     * @param request the clean request with content and output format
     * @return CleanResponse with original and cleaned content
     */
    @PostMapping("/clean")
    public ResponseEntity<CleanResponse> cleanNote(@Valid @RequestBody CleanRequest request) {
        CleanResponse response = noteService.cleanNote(request);
        return ResponseEntity.ok(response);
    }
}
