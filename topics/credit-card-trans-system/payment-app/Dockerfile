# Multi-stage Dockerfile for Payment Application
# Stage 1: Build stage
FROM maven:3.8.6-eclipse-temurin-11 AS builder

WORKDIR /app

# Copy pom.xml and download dependencies (cached layer)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code and build
COPY src ./src
RUN mvn clean package -DskipTests -B

# Stage 2: Runtime stage with distroless
FROM gcr.io/distroless/java11-debian11:nonroot

# Set working directory
WORKDIR /app

# Copy the built JAR from builder stage
COPY --from=builder /app/target/payment-app-1.0.0.jar /app/payment-app.jar

# Expose port
EXPOSE 8080

# Run as non-root user (distroless already uses nonroot user)
USER nonroot:nonroot

# Set JVM options for container environment
ENV JAVA_TOOL_OPTIONS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -XX:InitialRAMPercentage=50.0"

# Run the application
ENTRYPOINT ["java", "-jar", "/app/payment-app.jar"]

# Made with Bob
