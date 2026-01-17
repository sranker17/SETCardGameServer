FROM eclipse-temurin:17-jdk-alpine AS build
WORKDIR /app

# Install Maven
RUN apk add --no-cache maven

# Copy the project files and build the application
COPY . .
RUN mvn clean package -DskipTests

# Create the final image
FROM gcr.io/distroless/java17-debian11:nonroot

# Copy the built JAR file from the build stage
COPY --from=build /app/target/set-card-game-server.jar /app/app.jar

# Expose the application port
EXPOSE 8080

# Run the application
WORKDIR /app
ENTRYPOINT ["java", "-jar", "app.jar"]