FROM eclipse-temurin:21-jdk
COPY gemimeg-backend/target/gemimeg-backend.jar app.jar
EXPOSE 10013
ENTRYPOINT ["java","-jar","app.jar"]
