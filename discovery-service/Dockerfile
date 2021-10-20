FROM openjdk:11.0.12-jdk-slim-bullseye as builder

ARG WORKDIR=/app

WORKDIR ${WORKDIR}
COPY discovery-service*.jar application.jar
RUN java -Djarmode=layertools -jar application.jar extract

FROM openjdk:11.0.12-jdk-slim-bullseye

EXPOSE 7002
ENV JAVA_OPTS=""
ARG WORKDIR=/app

RUN addgroup acme && adduser --ingroup acme --shell /bin/false acme

WORKDIR ${WORKDIR}
COPY --from=builder ${WORKDIR}/dependencies/ ./
COPY --from=builder ${WORKDIR}/snapshot-dependencies/ ./
COPY --from=builder ${WORKDIR}/spring-boot-loader/ ./
COPY --from=builder ${WORKDIR}/application/ ./
RUN chown -R acme:acme ${WORKDIR}

USER acme
ENTRYPOINT java ${JAVA_OPTS} org.springframework.boot.loader.JarLauncher