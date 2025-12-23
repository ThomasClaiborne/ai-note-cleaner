# AI Note Cleaner - Architecture Decisions

This document records architectural decisions made during development and the reasoning behind them. Update this as you make significant choices.

---

## Decision Log

### ADR-001: Backend-First Development Approach

**Date:** 2024-12-23  
**Status:** Accepted

**Context:**  
Need to decide the order of building: Backend, AI integration, or Frontend first.

**Decision:**  
Build in order: Backend → AI → Frontend

**Reasoning:**
1. **Dependency direction** - Frontend depends on Backend API, not vice versa
2. **API contract stability** - Define the data contract before building consumers
3. **Testability** - Backend can be tested independently with curl/Postman
4. **Risk mitigation** - If backend architecture is wrong, everything built on top fails
5. **Learning value** - Understanding data flow from API to database first

**Consequences:**
- No visual demo until Phase 3
- Can test core logic before adding UI complexity
- Frontend development will be faster once API is stable

---

### ADR-002: Placeholder Service Before AI Integration

**Date:** 2024-12-23  
**Status:** Accepted

**Context:**  
Could integrate AI immediately or build with placeholder logic first.

**Decision:**  
Build NoteService with simple string manipulation first, replace with AI in Phase 2.

**Reasoning:**
1. **Separation of concerns** - Service interface stays the same, only implementation changes
2. **Testability** - Can write tests against predictable behavior
3. **Debugging** - If something breaks after AI integration, we know it's the AI part
4. **Development speed** - Don't need Ollama running to test basic flow
5. **Fallback option** - If AI fails, placeholder logic could serve as fallback

**Consequences:**
- Extra work replacing placeholder logic
- Tests may need adjustment when AI is added
- Clear separation between "plumbing" and "intelligence"

---

### ADR-003: Package Structure (Layered Architecture)

**Date:** 2024-12-23  
**Status:** Accepted

**Context:**  
Need to organize Java classes. Options: by layer, by feature, or flat.

**Decision:**  
Organize by layer:
```
com.ainote.backend/
├── controller/    # REST endpoints
├── service/       # Business logic
├── dto/           # Data transfer objects
├── exception/     # Error handling
├── model/         # JPA entities (Phase 4)
└── repository/    # Data access (Phase 4)
```

**Reasoning:**
1. **Standard convention** - Most Spring Boot tutorials use this structure
2. **Clear responsibilities** - Each package has one job
3. **Scalability** - Easy to find files as project grows
4. **Dev10 alignment** - Matches what you're learning in bootcamp

**Alternatives Considered:**
- **By feature** (`notes/`, `users/`) - Better for large apps, overkill here
- **Flat structure** - Gets messy quickly

**Consequences:**
- Need to create multiple packages
- Import statements reference specific packages
- Easy onboarding for anyone familiar with Spring conventions

---

### ADR-004: DTO Validation Strategy

**Date:** 2024-12-23  
**Status:** Accepted

**Context:**  
Need to validate incoming request data. Options: controller-level, service-level, or DTO annotations.

**Decision:**  
Use Bean Validation annotations (`@NotBlank`, `@Size`, `@Pattern`) on DTOs with `@Valid` in controller.

**Reasoning:**
1. **Declarative** - Validation rules live with the data structure
2. **Spring integration** - `@Valid` automatically triggers validation
3. **Error handling** - `MethodArgumentNotValidException` provides detailed errors
4. **DRY** - Don't repeat validation logic in multiple places

**Consequences:**
- Need `spring-boot-starter-validation` dependency (already included)
- Validation errors need custom exception handler for clean response
- Complex validations may need custom validators later

---

### ADR-005: Output Format as String Enum Pattern

**Date:** 2024-12-23  
**Status:** Accepted

**Context:**  
Need to validate that `outputFormat` is one of: bullets, paragraphs, numbered.

**Decision:**  
Use `@Pattern` annotation with regex, not Java enum.

```java
@Pattern(regexp = "^(bullets|paragraphs|numbered)$", 
         message = "outputFormat must be: bullets, paragraphs, or numbered")
private String outputFormat;
```

**Reasoning:**
1. **JSON simplicity** - String maps directly, no enum serialization config
2. **Error messages** - Pattern gives clear feedback on valid options
3. **Extensibility** - Easy to add new formats by updating regex
4. **Frontend friendly** - No need to know Java enum names

**Alternatives Considered:**
- **Java Enum** - Type-safe but requires Jackson configuration
- **Custom Validator** - More flexible but more code

**Consequences:**
- No compile-time safety on format values
- Need to handle string comparison in service layer
- Consistent with REST API conventions

---

### ADR-006: CORS Configuration for Local Development

**Date:** 2024-12-23  
**Status:** Accepted

**Context:**  
Frontend (port 5173) needs to call Backend (port 8080) during development.

**Decision:**  
Add `@CrossOrigin(origins = "http://localhost:5173")` to controller.

**Reasoning:**
1. **Simple** - One annotation, immediate effect
2. **Scoped** - Only allows frontend origin, not wide open
3. **Dev-focused** - Will be replaced with proper config for production

**Consequences:**
- Need to update for production deployment
- Only applies to annotated controllers
- Consider global CORS config in Phase 3

---

## Future Decisions (To Be Made)

### Pending: AI Provider Abstraction

**Question:** Should we create an interface for AI providers to allow swapping Ollama/OpenAI?

**Options:**
1. Direct Ollama integration (simpler)
2. Interface + Strategy pattern (more flexible)

**Decide in:** Phase 2

---

### Pending: Database Choice for Production

**Question:** Stick with H2 or migrate to MySQL/PostgreSQL?

**Options:**
1. H2 for simplicity (works for portfolio demo)
2. PostgreSQL for production-readiness

**Decide in:** Phase 4

---

### Pending: State Management in Frontend

**Question:** useState vs useReducer vs external library?

**Options:**
1. useState (simple, sufficient for small state)
2. useReducer (if state logic gets complex)
3. Zustand/Jotai (if we need global state)

**Decide in:** Phase 3

---

## Architecture Diagram

```
┌─────────────────────────────────────────────────────────────────┐
│                         FRONTEND                                │
│                    React + TypeScript                           │
│                      (Port 5173)                                │
└─────────────────────────────┬───────────────────────────────────┘
                              │ HTTP/JSON
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│                         BACKEND                                 │
│                      Spring Boot                                │
│                      (Port 8080)                                │
│  ┌─────────────┐    ┌─────────────┐    ┌─────────────────────┐ │
│  │ Controller  │───▶│   Service   │───▶│  AI Service         │ │
│  │ (REST API)  │    │  (Logic)    │    │  (Ollama/Spring AI) │ │
│  └─────────────┘    └─────────────┘    └─────────────────────┘ │
│         │                  │                                    │
│         ▼                  ▼                                    │
│  ┌─────────────┐    ┌─────────────┐                            │
│  │    DTOs     │    │ Repository  │ (Phase 4)                  │
│  │ (Validation)│    │ (Data)      │                            │
│  └─────────────┘    └─────────────┘                            │
└─────────────────────────────────────────────────────────────────┘
                              │
                              ▼ (Phase 4)
                    ┌─────────────────┐
                    │    Database     │
                    │   (H2 / MySQL)  │
                    └─────────────────┘
```

---

## Tech Stack Rationale

| Choice | Why |
|--------|-----|
| **Spring Boot 3.4** | Industry standard, excellent docs, Dev10 aligned |
| **Java 21** | LTS version, modern features, good for job market |
| **Maven** | More common than Gradle, better IDE support |
| **React 19** | Industry standard, strong job market demand |
| **TypeScript** | Type safety, better IDE support, industry trend |
| **Vite** | Faster than CRA, modern standard |
| **Tailwind CSS** | Rapid styling, no context switching |
| **Ollama** | Free local LLMs, no API keys needed |
| **Spring AI** | Official Spring integration, swappable providers |
| **H2** | Zero config for development, in-memory simplicity |

---

*Last Updated: 2024-12-23*
