FROM openjdk:11.0.12-jdk-slim

EXPOSE 7004
WORKDIR /app
ENV JAVA_OPTS=""
COPY api-gateway-*.jar api-gateway.jar

ENTRYPOINT java ${JAVA_OPTS} -jar api-gateway.jar