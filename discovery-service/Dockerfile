FROM openjdk:11.0.12-jdk-slim

RUN addgroup -S acme && adduser -S acme -G acme
USER acme

EXPOSE 7002
ENV JAVA_OPTS=""

WORKDIR /app
COPY BOOT-INF/lib lib/
COPY META-INF META-INF/
COPY BOOT-INF/classes ./

ENTRYPOINT java ${JAVA_OPTS} -cp .:lib/ com.acme.discovery.Application