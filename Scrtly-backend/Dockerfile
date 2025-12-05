FROM eclipse-temurin:19-jdk

WORKDIR /app

COPY target/Scrtly-backend-0.0.1-SNAPSHOT.jar app.jar

ENV SPRING_PROFILES_ACTIVE=docker

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
