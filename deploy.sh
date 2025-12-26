#!/bin/bash
# Deployment script for ArtCake

echo "ArtCake Deployment Script"
echo "=============================="

# Check if all required environment variables are set
echo "Checking environment variables..."

REQUIRED_VARS=(
    "DATABASE_URL"
    "DATABASE_USERNAME"
    "DATABASE_PASSWORD"
    "MAIL_PASSWORD"
    "APP_BASE_URL"
)

missing_vars=0
for var in "${REQUIRED_VARS[@]}"; do
    if [ -z "${!var}" ]; then
        echo "Missing environment variable: $var"
        missing_vars=$((missing_vars + 1))
    else
        echo "$var is set"
    fi
done

if [ $missing_vars -gt 0 ]; then
    echo ""
    echo "$missing_vars required environment variables are missing"
    echo "Please set all required variables before deployment"
    exit 1
fi

echo ""
echo "All required environment variables are set"
echo ""

# Build the application
echo "Building application..."
./mvnw clean package -DskipTests

if [ $? -ne 0 ]; then
    echo "Build failed"
    exit 1
fi

echo "Build successful"
echo ""

# Check if JAR file exists
JAR_FILE="target/ArtCake-0.0.1-SNAPSHOT.jar"
if [ ! -f "$JAR_FILE" ]; then
    echo "JAR file not found: $JAR_FILE"
    exit 1
fi

echo "JAR file ready: $JAR_FILE"
echo ""

# Test database connection (optional)
echo "Testing database connection..."
java -cp "$JAR_FILE" -Dspring.profiles.active=prod org.springframework.boot.loader.JarLauncher --spring.jpa.hibernate.ddl-auto=validate --server.port=0 &
TEST_PID=$!
sleep 5
if kill -0 $TEST_PID 2>/dev/null; then
    echo "Application started successfully"
    kill $TEST_PID
    wait $TEST_PID 2>/dev/null
else
    echo "Application failed to start"
    exit 1
fi

echo ""
echo "Deployment ready!"
echo ""
echo "To start the application:"
echo "java -jar $JAR_FILE --spring.profiles.active=prod"
echo ""
echo "Or run with systemd service (recommended for production)"
