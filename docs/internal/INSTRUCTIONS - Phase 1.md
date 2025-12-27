# AI Note Cleaner - Development Instructions

## Project Overview

Full-stack web application that transforms messy notes into clean, structured text using AI.

**Tech Stack:**
- Backend: Spring Boot 3.4.x, Java 21, Maven
- Frontend: React 19, TypeScript, Vite, Tailwind CSS
- AI: Spring AI with Ollama (local LLM)

**Repository Structure:**
```
ai-note-cleaner/
├── backend/                          # Spring Boot REST API
│   └── src/main/java/com/ainote/backend/
│       ├── BackendApplication.java   # Entry point (exists)
│       ├── controller/               # REST endpoints
│       ├── service/                  # Business logic
│       ├── dto/                      # Request/Response objects
│       └── exception/                # Error handling
├── frontend/                         # React TypeScript app
│   └── src/
├── docs/                             # Documentation
│   ├── INSTRUCTIONS.md               # This file
│   ├── ARCHITECTURE.md               # Design decisions
│   └── DEV_LOG.md                    # Development journal
└── README.md
```

---

## Workflow Rules

**CRITICAL - READ BEFORE ANY WORK:**

1. **Do NOT commit automatically** - Stage changes only
2. After completing each file, output: `READY FOR REVIEW: [filename]`
3. **Wait for human approval** before proceeding to next file
4. Explain what each file does and why it's structured that way
5. If unsure about anything, **ask before implementing**

---

## Current Phase: 1 - Backend API Foundation

### Goal

Create a working REST API that accepts note text and returns cleaned text.
For now, the "cleaning" is a placeholder - AI integration comes in Phase 2.

### What Exists

- Basic Spring Boot project scaffolded (`BackendApplication.java`)
- Basic React + Vite + Tailwind scaffolded (default template)
- H2 database dependency (for later phases)
- Maven wrapper configured

### What We're Building This Phase

**Endpoint:** `POST /api/notes/clean`

**Request Body:**
```json
{
  "content": "messy notes here with typos and bad formatting",
  "outputFormat": "bullets"
}
```

**Valid outputFormat values:** `"bullets"`, `"paragraphs"`, `"numbered"`

**Response Body (200 OK):**
```json
{
  "original": "messy notes here...",
  "cleaned": "• Point one\n• Point two\n• Point three",
  "outputFormat": "bullets",
  "timestamp": "2024-12-22T10:30:00Z"
}
```

**Error Response (400 Bad Request):**
```json
{
  "error": "Validation failed",
  "details": [
    { "field": "content", "message": "Content cannot be empty" }
  ],
  "timestamp": "2024-12-22T10:30:00Z"
}
```

---

## Files to Create

### Order of Implementation

Build in this order (each depends on the previous):

```
1. DTOs (data structures)
   └── dto/CleanRequest.java
   └── dto/CleanResponse.java
   └── dto/ErrorResponse.java

2. Service (business logic)
   └── service/NoteService.java

3. Controller (REST endpoint)
   └── controller/NoteController.java

4. Exception Handling
   └── exception/GlobalExceptionHandler.java

5. Tests
   └── test/.../service/NoteServiceTest.java
   └── test/.../controller/NoteControllerTest.java
```

### File Specifications

#### 1. CleanRequest.java
**Location:** `backend/src/main/java/com/ainote/backend/dto/CleanRequest.java`

```java
// Validation rules:
// - content: @NotBlank, @Size(max = 10000)
// - outputFormat: @NotBlank, @Pattern for valid values

// Fields:
// - String content
// - String outputFormat

// Include: getters, setters, no-arg constructor
```

#### 2. CleanResponse.java
**Location:** `backend/src/main/java/com/ainote/backend/dto/CleanResponse.java`

```java
// Fields:
// - String original (the input text echoed back)
// - String cleaned (the transformed text)
// - String outputFormat (the format used)
// - LocalDateTime timestamp (when processed)

// Include: getters, setters, no-arg constructor, all-args constructor
```

#### 3. ErrorResponse.java
**Location:** `backend/src/main/java/com/ainote/backend/dto/ErrorResponse.java`

```java
// Fields:
// - String error (main error message)
// - List<FieldError> details (field-specific errors)
// - LocalDateTime timestamp

// Inner class FieldError:
// - String field
// - String message
```

#### 4. NoteService.java
**Location:** `backend/src/main/java/com/ainote/backend/service/NoteService.java`

```java
// @Service annotation

// Method: CleanResponse cleanNote(CleanRequest request)

// PLACEHOLDER LOGIC (will be replaced with AI in Phase 2):
// - If format is "bullets": Split by sentences, prefix each with "• "
// - If format is "paragraphs": Return text as-is (trimmed)
// - If format is "numbered": Split by sentences, prefix with "1. ", "2. ", etc.

// Sentence splitting: Split on ". " or ".\n" (simple approach)
```

#### 5. NoteController.java
**Location:** `backend/src/main/java/com/ainote/backend/controller/NoteController.java`

```java
// @RestController
// @RequestMapping("/api/notes")
// @CrossOrigin(origins = "http://localhost:5173") // For frontend dev

// Constructor injection of NoteService

// @PostMapping("/clean")
// Accept: @Valid @RequestBody CleanRequest
// Return: ResponseEntity<CleanResponse>
```

#### 6. GlobalExceptionHandler.java
**Location:** `backend/src/main/java/com/ainote/backend/exception/GlobalExceptionHandler.java`

```java
// @ControllerAdvice
// @RestController

// Handle MethodArgumentNotValidException → 400 with ErrorResponse
// Handle generic Exception → 500 with simple error message
```

---

## Test Specifications

### NoteServiceTest.java
**Location:** `backend/src/test/java/com/ainote/backend/service/NoteServiceTest.java`

Test cases:
- `cleanNote_withBulletsFormat_returnsBulletPoints()`
- `cleanNote_withParagraphsFormat_returnsTrimmedText()`
- `cleanNote_withNumberedFormat_returnsNumberedList()`
- `cleanNote_withMultipleSentences_splitsCorrectly()`

### NoteControllerTest.java
**Location:** `backend/src/test/java/com/ainote/backend/controller/NoteControllerTest.java`

Use `@WebMvcTest(NoteController.class)` and mock NoteService.

Test cases:
- `cleanNote_withValidRequest_returns200()`
- `cleanNote_withEmptyContent_returns400()`
- `cleanNote_withContentTooLong_returns400()`
- `cleanNote_withInvalidFormat_returns400()`

---

## Acceptance Criteria

Before this phase is complete, verify:

- [ ] `POST /api/notes/clean` returns 200 with valid input
- [ ] Returns 400 when content is empty or blank
- [ ] Returns 400 when content exceeds 10000 characters
- [ ] Returns 400 when outputFormat is not bullets/paragraphs/numbered
- [ ] Response includes original text, cleaned text, format, and timestamp
- [ ] All unit tests pass (`./mvnw test`)
- [ ] Can test endpoint manually with curl:

```bash
curl -X POST http://localhost:8080/api/notes/clean \
  -H "Content-Type: application/json" \
  -d '{"content": "First sentence. Second sentence. Third sentence.", "outputFormat": "bullets"}'
```

---

## Do NOT

- Add AI/LLM integration (Phase 2)
- Create frontend components (Phase 3)
- Add database persistence (Phase 4)
- Add authentication/authorization
- Add logging configuration beyond defaults
- Over-engineer - keep it simple
- Commit without human review

---

## Commit Guidelines

Use conventional commits. Suggested commit sequence:

1. `feat: add request/response DTOs with validation`
2. `feat: add NoteService with placeholder cleaning logic`
3. `test: add NoteService unit tests`
4. `feat: add NoteController REST endpoint`
5. `feat: add GlobalExceptionHandler for validation errors`
6. `test: add NoteController integration tests`
7. `docs: add API usage examples to README`

---

## Phase Overview (For Context)

| Phase | Focus | Status |
|-------|-------|--------|
| 1 | Backend API Foundation | ← CURRENT |
| 2 | AI Integration (Spring AI + Ollama) | Not started |
| 3 | React Frontend | Not started |
| 4 | Database Persistence | Not started |

---

## Questions?

If anything is unclear about:
- Validation rules
- Response format
- Error message wording
- Package structure
- Test approach

**Ask before implementing.** It's better to clarify than to redo work.
