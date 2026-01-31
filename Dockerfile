# Production-ready Spring Boot Dockerfile
FROM eclipse-temurin:25-jre

WORKDIR /app

# Create a non-root user for security
RUN addgroup --system spring && adduser --system spring --ingroup spring

# Create data directory for H2 database files
RUN mkdir -p /app/data && chown -R spring:spring /app/data

# Switch to non-root user
USER spring:spring

# Environment Variables
ENV SERVER_PORT=10001
ENV SPRING_PROFILES_ACTIVE=prod
ENV SPRING_DATASOURCE_URL=jdbc:h2:file:/app/data/gemimeg
ENV SPRING_DATASOURCE_USERNAME=sa
ENV SPRING_DATASOURCE_PASSWORD=
ENV SPRING_DATASOURCE_DRIVER_CLASS_NAME=org.h2.Driver
ENV SPRING_JPA_HIBERNATE_DDL_AUTO=update
ENV SPRING_JPA_SHOW_SQL=false
ENV LOGGING_LEVEL_ROOT=INFO
ENV JAVA_OPTS="-Xmx512m -Xms256m -XX:+UseG1GC -XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -Djava.security.egd=file:/dev/./urandom"

# Service URLs and Configuration
ENV DCC_SERVICE_URL=http://dcc-service:8080
ENV GATEWAY_IAM_URL=http://gateway-iam:8080
ENV FRONTEND_URL=http://gemimeg-frontend:80

# Copy the pre-built JAR file (built by Maven/GitHub Actions)
COPY gemimeg-backend/target/gemimeg-backend.jar app.jar

# Expose the port the Spring Boot app runs on (configurable)
EXPOSE ${SERVER_PORT}

# Health check for Spring Boot Actuator
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
    CMD wget --no-verbose --tries=1 --spider http://localhost:${SERVER_PORT}/actuator/health || exit 1

# Run the Spring Boot application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
