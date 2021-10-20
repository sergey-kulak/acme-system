FROM openjdk:11.0.12-jdk-slim

EXPOSE 7040
WORKDIR /app
ENV JAVA_OPTS=""
COPY pp-service-*.jar pp-service.jar

ENTRYPOINT java ${JAVA_OPTS} -jar pp-service.jar