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

### 2024-12-24 - Phase 3: React Frontend

**Duration:** ~3 hours  
**Phase:** 3 - React Frontend

**What I Did:**
- Created TypeScript interfaces matching backend DTOs (types/index.ts)
- Built API service with custom ApiError class (services/api.ts)
- Created LoadingSpinner component with accessibility features
- Created ErrorMessage component with field-level error display
- Created FormatSelector component (radio buttons for output format)
- Created NoteInput component with character count and limit warnings
- Created NoteOutput component with copy-to-clipboard functionality
- Replaced App.tsx with full application orchestration
- Deleted unused App.css (all styling via Tailwind)
- Added CORS configuration to backend NoteController

**Commits Made:**
- `feat: add TypeScript types and API service`
- `feat: add LoadingSpinner and ErrorMessage components`
- `feat: add FormatSelector and NoteInput components`
- `feat: add NoteOutput component with copy functionality`
- `feat: implement main App with full workflow`
- `chore: add Phase 3 instructions and update package-lock.json`
- `fix: add CORS support for frontend development`

**Problems Encountered:**

1. **TypeScript errors in components**
   - Problem: `react/jsx-runtime` module not found, `JSX.IntrinsicElements` missing
   - Cause: `npm install` hadn't been run - node_modules didn't exist
   - Solution: Run `npm install` in frontend directory
   - Time spent: ~5 minutes

2. **CORS error when frontend called backend**
   - Problem: `Access to fetch blocked by CORS policy: No 'Access-Control-Allow-Origin' header`
   - Cause: Frontend (port 5173) and backend (port 8080) are different origins
   - Solution: Added `@CrossOrigin(origins = "http://localhost:5173")` to NoteController
   - Time spent: ~10 minutes
   - Lesson: Always consider CORS when frontend and backend run on different ports

**Key Learnings:**

- **TypeScript interfaces should mirror backend DTOs exactly** - Prevents type mismatches at runtime
- **Custom error classes are powerful** - `ApiError` carries statusCode and details for smart error handling
- **Controlled components in React** - Parent owns state, child receives value + onChange callback
- **Tailwind utility classes** - No separate CSS files needed, styles co-located with components
- **Clipboard API is async** - `navigator.clipboard.writeText()` returns a Promise
- **CORS is origin-based** - Different ports = different origins, even on localhost

**Code I Want to Remember:**

```typescript
// Custom error class with API details
export class ApiError extends Error {
  constructor(
    message: string,
    public statusCode: number,
    public details: ErrorResponse | null = null
  ) {
    super(message);
    this.name = 'ApiError';
  }
}

// Controlled input pattern
interface NoteInputProps {
  value: string;
  onChange: (content: string) => void;
  disabled?: boolean;
}

// Copy to clipboard with feedback
const handleCopy = async () => {
  try {
    await navigator.clipboard.writeText(content);
    setCopied(true);
    setTimeout(() => setCopied(false), 2000);
  } catch (err) {
    console.error('Failed to copy:', err);
  }
};

// Character limit with visual warnings
const isNearLimit = characterCount >= maxLength * 0.9;
const isAtLimit = characterCount >= maxLength;
```

**Frontend Structure Created:**
```
frontend/src/
├── types/
│   └── index.ts           # CleanRequest, CleanResponse, ErrorResponse, etc.
├── services/
│   └── api.ts             # cleanNote() function, ApiError class
├── components/
│   ├── LoadingSpinner.tsx # Accessible loading indicator
│   ├── ErrorMessage.tsx   # Error display with field details
│   ├── FormatSelector.tsx # Radio buttons for output format
│   ├── NoteInput.tsx      # Textarea with character count
│   └── NoteOutput.tsx     # Result display with copy button
└── App.tsx                # Main orchestration, state management
```

**Testing the Full Stack (PowerShell):**
```powershell
# Terminal 1: Ollama (if not running as service)
ollama serve

# Terminal 2: Backend
cd backend
.\mvnw spring-boot:run

# Terminal 3: Frontend
cd frontend
npm run dev

# Open browser to http://localhost:5173
```

**What I Learned About AI-Native Workflow:**
- OpenHands works file-by-file; need to specify commit points
- Always run `npm install` before expecting TypeScript to work
- CORS issues are predictable - should add to INSTRUCTIONS.md upfront
- AI tools sometimes skip ahead - need to enforce "one file at a time" rule

**Next Session:**
- Phase 4: Database Persistence (optional)
- Or: Polish, testing, deployment preparation

---

## Running Notes

### Useful Commands

```powershell
# Backend (PowerShell)
cd backend
.\mvnw spring-boot:run          # Start server
.\mvnw test                      # Run tests
.\mvnw clean package             # Build JAR

# Frontend (PowerShell)
cd frontend
npm run dev                      # Start dev server
npm run build                    # Production build
npm run lint                     # Check code quality

# Ollama
ollama serve                     # Start Ollama server
ollama run llama3.2              # Interactive mode with model

# Testing API (PowerShell)
Invoke-RestMethod -Uri "http://localhost:8080/api/notes/clean" `
  -Method POST `
  -ContentType "application/json" `
  -Body '{"content": "Test content.", "outputFormat": "bullets"}'

# Git
git status
git add .
git commit -m "type: description"
git push origin branch-name
```

### Things I Keep Forgetting

- [x] Use `.\` not `./` for running scripts in PowerShell
- [x] Run `npm install` before expecting TypeScript to work
- [x] CORS needs configuration when frontend/backend on different ports
- [ ] (Add items here as you encounter them)

### Interview Talking Points

Build this list as you develop - specific examples for behavioral interviews:

**Technical Decisions:**
- "I chose backend-first because the frontend depends on the API contract, not vice versa"
- "I used useState over Redux because the app only has 5 pieces of state, all in one component"
- "I chose native fetch over axios because Dev10 teaches fetch and we only have one endpoint"

**Problem Solving:**
- "When I got TypeScript errors about missing jsx-runtime, I realized node_modules wasn't installed"
- "The CORS error taught me that different ports are different origins to the browser"

**AI-Native Workflow:**
- "I structured my prompts with clear file specs and expected behavior"
- "When OpenHands generated code, I reviewed every file before approving"
- "I learned to specify commit points since the AI works one file at a time"

---

## Metrics

| Date | Hours | Files Created | Tests Written | Commits |
|------|-------|---------------|---------------|---------|
| 2024-12-23 | 2 | 0 | 0 | 0 |
| 2024-12-23 | 3 | 6 | 18 | 5 |
| 2024-12-24 | 2 | 2 | 10 | 1 |
| 2024-12-24 | 3 | 8 | 0 | 7 |

---

### 2024-12-27 - Phase 4: Test Fixes After Adding NoteHistoryRepository

**Duration:** ~1 hour  
**Phase:** 4 - Database Persistence (test fixes)

**Problem Encountered:**
After adding `NoteHistoryRepository` to `NoteService` constructor in Phase 4, all 20 tests started failing with two different errors:

1. **NoteServiceTest (11 tests):** `Constructor undefined` error
   - Test was calling `new NoteService(aiCleaningService)` with only 1 argument
   - But `NoteService` now requires 2 arguments: `AiCleaningService` AND `NoteHistoryRepository`

2. **NoteControllerTest (9 tests):** `No qualifying bean of type 'AiCleaningService'` error
   - Test used `@Import(NoteService.class)` which tried to create a real `NoteService` bean
   - `@WebMvcTest` only loads web layer - it couldn't autowire `AiCleaningService` or `NoteHistoryRepository`

**Root Cause:**
When you add a dependency to a class constructor, ALL code that instantiates that class must be updated - including tests. This is actually a feature, not a bug: the compiler forces you to handle the new dependency everywhere.

**Solutions Applied:**

**NoteServiceTest.java:**
```java
// Added new mock
@Mock
private NoteHistoryRepository noteHistoryRepository;

// Updated constructor call
@BeforeEach
void setUp() {
    noteService = new NoteService(aiCleaningService, noteHistoryRepository);
}
```

**NoteControllerTest.java:**
```java
// Removed: @Import(NoteService.class)
// Added: @MockitoBean to mock the entire service
@MockitoBean
private NoteService noteService;

// Added mock responses for success tests (200 OK)
when(noteService.cleanNote(any())).thenReturn(
    new CleanResponse("content", "cleaned", "bullets", LocalDateTime.now())
);
```

**Additional Learning - Spring Boot 3.4 Deprecation:**
`@MockBean` is deprecated in Spring Boot 3.4+. Use `@MockitoBean` instead:
- Old: `org.springframework.boot.test.mock.mockito.MockBean`
- New: `org.springframework.test.context.bean.override.mockito.MockitoBean`

**Key Learnings:**

| Concept | Explanation |
|---------|-------------|
| `@WebMvcTest` scope | Only loads web layer (controllers, filters). Cannot autowire services or repositories. |
| `@Import` vs `@MockitoBean` | `@Import` creates real bean (needs all dependencies). `@MockitoBean` creates mock (no dependencies needed). |
| When to mock service in controller tests | Always mock services in `@WebMvcTest` - you're testing HTTP layer, not business logic. |
| Validation tests don't need mocks | Validation happens BEFORE controller method runs, so service is never called. |

**Interview Angle:**
"When I added a repository dependency to my service layer, my tests broke. This taught me that `@WebMvcTest` only loads the web slice - it can't autowire service dependencies. The fix was switching from `@Import` to `@MockitoBean`, which creates a mock without needing real dependencies. This is actually the correct pattern: controller tests should mock services because you're testing HTTP behavior, not business logic."

**Tests After Fix:**
- NoteServiceTest: 11 passed ✓
- NoteControllerTest: 9 passed ✓
- Total: 20 tests, 0 failures

*This log is for YOU. Be honest about struggles - that's where learning happens.*