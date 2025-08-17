FROM maven:latest AS build

WORKDIR /app

COPY pom.xml /app/

COPY src /app/src

RUN mvn clean package -DskipTests

FROM openjdk:19-jdk-slim

RUN apt-get update \
 && apt-get install -y --no-install-recommends ffmpeg \
 && rm -rf /var/lib/apt/lists/*

WORKDIR /app

COPY --from=build /app/target/Scrtly-backend-app.jar /app/Scrtly-backend-app.jar

EXPOSE 5001

ENTRYPOINT ["java", "-jar", "/app/Scrtly-backend-app.jar"]
