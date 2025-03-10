# Stage 1: Build the Spring Boot application
FROM maven:3.9.9-amazoncorretto-23 AS build

# Set the working directory
WORKDIR /app

# Copy Maven files first (helps cache dependencies)
COPY ./pom.xml /app
COPY ./src /app/src

# Copy the application source code
RUN mvn clean package -Dmaven.test.skip=true

FROM amazoncorretto:23

WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]