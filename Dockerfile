FROM eclipse-temurin:21-jre-alpine

WORKDIR /run
COPY build/libs/*-service-*.jar service.jar

EXPOSE 8080
EXPOSE 9090
ENTRYPOINT ["java", "-jar", "service.jar"]
