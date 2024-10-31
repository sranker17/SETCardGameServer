FROM maven:3.8.3-openjdk-17-slim AS build
WORKDIR /app

# Install necessary tools and clean up cache
RUN apt-get update && apt-get install -y binutils && apt-get clean && rm -rf /var/lib/apt/lists/*

# Copy the project files and build the application
COPY . .
RUN mvn clean package -DskipTests && \
    jar xf target/set-card-game-server.jar && \
    jdeps --ignore-missing-deps -q \
        --recursive \
        --multi-release 17 \
        --print-module-deps \
        --class-path 'BOOT-INF/lib/*' \
        target/set-card-game-server.jar > deps.info && \
    jlink --add-modules $(cat deps.info) \
        --strip-debug \
        --compress 2 \
        --no-header-files \
        --no-man-pages \
        --output /myjre

# Create the final image
FROM debian:bullseye-slim
ENV JAVA_HOME /opt/java/custom-jre
ENV PATH $JAVA_HOME/bin:$PATH

# Copy the custom JRE from the build stage
COPY --from=build /myjre $JAVA_HOME

# Copy the built JAR file from the build stage
COPY --from=build /app/target/set-card-game-server.jar /app/app.jar

# Expose the application port
EXPOSE 8080

# Run the application using the custom JRE
WORKDIR /app
ENTRYPOINT ["java", "-jar", "app.jar"]