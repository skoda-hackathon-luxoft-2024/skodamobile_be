FROM openjdk:17.0.1-jdk-slim

ARG BUILD_DATE
ARG VCS_REF
ARG BUILD_VERSION

LABEL org.label-schema.schema-version="1.0"
LABEL org.label-schema.build-date=$BUILD_DATE
LABEL org.label-schema.name="skoda-hackathon-2022/api"
LABEL org.label-schema.description="Backend for the hackathon, providing a REST API to receive POI and store favorites"
LABEL org.label-schema.url="https://skoda-hackathon-2022-backend.westeurope.azurecontainer.io/"
LABEL org.label-schema.vcs-url="https://github.com/skoda-hackathon-2022/api"
LABEL org.label-schema.vcs-ref=$VCS_REF
LABEL org.label-schema.vendor="DXC Luxoft"
LABEL org.label-schema.version=$BUILD_VERSION

WORKDIR /app
COPY ./target/spring-boot-react-3.3.4.jar /app/hackathon.jar
COPY ./poi.json .
CMD ["java", "-jar", "/app/hackathon.jar", "--server.port=8080"]