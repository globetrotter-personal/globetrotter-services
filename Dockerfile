FROM amazoncorretto:17-alpine

# Install certificates for SSL
RUN apk add --no-cache ca-certificates

WORKDIR /app

COPY target/globetrotter-services-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
