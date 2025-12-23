# AI Note Cleaner Repository

## Purpose
AI Note Cleaner is a full-stack web application designed to help users clean up, organize, and enhance their notes automatically using artificial intelligence. The application leverages AI technology to improve note quality, structure, and readability.

## General Setup
This is a monorepo containing both frontend and backend components:

### Prerequisites
- Java 21 or higher
- Node.js 18 or higher
- npm or yarn

### Development Setup
1. **Backend**: Navigate to `backend/` and run `./mvnw spring-boot:run` (starts on port 8080)
2. **Frontend**: Navigate to `frontend/`, run `npm install` then `npm run dev` (starts on port 5173)

### Technology Stack
- **Backend**: Spring Boot 3.4.12, Java 21, Spring Data JPA, H2 Database, Maven
- **Frontend**: React 19.2.0, TypeScript, Vite (with Rolldown), Tailwind CSS, ESLint

## Repository Structure
```
ai-note-cleaner/
├── backend/                    # Spring Boot REST API
│   ├── src/main/java/         # Java source code
│   │   └── com/ainote/backend/
│   ├── src/main/resources/    # Configuration files
│   ├── src/test/              # Test files
│   ├── pom.xml               # Maven dependencies
│   ├── mvnw                  # Maven wrapper
│   └── README.md             # Backend documentation
├── frontend/                  # React TypeScript application
│   ├── src/                  # React source code
│   ├── public/               # Static assets
│   ├── package.json          # npm dependencies
│   ├── vite.config.ts        # Vite configuration
│   ├── eslint.config.js      # ESLint configuration
│   ├── tsconfig.json         # TypeScript configuration
│   └── README.md             # Frontend documentation
├── .gitignore                # Git ignore rules
└── README.md                 # Main project documentation
```

## Development Status
The project appears to be in early development stage:
- Backend has basic Spring Boot setup with minimal configuration
- Frontend contains default Vite + React template code
- No CI/CD workflows are currently configured (no `.github/` directory found)
- No automated testing or linting workflows in place

## Code Quality Tools
- **Frontend**: ESLint configured with TypeScript, React hooks, and React refresh plugins
- **Backend**: Standard Maven setup with Spring Boot testing framework
- **No CI checks**: No GitHub Actions workflows or other CI/CD pipelines detected

## Key Commands
### Backend
- `./mvnw spring-boot:run` - Start development server
- `./mvnw test` - Run tests
- `./mvnw clean package` - Build application

### Frontend
- `npm run dev` - Start development server
- `npm run build` - Build for production
- `npm run lint` - Run ESLint
- `npm run preview` - Preview production build