# Build stage: compile backend + frontend into one executable JAR
FROM maven:3.9.8-eclipse-temurin-17 AS builder
WORKDIR /app

COPY pom.xml mvnw.cmd ./
COPY .mvn .mvn
COPY src src
COPY frontend frontend

RUN mvn -B -DskipTests clean package

# Runtime stage: lightweight JRE image
FROM eclipse-temurin:17-jre
WORKDIR /opt/skillswap

COPY --from=builder /app/target/skillswap-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]

