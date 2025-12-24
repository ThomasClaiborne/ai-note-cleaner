package com.ainote.backend.service;

import com.ainote.backend.dto.CleanRequest;
import com.ainote.backend.dto.CleanResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for NoteService.
 */
class NoteServiceTest {

    private NoteService noteService;

    @BeforeEach
    void setUp() {
        noteService = new NoteService();
    }

    @Test
    @DisplayName("cleanNote returns response with original content preserved")
    void cleanNote_preservesOriginalContent() {
        CleanRequest request = new CleanRequest();
        request.setContent("my messy notes");
        request.setOutputFormat("bullets");

        CleanResponse response = noteService.cleanNote(request);

        assertEquals("my messy notes", response.getOriginal());
    }

    @Test
    @DisplayName("cleanNote returns response with correct output format")
    void cleanNote_returnsCorrectOutputFormat() {
        CleanRequest request = new CleanRequest();
        request.setContent("some content");
        request.setOutputFormat("paragraphs");

        CleanResponse response = noteService.cleanNote(request);

        assertEquals("paragraphs", response.getOutputFormat());
    }

    @Test
    @DisplayName("cleanNote returns cleaned content with bullets format prefix")
    void cleanNote_bulletsFormat_prefixesContent() {
        CleanRequest request = new CleanRequest();
        request.setContent("test content");
        request.setOutputFormat("bullets");

        CleanResponse response = noteService.cleanNote(request);

        assertEquals("[BULLETS] test content", response.getCleaned());
    }

    @Test
    @DisplayName("cleanNote returns cleaned content with paragraphs format prefix")
    void cleanNote_paragraphsFormat_prefixesContent() {
        CleanRequest request = new CleanRequest();
        request.setContent("test content");
        request.setOutputFormat("paragraphs");

        CleanResponse response = noteService.cleanNote(request);

        assertEquals("[PARAGRAPHS] test content", response.getCleaned());
    }

    @Test
    @DisplayName("cleanNote returns cleaned content with numbered format prefix")
    void cleanNote_numberedFormat_prefixesContent() {
        CleanRequest request = new CleanRequest();
        request.setContent("test content");
        request.setOutputFormat("numbered");

        CleanResponse response = noteService.cleanNote(request);

        assertEquals("[NUMBERED] test content", response.getCleaned());
    }

    @Test
    @DisplayName("cleanNote returns response with timestamp")
    void cleanNote_includesTimestamp() {
        CleanRequest request = new CleanRequest();
        request.setContent("content");
        request.setOutputFormat("bullets");

        CleanResponse response = noteService.cleanNote(request);

        assertNotNull(response.getTimestamp());
    }

    @Test
    @DisplayName("cleanNote handles empty string content")
    void cleanNote_emptyContent_stillProcesses() {
        CleanRequest request = new CleanRequest();
        request.setContent("");
        request.setOutputFormat("bullets");

        CleanResponse response = noteService.cleanNote(request);

        assertEquals("", response.getOriginal());
        assertEquals("[BULLETS] ", response.getCleaned());
    }

    @Test
    @DisplayName("cleanNote handles content with special characters")
    void cleanNote_specialCharacters_preservesContent() {
        CleanRequest request = new CleanRequest();
        request.setContent("Notes with émojis 🎉 and spëcial chars!");
        request.setOutputFormat("numbered");

        CleanResponse response = noteService.cleanNote(request);

        assertEquals("Notes with émojis 🎉 and spëcial chars!", response.getOriginal());
        assertEquals("[NUMBERED] Notes with émojis 🎉 and spëcial chars!", response.getCleaned());
    }

    @Test
    @DisplayName("cleanNote handles multiline content")
    void cleanNote_multilineContent_preservesContent() {
        CleanRequest request = new CleanRequest();
        request.setContent("Line 1\nLine 2\nLine 3");
        request.setOutputFormat("paragraphs");

        CleanResponse response = noteService.cleanNote(request);

        assertEquals("Line 1\nLine 2\nLine 3", response.getOriginal());
        assertEquals("[PARAGRAPHS] Line 1\nLine 2\nLine 3", response.getCleaned());
    }
}
