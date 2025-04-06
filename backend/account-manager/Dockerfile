# Build stage
FROM maven:3.8.5-openjdk-17 AS build
WORKDIR /app

# Copy the POM file first to cache dependencies
COPY pom.xml .
# Download dependencies (including Spring Security)
RUN mvn dependency:go-offline -B --no-transfer-progress

# Copy source code and build the application
COPY src ./src
RUN mvn package -DskipTests --no-transfer-progress

# Run stage
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

# Expose the Spring Boot port (default: 8080)
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
