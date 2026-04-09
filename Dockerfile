# ---------- BUILD ----------
FROM maven:3.9.6-eclipse-temurin-17 AS build

WORKDIR /app

# Copy only pom.xml first to use dependency caching
COPY pom.xml .

# Download dependencies (this will be cached)
RUN mvn -B -U dependency:resolve dependency:resolve-plugins

# Copy the entire repository code
COPY . .

# Compile the project, generating static files and jar file
RUN mvn clean install -DENV=docker-prod -DskipTests

# ---------- RUNTIME ----------
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Copy the build JAR
COPY --from=build /app/target/franchises-0.0.1-SNAPSHOT.jar app.jar

# security: run as non-root user
# Create a non-root user to run the application
RUN addgroup -S appgroup && adduser -S appuser -G appgroup
USER appuser

EXPOSE ${PORT_SERVER}

ENTRYPOINT ["java","-jar","app.jar"]