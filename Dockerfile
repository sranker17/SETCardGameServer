FROM maven:3.8.3-openjdk-17-slim AS build
WORKDIR /app

# Copy the project files and build the application
COPY . .
RUN mvn clean package -DskipTests

# Create a custom JRE with the necessary modules
FROM openjdk:17-slim AS jre-builder
RUN jlink --module-path /opt/java/openjdk/jmods \
          --add-modules java.base,java.logging,java.desktop \
          --output /custom-jre

# Create the final image
FROM debian:bullseye-slim
WORKDIR /app

# Copy the custom JRE from the jre-builder stage
COPY --from=jre-builder /custom-jre /opt/java/custom-jre

# Copy the built JAR file from the build stage
COPY --from=build /app/target/set-card-game-server.jar /app/app.jar

# Expose the application port
EXPOSE 8080

# Run the application using the custom JRE
ENTRYPOINT ["/opt/java/custom-jre/bin/java", "-jar", "app.jar"]