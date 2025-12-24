package com.ainote.backend.service;

import com.ainote.backend.exception.AiServiceException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for AiCleaningService.
 * These tests require Ollama to be running locally with llama3.2 model.
 * Tests are skipped gracefully if Ollama is unavailable.
 */
@SpringBootTest
class AiCleaningServiceTest {

    @Autowired
    private AiCleaningService aiCleaningService;

    @Test
    @DisplayName("cleanContent with bullets format returns bullet points")
    void cleanContent_withBulletsFormat_returnsBulletPoints() {
        String messyContent = "buy milk. get eggs. need bread";

        try {
            String result = aiCleaningService.cleanContent(messyContent, "bullets");

            assertNotNull(result);
            assertFalse(result.isBlank());
            // AI should return some form of bullet points (• or -)
            assertTrue(
                    result.contains("•") || result.contains("-") || result.contains("*"),
                    "Expected bullet points in response: " + result
            );
        } catch (AiServiceException e) {
            skipIfOllamaUnavailable(e);
        }
    }

    @Test
    @DisplayName("cleanContent with paragraphs format returns prose")
    void cleanContent_withParagraphsFormat_returnsParagraphs() {
        String messyContent = "meeting was good. discussed project. need follow up";

        try {
            String result = aiCleaningService.cleanContent(messyContent, "paragraphs");

            assertNotNull(result);
            assertFalse(result.isBlank());
            // Paragraphs should not start with bullet or number markers
            assertFalse(
                    result.trim().startsWith("•") || result.trim().startsWith("-") || result.trim().matches("^\\d+\\..*"),
                    "Expected paragraph prose, not list format: " + result
            );
        } catch (AiServiceException e) {
            skipIfOllamaUnavailable(e);
        }
    }

    @Test
    @DisplayName("cleanContent with numbered format returns numbered list")
    void cleanContent_withNumberedFormat_returnsNumberedList() {
        String messyContent = "first do this. then do that. finally finish up";

        try {
            String result = aiCleaningService.cleanContent(messyContent, "numbered");

            assertNotNull(result);
            assertFalse(result.isBlank());
            // Should contain numbered items (1. or 1) format)
            assertTrue(
                    result.contains("1.") || result.contains("1)"),
                    "Expected numbered list in response: " + result
            );
        } catch (AiServiceException e) {
            skipIfOllamaUnavailable(e);
        }
    }

    @Test
    @DisplayName("cleanContent fixes spelling and grammar errors")
    void cleanContent_withMessyInput_fixesSpellingAndGrammar() {
        String messyContent = "teh quikc brown fox jumps ovr the lazzy dog";

        try {
            String result = aiCleaningService.cleanContent(messyContent, "paragraphs");

            assertNotNull(result);
            assertFalse(result.isBlank());
            // AI should fix obvious spelling errors
            String lowerResult = result.toLowerCase();
            assertTrue(
                    lowerResult.contains("the") && lowerResult.contains("quick") && lowerResult.contains("brown"),
                    "Expected spelling corrections in response: " + result
            );
        } catch (AiServiceException e) {
            skipIfOllamaUnavailable(e);
        }
    }

    @Test
    @DisplayName("cleanContent preserves meaning of original content")
    void cleanContent_preservesMeaning() {
        String messyContent = "java is a programing language. python is also populer";

        try {
            String result = aiCleaningService.cleanContent(messyContent, "paragraphs");

            assertNotNull(result);
            String lowerResult = result.toLowerCase();
            // Core concepts should be preserved
            assertTrue(lowerResult.contains("java"), "Should preserve mention of Java: " + result);
            assertTrue(lowerResult.contains("python"), "Should preserve mention of Python: " + result);
            assertTrue(
                    lowerResult.contains("programming") || lowerResult.contains("language"),
                    "Should preserve programming context: " + result
            );
        } catch (AiServiceException e) {
            skipIfOllamaUnavailable(e);
        }
    }

    /**
     * Helper to skip test gracefully when Ollama is not available.
     */
    private void skipIfOllamaUnavailable(AiServiceException e) {
        if (isConnectionError(e)) {
            System.out.println("Skipping test - Ollama not available: " + e.getMessage());
            return; // Test passes but is effectively skipped
        }
        throw e; // Re-throw if it's a different error
    }

    private boolean isConnectionError(Throwable e) {
        Throwable cause = e;
        while (cause != null) {
            if (cause instanceof java.net.ConnectException) {
                return true;
            }
            cause = cause.getCause();
        }
        return false;
    }
}
