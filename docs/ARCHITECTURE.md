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

### ADR-007: Separate AI Service Layer

**Date:** 2024-12-24  
**Status:** Accepted

**Context:**  
Need to integrate AI cleaning via Ollama. Could put AI logic directly in NoteService or create a separate service.

**Decision:**  
Create a separate `AiCleaningService` that `NoteService` calls.

**Reasoning:**
1. **Single Responsibility** - NoteService orchestrates the flow, AiCleaningService handles AI communication
2. **Testability** - Can mock AiCleaningService for fast unit tests of NoteService
3. **Swappability** - Could swap Ollama for OpenAI later without touching NoteService
4. **Error Isolation** - AI-specific errors handled in one place

**Consequences:**
- Extra class to maintain
- Clear separation of concerns
- Easier to add fallback providers later

---

### ADR-008: AI Error Handling Strategy

**Date:** 2024-12-24  
**Status:** Accepted

**Context:**  
Need to decide what happens when AI service fails (Ollama down, timeout, bad response).

**Decision:**  
Return HTTP error codes, do NOT fall back to original content silently.

**Reasoning:**
1. **Explicit failure** - User knows something went wrong
2. **No false success** - Don't pretend cleaning happened when it didn't
3. **Debuggability** - Clear error messages help troubleshooting
4. **User choice** - User can retry or use content as-is

**Error Mapping:**
- Ollama not running → 503 Service Unavailable
- Request timeout → 504 Gateway Timeout
- Other AI errors → 500 Internal Server Error

**Consequences:**
- User must handle errors
- No degraded experience (either works or fails)
- Clear contract with frontend

---

### ADR-009: Prompt Engineering Strategy

**Date:** 2024-12-24  
**Status:** Accepted

**Context:**  
Need to instruct the AI on how to clean notes and format output.

**Decision:**  
Use a structured system prompt with clear instructions and format-specific guidance.

**Prompt Structure:**
1. Role definition (you are a note cleaning assistant)
2. Task definition (clean, structure, format)
3. Constraints (preserve meaning, no explanations)
4. Format-specific instructions (bullets/paragraphs/numbered)
5. Output requirement (return ONLY the cleaned content)

**Reasoning:**
1. **Consistency** - Same cleaning quality across requests
2. **Control** - Explicit instructions reduce AI hallucination
3. **Format compliance** - Clear format rules ensure usable output

**Consequences:**
- Prompt may need tuning based on results
- Model-specific adjustments might be needed
- Easy to iterate on prompt without code changes

---

### ADR-010: React State Management with useState

**Date:** 2024-12-24  
**Status:** Accepted

**Context:**  
Need to manage application state in React frontend. Options: useState, useReducer, or external library (Zustand, Redux).

**Decision:**  
Use React's built-in `useState` hooks for all state management.

**Reasoning:**
1. **Simple state shape** - Only 5 pieces of state: content, outputFormat, result, isLoading, error
2. **No cross-component sharing** - All state lives in App.tsx, passed down as props
3. **No async complexity** - Single API call, no caching needed
4. **Dev10 alignment** - Teaches React hooks, not external libraries
5. **YAGNI** - Don't add complexity until we need it

**State Shape:**
```typescript
const [content, setContent] = useState('');
const [outputFormat, setOutputFormat] = useState<OutputFormat>('bullets');
const [result, setResult] = useState<CleanResponse | null>(null);
const [isLoading, setIsLoading] = useState(false);
const [error, setError] = useState<ErrorState | null>(null);
```

**Consequences:**
- All state in one component (App.tsx)
- Props drilling for 1 level (acceptable for small app)
- Easy to understand and debug
- Could refactor to useReducer if state logic gets complex

---

### ADR-011: Flat Component Structure

**Date:** 2024-12-24  
**Status:** Accepted

**Context:**  
Need to organize React components. Options: flat `components/` folder, feature folders, or atomic design.

**Decision:**  
Use a flat `components/` folder with all components at the same level.

**Reasoning:**
1. **Small number of components** - Only 5 components, no nesting needed
2. **No shared sub-components** - Each component is self-contained
3. **Easy to find files** - All in one place
4. **Matches project scale** - Feature folders overkill for MVP

**Structure:**
```
src/
├── components/
│   ├── NoteInput.tsx
│   ├── FormatSelector.tsx
│   ├── NoteOutput.tsx
│   ├── LoadingSpinner.tsx
│   └── ErrorMessage.tsx
├── services/
│   └── api.ts
├── types/
│   └── index.ts
└── App.tsx
```

**Consequences:**
- Simple mental model
- May need reorganization if app grows significantly
- No component hierarchy complexity

---

### ADR-012: Plain Fetch for API Communication

**Date:** 2024-12-24  
**Status:** Accepted

**Context:**  
Need to make HTTP requests to backend API. Options: native fetch, axios, TanStack Query (React Query).

**Decision:**  
Use native `fetch` API with a simple wrapper function in `services/api.ts`.

**Reasoning:**
1. **Dev10 teaches fetch** - Matches curriculum, reinforces learning
2. **No caching needed** - Each request is independent, no query invalidation
3. **One API endpoint** - POST /api/notes/clean, no complex data fetching
4. **Browser native** - No additional dependency
5. **TypeScript friendly** - Easy to type response handling

**Implementation:**
```typescript
export async function cleanNote(request: CleanRequest): Promise<CleanResponse> {
  const response = await fetch(`${API_BASE_URL}/notes/clean`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(request),
  });
  
  if (!response.ok) {
    const error = await response.json();
    throw new ApiError(error.message, response.status, error);
  }
  
  return response.json();
}
```

**Alternatives Considered:**
- **axios** - More features than needed, extra dependency
- **TanStack Query** - Excellent for complex apps, overkill here

**Consequences:**
- Manual error handling required
- No automatic retries (acceptable for MVP)
- Easy to swap to axios/TanStack Query later if needed

---

### ADR-013: Tailwind CSS Utility Classes Only

**Date:** 2024-12-24  
**Status:** Accepted

**Context:**  
Tailwind CSS is configured. Need to decide how to apply styles: utility classes inline, @apply in CSS, or mixed approach.

**Decision:**  
Use Tailwind utility classes directly in JSX. No custom CSS classes, no @apply directives.

**Reasoning:**
1. **Rapid development** - No context switching to CSS files
2. **Co-location** - Styles live with the component using them
3. **Consistency** - Tailwind's design system ensures visual consistency
4. **No dead CSS** - Only used classes are included in build
5. **Easy to modify** - Change styles without hunting through CSS files

**Example:**
```tsx
<button 
  className="px-4 py-2 bg-blue-600 hover:bg-blue-700 text-white rounded-lg 
             disabled:bg-gray-400 disabled:cursor-not-allowed transition-colors"
>
  Clean Notes
</button>
```

**Consequences:**
- Longer className strings (acceptable tradeoff)
- Some repetition across components (extract to components, not CSS)
- Delete App.css (not needed)

---

### ADR-014: Controlled Form Inputs

**Date:** 2024-12-24  
**Status:** Accepted

**Context:**  
Need to handle form inputs (textarea, radio buttons). Options: controlled components (React state), uncontrolled (refs), or form library.

**Decision:**  
Use controlled components with React state for all inputs.

**Reasoning:**
1. **React idiom** - Standard React pattern, taught in Dev10
2. **Simple form** - Only 2 inputs (textarea, format selector)
3. **Immediate validation** - Can validate as user types
4. **No library needed** - React Hook Form overkill for 2 fields
5. **State visibility** - Always know current form values

**Pattern:**
```tsx
// In App.tsx
const [content, setContent] = useState('');

// Passed to component
<NoteInput 
  value={content} 
  onChange={(value) => setContent(value)} 
/>
```

**Consequences:**
- Re-renders on every keystroke (acceptable for small form)
- Clear data flow
- Easy to reset form after submission

---

### ADR-015: Error Handling Strategy (Frontend)

**Date:** 2024-12-24  
**Status:** Accepted

**Context:**  
Need to handle and display errors from API. Types: validation errors (400), server errors (500), AI unavailable (503).

**Decision:**  
Create custom `ApiError` class and display errors inline with `ErrorMessage` component.

**Reasoning:**
1. **User-friendly messages** - Transform technical errors to readable messages
2. **Field-level display** - Show validation errors next to relevant fields (or grouped)
3. **Dismissable** - User can close error and retry
4. **Type safety** - ApiError class carries status code and details

**Error Flow:**
```
API Error → ApiError thrown → Caught in App.tsx → Set error state → ErrorMessage renders
```

**Error Display Rules:**
- 400 (Validation): Show field-level messages from `details` array
- 503 (AI Unavailable): "AI service is unavailable. Please ensure Ollama is running."
- 504 (Timeout): "Request timed out. Please try again."
- 500 (Server Error): "Something went wrong. Please try again."

**Consequences:**
- Consistent error display across app
- User knows what went wrong and can retry
- No silent failures

---

### ADR-016: Component Props Interface Pattern

**Date:** 2024-12-24  
**Status:** Accepted

**Context:**  
Need a consistent pattern for defining component props in TypeScript.

**Decision:**  
Define props as interfaces named `[ComponentName]Props`, exported from the component file.

**Reasoning:**
1. **Consistency** - Same pattern for all components
2. **Co-location** - Props defined where used
3. **Self-documenting** - Clear contract for component usage
4. **IDE support** - TypeScript autocomplete for props

**Pattern:**
```tsx
// NoteInput.tsx
interface NoteInputProps {
  value: string;
  onChange: (value: string) => void;
  disabled?: boolean;
  placeholder?: string;
  maxLength?: number;
}

export function NoteInput({ value, onChange, disabled = false, ...rest }: NoteInputProps) {
  // ...
}
```

**Consequences:**
- Verbose but clear
- Good TypeScript integration
- Easy to see required vs optional props

---

## Future Decisions (To Be Made)

### Pending: Database Choice for Production

**Question:** Stick with H2 or migrate to MySQL/PostgreSQL?

**Options:**
1. H2 for simplicity (works for portfolio demo)
2. PostgreSQL for production-readiness

**Decide in:** Phase 4

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

*Last Updated: 2024-12-24*