# Use a base image with Maven and Java 17
FROM maven:3.8.4-openjdk-17-slim AS build

# Set the working directory
WORKDIR /build

# Copy the project's POM file
COPY pom.xml .

# Download dependencies
RUN mvn dependency:go-offline

# Copy the entire project
COPY . .

# Build the project
RUN mvn clean install

# Use a base image with Java 17
FROM openjdk:17-jdk-slim

# Set the working directory
WORKDIR /app

# Copy the built JAR file from the previous stage
COPY --from=build /build/target/backend-0.0.1.jar backend.jar

# Expose the port the app runs on
EXPOSE 8080

# Command to run the application
ENTRYPOINT ["java", "-jar", "backend.jar"]
