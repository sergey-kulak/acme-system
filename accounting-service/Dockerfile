FROM openjdk:11.0.12-jdk-slim

EXPOSE 7030
WORKDIR /app
ENV JAVA_OPTS=""
COPY accounting-service-*.jar accounting-service.jar

ENTRYPOINT java ${JAVA_OPTS} -jar accounting-service.jar