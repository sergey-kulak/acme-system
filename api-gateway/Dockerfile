FROM maven:3.8.3-openjdk-11 as build

WORKDIR /build

COPY pom.xml /build
RUN mvn assembly:help compiler:help enforcer:help exec:help failsafe:help install:help jar:help resources:help surefire:help
RUN mvn dependency:resolve && mvn dependency:resolve-plugins

COPY src/ /build/src
RUN mvn -o package

FROM openjdk:11.0.12-jdk-slim

EXPOSE 7002
COPY --from=build /build/target/api-gateway-*.jar api-gateway.jar

ENTRYPOINT ["java", "-jar", "api-gateway.jar"]