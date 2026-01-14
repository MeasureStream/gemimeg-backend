# Production-ready Spring Boot Dockerfile
FROM eclipse-temurin:21-jre

WORKDIR /app

# Create a non-root user for security
RUN addgroup --system spring && adduser --system spring --ingroup spring
USER spring:spring

# Copy the pre-built JAR file (built by Maven/GitHub Actions)
COPY gemimeg-backend/target/gemimeg-backend.jar app.jar

# Expose the port the Spring Boot app runs on
EXPOSE 10001

# Health check for Spring Boot Actuator (if enabled)
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
    CMD wget --no-verbose --tries=1 --spider http://localhost:10001/actuator/health || exit 1

# JVM options optimized for containers and Spring Boot
ENV JAVA_OPTS="-Xmx512m -Xms256m -XX:+UseG1GC -XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -Djava.security.egd=file:/dev/./urandom"

# Run the Spring Boot application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
