package com.ainote.backend.service;

import com.ainote.backend.dto.CleanRequest;
import com.ainote.backend.dto.CleanResponse;
import com.ainote.backend.exception.AiServiceException;
import com.ainote.backend.repository.NoteHistoryRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Unit tests for NoteService.
 */
@ExtendWith(MockitoExtension.class)
class NoteServiceTest {

    @Mock
    private AiCleaningService aiCleaningService;

    @Mock
    private NoteHistoryRepository noteHistoryRepository;  // ADD THIS

    private NoteService noteService;

    @BeforeEach
    void setUp() {
        noteService = new NoteService(aiCleaningService, noteHistoryRepository);  // ADD SECOND ARG
    }

    @Test
    @DisplayName("cleanNote returns response with original content preserved")
    void cleanNote_preservesOriginalContent() {
        when(aiCleaningService.cleanContent(anyString(), anyString())).thenReturn("cleaned content");

        CleanRequest request = new CleanRequest();
        request.setContent("my messy notes");
        request.setOutputFormat("bullets");

        CleanResponse response = noteService.cleanNote(request);

        assertEquals("my messy notes", response.getOriginal());
    }

    @Test
    @DisplayName("cleanNote returns response with correct output format")
    void cleanNote_returnsCorrectOutputFormat() {
        when(aiCleaningService.cleanContent(anyString(), anyString())).thenReturn("cleaned content");

        CleanRequest request = new CleanRequest();
        request.setContent("some content");
        request.setOutputFormat("paragraphs");

        CleanResponse response = noteService.cleanNote(request);

        assertEquals("paragraphs", response.getOutputFormat());
    }

    @Test
    @DisplayName("cleanNote delegates to AiCleaningService with correct parameters")
    void cleanNote_delegatesToAiCleaningService() {
        when(aiCleaningService.cleanContent("test content", "bullets")).thenReturn("• Test content");

        CleanRequest request = new CleanRequest();
        request.setContent("test content");
        request.setOutputFormat("bullets");

        CleanResponse response = noteService.cleanNote(request);

        verify(aiCleaningService).cleanContent("test content", "bullets");
        assertEquals("• Test content", response.getCleaned());
    }

    @Test
    @DisplayName("cleanNote returns AI-cleaned content for bullets format")
    void cleanNote_bulletsFormat_returnsAiCleanedContent() {
        when(aiCleaningService.cleanContent("messy notes here", "bullets"))
                .thenReturn("• Clean note 1\n• Clean note 2");

        CleanRequest request = new CleanRequest();
        request.setContent("messy notes here");
        request.setOutputFormat("bullets");

        CleanResponse response = noteService.cleanNote(request);

        assertEquals("• Clean note 1\n• Clean note 2", response.getCleaned());
    }

    @Test
    @DisplayName("cleanNote returns AI-cleaned content for paragraphs format")
    void cleanNote_paragraphsFormat_returnsAiCleanedContent() {
        when(aiCleaningService.cleanContent("messy notes", "paragraphs"))
                .thenReturn("This is a clean paragraph.");

        CleanRequest request = new CleanRequest();
        request.setContent("messy notes");
        request.setOutputFormat("paragraphs");

        CleanResponse response = noteService.cleanNote(request);

        assertEquals("This is a clean paragraph.", response.getCleaned());
    }

    @Test
    @DisplayName("cleanNote returns AI-cleaned content for numbered format")
    void cleanNote_numberedFormat_returnsAiCleanedContent() {
        when(aiCleaningService.cleanContent("messy notes", "numbered"))
                .thenReturn("1. First item\n2. Second item");

        CleanRequest request = new CleanRequest();
        request.setContent("messy notes");
        request.setOutputFormat("numbered");

        CleanResponse response = noteService.cleanNote(request);

        assertEquals("1. First item\n2. Second item", response.getCleaned());
    }

    @Test
    @DisplayName("cleanNote returns response with timestamp")
    void cleanNote_includesTimestamp() {
        when(aiCleaningService.cleanContent(anyString(), anyString())).thenReturn("cleaned");

        CleanRequest request = new CleanRequest();
        request.setContent("content");
        request.setOutputFormat("bullets");

        CleanResponse response = noteService.cleanNote(request);

        assertNotNull(response.getTimestamp());
    }

    @Test
    @DisplayName("cleanNote propagates AiServiceException")
    void cleanNote_aiServiceFails_propagatesException() {
        when(aiCleaningService.cleanContent(anyString(), anyString()))
                .thenThrow(new AiServiceException("AI service failed"));

        CleanRequest request = new CleanRequest();
        request.setContent("content");
        request.setOutputFormat("bullets");

        assertThrows(AiServiceException.class, () -> noteService.cleanNote(request));
    }

    @Test
    @DisplayName("cleanNote handles special characters in content")
    void cleanNote_specialCharacters_passesToAiService() {
        String specialContent = "Notes with émojis 🎉 and spëcial chars!";
        when(aiCleaningService.cleanContent(specialContent, "numbered"))
                .thenReturn("Notes with emojis and special characters!");

        CleanRequest request = new CleanRequest();
        request.setContent(specialContent);
        request.setOutputFormat("numbered");

        CleanResponse response = noteService.cleanNote(request);

        verify(aiCleaningService).cleanContent(specialContent, "numbered");
        assertEquals(specialContent, response.getOriginal());
    }

    @Test
    @DisplayName("cleanNote handles multiline content")
    void cleanNote_multilineContent_passesToAiService() {
        String multilineContent = "Line 1\nLine 2\nLine 3";
        when(aiCleaningService.cleanContent(multilineContent, "paragraphs"))
                .thenReturn("Line 1. Line 2. Line 3.");

        CleanRequest request = new CleanRequest();
        request.setContent(multilineContent);
        request.setOutputFormat("paragraphs");

        CleanResponse response = noteService.cleanNote(request);

        verify(aiCleaningService).cleanContent(multilineContent, "paragraphs");
        assertEquals(multilineContent, response.getOriginal());
    }
}
