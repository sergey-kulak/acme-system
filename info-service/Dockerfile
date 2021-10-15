FROM openjdk:11.0.12-jdk-slim

EXPOSE 7006
WORKDIR /app
COPY info-service-*.jar info-service.jar

ENTRYPOINT ["java", "-jar", "info-service.jar"]