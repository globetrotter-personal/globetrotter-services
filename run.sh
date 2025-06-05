#!/bin/bash

# Build the application
mvn clean package -DskipTests

# Run the application
java -jar target/globetrotter-services-0.0.1-SNAPSHOT.jar
