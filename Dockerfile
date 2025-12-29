# Usage:
#   ./gradlew bootJar
#   docker build -t sillog:latest .

FROM eclipse-temurin:21-jre-alpine

RUN apk add --no-cache git curl

RUN addgroup -g 1000 sillog && \
    adduser -u 1000 -G sillog -s /bin/sh -D sillog

WORKDIR /app

COPY build/libs/*.jar app.jar

RUN chown -R sillog:sillog /app

USER sillog

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
