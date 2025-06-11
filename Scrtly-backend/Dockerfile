FROM maven:latest AS build

# Set the working directory
WORKDIR /app

# Copy the pom.xml file
COPY pom.xml /app/

# Copy the source code
COPY src /app/src

# Build the application
RUN mvn clean package -DskipTests

# Stage 2: Run the application
FROM openjdk:19-jdk

RUN apt-get update \
    && apt-get install -y ffmpeg \
    && rm -rf /var/lib/apt/lists/*

# Set the working directory
WORKDIR /app

# Copy the built jar file from the build stage (the name comes from pom.xml)
COPY --from=build /app/target/Scrtly-backend-app.jar /app/Scrtly-backend-app.jar

# Expose the port the app runs on
EXPOSE 5001

# Command to run the application
ENTRYPOINT ["java", "-jar", "/app/Scrtly-backend-app.jar"]
