FROM openjdk:11.0.12-jdk-slim

EXPOSE 7050
WORKDIR /app
ENV JAVA_OPTS=""
COPY menu-service-*.jar menu-service.jar

ENTRYPOINT java ${JAVA_OPTS} -jar menu-service.jar