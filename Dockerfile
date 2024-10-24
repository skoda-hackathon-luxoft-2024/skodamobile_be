FROM maven:3-openjdk-17-slim as builder

WORKDIR /app
COPY . /app
ARG VERSION=1.0.0
RUN mvn versions:set -DnewVersion=${VERSION}
RUN mvn --batch-mode --update-snapshots verify

FROM openjdk:17.0.1-jdk-slim

ARG BUILD_DATE
ARG VCS_REF
ARG VERSION=1.0.0

LABEL org.label-schema.name="skoda-hackathon-2024/api"
LABEL org.label-schema.version=$VERSION
LABEL org.label-schema.build-date=$BUILD_DATE
LABEL org.label-schema.vcs-ref=$VCS_REF

ENV BUILD_DATE=$BUILD_DATE
ENV VCS_REF=$VCS_REF
ENV VERSION=$VERSION

WORKDIR /app
COPY --from=builder /app/target/skoda-mobile-backend.jar /app/hackathon.jar

EXPOSE 8080 80

CMD ["java", "-jar", "/app/hackathon.jar", "--server.port=8080"]
