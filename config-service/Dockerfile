FROM openjdk:11.0.12-jdk-slim

EXPOSE 7000
WORKDIR /app
COPY config-service-*.jar config-service.jar

ENTRYPOINT ["java", "-jar", "config-service.jar"]