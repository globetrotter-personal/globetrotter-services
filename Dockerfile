# Build stage
FROM maven:3.8-openjdk-17-slim as build
WORKDIR /build
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Run stage
FROM amazoncorretto:17-alpine

# Install certificates for SSL
RUN apk add --no-cache ca-certificates

WORKDIR /app

# Copy the JAR from the build stage
COPY --from=build /build/target/globetrotter-services-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]