FROM eclipse-temurin:17-jdk-alpine

WORKDIR /app

# Copy Maven wrapper and dependencies
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

# Make mvnw executable
RUN chmod +x ./mvnw

# Download dependencies (for better caching)
RUN ./mvnw dependency:go-offline -B

# Copy source code
COPY src src

# Build application
RUN ./mvnw clean package -DskipTests

# Expose port
EXPOSE 8080

# Run application
CMD ["java", "-Dserver.port=${PORT:-8080}", "-Xmx400m", "-jar", "target/artcake.jar"]
