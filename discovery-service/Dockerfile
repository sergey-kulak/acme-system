FROM openjdk:11.0.12-jdk-slim

EXPOSE 7002
WORKDIR /app
COPY discovery-service-*.jar discovery-service.jar

ENTRYPOINT ["java", "-jar", "discovery-service.jar"]