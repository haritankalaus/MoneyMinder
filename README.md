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

## Architecture Overview

The application follows a cloud-native architecture leveraging AWS services for scalability, reliability, and security:

### Infrastructure Components

#### Frontend Hosting
- Static Vue 3 files hosted on Amazon S3
- CloudFront distribution for CDN and HTTPS
  - Edge locations for global content delivery
  - Custom domain configuration
  - WebSocket support for real-time features
  - Caching strategies for optimal performance
- Automatic SSL/TLS certificate management via ACM
- S3 bucket versioning for rollback capability

#### Backend Services
- Spring Boot applications deployed to AWS Elastic Beanstalk
  - Multi-container Docker environments
  - Auto-scaling based on CPU/Memory metrics
  - Rolling deployments for zero downtime
  - Environment variables for configuration
- Application Load Balancer (ALB) for traffic distribution
  - SSL termination
  - Health checks and automatic instance replacement
  - Path-based routing for microservices
- Elastic Beanstalk worker environments for background jobs

#### Database
- Amazon RDS (MySQL/PostgreSQL) for data persistence
  - Multi-AZ deployment for high availability
  - Read replicas for improved performance
  - Automated backups with 30-day retention
  - Point-in-time recovery capability
- Connection pooling for optimal resource utilization
- Encryption at rest using AWS KMS
- Performance Insights for query optimization

#### CI/CD Pipeline
- AWS CodePipeline for automated deployments
  - Source control integration with GitHub
  - Branch-based deployment strategies
  - Manual approval gates for production
- CodeBuild for building and testing applications
  - Custom build environments using Docker
  - Caching for faster builds
  - Test reports and code coverage metrics
- Artifact management in S3
- Deployment notifications via SNS

#### Security Infrastructure
- HTTPS encryption via AWS Certificate Manager (ACM)
  - Automatic certificate renewal
  - Custom domain validation
- Security groups for network access control
  - Inbound rules limited to necessary ports
  - VPC isolation for backend services
- IAM roles and policies
  - Least privilege principle
  - Service-linked roles
  - Resource-based policies
- AWS WAF for web application firewall
  - DDoS protection
  - IP-based rate limiting
  - SQL injection prevention

#### Monitoring and Logging
- CloudWatch for centralized monitoring
  - Custom metrics and dashboards
  - Log aggregation and analysis
  - Alarm configuration for critical metrics
- X-Ray for distributed tracing
  - Service map visualization
  - Performance bottleneck identification
  - Error tracking and debugging
- AWS Systems Manager
  - Parameter Store for secrets
  - Session Manager for secure shell access
  - Patch Manager for OS updates

### Cost Optimization
- Free tier eligible components where possible
- Auto-scaling for cost-effective resource utilization
- S3 lifecycle policies for old artifacts
- Reserved instances for predictable workloads
- Cost allocation tags for billing analysis

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
