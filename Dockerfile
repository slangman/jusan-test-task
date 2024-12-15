# Use a base image with JDK for building the app
FROM openjdk:17-jdk-slim as build

# Set the working directory in the container
WORKDIR /app

# Copy Gradle Wrapper files first (to leverage Docker layer caching)
COPY gradlew /app/
COPY gradle /app/gradle/
RUN chmod +x gradlew

# Copy Gradle configuration files
COPY build.gradle settings.gradle /app/

# Download dependencies to leverage Docker caching
RUN ./gradlew dependencies --no-daemon || true

# Copy the entire project source code
COPY . /app

# Build the application using Gradle Wrapper
RUN ./gradlew bootJar --no-daemon

# Use a lightweight JDK base image for running the app
FROM openjdk:17-jdk-slim

# Set the working directory for the application
WORKDIR /app

# Copy the JAR file from the build stage
COPY --from=build /app/build/libs/*.jar app.jar

# Expose the application port
EXPOSE 8080

# Command to run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
