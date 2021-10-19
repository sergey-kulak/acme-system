FROM openjdk:11.0.12-jdk-slim

EXPOSE 7050
WORKDIR /app
COPY menu-service-*.jar menu-service.jar

ENTRYPOINT ["java", "-jar", "menu-service.jar"]