FROM maven:3.9.9-eclipse-temurin-23-alpine

WORKDIR /app

COPY . .

RUN mvn clean package -DskipTests

COPY target/chatapp-backend-0.0.1-SNAPSHOT.jar chatapp-backend-0.0.1-SNAPSHOT.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "chatapp-backend-0.0.1-SNAPSHOT.jar"]