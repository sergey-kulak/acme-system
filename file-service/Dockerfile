FROM openjdk:11.0.12-jdk-slim

EXPOSE 7060
WORKDIR /app
COPY file-service-*.jar file-service.jar

ENTRYPOINT ["java", "-jar", "file-service.jar"]