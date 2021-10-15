FROM openjdk:11.0.12-jdk-slim

EXPOSE 7010
WORKDIR /app
COPY user-service-*.jar user-service.jar

ENTRYPOINT ["java", "-jar", "user-service.jar"]