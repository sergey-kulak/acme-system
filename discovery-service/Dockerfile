FROM openjdk:11.0.12-jdk-slim

EXPOSE 7002
WORKDIR /app
ENV JAVA_OPTS=""
COPY discovery-service-*.jar discovery-service.jar

ENTRYPOINT java ${JAVA_OPTS} -jar discovery-service.jar