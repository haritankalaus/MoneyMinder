# ---------- Stage 1: Build ProjectA ----------
FROM maven:3.8.5-openjdk-17 AS build-spring-security

WORKDIR /build/spring-security
COPY spring-security /build/spring-security
RUN mvn clean install -DskipTests

# ---------- Stage 2: Build ProjectB ----------
FROM maven:3.8.5-openjdk-17 AS build-account-manager

# Copy ProjectB source
WORKDIR /build/account-manager
COPY account-manager /build/account-manager

# Copy local maven repo from build-spring-security (just spring-security jar)
COPY --from=build-spring-security /root/.m2 /root/.m2

RUN mvn clean package -DskipTests

# ---------- Stage 3: Final Image ----------
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Copy built jar from build-account-manager
COPY --from=build-account-manager /build/account-manager/target/account-manager-*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
