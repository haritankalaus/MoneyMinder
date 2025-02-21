# Personal Account Manager

A modern personal finance management application built with Spring Boot and Vue 3 + Vuetify.

## Features

- Secure user authentication with JWT
- Account management and tracking
- Transaction monitoring
- Budget planning
- Bill analytics
- Modern and responsive UI with Vuetify 3

## Prerequisites

- Java 17 or higher
- Node.js 16 or higher
- PostgreSQL 12 or higher
- Maven 3.6 or higher

## Setup

### Backend Setup

1. Create a PostgreSQL database named `account_manager`
2. Update database credentials in `src/main/resources/application.yml` if needed
3. Navigate to the project root directory
4. Run: `mvn clean install`
5. Start the application: `mvn spring-boot:run`

The backend will start on `http://localhost:8080`

### Frontend Setup

1. Navigate to the frontend directory: `cd frontend`
2. Install dependencies: `npm install`
3. Start the development server: `npm run dev`

The frontend will start on `http://localhost:3000`

## Security Features

- JWT-based authentication
- Password encryption
- CORS configuration
- Spring Security implementation
- Protected API endpoints
- Secure session management

## Development

- Backend API runs on `http://localhost:8080/api`
- Frontend development server runs on `http://localhost:3000`
- API documentation available at `http://localhost:8080/api/swagger-ui.html`

## License

MIT
