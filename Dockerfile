FROM eclipse-temurin:25-jre-alpine

WORKDIR /run

RUN apk update && \
    apk add --no-cache gcc && \
    addgroup -S run && adduser -S -D -s /bin/sh -G run run

COPY --chown=run:run build/libs/*-service-*.jar service.jar

USER run
EXPOSE 8080
EXPOSE 9090
ENTRYPOINT ["java", "-jar", "service.jar"]
