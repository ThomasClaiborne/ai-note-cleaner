# AI Note Cleaner - Backend

The backend API for AI Note Cleaner, built with Spring Boot.

## Technology Stack

- **Java 21** - Programming language
- **Spring Boot 3.4.12** - Application framework
- **Spring Data JPA** - Data persistence
- **Spring Boot Validation** - Input validation
- **H2 Database** - In-memory database for development
- **Maven** - Build tool and dependency management

## Getting Started

### Prerequisites

- Java 21 or higher
- Maven (or use the included Maven wrapper)

### Running the Application

1. Navigate to the backend directory:
   ```bash
   cd backend
   ```

2. Run the application using Maven wrapper:
   ```bash
   ./mvnw spring-boot:run
   ```

   Or on Windows:
   ```cmd
   mvnw.cmd spring-boot:run
   ```

The application will start on `http://localhost:8080`

### Available Maven Commands

- `./mvnw spring-boot:run` - Start the application
- `./mvnw test` - Run all tests
- `./mvnw clean package` - Build the application
- `./mvnw clean install` - Build and install to local repository

## Project Structure

```
backend/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/ainote/backend/
│   │   └── resources/
│   │       └── application.properties
│   └── test/
│       └── java/
├── pom.xml
├── mvnw
└── mvnw.cmd
```

## Configuration

The application uses Spring Boot's default configuration with:
- H2 in-memory database for development
- JPA for data persistence
- Spring Boot DevTools for hot reloading during development

## API Documentation

Once the application is running, you can access:
- Application: `http://localhost:8080`
- H2 Console (development): `http://localhost:8080/h2-console`

## Development

### Adding Dependencies

Add new dependencies to the `pom.xml` file and run:
```bash
./mvnw clean install
```

### Database

The application uses H2 in-memory database for development. The database is recreated on each application restart.

For production, you can configure a different database by updating the `application.properties` file.

### Testing

Run tests with:
```bash
./mvnw test
```

## Building for Production

To build the application for production:
```bash
./mvnw clean package
```

This will create a JAR file in the `target/` directory that can be deployed to any Java-compatible server.