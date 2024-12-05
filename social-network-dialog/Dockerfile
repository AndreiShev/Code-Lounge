FROM eclipse-temurin:17-jdk-alpine
LABEL authors="Bunchiek"
WORKDIR /app
COPY target/social-network-dialog-0.0.1-SNAPSHOT.jar app.jar

ENTRYPOINT ["java","-jar","app.jar"]