FROM openjdk:11.0.12-jdk-slim-bullseye as builder

ARG WORKDIR=/app

WORKDIR ${WORKDIR}
COPY rf-data-service*.jar application.jar
RUN java -Djarmode=layertools -jar application.jar extract

FROM openjdk:11.0.12-jdk-slim-bullseye

EXPOSE 7020
ENV JAVA_OPTS=""
ARG WORKDIR=/app

RUN addgroup acme && adduser --ingroup acme --shell /bin/false acme

WORKDIR ${WORKDIR}
COPY --from=builder --chown=acme:acme ${WORKDIR}/dependencies/ ./
COPY --from=builder --chown=acme:acme ${WORKDIR}/spring-boot-loader/ ./
COPY --from=builder --chown=acme:acme ${WORKDIR}/snapshot-dependencies/ ./
COPY --from=builder --chown=acme:acme ${WORKDIR}/application/ ./

USER acme
ENTRYPOINT java ${JAVA_OPTS} org.springframework.boot.loader.JarLauncher