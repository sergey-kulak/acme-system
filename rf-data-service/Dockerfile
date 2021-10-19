FROM openjdk:11.0.12-jdk-slim

EXPOSE 7020
WORKDIR /app
ENV JAVA_OPTS=""
COPY rf-data-service-*.jar rf-data-service.jar

ENTRYPOINT java ${JAVA_OPTS} -jar rf-data-service.jar