# STAGE 1: Build the application
# Use a full JDK image to build the application
FROM eclipse-temurin:17-jdk-focal AS build
WORKDIR /app

# Copy the Maven wrapper and pom.xml
COPY .mvn/ .mvn
COPY mvnw pom.xml ./

# Make the Maven wrapper executable
RUN chmod +x mvnw

# Download dependencies
RUN ./mvnw dependency:resolve

# Copy the source code and build the application
COPY src ./src
RUN ./mvnw package -DskipTests

# STAGE 2: Create the final, minimal runtime image
# Use a lightweight JRE-only image
FROM eclipse-temurin:17-jre-focal
WORKDIR /app

# Copy the built JAR from the 'build' stage
COPY --from=build /app/target/*.jar app.jar

# Expose the port your app runs on
EXPOSE 8080

# Command to run the application
# --- THIS IS THE FIX ---
# We are adding "-Dspring.profiles.active=docker" to the java command
ENTRYPOINT ["java", "-Dspring.profiles.active=docker", "-jar", "app.jar"]