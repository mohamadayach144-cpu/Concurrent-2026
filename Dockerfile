# --- Stage 1: Build Stage ---
FROM maven:3.9.9-eclipse-temurin-23 AS build
WORKDIR /app

# Cache Maven dependencies
COPY pom.xml .
RUN mvn dependency:go-offline -B || mvn dependency:resolve

# Copy sources and build the executable fat JAR
COPY src ./src
RUN mvn clean package -DskipTests

# --- Stage 2: Runtime Stage ---
FROM eclipse-temurin:23-jre
WORKDIR /app

# FIX HERE: Copy the jar to the root folder (/) instead of (/app) so the volume mount doesn't hide it
COPY --from=build /app/target/MonteCarloPiEstimator-1.0-SNAPSHOT-jar-with-dependencies.jar /app.jar

# Run JVM in headless mode pointing to the root jar
ENTRYPOINT ["java", "-Djava.awt.headless=true", "-jar", "/app.jar"]