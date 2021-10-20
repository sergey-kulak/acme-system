FROM openjdk:11.0.12-jdk-slim-bullseye

EXPOSE 7002
ENV JAVA_OPTS=""
ARG WORKDIR=/app

RUN addgroup acme && adduser --ingroup acme --shell /bin/false acme

WORKDIR ${WORKDIR}
COPY BOOT-INF/lib lib/
COPY META-INF META-INF/
COPY BOOT-INF/classes ./
RUN chown -R acme:acme ${WORKDIR}

USER acme
ENTRYPOINT java ${JAVA_OPTS} -cp .:lib/* com.acme.discovery.Application