FROM openjdk:11.0.12-jdk-slim

EXPOSE 7000
WORKDIR /app
ENV JAVA_OPTS=""
COPY config-service-*.jar config-service.jar

ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -jar config-service.jar"]