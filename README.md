# AI Note Cleaner

An intelligent note cleaning application that helps users organize and improve their notes using AI technology.

## Overview

AI Note Cleaner is a full-stack web application designed to help users clean up, organize, and enhance their notes automatically. The application leverages artificial intelligence to improve note quality, structure, and readability.

## Architecture

This project consists of two main components:

- **Backend**: Spring Boot application with REST API ([backend/](./backend/))
- **Frontend**: React + TypeScript application with Vite ([frontend/](./frontend/))

## Technology Stack

### Backend
- Java 21
- Spring Boot 3.4.12
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
- npm or yarn

### Backend Setup

1. Navigate to the backend directory:
   ```bash
   cd backend
   ```

2. Run the Spring Boot application:
   ```bash
   ./mvnw spring-boot:run
   ```

   Or on Windows:
   ```cmd
   mvnw.cmd spring-boot:run
   ```

The backend will start on `http://localhost:8080`

### Frontend Setup

1. Navigate to the frontend directory:
   ```bash
   cd frontend
   ```

2. Install dependencies:
   ```bash
   npm install
   ```

3. Start the development server:
   ```bash
   npm run dev
   ```

The frontend will start on `http://localhost:5173`

## Development

### Backend Development

The backend uses Spring Boot with the following key features:
- RESTful API endpoints
- JPA for data persistence
- H2 in-memory database for development
- Spring Boot DevTools for hot reloading

### Frontend Development

The frontend is built with modern React and includes:
- TypeScript for type safety
- Vite for fast development and building
- Tailwind CSS for styling
- ESLint for code quality

### Available Scripts

#### Frontend
- `npm run dev` - Start development server
- `npm run build` - Build for production
- `npm run lint` - Run ESLint
- `npm run preview` - Preview production build

#### Backend
- `./mvnw spring-boot:run` - Start the application
- `./mvnw test` - Run tests
- `./mvnw clean package` - Build the application

## Project Structure

```
ai-note-cleaner/
├── backend/                 # Spring Boot backend
│   ├── src/
│   │   ├── main/
│   │   └── test/
│   ├── pom.xml
│   └── mvnw
├── frontend/                # React frontend
│   ├── src/
│   ├── public/
│   ├── package.json
│   └── vite.config.ts
└── README.md               # This file
```

## Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.