# Use official OpenJDK 21 image
FROM openjdk:21-jdk

# Set working directory inside container
WORKDIR /app

# Copy the built jar
COPY target/crypto-service.jar app.jar

# Expose Spring Boot default port
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
