# AI Note Cleaner - Development Log

A record of development sessions, decisions made, problems solved, and lessons learned.

---

## Table of Contents

- [Phase 1: Backend API Foundation](#phase-1-backend-api-foundation)
- [Phase 2: AI Integration](#phase-2-ai-integration)
- [Phase 3: React Frontend](#phase-3-react-frontend)
- [Phase 4: Database Persistence](#phase-4-database-persistence)
- [Quick Reference](#quick-reference)
- [Interview Talking Points](#interview-talking-points)

---

## Phase 1: Backend API Foundation

### 2024-12-23 - Project Planning & Architecture

**Duration:** ~2 hours

**What I Did:**
- Reviewed AI-native engineering workflow
- Created project documentation structure
- Defined Phase 1 scope and acceptance criteria
- Made architecture decisions (documented in ARCHITECTURE.md)

**Key Decisions Made:**

| Decision | Reasoning |
|----------|-----------|
| Backend-first approach | Frontend depends on API contract, not vice versa |
| Placeholder before AI | Separates "does plumbing work" from "does AI work" |
| Layered package structure | Standard Spring convention, easier for others to understand |

**What I Learned:**
- The importance of asking "what depends on what" when deciding build order
- How to structure instructions for AI coding tools
- ADR (Architecture Decision Record) format for documenting choices

---

### 2024-12-23 - Phase 1 Implementation

**Duration:** ~3 hours

**What I Built:**
- CleanRequest, CleanResponse, ErrorResponse DTOs with validation
- NoteService with placeholder cleaning logic
- NoteController REST endpoint (POST /api/notes/clean)
- GlobalExceptionHandler for validation errors
- 18 tests (9 unit, 9 integration) - all passing

**Commits Made:**
- `feat: add request/response DTOs with validation`
- `feat: add NoteService with placeholder cleaning logic`
- `feat: add REST endpoint and exception handling`
- `test: add NoteService and NoteController tests`
- `docs: update README with docs folder structure`

**Key Learnings:**

| Concept | What I Learned |
|---------|----------------|
| `@NotBlank` vs `@NotNull` | NotBlank also rejects whitespace-only strings |
| `@Pattern` | Uses regex to validate specific string values |
| Constructor injection | Preferred over `@Autowired` on fields (better testability) |
| `@WebMvcTest` | Loads only web layer (faster than `@SpringBootTest`) |
| `MockMvc` | Simulates HTTP requests without starting a real server |

**Code I Want to Remember:**
```java
// Validation annotations on DTO
@NotBlank(message = "Content cannot be empty")
@Size(max = 10000, message = "Content cannot exceed 10000 characters")
private String content;

@NotBlank(message = "Output format is required")
@Pattern(regexp = "bullets|paragraphs|numbered", 
         message = "outputFormat must be: bullets, paragraphs, or numbered")
private String outputFormat;
```

---

## Phase 2: AI Integration

### 2024-12-24 - Spring AI + Ollama Integration

**Duration:** ~2 hours

**What I Built:**
- Added Spring AI BOM and Ollama starter dependencies
- Configured Ollama connection (localhost:11434, llama3.2 model)
- Created AiServiceException for wrapping AI failures
- Created AiCleaningService with prompt engineering
- Updated NoteService to delegate to AiCleaningService
- Added AI error handling (503/504/500 responses)
- Updated tests with mocked AiCleaningService

**Commits Made:**
- `feat: integrate Spring AI with Ollama for note cleaning`

**Key Learnings:**

| Concept | What I Learned |
|---------|----------------|
| Spring AI BOM | Manages versions for all Spring AI dependencies |
| Temperature setting | Low (0.3) produces more consistent, deterministic outputs |
| Exception chains | Need traversal to find root cause (ConnectException wrapped multiple times) |
| Integration tests | Can gracefully skip when external services unavailable |

**Prompt Template Used:**
```
You are a note cleaning assistant. Your task is to clean and improve the following notes.

Instructions:
1. Fix all spelling and grammar errors
2. Improve clarity and remove redundant content
3. Organize the content logically
4. Format the output as {FORMAT_INSTRUCTION}
5. Preserve the original meaning - do not add new information
6. Return ONLY the cleaned content with no explanations

Notes to clean:
{CONTENT}
```

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

---

## Phase 3: React Frontend

### 2024-12-24 - Frontend Implementation

**Duration:** ~3 hours

**What I Built:**
- TypeScript interfaces matching backend DTOs
- API service with custom ApiError class
- LoadingSpinner component with accessibility
- ErrorMessage component with field-level errors
- FormatSelector component (radio buttons)
- NoteInput component with character count
- NoteOutput component with copy-to-clipboard
- Main App with full state management
- CORS configuration on backend

**Commits Made:**
- `feat: add TypeScript types and API service`
- `feat: add LoadingSpinner and ErrorMessage components`
- `feat: add FormatSelector and NoteInput components`
- `feat: add NoteOutput component with copy functionality`
- `feat: implement main App with full workflow`
- `fix: add CORS support for frontend development`

**Problems Encountered & Solutions:**

| Problem | Cause | Solution | Time |
|---------|-------|----------|------|
| TypeScript errors (`jsx-runtime` not found) | node_modules didn't exist | Run `npm install` | 5 min |
| CORS error calling backend | Different ports = different origins | Add `@CrossOrigin` to controller | 10 min |

**Key Learnings:**

| Concept | What I Learned |
|---------|----------------|
| TypeScript interfaces | Should mirror backend DTOs exactly |
| Custom error classes | ApiError carries statusCode and details for smart handling |
| Controlled components | Parent owns state, child receives value + onChange |
| Clipboard API | `navigator.clipboard.writeText()` is async |
| CORS | Different ports = different origins, even on localhost |

**Code I Want to Remember:**
```typescript
// Custom error class with API details
export class ApiError extends Error {
  constructor(
    public statusCode: number,
    public details: ErrorResponse | null,
    message?: string
  ) {
    super(message || details?.error || 'An error occurred');
    this.name = 'ApiError';
  }
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
```

**Frontend Structure Created:**
```
frontend/src/
├── types/
│   └── index.ts           # CleanRequest, CleanResponse, ErrorResponse
├── services/
│   └── api.ts             # cleanNote(), ApiError class
├── components/
│   ├── LoadingSpinner.tsx # Accessible loading indicator
│   ├── ErrorMessage.tsx   # Error display with field details
│   ├── FormatSelector.tsx # Radio buttons for output format
│   ├── NoteInput.tsx      # Textarea with character count
│   └── NoteOutput.tsx     # Result display with copy button
└── App.tsx                # Main orchestration, state management
```

---

## Phase 4: Database Persistence

### 2024-12-27 - Backend Database Layer

**Duration:** ~2 hours

**What I Built:**
- NoteHistory entity with JPA annotations
- NoteHistoryRepository with Spring Data JPA query method
- NoteHistoryResponse DTO with entity-to-DTO conversion
- Updated NoteService to save history after cleaning
- GET /api/notes/history endpoint
- H2 in-memory database configuration

**Commits Made:**
- `feat: add NoteHistory entity and repository`
- `feat: add history persistence to NoteService`
- `feat: add GET /api/notes/history endpoint`
- `chore: configure H2 database and console`

**Key Learnings:**

| Concept | What I Learned |
|---------|----------------|
| `@Entity` | Marks class as JPA entity mapped to database table |
| `@GeneratedValue(IDENTITY)` | Auto-increment ID, database handles generation |
| `@Column(columnDefinition = "TEXT")` | Override default VARCHAR(255) for long content |
| Spring Data JPA naming | `findTop50ByOrderByCreatedAtDesc()` auto-generates query |
| H2 Console | Access at /h2-console for debugging database |

**Code I Want to Remember:**
```java
// Entity with JPA annotations
@Entity
@Table(name = "note_history")
public class NoteHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String originalContent;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}

// Spring Data JPA method naming convention
public interface NoteHistoryRepository extends JpaRepository {
    List findTop50ByOrderByCreatedAtDesc();
}

// Entity to DTO conversion
public static NoteHistoryResponse fromEntity(NoteHistory entity) {
    return new NoteHistoryResponse(
        entity.getId(),
        entity.getOriginalContent(),
        entity.getCleanedContent(),
        entity.getOutputFormat(),
        entity.getCreatedAt()
    );
}
```

---

### 2024-12-27 - Test Fixes After Constructor Change

**Duration:** ~1 hour

**Problem Encountered:**
After adding `NoteHistoryRepository` to `NoteService` constructor, all 20 tests failed with two different errors:

| Test Class | Error | Root Cause |
|------------|-------|------------|
| NoteServiceTest (11 tests) | `Constructor undefined` | Calling constructor with 1 arg instead of 2 |
| NoteControllerTest (9 tests) | `No qualifying bean of type 'AiCleaningService'` | `@Import` tried to create real bean |

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

// Added mock responses for success tests
when(noteService.cleanNote(any())).thenReturn(
    new CleanResponse("content", "cleaned", "bullets", LocalDateTime.now())
);
```

**Key Learnings:**

| Concept | What I Learned |
|---------|----------------|
| `@WebMvcTest` scope | Only loads web layer - cannot autowire services or repositories |
| `@Import` vs `@MockitoBean` | `@Import` creates real bean (needs dependencies), `@MockitoBean` creates mock |
| When to mock in controller tests | Always mock services - you're testing HTTP layer, not business logic |
| Validation test mocking | Not needed - validation happens BEFORE controller method runs |
| Spring Boot 3.4 change | `@MockBean` deprecated, use `@MockitoBean` from new package |

**Interview Angle:**
> "When I added a repository dependency to my service layer, my tests broke. This taught me that `@WebMvcTest` only loads the web slice - it can't autowire service dependencies. The fix was switching from `@Import` to `@MockitoBean`, which creates a mock without needing real dependencies. This is actually the correct pattern: controller tests should mock services because you're testing HTTP behavior, not business logic."

---

### 2024-12-27 - Frontend History Feature

**Duration:** ~1 hour

**What I Built:**
- Added NoteHistoryItem interface to types
- Added getHistory() function to API service
- Created HistoryItem component with expand/collapse
- Created NoteHistory component with loading/error/empty states
- Integrated history into App with auto-refresh after cleaning

**Commits Made:**
- `feat: add frontend history types and API`
- `feat: add HistoryItem component with expand/collapse`
- `feat: add NoteHistory component with refresh`
- `feat: integrate history into App with auto-refresh`

**Key Learnings:**

| Concept | What I Learned |
|---------|----------------|
| Refresh trigger pattern | Use incrementing number state to trigger useEffect re-fetch |
| Conditional rendering | Show loading → error → empty → content based on state |
| Expand/collapse UI | Toggle boolean state, conditionally render expanded content |
| Date formatting | `toLocaleDateString()` with options for custom format |

**Code I Want to Remember:**
```typescript
// Refresh trigger pattern
const [historyRefreshTrigger, setHistoryRefreshTrigger] = useState(0);

// After successful action, trigger refresh
setHistoryRefreshTrigger(prev => prev + 1);

// In child component, re-fetch when trigger changes
useEffect(() => {
  fetchHistory();
}, [refreshTrigger]);

// Expand/collapse pattern
const [isExpanded, setIsExpanded] = useState(false);
<button onClick={() => setIsExpanded(!isExpanded)}>
  {isExpanded ? '▼' : '▶'}

{isExpanded && Expanded content here}
```

**Frontend Structure Updated:**
```
frontend/src/
├── types/
│   └── index.ts           # Added NoteHistoryItem
├── services/
│   └── api.ts             # Added getHistory()
├── components/
│   ├── HistoryItem.tsx    # NEW - Single item with expand/collapse
│   ├── NoteHistory.tsx    # NEW - List with loading/error states
│   └── ...existing components
└── App.tsx                # Added historyRefreshTrigger state
```

---

## Quick Reference

### Useful Commands
```powershell
# Backend (PowerShell)
cd backend
.\mvnw spring-boot:run          # Start server
.\mvnw test                      # Run tests
.\mvnw clean test                # Clean and run tests

# Frontend (PowerShell)
cd frontend
npm install                      # Install dependencies
npm run dev                      # Start dev server
npm run build                    # Production build

# Ollama
ollama serve                     # Start Ollama server
ollama run llama3.2              # Interactive mode

# H2 Console
# URL: http://localhost:8080/h2-console
# JDBC URL: jdbc:h2:mem:notedb
# Username: sa
# Password: (blank)
```

### Testing the Full Stack
```powershell
# Terminal 1: Ollama
ollama serve

# Terminal 2: Backend
cd backend
.\mvnw spring-boot:run

# Terminal 3: Frontend
cd frontend
npm run dev

# Open browser: http://localhost:5173
```

### API Testing with PowerShell
```powershell
# Clean a note
Invoke-RestMethod -Uri "http://localhost:8080/api/notes/clean" `
  -Method POST `
  -ContentType "application/json" `
  -Body '{"content": "Test content.", "outputFormat": "bullets"}'

# Get history
Invoke-RestMethod -Uri "http://localhost:8080/api/notes/history" -Method GET
```

---

## Interview Talking Points

### Technical Decisions

- "I chose backend-first because the frontend depends on the API contract, not vice versa"
- "I used placeholder logic in Phase 1 to separate 'does the plumbing work' from 'does the AI work'"
- "I used useState over Redux because the app only has a few pieces of state, all in one component"
- "I chose native fetch over axios because we only have two endpoints and it reduces dependencies"
- "I used H2 in-memory database for development to avoid external database setup"

### Problem Solving

- "When I got TypeScript errors about missing jsx-runtime, I realized node_modules wasn't installed"
- "The CORS error taught me that different ports are different origins to the browser"
- "When my tests broke after adding a dependency, I learned the difference between `@Import` and `@MockitoBean`"
- "I learned that `@WebMvcTest` only loads the web layer, so I need to mock service dependencies"

### AI-Native Workflow

- "I structured my prompts with clear file specs and expected behavior"
- "When OpenHands generated code, I reviewed every file before approving"
- "I created INSTRUCTIONS.md files for each phase to guide the AI coding assistant"
- "I maintained a DEV_LOG to track decisions and learnings for interview prep"

---

## Metrics

| Phase | Date | Hours | Files Created | Tests | Commits |
|-------|------|-------|---------------|-------|---------|
| Planning | 2024-12-23 | 2 | 3 (docs) | 0 | 0 |
| Phase 1 | 2024-12-23 | 3 | 6 | 18 | 5 |
| Phase 2 | 2024-12-24 | 2 | 2 | 10 | 1 |
| Phase 3 | 2024-12-24 | 3 | 8 | 0 | 7 |
| Phase 4 | 2024-12-27 | 4 | 6 | 0 | 9 |
| **Total** | | **14** | **25** | **28** | **22** |