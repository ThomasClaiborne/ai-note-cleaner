package com.ainote.backend.service;

import com.ainote.backend.exception.AiServiceException;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.stereotype.Service;

/**
 * Service for AI-powered note cleaning using Ollama.
 */
@Service
public class AiCleaningService {

    private final OllamaChatModel chatModel;

    public AiCleaningService(OllamaChatModel chatModel) {
        this.chatModel = chatModel;
    }

    /**
     * Cleans and formats content using AI.
     *
     * @param content the raw note content to clean
     * @param outputFormat the desired output format (bullets, paragraphs, numbered)
     * @return the AI-cleaned and formatted content
     * @throws AiServiceException if AI communication fails
     */
    public String cleanContent(String content, String outputFormat) {
        String prompt = buildPrompt(content, outputFormat);

        try {
            return chatModel.call(prompt);
        } catch (Exception e) {
            throw new AiServiceException("Failed to communicate with AI service", e);
        }
    }

    private String buildPrompt(String content, String outputFormat) {
        String formatInstruction = getFormatInstruction(outputFormat);

        return """
            You are a note cleaning assistant. Your task is to clean and improve the following notes.
            
            Instructions:
            1. Fix all spelling and grammar errors
            2. Improve clarity and remove redundant content
            3. Organize the content logically
            4. Format the output as %s
            5. Preserve the original meaning - do not add new information
            6. Return ONLY the cleaned content with no explanations, preambles, or commentary
            
            Notes to clean:
            %s
            
            Cleaned notes:
            """.formatted(formatInstruction, content);
    }

    private String getFormatInstruction(String outputFormat) {
        return switch (outputFormat.toLowerCase()) {
            case "bullets" -> "bullet points using • or - symbols";
            case "numbered" -> "a numbered list (1., 2., 3., etc.)";
            case "paragraphs" -> "clean, well-structured paragraphs";
            default -> "clean, well-structured paragraphs";
        };
    }
}
