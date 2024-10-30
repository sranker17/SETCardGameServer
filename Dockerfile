FROM maven:3.8.3-openjdk-17-slim AS build
WORKDIR /app

# Copy only the necessary files to reduce the context size
COPY pom.xml .
COPY src ./src

# Build the application
RUN mvn clean install -DskipTests

# Use a minimal base image for the runtime
FROM openjdk:17-jdk-slim
WORKDIR /app

# Copy the built JAR file from the build stage
COPY --from=build /app/target/set-card-game-server.jar /app/app.jar

# Expose the application port
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]