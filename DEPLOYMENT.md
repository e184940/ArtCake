# ArtCake Production Deployment Guide

## 🚀 Deployment Checklist

### 1. Sikkerhet og Konfigurasjon
- [x] ✅ Hardkodet passord fjernet fra `application.properties`
- [ ] Sett opp HTTPS (SSL-sertifikat)
- [ ] Konfigurer brannmur (åpne kun port 80/443)
- [ ] Sett opp backup av database
- [ ] Test SMTP-konfigurasjon i produksjonsmiljø

### 2. Miljøvariabler som MÅ settes

```bash
# Database
export DATABASE_URL="jdbc:postgresql://ider-database.westeurope.cloudapp.azure.com:5433/h184940"
export DATABASE_USERNAME="h184940"
export DATABASE_PASSWORD="din_db_passord"

# Email
export MAIL_PASSWORD="ditt_mail_passord"
export MAIL_HOST="mail.artcake.no"
export MAIL_PORT="465"
export MAIL_USERNAME="artcake@artcake.no"

# Application
export APP_BASE_URL="https://www.artcake.no"  # Endre til din faktiske domene
export APP_MAIL_FROM="artcake@artcake.no"
```

### 3. Server-krav

**Minimum:**
- CPU: 2 cores
- RAM: 2GB
- Disk: 20GB
- Java 21 eller høyere
- PostgreSQL tilgang (remote)

**Anbefalt:**
- CPU: 4 cores
- RAM: 4GB
- Disk: 50GB
- Load balancer for høy trafikk

### 4. Deployment-alternativer

#### A. Tradisjonell Server (Ubuntu/CentOS)

1. **Installer Java:**
```bash
sudo apt update
sudo apt install openjdk-21-jdk
```

2. **Opprett bruker:**
```bash
sudo useradd -m -s /bin/bash artcake
sudo mkdir /opt/artcake
sudo chown artcake:artcake /opt/artcake
```

3. **Kopier filer:**
```bash
scp target/ArtCake-0.0.1-SNAPSHOT.jar server:/opt/artcake/
scp artcake.service server:/etc/systemd/system/
```

4. **Start service:**
```bash
sudo systemctl daemon-reload
sudo systemctl enable artcake
sudo systemctl start artcake
```

#### B. Docker (Anbefalt)

```dockerfile
FROM openjdk:21-jdk-slim

WORKDIR /app
COPY target/ArtCake-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar", "--spring.profiles.active=prod"]
```

**Build og kjør:**
```bash
docker build -t artcake .
docker run -p 8080:8080 --env-file .env artcake
```

#### C. Cloud Platforms

**Azure App Service:**
- Last opp JAR-fil
- Sett environment variables i App Settings
- Aktiver Application Insights for monitoring

**Heroku:**
- Legg til `Procfile`: `web: java -jar target/ArtCake-0.0.1-SNAPSHOT.jar --server.port=$PORT`
- Sett config vars for environment variables

### 5. Reverse Proxy (Nginx)

```nginx
server {
    listen 80;
    server_name www.artcake.no artcake.no;
    return 301 https://$host$request_uri;
}

server {
    listen 443 ssl http2;
    server_name www.artcake.no artcake.no;

    ssl_certificate /path/to/certificate.crt;
    ssl_certificate_key /path/to/private.key;

    location / {
        proxy_pass http://localhost:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }

    location /static/ {
        alias /opt/artcake/static/;
        expires 1y;
        add_header Cache-Control "public, immutable";
    }
}
```

### 6. Monitoring og Logging

**Health Check:**
- URL: `http://your-domain/actuator/health`
- Legg til i `pom.xml`: `spring-boot-starter-actuator`

**Logging:**
```bash
# Se logger
sudo journalctl -u artcake -f

# Log-rotasjon
sudo logrotate -f /etc/logrotate.d/artcake
```

### 7. Database Migration

**Første deployment:**
```bash
java -jar app.jar --spring.jpa.hibernate.ddl-auto=create-drop --spring.profiles.active=prod
```

**Produksjon:**
```bash
java -jar app.jar --spring.jpa.hibernate.ddl-auto=validate --spring.profiles.active=prod
```

### 8. Testing før Go-Live

```bash
# Test email sending
curl "http://localhost:8080/admin/test-email?to=test@domain.com"

# Test database connection
curl "http://localhost:8080/actuator/health"

# Test full order flow
# 1. Legg til kake i handlekurv
# 2. Fullfør bestilling
# 3. Sjekk at email ble sendt til både kunde og konditor
```

### 9. Backup og Sikkerhet

**Database backup (daglig):**
```bash
#!/bin/bash
pg_dump -h ider-database.westeurope.cloudapp.azure.com -p 5433 -U h184940 -d h184940 --schema=artcake > backup_$(date +%Y%m%d).sql
```

**SSL-sertifikat (Let's Encrypt):**
```bash
sudo apt install certbot python3-certbot-nginx
sudo certbot --nginx -d artcake.no -d www.artcake.no
```

### 10. Performance Tuning

**JVM-innstillinger:**
```bash
JAVA_OPTS="-Xmx1g -Xms512m -server -XX:+UseG1GC -XX:MaxGCPauseMillis=200"
```

**Database connection pool:**
```properties
spring.datasource.hikari.maximum-pool-size=10
spring.datasource.hikari.minimum-idle=5
spring.datasource.hikari.connection-timeout=30000
```

---

## 🎯 Quick Start Commands

**Build for production:**
```bash
./mvnw clean package -DskipTests
```

**Start application:**
```bash
# With .env file (recommended)
java -jar target/ArtCake-0.0.1-SNAPSHOT.jar

# OR with environment variables directly
DATABASE_PASSWORD=xyz MAIL_PASSWORD=abc java -jar target/ArtCake-0.0.1-SNAPSHOT.jar
```

**Verify deployment:**
```bash
curl https://www.artcake.no/admin/test-email
```
