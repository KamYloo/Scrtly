# Używamy oficjalnego obrazu OpenJDK 19 jako bazy
FROM eclipse-temurin:19-jdk

# Ustawiamy katalog roboczy w kontenerze
WORKDIR /app

# Kopiujemy plik JAR do kontenera
COPY target/Scrtly-backend-0.0.1-SNAPSHOT.jar app.jar

# Ustawiamy zmienną środowiskową dla Spring Boot
ENV SPRING_PROFILES_ACTIVE=docker

# Otwieramy port, na którym działa aplikacja
EXPOSE 8080

# Uruchamiamy aplikację Spring Boot
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
