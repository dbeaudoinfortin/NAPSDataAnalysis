FROM maven:3-openjdk-17 AS builder

COPY ./src /app/src
COPY ./pom.xml /app

WORKDIR /app

RUN mvn clean package

FROM openjdk:17

COPY --from=builder /app/target /app/target
COPY docker/entrypoint.sh /entrypoint.sh

WORKDIR /app/target

ENTRYPOINT ["/entrypoint.sh"]
