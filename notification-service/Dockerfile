# ===== STAGE 1: build =====
FROM maven:3.9.6-eclipse-temurin-17 AS builder

WORKDIR /app

ENV MAVEN_CONFIG=/root/.m2
ENV MAVEN_OPTS="-Xmx512m"

COPY pom.xml .
COPY .mvn .mvn
COPY mvnw .

RUN ./mvnw -B dependency:go-offline

COPY . .

RUN ./mvnw -B package -DskipTests

# ===== STAGE 2: runtime =====
FROM eclipse-temurin:17-jdk-alpine

WORKDIR /app

COPY --from=builder /app/target/notification-service-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8090

ENTRYPOINT ["java", "-jar", "app.jar"]
