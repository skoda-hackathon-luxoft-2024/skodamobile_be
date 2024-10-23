FROM openjdk:17.0.1-jdk-slim

ARG BUILD_DATE
ARG VCS_REF
ARG BUILD_VERSION

LABEL org.label-schema.name="skoda-hackathon-2024/api"
LABEL org.label-schema.version=$BUILD_VERSION

WORKDIR /app
COPY ./target/skoda-mobile-backend.jar /app/hackathon.jar

EXPOSE 8080 80

CMD ["java", "-jar", "/app/hackathon.jar", "--server.port=8080"]
