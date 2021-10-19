FROM openjdk:11.0.12-jdk-slim

EXPOSE 7010
WORKDIR /app
ENV JAVA_OPTS=""
COPY user-service-*.jar user-service.jar

ENTRYPOINT java ${JAVA_OPTS} -jar user-service.jar