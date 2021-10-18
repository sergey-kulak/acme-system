FROM openjdk:11.0.12-jdk-slim

EXPOSE 7020
WORKDIR /app
COPY rf-data-service-*.jar rf-data-service.jar

ENTRYPOINT ["java", "-jar", "rf-data-service.jar"]