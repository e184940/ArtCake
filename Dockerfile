FROM openjdk:21-jdk-slim

WORKDIR /app

# Copy Maven wrapper and pom.xml first for better Docker layer caching
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

# Make mvnw executable
RUN chmod +x ./mvnw

# Download dependencies (this layer will be cached unless pom.xml changes)
RUN ./mvnw dependency:go-offline -B

# Copy source code
COPY src src

# Build the application
RUN ./mvnw clean package -DskipTests

# Create directory for uploaded files
RUN mkdir -p /tmp/custom-uploads

# Expose the port
EXPOSE 8080

# Set JVM options for cloud deployment
ENV JAVA_OPTS="-Xmx400m -Xms200m -XX:+UseG1GC -XX:MaxGCPauseMillis=200"

# Run the application
CMD ["sh", "-c", "java $JAVA_OPTS -jar target/ArtCake-0.0.1-SNAPSHOT.jar"]
