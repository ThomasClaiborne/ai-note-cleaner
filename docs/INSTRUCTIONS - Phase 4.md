# AI Note Cleaner - Phase 4: Database Persistence

## Prerequisites

- Phase 3 complete (React frontend working with backend API)
- Backend and frontend both running and tested
- H2 dependency already in pom.xml (from initial setup)

## Goal

Add persistent storage for note transformations, allowing users to view their history of cleaned notes.

---

## Design Decisions

| Decision | Choice | Reasoning |
|----------|--------|-----------|
| Database (dev) | H2 (in-memory) | Zero setup, already configured |
| Database (prod) | MySQL | Production-ready, can configure later |
| History limit | 50 items | Reasonable scope, prevents bloat |
| Sort order | Descending (newest first) | Standard UX pattern |
| Display format | Date + Format + Preview | Scannable with context |
| Preview length | 50 characters | Fits on one line |
| Click behavior | Expand inline (accordion) | Simple, no navigation |
| Delete capability | No (MVP) | Can add later |

---

## What We're Building

### Database Schema

```sql
CREATE TABLE note_history (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    original_content TEXT NOT NULL,
    cleaned_content TEXT NOT NULL,
    output_format VARCHAR(20) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | /api/notes/clean | (existing) Now also saves to database |
| GET | /api/notes/history | Returns last 50 transformations |

**GET /api/notes/history Response:**
```json
[
  {
    "id": 42,
    "originalContent": "messy notes here...",
    "cleanedContent": "• Clean point one\n• Clean point two",
    "outputFormat": "bullets",
    "createdAt": "2024-12-27T14:30:00"
  },
  {
    "id": 41,
    "originalContent": "another note...",
    "cleanedContent": "Cleaned paragraph text.",
    "outputFormat": "paragraphs",
    "createdAt": "2024-12-27T11:15:00"
  }
]
```

---

## File Creation Order

Build in this order (each depends on the previous):

```
Backend:
1. model/NoteHistory.java           # JPA Entity
2. repository/NoteHistoryRepository.java  # Data access
3. dto/NoteHistoryResponse.java     # Response DTO
4. service/NoteService.java         # UPDATE - save after cleaning
5. controller/NoteController.java   # UPDATE - add GET endpoint
6. application.properties           # UPDATE - H2 console config

Frontend:
7. types/index.ts                   # UPDATE - add history types
8. services/api.ts                  # UPDATE - add getHistory()
9. components/HistoryItem.tsx       # NEW - single history item
10. components/NoteHistory.tsx      # NEW - history list
11. App.tsx                         # UPDATE - add history section
```

---

## Backend Files

### 1. NoteHistory.java (NEW)

**Location:** `backend/src/main/java/com/ainote/backend/model/NoteHistory.java`

```java
package com.ainote.backend.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entity representing a saved note transformation.
 */
@Entity
@Table(name = "note_history")
public class NoteHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "original_content", nullable = false, columnDefinition = "TEXT")
    private String originalContent;

    @Column(name = "cleaned_content", nullable = false, columnDefinition = "TEXT")
    private String cleanedContent;

    @Column(name = "output_format", nullable = false, length = 20)
    private String outputFormat;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    // Default constructor required by JPA
    public NoteHistory() {
    }

    // Constructor for creating new entries
    public NoteHistory(String originalContent, String cleanedContent, String outputFormat) {
        this.originalContent = originalContent;
        this.cleanedContent = cleanedContent;
        this.outputFormat = outputFormat;
        this.createdAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOriginalContent() {
        return originalContent;
    }

    public void setOriginalContent(String originalContent) {
        this.originalContent = originalContent;
    }

    public String getCleanedContent() {
        return cleanedContent;
    }

    public void setCleanedContent(String cleanedContent) {
        this.cleanedContent = cleanedContent;
    }

    public String getOutputFormat() {
        return outputFormat;
    }

    public void setOutputFormat(String outputFormat) {
        this.outputFormat = outputFormat;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
```

**What it does:**
- JPA entity mapped to `note_history` table
- `@Id` + `@GeneratedValue` = auto-incrementing primary key
- `TEXT` column type for content fields (supports long notes)
- `createdAt` set automatically in constructor

---

### 2. NoteHistoryRepository.java (NEW)

**Location:** `backend/src/main/java/com/ainote/backend/repository/NoteHistoryRepository.java`

```java
package com.ainote.backend.repository;

import com.ainote.backend.model.NoteHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for NoteHistory entity.
 * Spring Data JPA generates implementation automatically.
 */
@Repository
public interface NoteHistoryRepository extends JpaRepository<NoteHistory, Long> {

    /**
     * Find the 50 most recent note transformations.
     * Method name follows Spring Data JPA naming convention.
     */
    List<NoteHistory> findTop50ByOrderByCreatedAtDesc();
}
```

**What it does:**
- Extends `JpaRepository` = get save(), findAll(), findById(), delete() for free
- Custom method `findTop50ByOrderByCreatedAtDesc()` = Spring Data parses the name:
  - `findTop50` = LIMIT 50
  - `ByOrderByCreatedAtDesc` = ORDER BY created_at DESC

---

### 3. NoteHistoryResponse.java (NEW)

**Location:** `backend/src/main/java/com/ainote/backend/dto/NoteHistoryResponse.java`

```java
package com.ainote.backend.dto;

import com.ainote.backend.model.NoteHistory;
import java.time.LocalDateTime;

/**
 * Response DTO for note history items.
 * Separates API contract from JPA entity.
 */
public class NoteHistoryResponse {

    private Long id;
    private String originalContent;
    private String cleanedContent;
    private String outputFormat;
    private LocalDateTime createdAt;

    public NoteHistoryResponse() {
    }

    /**
     * Factory method to convert entity to DTO.
     */
    public static NoteHistoryResponse fromEntity(NoteHistory entity) {
        NoteHistoryResponse dto = new NoteHistoryResponse();
        dto.setId(entity.getId());
        dto.setOriginalContent(entity.getOriginalContent());
        dto.setCleanedContent(entity.getCleanedContent());
        dto.setOutputFormat(entity.getOutputFormat());
        dto.setCreatedAt(entity.getCreatedAt());
        return dto;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOriginalContent() {
        return originalContent;
    }

    public void setOriginalContent(String originalContent) {
        this.originalContent = originalContent;
    }

    public String getCleanedContent() {
        return cleanedContent;
    }

    public void setCleanedContent(String cleanedContent) {
        this.cleanedContent = cleanedContent;
    }

    public String getOutputFormat() {
        return outputFormat;
    }

    public void setOutputFormat(String outputFormat) {
        this.outputFormat = outputFormat;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
```

**What it does:**
- DTO pattern separates API response from database entity
- `fromEntity()` factory method handles conversion
- If entity changes, API can stay stable

---

### 4. NoteService.java (UPDATE)

**Location:** `backend/src/main/java/com/ainote/backend/service/NoteService.java`

**Changes:**
- Inject `NoteHistoryRepository`
- Save to database after successful AI cleaning
- Add method to retrieve history

```java
package com.ainote.backend.service;

import com.ainote.backend.dto.CleanRequest;
import com.ainote.backend.dto.CleanResponse;
import com.ainote.backend.dto.NoteHistoryResponse;
import com.ainote.backend.model.NoteHistory;
import com.ainote.backend.repository.NoteHistoryRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for processing and cleaning notes.
 * Delegates to AiCleaningService and persists results.
 */
@Service
public class NoteService {

    private final AiCleaningService aiCleaningService;
    private final NoteHistoryRepository noteHistoryRepository;

    public NoteService(AiCleaningService aiCleaningService, 
                       NoteHistoryRepository noteHistoryRepository) {
        this.aiCleaningService = aiCleaningService;
        this.noteHistoryRepository = noteHistoryRepository;
    }

    /**
     * Cleans the provided note content, saves to history, and returns result.
     */
    public CleanResponse cleanNote(CleanRequest request) {
        String original = request.getContent();
        String outputFormat = request.getOutputFormat();

        // Get AI-cleaned content
        String cleaned = aiCleaningService.cleanContent(original, outputFormat);

        // Save to database
        NoteHistory history = new NoteHistory(original, cleaned, outputFormat);
        noteHistoryRepository.save(history);

        return new CleanResponse(original, cleaned, outputFormat, LocalDateTime.now());
    }

    /**
     * Retrieves the 50 most recent note transformations.
     */
    public List<NoteHistoryResponse> getHistory() {
        return noteHistoryRepository.findTop50ByOrderByCreatedAtDesc()
                .stream()
                .map(NoteHistoryResponse::fromEntity)
                .collect(Collectors.toList());
    }
}
```

**What changed:**
- Added `NoteHistoryRepository` injection
- Added `noteHistoryRepository.save()` after AI cleaning
- Added `getHistory()` method with entity-to-DTO conversion

---

### 5. NoteController.java (UPDATE)

**Location:** `backend/src/main/java/com/ainote/backend/controller/NoteController.java`

**Changes:**
- Add GET /api/notes/history endpoint

```java
package com.ainote.backend.controller;

import com.ainote.backend.dto.CleanRequest;
import com.ainote.backend.dto.CleanResponse;
import com.ainote.backend.dto.NoteHistoryResponse;
import com.ainote.backend.service.NoteService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for note cleaning operations.
 */
@RestController
@RequestMapping("/api/notes")
@CrossOrigin(origins = "http://localhost:5173")
public class NoteController {

    private final NoteService noteService;

    public NoteController(NoteService noteService) {
        this.noteService = noteService;
    }

    /**
     * Cleans and formats the provided note content.
     */
    @PostMapping("/clean")
    public ResponseEntity<CleanResponse> cleanNote(@Valid @RequestBody CleanRequest request) {
        CleanResponse response = noteService.cleanNote(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Retrieves the history of note transformations.
     */
    @GetMapping("/history")
    public ResponseEntity<List<NoteHistoryResponse>> getHistory() {
        List<NoteHistoryResponse> history = noteService.getHistory();
        return ResponseEntity.ok(history);
    }
}
```

**What changed:**
- Added `@CrossOrigin` at class level (applies to all endpoints)
- Added `getHistory()` endpoint mapped to GET /api/notes/history

---

### 6. application.properties (UPDATE)

**Location:** `backend/src/main/resources/application.properties`

**Add H2 console and JPA settings:**

```properties
spring.application.name=backend

# Ollama Configuration
spring.ai.ollama.base-url=http://localhost:11434
spring.ai.ollama.chat.options.model=llama3.2
spring.ai.ollama.chat.options.temperature=0.3

# H2 Database Configuration
spring.datasource.url=jdbc:h2:mem:notedb
spring.datasource.driver-class-name=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=

# H2 Console (for debugging)
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console

# JPA Configuration
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
spring.jpa.hibernate.ddl-auto=create-drop
spring.jpa.show-sql=true
```

**What this does:**
- `jdbc:h2:mem:notedb` = in-memory database named "notedb"
- `spring.h2.console.enabled=true` = web console at /h2-console
- `ddl-auto=create-drop` = auto-create tables from entities, drop on shutdown
- `show-sql=true` = log SQL statements (helpful for debugging)

**Access H2 Console:** http://localhost:8080/h2-console
- JDBC URL: `jdbc:h2:mem:notedb`
- Username: `sa`
- Password: (leave blank)

---

## Frontend Files

### 7. types/index.ts (UPDATE)

**Location:** `frontend/src/types/index.ts`

**Add history types:**

```typescript
// Output format options
export type OutputFormat = 'bullets' | 'paragraphs' | 'numbered';

// Request to clean notes
export interface CleanRequest {
  content: string;
  outputFormat: OutputFormat;
}

// Response from clean endpoint
export interface CleanResponse {
  original: string;
  cleaned: string;
  outputFormat: string;
  timestamp: string;
}

// Field-level validation error
export interface FieldError {
  field: string;
  message: string;
}

// Error response from API
export interface ErrorResponse {
  error: string;
  details: FieldError[] | null;
  timestamp: string;
}

// History item from database
export interface NoteHistoryItem {
  id: number;
  originalContent: string;
  cleanedContent: string;
  outputFormat: string;
  createdAt: string;
}
```

**What changed:**
- Added `NoteHistoryItem` interface matching backend DTO

---

### 8. services/api.ts (UPDATE)

**Location:** `frontend/src/services/api.ts`

**Add getHistory function:**

```typescript
import type { CleanRequest, CleanResponse, ErrorResponse, NoteHistoryItem } from '../types';

const API_BASE_URL = 'http://localhost:8080/api/notes';

/**
 * Custom error class that carries API error details.
 */
export class ApiError extends Error {
  public status: number;
  public errorResponse: ErrorResponse | null;

  constructor(message: string, status: number, errorResponse: ErrorResponse | null = null) {
    super(message);
    this.name = 'ApiError';
    this.status = status;
    this.errorResponse = errorResponse;
  }
}

/**
 * Send notes to the API for AI cleaning.
 */
export async function cleanNote(request: CleanRequest): Promise<CleanResponse> {
  const response = await fetch(`${API_BASE_URL}/clean`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(request),
  });

  if (!response.ok) {
    let errorResponse: ErrorResponse | null = null;
    try {
      errorResponse = await response.json();
    } catch {
      // Response wasn't JSON
    }

    const message = errorResponse?.error || `Request failed with status ${response.status}`;
    throw new ApiError(message, response.status, errorResponse);
  }

  return response.json();
}

/**
 * Fetch the history of note transformations.
 */
export async function getHistory(): Promise<NoteHistoryItem[]> {
  const response = await fetch(`${API_BASE_URL}/history`, {
    method: 'GET',
    headers: {
      'Accept': 'application/json',
    },
  });

  if (!response.ok) {
    throw new ApiError(
      `Failed to fetch history with status ${response.status}`,
      response.status,
      null
    );
  }

  return response.json();
}
```

**What changed:**
- Added `getHistory()` function
- Imported `NoteHistoryItem` type

---

### 9. HistoryItem.tsx (NEW)

**Location:** `frontend/src/components/HistoryItem.tsx`

```tsx
import { useState } from 'react';
import type { NoteHistoryItem } from '../types';

interface HistoryItemProps {
  item: NoteHistoryItem;
}

/**
 * Single history item with expand/collapse functionality.
 */
export default function HistoryItem({ item }: HistoryItemProps) {
  const [isExpanded, setIsExpanded] = useState(false);
  const [copied, setCopied] = useState(false);

  // Format the date for display
  const formatDate = (dateString: string): string => {
    const date = new Date(dateString);
    return date.toLocaleDateString('en-US', {
      month: 'short',
      day: 'numeric',
      hour: 'numeric',
      minute: '2-digit',
      hour12: true,
    });
  };

  // Truncate text for preview
  const truncate = (text: string, maxLength: number = 50): string => {
    if (text.length <= maxLength) return text;
    return text.substring(0, maxLength).trim() + '...';
  };

  // Format label for output format
  const formatLabel = (format: string): string => {
    return format.charAt(0).toUpperCase() + format.slice(1);
  };

  // Copy cleaned content to clipboard
  const handleCopy = async () => {
    try {
      await navigator.clipboard.writeText(item.cleanedContent);
      setCopied(true);
      setTimeout(() => setCopied(false), 2000);
    } catch (err) {
      console.error('Failed to copy:', err);
    }
  };

  return (
    <div className="border border-gray-200 rounded-lg overflow-hidden">
      {/* Header - always visible */}
      <button
        onClick={() => setIsExpanded(!isExpanded)}
        className="w-full px-4 py-3 text-left bg-gray-50 hover:bg-gray-100 transition-colors"
      >
        <div className="flex items-center justify-between">
          <div className="flex items-center gap-2">
            <span className="text-gray-400">
              {isExpanded ? '▼' : '▶'}
            </span>
            <span className="text-sm text-gray-600">
              {formatDate(item.createdAt)}
            </span>
            <span className="text-xs px-2 py-0.5 bg-blue-100 text-blue-700 rounded">
              {formatLabel(item.outputFormat)}
            </span>
          </div>
        </div>
        <p className="mt-1 text-sm text-gray-500 truncate pl-5">
          "{truncate(item.originalContent)}"
        </p>
      </button>

      {/* Expanded content */}
      {isExpanded && (
        <div className="px-4 py-3 border-t border-gray-200 bg-white">
          {/* Original */}
          <div className="mb-3">
            <h4 className="text-xs font-semibold text-gray-500 uppercase mb-1">
              Original
            </h4>
            <p className="text-sm text-gray-700 whitespace-pre-wrap bg-gray-50 p-2 rounded">
              {item.originalContent}
            </p>
          </div>

          {/* Cleaned */}
          <div className="mb-3">
            <h4 className="text-xs font-semibold text-gray-500 uppercase mb-1">
              Cleaned
            </h4>
            <p className="text-sm text-gray-700 whitespace-pre-wrap bg-green-50 p-2 rounded">
              {item.cleanedContent}
            </p>
          </div>

          {/* Copy button */}
          <button
            onClick={handleCopy}
            className="text-sm px-3 py-1 bg-blue-600 text-white rounded hover:bg-blue-700 transition-colors"
          >
            {copied ? '✓ Copied!' : 'Copy Cleaned'}
          </button>
        </div>
      )}
    </div>
  );
}
```

**What it does:**
- Displays single history item with date, format badge, and preview
- Click to expand/collapse
- Shows full original and cleaned content when expanded
- Copy button with 2-second feedback

---

### 10. NoteHistory.tsx (NEW)

**Location:** `frontend/src/components/NoteHistory.tsx`

```tsx
import { useState, useEffect } from 'react';
import { getHistory, ApiError } from '../services/api';
import type { NoteHistoryItem } from '../types';
import HistoryItem from './HistoryItem';
import LoadingSpinner from './LoadingSpinner';

interface NoteHistoryProps {
  refreshTrigger: number; // Increment to trigger refresh
}

/**
 * Displays the list of past note transformations.
 */
export default function NoteHistory({ refreshTrigger }: NoteHistoryProps) {
  const [history, setHistory] = useState<NoteHistoryItem[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const fetchHistory = async () => {
      setIsLoading(true);
      setError(null);

      try {
        const data = await getHistory();
        setHistory(data);
      } catch (err) {
        if (err instanceof ApiError) {
          setError(err.message);
        } else {
          setError('Failed to load history');
        }
      } finally {
        setIsLoading(false);
      }
    };

    fetchHistory();
  }, [refreshTrigger]);

  if (isLoading) {
    return (
      <div className="mt-8">
        <h2 className="text-lg font-semibold text-gray-700 mb-4">📝 Note History</h2>
        <div className="flex justify-center py-8">
          <LoadingSpinner />
        </div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="mt-8">
        <h2 className="text-lg font-semibold text-gray-700 mb-4">📝 Note History</h2>
        <p className="text-red-600 text-sm">{error}</p>
      </div>
    );
  }

  if (history.length === 0) {
    return (
      <div className="mt-8">
        <h2 className="text-lg font-semibold text-gray-700 mb-4">📝 Note History</h2>
        <p className="text-gray-500 text-sm">No notes cleaned yet. Try cleaning some notes above!</p>
      </div>
    );
  }

  return (
    <div className="mt-8">
      <h2 className="text-lg font-semibold text-gray-700 mb-4">
        📝 Note History ({history.length})
      </h2>
      <div className="space-y-2">
        {history.map((item) => (
          <HistoryItem key={item.id} item={item} />
        ))}
      </div>
    </div>
  );
}
```

**What it does:**
- Fetches history on mount and when `refreshTrigger` changes
- Shows loading spinner while fetching
- Shows error message if fetch fails
- Shows empty state if no history
- Renders list of `HistoryItem` components

---

### 11. App.tsx (UPDATE)

**Location:** `frontend/src/App.tsx`

**Add history section and refresh trigger:**

```tsx
import { useState } from 'react';
import NoteInput from './components/NoteInput';
import FormatSelector from './components/FormatSelector';
import NoteOutput from './components/NoteOutput';
import LoadingSpinner from './components/LoadingSpinner';
import ErrorMessage from './components/ErrorMessage';
import NoteHistory from './components/NoteHistory';
import { cleanNote, ApiError } from './services/api';
import type { OutputFormat, CleanResponse, FieldError } from './types';

// Error state structure
interface ErrorState {
  message: string;
  fieldErrors: FieldError[] | null;
}

function App() {
  // Form state
  const [content, setContent] = useState('');
  const [outputFormat, setOutputFormat] = useState<OutputFormat>('bullets');

  // Result state
  const [result, setResult] = useState<CleanResponse | null>(null);

  // Loading and error state
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState<ErrorState | null>(null);

  // History refresh trigger - increment to refresh history
  const [historyRefresh, setHistoryRefresh] = useState(0);

  const handleSubmit = async () => {
    // Clear previous state
    setError(null);
    setResult(null);
    setIsLoading(true);

    try {
      const response = await cleanNote({ content, outputFormat });
      setResult(response);
      
      // Trigger history refresh after successful clean
      setHistoryRefresh((prev) => prev + 1);
    } catch (err) {
      if (err instanceof ApiError) {
        setError({
          message: err.message,
          fieldErrors: err.errorResponse?.details || null,
        });
      } else {
        setError({
          message: 'An unexpected error occurred',
          fieldErrors: null,
        });
      }
    } finally {
      setIsLoading(false);
    }
  };

  const handleDismissError = () => {
    setError(null);
  };

  const isSubmitDisabled = isLoading || content.trim() === '' || content.length > 10000;

  return (
    <div className="min-h-screen bg-gray-100 py-8 px-4">
      <div className="max-w-2xl mx-auto">
        {/* Header */}
        <header className="text-center mb-8">
          <h1 className="text-3xl font-bold text-gray-800">AI Note Cleaner</h1>
          <p className="text-gray-600 mt-2">
            Transform messy notes into clean, structured text
          </p>
        </header>

        {/* Main Form */}
        <main className="bg-white rounded-lg shadow-md p-6">
          <NoteInput
            value={content}
            onChange={setContent}
            disabled={isLoading}
          />

          <div className="mt-4">
            <FormatSelector
              value={outputFormat}
              onChange={setOutputFormat}
              disabled={isLoading}
            />
          </div>

          <button
            onClick={handleSubmit}
            disabled={isSubmitDisabled}
            className="mt-6 w-full py-3 px-4 bg-blue-600 text-white font-medium rounded-lg
                       hover:bg-blue-700 disabled:bg-gray-400 disabled:cursor-not-allowed
                       transition-colors"
          >
            {isLoading ? 'Cleaning...' : 'Clean My Notes'}
          </button>

          {/* Loading State */}
          {isLoading && (
            <div className="mt-6 flex justify-center">
              <LoadingSpinner />
            </div>
          )}

          {/* Error State */}
          {error && (
            <div className="mt-6">
              <ErrorMessage
                message={error.message}
                fieldErrors={error.fieldErrors}
                onDismiss={handleDismissError}
              />
            </div>
          )}

          {/* Result */}
          {result && !isLoading && (
            <div className="mt-6">
              <NoteOutput content={result.cleaned} />
            </div>
          )}
        </main>

        {/* History Section */}
        <section className="mt-8 bg-white rounded-lg shadow-md p-6">
          <NoteHistory refreshTrigger={historyRefresh} />
        </section>
      </div>
    </div>
  );
}

export default App;
```

**What changed:**
- Added `historyRefresh` state (number that increments to trigger refresh)
- Increment `historyRefresh` after successful clean
- Added `<NoteHistory>` component in new section below main form

---

## Testing

### Backend Tests

Test the new endpoint manually:

```powershell
# First, clean a note to populate history
Invoke-RestMethod -Uri "http://localhost:8080/api/notes/clean" `
  -Method POST `
  -ContentType "application/json" `
  -Body '{"content": "test note here", "outputFormat": "bullets"}'

# Then fetch history
Invoke-RestMethod -Uri "http://localhost:8080/api/notes/history" -Method GET
```

### H2 Console

1. Start backend: `.\mvnw spring-boot:run`
2. Open browser: http://localhost:8080/h2-console
3. Connect with:
   - JDBC URL: `jdbc:h2:mem:notedb`
   - Username: `sa`
   - Password: (blank)
4. Query: `SELECT * FROM NOTE_HISTORY;`

### Full Stack Test

1. Start Ollama, backend, frontend (3 terminals)
2. Open http://localhost:5173
3. Clean a note
4. Verify it appears in history section
5. Click to expand, verify content shows
6. Clean another note, verify history updates

---

## Acceptance Criteria

Before this phase is complete, verify:

- [ ] NoteHistory entity created with correct JPA annotations
- [ ] NoteHistoryRepository with findTop50 method
- [ ] POST /api/notes/clean saves to database
- [ ] GET /api/notes/history returns last 50 items
- [ ] History sorted descending (newest first)
- [ ] Frontend displays history list
- [ ] Clicking item expands to show full content
- [ ] Copy button works on expanded items
- [ ] History refreshes after cleaning a new note
- [ ] H2 console accessible at /h2-console
- [ ] All existing tests still pass

---

## Commit Guidelines

Suggested commit sequence:

1. `feat: add NoteHistory entity and repository`
2. `feat: add history persistence to NoteService`
3. `feat: add GET /api/notes/history endpoint`
4. `chore: configure H2 database and console`
5. `feat: add frontend history types and API`
6. `feat: add HistoryItem component with expand/collapse`
7. `feat: add NoteHistory component with refresh`
8. `feat: integrate history into App with auto-refresh`
9. `docs: update DEV_LOG with Phase 4 notes`

---

## Do NOT

- Add delete functionality (future enhancement)
- Add search/filter (future enhancement)
- Add pagination (50 items is enough for MVP)
- Switch to MySQL yet (save for deployment)
- Add authentication
- Over-engineer - keep it simple

---

## Architecture Decisions to Document

After completing Phase 4, add these ADRs to ARCHITECTURE.md:

- **ADR-017**: H2 for development, MySQL for production
- **ADR-018**: Entity-to-DTO conversion pattern
- **ADR-019**: Spring Data JPA method naming conventions
- **ADR-020**: History refresh via prop trigger pattern
