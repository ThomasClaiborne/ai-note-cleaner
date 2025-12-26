# AI Note Cleaner

An intelligent note cleaning application that helps users organize and improve their notes using AI technology.

## Overview

AI Note Cleaner is a full-stack web application designed to help you clean up, organize, and enhance your notes automatically. The application leverages artificial intelligence to improve note quality, structure, and readability.

**Features:**
- Transform messy notes into clean, structured text
- Choose output format: bullet points, paragraphs, or numbered lists
- AI-powered spelling, grammar, and clarity improvements
- Copy cleaned notes to clipboard with one click

## Architecture

This project consists of two main components:

- **Backend**: Spring Boot REST API with Spring AI integration ([backend/](./backend/))
- **Frontend**: React + TypeScript SPA with Tailwind CSS ([frontend/](./frontend/))

For detailed architecture decisions and design documentation, see [docs/ARCHITECTURE.md](./docs/ARCHITECTURE.md).

## Technology Stack

### Backend
- Java 21
- Spring Boot 3.4.12
- Spring AI 1.0.0-M4
- Ollama (local LLM runtime)
- Spring Data JPA
- H2 Database (development)
- Maven

### Frontend
- React 19.2.0
- TypeScript
- Vite (with Rolldown)
- Tailwind CSS
- ESLint

## Getting Started

### Prerequisites

- Java 21 or higher
- Node.js 18 or higher
- npm
- Ollama (for AI features)

### Ollama Setup

The application uses Ollama for AI-powered note cleaning. You'll need to install and configure it:

1. **Install Ollama** from [ollama.ai](https://ollama.ai)

2. **Pull the required model**:
   ```powershell
   ollama pull llama3.2
   ```

3. **Start Ollama** (if not running as a service):
   ```powershell
   ollama serve
   ```

4. **Verify Ollama is running**:
   ```powershell
   Invoke-RestMethod -Uri "http://localhost:11434"
   # Should return: "Ollama is running"
   ```

### Running the Full Stack

You need **three terminals** running simultaneously:

#### Terminal 1: Ollama (if not running as a service)
```powershell
ollama serve
```

#### Terminal 2: Backend
```powershell
cd backend
.\mvnw spring-boot:run
```
The backend will start on `http://localhost:8080`

#### Terminal 3: Frontend
```powershell
cd frontend
npm install    # Only needed first time
npm run dev
```
The frontend will start on `http://localhost:5173`

### Using the Application

1. Open your browser to `http://localhost:5173`
2. Enter your messy notes in the text area
3. Select your preferred output format (bullets, paragraphs, or numbered)
4. Click "Clean My Notes"
5. View the AI-cleaned result and click "Copy" to copy to clipboard

## Development

### Backend Development

```powershell
cd backend

# Start the application
.\mvnw spring-boot:run

# Run tests
.\mvnw test

# Build the application
.\mvnw clean package
```

### Frontend Development

```powershell
cd frontend

# Install dependencies
npm install

# Start development server with hot reload
npm run dev

# Build for production
npm run build

# Run linter
npm run lint

# Preview production build
npm run preview
```

### Testing the API Directly

```powershell
# Test the clean endpoint
Invoke-RestMethod -Uri "http://localhost:8080/api/notes/clean" `
  -Method POST `
  -ContentType "application/json" `
  -Body '{"content": "messy notes here", "outputFormat": "bullets"}'
```

## Project Structure

```
ai-note-cleaner/
├── backend/                          # Spring Boot backend
│   ├── src/main/java/com/ainote/backend/
│   │   ├── BackendApplication.java   # Entry point
│   │   ├── controller/
│   │   │   └── NoteController.java   # REST endpoint
│   │   ├── service/
│   │   │   ├── NoteService.java      # Business logic
│   │   │   └── AiCleaningService.java # AI integration
│   │   ├── dto/
│   │   │   ├── CleanRequest.java     # Request DTO
│   │   │   ├── CleanResponse.java    # Response DTO
│   │   │   └── ErrorResponse.java    # Error DTO
│   │   └── exception/
│   │       ├── AiServiceException.java
│   │       └── GlobalExceptionHandler.java
│   ├── src/main/resources/
│   │   └── application.properties    # Configuration
│   ├── src/test/java/                # Tests
│   ├── pom.xml                       # Maven config
│   └── mvnw, mvnw.cmd               # Maven wrapper
│
├── frontend/                         # React frontend
│   ├── src/
│   │   ├── components/
│   │   │   ├── NoteInput.tsx         # Text input with char count
│   │   │   ├── FormatSelector.tsx    # Output format selector
│   │   │   ├── NoteOutput.tsx        # Result display + copy
│   │   │   ├── LoadingSpinner.tsx    # Loading indicator
│   │   │   └── ErrorMessage.tsx      # Error display
│   │   ├── services/
│   │   │   └── api.ts                # API client
│   │   ├── types/
│   │   │   └── index.ts              # TypeScript interfaces
│   │   ├── App.tsx                   # Main component
│   │   ├── main.tsx                  # Entry point
│   │   └── index.css                 # Tailwind imports
│   ├── package.json
│   ├── tsconfig.json
│   └── vite.config.ts
│
├── docs/                             # Documentation
│   ├── ARCHITECTURE.md               # Design decisions (ADRs)
│   ├── DEV_LOG.md                    # Development journal
│   └── INSTRUCTIONS - Phase X.md    # Phase instructions
│
├── .gitignore
└── README.md                         # This file
```

## API Reference

### POST /api/notes/clean

Clean and format note content using AI.

**Request:**
```json
{
  "content": "messy notes here with typos and bad formatting",
  "outputFormat": "bullets"
}
```

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| content | string | Yes | The note text to clean (max 10,000 chars) |
| outputFormat | string | Yes | One of: `bullets`, `paragraphs`, `numbered` |

**Success Response (200):**
```json
{
  "original": "messy notes here...",
  "cleaned": "• Clean point one\n• Clean point two",
  "outputFormat": "bullets",
  "timestamp": "2024-12-24T14:30:00.000"
}
```

**Error Responses:**

| Status | Meaning | Example |
|--------|---------|---------|
| 400 | Validation failed | Empty content, invalid format |
| 503 | AI service unavailable | Ollama not running |
| 504 | AI request timeout | Model took too long |
| 500 | Server error | Unexpected failure |

## Troubleshooting

### "AI service is unavailable"
- Make sure Ollama is running: `ollama serve`
- Verify the model is installed: `ollama list` should show `llama3.2`

### CORS errors in browser console
- Make sure you're accessing the frontend at `http://localhost:5173`
- The backend is configured to allow requests from this origin

### TypeScript errors in frontend
- Run `npm install` to ensure all dependencies are installed
- Check that you're using Node.js 18+

### Backend won't start
- Ensure Java 21 is installed: `java -version`
- Check that port 8080 is not in use

## Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'feat: add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.