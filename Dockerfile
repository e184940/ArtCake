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

# Copy source code and resources
COPY src src

# Verify images are copied
RUN ls -la src/main/resources/static/images/ | head -10

# Build application
RUN ./mvnw clean package -DskipTests

# Verify images are in the JAR
RUN jar -tf target/artcake.jar | grep "static/images" | head -10 || echo "No images found in JAR!"
RUN jar -tf target/artcake.jar | grep "logo_hvit_nobg.png" || echo "Logo not found!"

# Expose port
EXPOSE 8080

# Run application
CMD ["java", "-Dserver.port=${PORT:-8080}", "-Xmx400m", "-jar", "target/artcake.jar"]
