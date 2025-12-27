package com.ainote.backend.controller;

import com.ainote.backend.dto.CleanResponse;
import com.ainote.backend.service.NoteService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for NoteController.
 * Tests HTTP layer including validation and error handling.
 */
@WebMvcTest(NoteController.class)
class NoteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private NoteService noteService;

    @Test
    @DisplayName("POST /api/notes/clean with valid request returns 200 OK")
    void cleanNote_validRequest_returns200() throws Exception {
        when(noteService.cleanNote(any())).thenReturn(
                new CleanResponse("my messy notes", "[BULLETS] my messy notes", "bullets", LocalDateTime.now())
        );

        String requestBody = """
                {
                    "content": "my messy notes",
                    "outputFormat": "bullets"
                }
                """;

        mockMvc.perform(post("/api/notes/clean")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.original").value("my messy notes"))
                .andExpect(jsonPath("$.cleaned").value("[BULLETS] my messy notes"))
                .andExpect(jsonPath("$.outputFormat").value("bullets"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    @DisplayName("POST /api/notes/clean with empty content returns 400 Bad Request")
    void cleanNote_emptyContent_returns400() throws Exception {
        String requestBody = """
                {
                    "content": "",
                    "outputFormat": "bullets"
                }
                """;

        mockMvc.perform(post("/api/notes/clean")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Validation failed"))
                .andExpect(jsonPath("$.details[0].field").value("content"))
                .andExpect(jsonPath("$.details[0].message").value("Content cannot be empty"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    @DisplayName("POST /api/notes/clean with null content returns 400 Bad Request")
    void cleanNote_nullContent_returns400() throws Exception {
        String requestBody = """
                {
                    "outputFormat": "bullets"
                }
                """;

        mockMvc.perform(post("/api/notes/clean")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Validation failed"))
                .andExpect(jsonPath("$.details[0].field").value("content"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    @DisplayName("POST /api/notes/clean with invalid outputFormat returns 400 Bad Request")
    void cleanNote_invalidOutputFormat_returns400() throws Exception {
        String requestBody = """
                {
                    "content": "some notes",
                    "outputFormat": "invalid"
                }
                """;

        mockMvc.perform(post("/api/notes/clean")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Validation failed"))
                .andExpect(jsonPath("$.details[0].field").value("outputFormat"))
                .andExpect(jsonPath("$.details[0].message").value("outputFormat must be: bullets, paragraphs, or numbered"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    @DisplayName("POST /api/notes/clean with missing outputFormat returns 400 Bad Request")
    void cleanNote_missingOutputFormat_returns400() throws Exception {
        String requestBody = """
                {
                    "content": "some notes"
                }
                """;

        mockMvc.perform(post("/api/notes/clean")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Validation failed"))
                .andExpect(jsonPath("$.details[0].field").value("outputFormat"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    @DisplayName("POST /api/notes/clean with multiple validation errors returns all errors")
    void cleanNote_multipleErrors_returnsAllErrors() throws Exception {
        String requestBody = """
                {
                    "content": "",
                    "outputFormat": "invalid"
                }
                """;

        mockMvc.perform(post("/api/notes/clean")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Validation failed"))
                .andExpect(jsonPath("$.details.length()").value(2))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    @DisplayName("POST /api/notes/clean with paragraphs format returns correct response")
    void cleanNote_paragraphsFormat_returns200() throws Exception {
        when(noteService.cleanNote(any())).thenReturn(
                new CleanResponse("test content", "[PARAGRAPHS] test content", "paragraphs", LocalDateTime.now())
        );

        String requestBody = """
                {
                    "content": "test content",
                    "outputFormat": "paragraphs"
                }
                """;

        mockMvc.perform(post("/api/notes/clean")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cleaned").value("[PARAGRAPHS] test content"))
                .andExpect(jsonPath("$.outputFormat").value("paragraphs"));
    }

    @Test
    @DisplayName("POST /api/notes/clean with numbered format returns correct response")
    void cleanNote_numberedFormat_returns200() throws Exception {
        when(noteService.cleanNote(any())).thenReturn(
                new CleanResponse("test content", "[NUMBERED] test content", "numbered", LocalDateTime.now())
        );

        String requestBody = """
                {
                    "content": "test content",
                    "outputFormat": "numbered"
                }
                """;

        mockMvc.perform(post("/api/notes/clean")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cleaned").value("[NUMBERED] test content"))
                .andExpect(jsonPath("$.outputFormat").value("numbered"));
    }

    @Test
    @DisplayName("POST /api/notes/clean with content exceeding max length returns 400")
    void cleanNote_contentTooLong_returns400() throws Exception {
        String longContent = "a".repeat(10001);
        String requestBody = """
                {
                    "content": "%s",
                    "outputFormat": "bullets"
                }
                """.formatted(longContent);

        mockMvc.perform(post("/api/notes/clean")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Validation failed"))
                .andExpect(jsonPath("$.details[0].field").value("content"))
                .andExpect(jsonPath("$.details[0].message").value("Content cannot exceed 10000 characters"));
    }
}