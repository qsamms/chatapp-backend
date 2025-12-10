FROM maven:3.9.9-eclipse-temurin-23-alpine AS builder

WORKDIR /app

COPY pom.xml .

RUN mvn dependency:go-offline -B

COPY src ./src

RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jdk-jammy

RUN useradd -ms /bin/bash chat

RUN apt-get update && apt-get install -y ffmpeg vim-tiny

COPY ./conf/docker_entrypoint.sh /
RUN chown -R chat:chat /docker_entrypoint.sh
RUN chmod +x /docker_entrypoint.sh

WORKDIR /app

RUN mkdir -p /app/uploads /app/videos && chown -R chat:chat /app
RUN chown chat:chat /var/log/

USER chat

COPY --from=builder /app/target/chatapp-backend-0.0.1-SNAPSHOT.jar .

EXPOSE 8080

ENTRYPOINT /docker_entrypoint.sh
