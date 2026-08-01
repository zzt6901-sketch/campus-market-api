# syntax=docker/dockerfile:1
# Multi-stage build: compile with Maven, run with a slim JRE image.
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn -B dependency:go-offline
COPY src ./src
RUN mvn -B clean package -DskipTests

FROM eclipse-temurin:17-jre
WORKDIR /app
RUN mkdir -p /app/uploads
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
# Safe demo defaults; override at runtime (especially DB_PASSWORD and JWT_SECRET).
ENV SERVER_PORT=8080 \
    DB_HOST=localhost \
    DB_PORT=3306 \
    DB_NAME=campus_market \
    DB_USERNAME=root \
    DB_PASSWORD= \
    JWT_SECRET=campus-market-docker-demo-secret-change-me \
    UPLOAD_PATH=/app/uploads
ENTRYPOINT ["java", "-jar", "/app.jar"]
