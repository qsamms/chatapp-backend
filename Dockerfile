FROM maven:3.9.9-eclipse-temurin-23-alpine AS builder

WORKDIR /app

COPY pom.xml .
COPY src ./src

RUN mvn clean package -DskipTests

FROM openjdk:21-jdk

WORKDIR /app

COPY --from=builder /app/target/chatapp-backend-0.0.1-SNAPSHOT.jar .

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "chatapp-backend-0.0.1-SNAPSHOT.jar"]