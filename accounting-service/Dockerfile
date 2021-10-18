FROM openjdk:11.0.12-jdk-slim

EXPOSE 7030
WORKDIR /app
COPY accounting-service-*.jar accounting-service.jar

ENTRYPOINT ["java", "-jar", "accounting-service.jar"]