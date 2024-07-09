# Use an official Maven runtime as a parent image
FROM maven:3.8.4-openjdk-17-slim AS build
WORKDIR /app

# Copy the POM file to the container at /app
COPY pom.xml /app/

# Copy the entire project to the container at /app
COPY . /app/

# Build the JAR file
RUN mvn clean package
# Set the working directory in the container
FROM openjdk:17-jdk-slim
WORKDIR /app

# Install curl
RUN apt-get update && apt-get install -y curl

# Copy the Maven project's JAR file into the container
COPY --from=build /app/gateway/target/gateway-0.0.1-exec.jar .
COPY /addition/config/config.properties /app/config/

# Expose the port your application will run on (if applicable)
EXPOSE 8081

# Define the command to run your Java application
#CMD ["java", "-jar", "gateway-0.0.1-exec.jar", "--network", "lasso-network", "--db-user", "lasso", "--db-pass", "password", "--db-url", "mysql:3306/dev", "--keycloak", "http://keycloak:8080/realms/lasso-realm", "--logging", "DEBUG" ]
ENTRYPOINT ["java", "-jar", "gateway-0.0.1-exec.jar"]
#CMD ["--network", "lasso-network", "--db-user", "lasso", "--db-pass", "password", "--db-url", "mysql:3306/dev", "--keycloak", "http://keycloak:8080/realms/lasso-realm", "--logging", "DEBUG"]
#CMD ["sh", "-c", "telnet mysql 3306"]