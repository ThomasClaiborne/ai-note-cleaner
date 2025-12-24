# AI Note Cleaner - Phase 3: React Frontend

## Prerequisites

- Phase 1 & 2 complete (Backend API working with AI integration)
- Backend running at `http://localhost:8080`
- Ollama running with llama3.2 model
- Node.js 18+ installed

## Goal

Build a React frontend that allows users to input messy notes, select an output format, submit to the backend API, and view the AI-cleaned result.

---

## Workflow Rules

**CRITICAL - READ BEFORE ANY WORK:**

1. **Do NOT commit automatically** - Stage changes only
2. After completing each file, output: `READY FOR REVIEW: [filename]`
3. **Wait for human approval** before proceeding to next file
4. Explain what each file does and why it's structured that way
5. If unsure about anything, **ask before implementing**

---

## What Exists

### Backend API (From Phases 1-2)

```
POST /api/notes/clean
- Request: { content: string, outputFormat: "bullets"|"paragraphs"|"numbered" }
- Success (200): { original, cleaned, outputFormat, timestamp }
- Validation Error (400): { error, details: [{field, message}], timestamp }
- AI Unavailable (503): { error, details: null, timestamp }
```

### Frontend (Default Vite Template)

```
frontend/
├── src/
│   ├── App.tsx          # Default template - REPLACE
│   ├── App.css          # Default styles - DELETE
│   ├── main.tsx         # Entry point - KEEP
│   └── index.css        # Tailwind import - KEEP
├── package.json         # Dependencies configured
└── vite.config.ts       # Vite + Tailwind configured
```

---

## What We're Building This Phase

### Target Structure

```
frontend/src/
├── types/
│   └── index.ts               # TypeScript interfaces
├── services/
│   └── api.ts                 # API communication
├── components/
│   ├── LoadingSpinner.tsx     # Loading indicator
│   ├── ErrorMessage.tsx       # Error display
│   ├── FormatSelector.tsx     # Output format selection
│   ├── NoteInput.tsx          # Text input area
│   └── NoteOutput.tsx         # Result display + copy
└── App.tsx                    # Main orchestration
```

---

## Files to Create

### Order of Implementation

Build in this order (each depends on the previous):

```
1. Types (data structures)
   └── types/index.ts

2. API Service (communication)
   └── services/api.ts

3. Simple Components (no dependencies)
   └── components/LoadingSpinner.tsx
   └── components/ErrorMessage.tsx

4. Input Components
   └── components/FormatSelector.tsx
   └── components/NoteInput.tsx

5. Output Component
   └── components/NoteOutput.tsx

6. Main App
   └── App.tsx
   └── DELETE App.css
```

---

### File Specifications

#### 1. types/index.ts
**Location:** `frontend/src/types/index.ts`

```typescript
// Define interfaces matching backend DTOs:
// - CleanRequest: content (string), outputFormat (union type)
// - CleanResponse: original, cleaned, outputFormat, timestamp
// - FieldError: field, message
// - ErrorResponse: error, details (FieldError[] | null), timestamp
// - OutputFormat: type alias for the three format options
```

#### 2. services/api.ts
**Location:** `frontend/src/services/api.ts`

```typescript
// API service for backend communication
// Uses native fetch (no axios needed)

// Constants:
// - API_BASE_URL = 'http://localhost:8080/api'

// Custom Error Class:
// - ApiError extends Error
// - Include statusCode and details (ErrorResponse)

// Function: cleanNote(request: CleanRequest): Promise<CleanResponse>
// - POST to /notes/clean
// - Set Content-Type header
// - Handle non-ok responses by throwing ApiError
// - Return parsed CleanResponse on success
```

#### 3. components/LoadingSpinner.tsx
**Location:** `frontend/src/components/LoadingSpinner.tsx`

```typescript
// Simple loading spinner component
// No props needed (stateless)

// Requirements:
// - Use Tailwind animate-spin class
// - Include role="status" for accessibility
// - Add screen reader text (sr-only)
// - Center the spinner
```

#### 4. components/ErrorMessage.tsx
**Location:** `frontend/src/components/ErrorMessage.tsx`

```typescript
// Displays error messages to the user

// Props:
// - message: string (main error text)
// - details?: FieldError[] (optional field-level errors)
// - onDismiss?: () => void (optional close callback)

// Requirements:
// - Red/error themed styling
// - Show field errors as list if present
// - Optional dismiss button
```

#### 5. components/FormatSelector.tsx
**Location:** `frontend/src/components/FormatSelector.tsx`

```typescript
// Radio button group for output format selection

// Props:
// - value: OutputFormat (current selection)
// - onChange: (format: OutputFormat) => void
// - disabled?: boolean

// Requirements:
// - Three options: bullets, paragraphs, numbered
// - Visual indication of selected option
// - Keyboard accessible
// - Disable all options when disabled prop is true
```

#### 6. components/NoteInput.tsx
**Location:** `frontend/src/components/NoteInput.tsx`

```typescript
// Textarea for entering messy notes

// Props:
// - value: string
// - onChange: (content: string) => void
// - disabled?: boolean
// - maxLength?: number (default 10000, matches backend)

// Requirements:
// - Large textarea with placeholder
// - Character count display (current/max)
// - Visual warning when approaching limit
// - Controlled input pattern
```

#### 7. components/NoteOutput.tsx
**Location:** `frontend/src/components/NoteOutput.tsx`

```typescript
// Displays cleaned note output with copy functionality

// Props:
// - content: string (cleaned text)
// - format: string (for display label)

// Requirements:
// - Pre-formatted text (preserve newlines, bullets)
// - Copy to clipboard button
// - "Copied!" feedback (show for ~2 seconds)
// - Distinct visual style from input area
```

#### 8. App.tsx
**Location:** `frontend/src/App.tsx`

```typescript
// Main application component - REPLACE existing file

// State to manage (useState):
// - content: string
// - outputFormat: OutputFormat (default 'bullets')
// - result: CleanResponse | null
// - isLoading: boolean
// - error: { message: string, details?: FieldError[] } | null

// Layout (top to bottom):
// - Header with app title
// - NoteInput
// - FormatSelector
// - Submit button
// - LoadingSpinner (conditional)
// - ErrorMessage (conditional)
// - NoteOutput (conditional)

// Submit handler:
// 1. Set isLoading=true, error=null, result=null
// 2. Call cleanNote() from api.ts
// 3. On success: set result
// 4. On error: extract message/details from ApiError
// 5. Finally: set isLoading=false

// Button states:
// - Disabled when content is empty
// - Disabled when isLoading is true
// - Show different text when loading
```

#### 9. Delete App.css
**Location:** `frontend/src/App.css`

Delete this file entirely. Remove its import from App.tsx. All styling uses Tailwind classes.

---

## Styling Guidelines

Use Tailwind CSS utility classes directly in JSX:

- **Primary button:** `bg-blue-600 hover:bg-blue-700 text-white px-4 py-2 rounded-lg disabled:bg-gray-400`
- **Error styling:** `bg-red-50 border border-red-500 text-red-700 p-4 rounded-lg`
- **Input focus:** `focus:ring-2 focus:ring-blue-500 focus:border-blue-500`
- **Container:** `max-w-3xl mx-auto p-6`
- **Spacing:** `space-y-4` between sections

---

## Acceptance Criteria

Before this phase is complete, verify:

- [ ] Can enter text in NoteInput textarea
- [ ] Character count displays and updates correctly
- [ ] Can select between bullets, paragraphs, numbered formats
- [ ] Submit button disabled when content empty or loading
- [ ] Loading spinner appears during API call
- [ ] Cleaned result displays after successful API call
- [ ] Copy button works and shows "Copied!" feedback
- [ ] Validation errors display with field details
- [ ] Server errors (503, 500) display user-friendly messages
- [ ] No TypeScript errors (`npm run build`)
- [ ] No ESLint errors (`npm run lint`)

### Test Commands (PowerShell)

```powershell
# Terminal 1: Start backend
cd backend
./mvnw spring-boot:run

# Terminal 2: Start frontend
cd frontend
npm run dev

# Terminal 2: Build check
npm run build

# Terminal 2: Lint check
npm run lint
```

---

## CORS Configuration

If you get CORS errors, the backend NoteController needs this annotation:

```java
@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/notes")
public class NoteController {
```

Ask before implementing if needed.

---

## Commit Guidelines

Use conventional commits. Suggested sequence:

1. `feat: add TypeScript types for API contracts`
2. `feat: add API service with cleanNote function`
3. `feat: add LoadingSpinner and ErrorMessage components`
4. `feat: add FormatSelector component`
5. `feat: add NoteInput component with character count`
6. `feat: add NoteOutput component with copy functionality`
7. `feat: implement main App with full workflow`
8. `chore: remove unused App.css`
9. `fix: add CORS configuration to backend` (if needed)

---

## Do NOT

- Add React Router (single page app)
- Add state management library (useState is sufficient)
- Add form library (React Hook Form) - only 2 inputs
- Add UI component library (MUI, Chakra) - Tailwind sufficient
- Add testing framework - out of scope for Phase 3
- Add dark mode - stretch goal only
- Add note history UI - that's Phase 4
- Over-engineer - keep components simple

---

## Questions?

If anything is unclear about:
- Component props or behavior
- Styling decisions  
- Error handling approach
- State management

**Ask before implementing.** It's better to clarify than to redo work.

