FROM acme-commons:1.0-SNAPSHOT as commons
FROM acme-test-commons:1.0-SNAPSHOT as test-commons

FROM maven:3.8.3-openjdk-11 as build

COPY --from=commons /commons.jar /build/
COPY --from=test-commons /test-commons.jar /build/
RUN mvn install:install-file -Dfile=/build/commons.jar -DgroupId=com.acme \
     -DartifactId=commons -Dversion=1.0-SNAPSHOT -Dpackaging=jar && \
    mvn install:install-file -Dfile=/build/test-commons.jar -DgroupId=com.acme \
     -DartifactId=test-commons -Dversion=1.0-SNAPSHOT -Dpackaging=jar

WORKDIR /build

COPY pom.xml /build
RUN mvn verify
#compiler:help enforcer:help failsafe:help install:help jar:help resources:help surefire:help
#RUN mvn dependency:resolve && mvn dependency:resolve-plugins

#COPY src/ /build/src
#RUN mvn -o package
#
#FROM openjdk:11.0.12-jdk-slim
#
#EXPOSE 7002
#COPY --from=build /build/target/user-service-*.jar user-service.jar
#
#ENTRYPOINT ["java", "-jar", "user-service.jar"]