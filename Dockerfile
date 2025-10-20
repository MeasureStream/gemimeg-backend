FROM eclipse-temurin:25-jdk
COPY gemimeg-backend/target/gemimeg-backend.jar app.jar
EXPOSE 10001
ENTRYPOINT ["java","-jar","app.jar"]
