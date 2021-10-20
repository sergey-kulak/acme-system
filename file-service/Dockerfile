FROM openjdk:11.0.12-jdk-slim

EXPOSE 7060
WORKDIR /app
ENV JAVA_OPTS=""
COPY file-service-*.jar file-service.jar

ENTRYPOINT java ${JAVA_OPTS} -jar file-service.jar