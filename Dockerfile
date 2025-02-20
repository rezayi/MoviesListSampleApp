FROM maven:3.9.9-eclipse-temurin-21-alpine AS build

COPY pom.xml .
COPY src src
RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jre

COPY target/sample-movies-application-1.0-SNAPSHOT.jar .
EXPOSE 8080

ENTRYPOINT ["java" , "-Xmx512m" ,"-jar", "sample-movies-application-1.0-SNAPSHOT.jar"]

