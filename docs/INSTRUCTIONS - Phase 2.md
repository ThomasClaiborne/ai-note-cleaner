# AI Note Cleaner - Phase 2: AI Integration

## Prerequisites

- Phase 1 complete (POST /api/notes/clean working with placeholder logic)
- Ollama installed and running locally (`ollama serve`)
- Model available: `llama3.2`

## Goal

Replace placeholder cleaning logic with actual AI-powered note transformation using Spring AI + Ollama.

---

## Workflow Rules

**CRITICAL - READ BEFORE ANY WORK:**

1. **Do NOT commit automatically** - Stage changes only
2. After completing each file, output: `READY FOR REVIEW: [filename]`
3. **Wait for human approval** before proceeding to next file
4. Explain what each file does and why it's structured that way
5. If unsure about anything, **ask before implementing**

---

## What Exists (From Phase 1)

```
backend/src/main/java/com/ainote/backend/
├── BackendApplication.java
├── controller/
│   └── NoteController.java
├── dto/
│   ├── CleanRequest.java
│   ├── CleanResponse.java
│   └── ErrorResponse.java
├── exception/
│   └── GlobalExceptionHandler.java
└── service/
    └── NoteService.java (has placeholder formatPlaceholder() method)
```

---

## What We're Building

### 1. Dependencies (pom.xml)

Add Spring AI BOM to dependency management and Ollama starter:

```xml
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>org.springframework.ai</groupId>
            <artifactId>spring-ai-bom</artifactId>
            <version>1.0.0-M4</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>

<dependencies>
    <!-- existing dependencies... -->
    
    <dependency>
        <groupId>org.springframework.ai</groupId>
        <artifactId>spring-ai-ollama-spring-boot-starter</artifactId>
    </dependency>
</dependencies>
```

Also add Spring Milestones repository:

```xml
<repositories>
    <repository>
        <id>spring-milestones</id>
        <name>Spring Milestones</name>
        <url>https://repo.spring.io/milestone</url>
        <snapshots>
            <enabled>false</enabled>
        </snapshots>
    </repository>
</repositories>
```

### 2. Application Properties

Update `backend/src/main/resources/application.properties`:

```properties
spring.application.name=backend

# Ollama Configuration
spring.ai.ollama.base-url=http://localhost:11434
spring.ai.ollama.chat.options.model=llama3.2
spring.ai.ollama.chat.options.temperature=0.3
```

### 3. New Files to Create

#### 3a. AiCleaningService.java

**Location:** `backend/src/main/java/com/ainote/backend/service/AiCleaningService.java`

```java
// @Service annotation
// Inject OllamaChatModel from Spring AI
// 
// Method: String cleanContent(String content, String outputFormat)
//
// Build a prompt that instructs the AI to:
// 1. Fix spelling and grammar errors
// 2. Improve clarity and remove redundancy  
// 3. Structure content logically
// 4. Format according to outputFormat:
//    - "bullets": Use bullet points (• or -)
//    - "paragraphs": Clean prose paragraphs
//    - "numbered": Numbered list (1., 2., 3.)
// 5. Preserve the original meaning
// 6. Return ONLY the cleaned content, no explanations or preamble
//
// Use ChatClient or OllamaChatModel.call() to send prompt
// Extract and return the AI response text
//
// Wrap any AI communication errors in AiServiceException
```

#### 3b. AiServiceException.java

**Location:** `backend/src/main/java/com/ainote/backend/exception/AiServiceException.java`

```java
// Runtime exception for AI service failures
// Include message and cause
// Used to wrap connection errors, timeouts, etc.
```

### 4. Files to Modify

#### 4a. NoteService.java

- Add constructor injection for `AiCleaningService`
- Replace the `formatPlaceholder()` call with `aiCleaningService.cleanContent()`
- Remove or deprecate the `formatPlaceholder()` method

#### 4b. GlobalExceptionHandler.java

Add handler for `AiServiceException`:
- Check if cause is connection refused → return 503 Service Unavailable
- Check if cause is timeout → return 504 Gateway Timeout  
- Otherwise → return 500 Internal Server Error

Use appropriate error messages:
- 503: "AI service is unavailable. Please ensure Ollama is running."
- 504: "AI service request timed out. Please try again."
- 500: "An error occurred while processing your request."

### 5. Tests to Update/Create

#### 5a. AiCleaningServiceTest.java (Integration Test)

**Location:** `backend/src/test/java/com/ainote/backend/service/AiCleaningServiceTest.java`

```java
// @SpringBootTest - needs full context for AI beans
// 
// Tests (require Ollama running):
// - cleanContent_withBulletsFormat_returnsBulletPoints()
// - cleanContent_withParagraphsFormat_returnsParagraphs()
// - cleanContent_withNumberedFormat_returnsNumberedList()
// - cleanContent_withMessyInput_fixesSpellingAndGrammar()
//
// Use @DisabledIf or try-catch to handle when Ollama not running
// These are integration tests - they hit real AI
```

#### 5b. NoteServiceTest.java (Update)

- Add `@Mock` for `AiCleaningService`
- Update existing tests to mock AI responses
- Verify NoteService correctly delegates to AiCleaningService

#### 5c. NoteControllerTest.java (Update if needed)

- May need to mock or configure AiCleaningService
- Existing validation tests should still pass

---

## Prompt Template

Use this structure for the AI prompt:

```
You are a note cleaning assistant. Your task is to clean and improve the following notes.

Instructions:
1. Fix all spelling and grammar errors
2. Improve clarity and remove redundant content
3. Organize the content logically
4. Format the output as {FORMAT_INSTRUCTION}
5. Preserve the original meaning - do not add new information
6. Return ONLY the cleaned content with no explanations, preambles, or commentary

Format instruction for bullets: "bullet points using • or - symbols"
Format instruction for paragraphs: "clean, well-structured paragraphs"  
Format instruction for numbered: "a numbered list (1., 2., 3., etc.)"

Notes to clean:
{CONTENT}

Cleaned notes:
```

---

## File Creation Order

1. Update `pom.xml` with dependencies
2. Update `application.properties` with Ollama config
3. Create `AiServiceException.java`
4. Create `AiCleaningService.java`
5. Update `NoteService.java` to use AiCleaningService
6. Update `GlobalExceptionHandler.java` with AI error handling
7. Update `NoteServiceTest.java` to mock AI
8. Create `AiCleaningServiceTest.java` for integration tests
9. Update `README.md` with Ollama setup instructions
10. Update `DEV_LOG.md` with session notes

---

## Acceptance Criteria

Before this phase is complete, verify:

- [ ] POST /api/notes/clean returns AI-cleaned content (not placeholder)
- [ ] Bullets format returns actual bullet points from AI
- [ ] Numbered format returns numbered list from AI
- [ ] Paragraphs format returns clean prose from AI
- [ ] Returns 503 when Ollama is not running
- [ ] Returns appropriate error when AI times out
- [ ] All existing validation still works (empty content, invalid format, etc.)
- [ ] Unit tests pass with mocked AI
- [ ] Can test with curl and see real AI-cleaned output

**Test command:**
```bash
curl -X POST http://localhost:8080/api/notes/clean \
  -H "Content-Type: application/json" \
  -d '{"content": "hte quikc brown fox jumps ovr the lazy dog. very fast. much speed.", "outputFormat": "bullets"}'
```

Expected: AI returns cleaned content with proper bullet formatting, fixed spelling.

---

## Commit Guidelines

Use conventional commits:

1. `build: add Spring AI Ollama dependencies`
2. `feat: add AiCleaningService with Ollama integration`
3. `feat: integrate AI cleaning into NoteService`
4. `feat: add AI error handling to GlobalExceptionHandler`
5. `test: update NoteServiceTest with AI mocking`
6. `test: add AiCleaningService integration tests`
7. `docs: update README with Ollama setup instructions`
8. `docs: update DEV_LOG with Phase 2 session notes`

---

## Do NOT

- Add frontend changes (Phase 3)
- Add database persistence (Phase 4)
- Add streaming responses
- Add multiple AI provider support
- Change the API contract (CleanRequest/CleanResponse DTOs stay the same)
- Add authentication

---

## Questions?

If anything is unclear about:
- Spring AI configuration
- Prompt wording
- Error handling approach
- Test structure

**Ask before implementing.** It's better to clarify than to redo work.