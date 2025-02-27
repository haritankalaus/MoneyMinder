# MoneyMinder - Personal Finance Management Application

MoneyMinder is a modern, full-stack personal finance management application that helps users track their expenses, manage budgets, and gain insights into their financial health.

## Project Structure

The project is organized into two main components:

### Frontend (`/frontend`)
- Built with Vue 3 + TypeScript
- Features a modern, responsive UI using Vuetify
- Implements secure authentication
- Includes dashboard views for expenses, customers, and settings
- Supports internationalization (i18n)

### Backend (`/backend`)
- Java Spring Boot application
- Two main services:
  - `spring-security`: Handles authentication and JWT token management
  - `account-manager`: Manages core financial data and business logic

## Features

- 🔐 Secure user authentication
- 💰 Expense tracking
- 📊 Financial dashboard
- 👥 Customer management
- ⚙️ User settings and preferences
- 🌐 Responsive design
- 🔄 Real-time updates

## Getting Started

### Prerequisites
- Node.js (for frontend)
- Java JDK (for backend)
- Maven or Gradle (for backend build)

### Running the Application

1. **Backend Setup**
   ```bash
   cd backend
   # Run spring-security service
   cd spring-security
   ./mvnw spring-boot:run

   # Run account-manager service
   cd ../account-manager
   ./mvnw spring-boot:run
   ```

2. **Frontend Setup**
   ```bash
   cd frontend
   npm install
   npm run dev
   ```

## Development

The application uses modern development practices:
- TypeScript for type-safe JavaScript code
- Vue 3 Composition API
- Spring Boot for robust backend services
- JWT for secure authentication
- Responsive design principles

## License

This project is private and confidential. All rights reserved.

---

For more information or support, please contact the project maintainers.
