# ===== 1. Build Stage =====
FROM maven:3.9.6-eclipse-temurin-17 AS build

WORKDIR /app

# Copy pom.xml and download dependencies
COPY pom.xml .
RUN mvn -B dependency:go-offline

# Copy source and build the jar
COPY src ./src
RUN mvn -B clean package -DskipTests

# ===== 2. Run Stage =====
FROM eclipse-temurin:17-jdk-alpine

WORKDIR /app

# Copy the jar from the build stage
COPY --from=build /app/target/*.jar app.jar

# Render will inject PORT env var; expose it
EXPOSE 8080

# Start Spring Boot
ENTRYPOINT ["java","-jar","app.jar"]