FROM openjdk:11.0.12-jdk-slim

EXPOSE 7004
WORKDIR /app
COPY api-gateway-*.jar api-gateway.jar

ENTRYPOINT ["java", "-jar", "api-gateway.jar"]