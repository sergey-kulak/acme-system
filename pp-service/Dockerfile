FROM openjdk:11.0.12-jdk-slim

EXPOSE 7040
WORKDIR /app
COPY pp-service-*.jar pp-service.jar

ENTRYPOINT ["java", "-jar", "pp-service.jar"]