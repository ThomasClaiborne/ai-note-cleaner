# AI Note Cleaner - Development Log

A record of development sessions, decisions made, problems solved, and lessons learned.

---

## How to Use This Log

After each development session, add an entry with:
- **Date and session duration**
- **What you worked on**
- **Decisions made and why**
- **Problems encountered and solutions**
- **AI prompts that worked well (or didn't)**
- **What you learned**
- **Next steps**

This becomes interview material and helps you remember your thought process.

---

## Log Entries

### 2024-12-23 - Project Planning & Architecture

**Duration:** ~2 hours  
**Phase:** Pre-development

**What I Did:**
- Reviewed AI-native engineering workflow
- Created project documentation structure
- Defined Phase 1 scope and acceptance criteria
- Made architecture decisions (documented in ARCHITECTURE.md)

**Key Decisions Made:**

1. **Backend-first approach** - Build API before UI because frontend depends on backend, not vice versa. Lets me test core logic before adding visual complexity.

2. **Placeholder before AI** - Implement simple string manipulation first, then swap in AI. This separates "does the API work" from "does the AI work."

3. **Layered package structure** - Organizing by layer (controller/, service/, dto/) rather than by feature. Standard Spring convention, easier for others to understand.

**Problems Encountered:**
- None yet - still in planning phase

**AI Tool Usage:**
- Used Claude to understand architecture decision framework
- Created INSTRUCTIONS.md template for guiding OpenHands

**What I Learned:**
- The importance of asking "what depends on what" when deciding build order
- How to structure instructions for AI coding tools
- ADR (Architecture Decision Record) format for documenting choices

**Next Session:**
- Create the docs/ folder in actual repo
- Add INSTRUCTIONS.md, ARCHITECTURE.md, DEV_LOG.md
- Start Phase 1 with OpenHands: begin with DTOs

---

### 2024-12-23 - Phase 1: Backend API Foundation

**Duration:** ~3 hours  
**Phase:** 1 - Backend API Foundation

**What I Did:**
- Created CleanRequest, CleanResponse, ErrorResponse DTOs with validation
- Built NoteService with placeholder cleaning logic
- Implemented NoteController REST endpoint (POST /api/notes/clean)
- Added GlobalExceptionHandler for validation errors
- Wrote 18 tests (9 unit, 9 integration) - all passing
- Updated README with docs folder structure

**Commits Made:**
- `feat: add request/response DTOs with validation`
- `feat: add NoteService with placeholder cleaning logic`
- `feat: add REST endpoint and exception handling`
- `test: add NoteService and NoteController tests`
- `docs: update README with docs folder structure`

**Key Learnings:**
- `@NotBlank` vs `@NotNull` - NotBlank also rejects whitespace-only strings
- `@Pattern` uses regex to validate specific string values (e.g., `bullets|paragraphs|numbered`)
- Constructor injection preferred over `@Autowired` on fields (better testability, immutability)
- `@WebMvcTest` loads only web layer (faster than `@SpringBootTest` for controller tests)
- `MockMvc` simulates HTTP requests without starting a real server
- JSONPath syntax for testing: `$.field`, `$.array[0].property`

**Code I Want to Remember:**
```java
// Validation annotations on DTO
@NotBlank(message = "Content is required")
private String content;

@Pattern(regexp = "bullets|paragraphs|numbered", message = "Invalid output format")
private String outputFormat;

// Controller test with MockMvc
mockMvc.perform(post("/api/notes/clean")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)))
    .andExpect(status().isOk())
    .andExpect(jsonPath("$.cleaned").exists());
```

**API Tested Successfully:**
```bash
curl -X POST http://localhost:8080/api/notes/clean \
  -H "Content-Type: application/json" \
  -d '{"content": "Test content.", "outputFormat": "bullets"}'
```

**Next Session:**
- Phase 2: AI Integration with Spring AI + Ollama

---

### 2024-12-24 - Phase 2: AI Integration with Spring AI + Ollama

**Duration:** ~2 hours  
**Phase:** 2 - AI Integration

**What I Did:**
- Added Spring AI BOM and Ollama starter dependencies to pom.xml
- Configured Ollama connection in application.properties (localhost:11434, llama3.2 model)
- Created AiServiceException for wrapping AI communication failures
- Created AiCleaningService with prompt engineering for note cleaning
- Updated NoteService to delegate to AiCleaningService (replaced placeholder logic)
- Added AI error handling to GlobalExceptionHandler (503/504/500 responses)
- Updated NoteServiceTest with mocked AiCleaningService
- Created AiCleaningServiceTest integration tests (gracefully skip if Ollama unavailable)
- Updated README with Ollama setup instructions

**Commits Made:**
- `feat: integrate Spring AI with Ollama for note cleaning`

**Key Learnings:**
- Spring AI BOM manages versions for all Spring AI dependencies - no need to specify versions individually
- Spring AI 1.0.0-M4 is a milestone release, requires Spring Milestones repository
- `OllamaChatModel.call(prompt)` is the simplest API for string-in/string-out AI calls
- Low temperature (0.3) produces more consistent, deterministic outputs for note cleaning
- Exception chains need traversal to find root cause (e.g., ConnectException wrapped multiple times)
- Integration tests can gracefully skip when external services unavailable using try-catch pattern

**Code I Want to Remember:**
```java
// Simple AI call with Spring AI
@Service
public class AiCleaningService {
    private final OllamaChatModel chatModel;
    
    public String cleanContent(String content, String outputFormat) {
        String prompt = buildPrompt(content, outputFormat);
        try {
            return chatModel.call(prompt);
        } catch (Exception e) {
            throw new AiServiceException("Failed to communicate with AI service", e);
        }
    }
}

// Finding root cause in exception chain
private Throwable findRootCause(Throwable throwable) {
    Throwable cause = throwable;
    while (cause.getCause() != null && cause.getCause() != cause) {
        cause = cause.getCause();
    }
    return cause;
}
```

**Prompt Template Used:**
```
You are a note cleaning assistant. Your task is to clean and improve the following notes.

Instructions:
1. Fix all spelling and grammar errors
2. Improve clarity and remove redundant content
3. Organize the content logically
4. Format the output as {FORMAT_INSTRUCTION}
5. Preserve the original meaning - do not add new information
6. Return ONLY the cleaned content with no explanations, preambles, or commentary

Notes to clean:
{CONTENT}

Cleaned notes:
```

**API Tested Successfully:**
```bash
curl -X POST http://localhost:8080/api/notes/clean \
  -H "Content-Type: application/json" \
  -d '{"content": "hte quikc brown fox jumps ovr the lazy dog. very fast. much speed.", "outputFormat": "bullets"}'

# Response:
# {"original":"hte quikc brown fox...","cleaned":"• The quick brown fox jumps over the lazy dog.\n• It is known for...","outputFormat":"bullets","timestamp":"..."}
```

**Problems Encountered:**
- AI sometimes adds extra information despite prompt instructions (e.g., added trivia about the pangram)
- Solution: Could refine prompt further, but acceptable for MVP

**Next Session:**
- Phase 3: Frontend Integration (React UI to call the API)

---

### Template for Future Entries

```markdown
### YYYY-MM-DD - [Session Title]

**Duration:** X hours  
**Phase:** [1/2/3/4]

**What I Did:**
- 

**Commits Made:**
- `type: description` - [link if applicable]

**Problems Encountered:**
- Problem:
- Solution:
- Time spent:

**AI Prompts That Worked:**
```
[paste effective prompts here]
```

**AI Prompts That Didn't Work:**
```
[paste ineffective prompts and why]
```

**What I Learned:**
- 

**Code I Want to Remember:**
```java
// paste notable code snippets
```

**Questions for Later:**
- 

**Next Session:**
- 
```

---

## Running Notes

### Useful Commands

```bash
# Backend
cd backend
./mvnw spring-boot:run          # Start server
./mvnw test                      # Run tests
./mvnw clean package             # Build JAR

# Frontend
cd frontend
npm run dev                      # Start dev server
npm run build                    # Production build
npm run lint                     # Check code quality

# Testing API
curl -X POST http://localhost:8080/api/notes/clean \
  -H "Content-Type: application/json" \
  -d '{"content": "Test content.", "outputFormat": "bullets"}'

# Git
git status
git add .
git commit -m "type: description"
git push origin main
```

### Things I Keep Forgetting

- [ ] (Add items here as you encounter them)

### Interview Talking Points

Build this list as you develop - specific examples for behavioral interviews:

**Technical Decisions:**
- "I chose backend-first because..."

**Problem Solving:**
- (Add as you solve problems)

**AI-Native Workflow:**
- "I structured my prompts by..."
- "When the AI gave me code I didn't understand, I..."

---

## Metrics (Optional)

Track if you're curious about your development patterns:

| Date | Hours | Lines Added | Tests Written | AI Prompts | Commits |
|------|-------|-------------|---------------|------------|---------|
| 2024-12-23 | 2 | 0 | 0 | 5 | 0 |
| 2024-12-23 | 3 | ~400 | 18 | ~15 | 5 |
| 2024-12-24 | 2 | ~350 | 10 | ~10 | 1 |

---

*This log is for YOU. Be honest about struggles - that's where learning happens.*
