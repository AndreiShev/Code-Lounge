FROM eclipse-temurin:21-jdk-alpine
ARG JAR_FILE=/home/app/target/*.jar
COPY target/social-network-gateway-0.0.1-SNAPSHOT.jar app.jar
ENV JAVA_OPTS=""
EXPOSE 9090
ENTRYPOINT ["java", "-jar", "app.jar"]