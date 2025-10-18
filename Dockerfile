FROM eclipse-temurin:21-jdk AS builder

# Set working directory inside the container
WORKDIR /app

# Copy only the files needed for the build
COPY pom.xml mvnw ./
COPY .mvn .mvn
COPY src src
COPY jars jars

# Install jNBT into the local Maven repository
RUN ./mvnw install:install-file \
  -Dfile=jars/jNBT-1.6.0.jar \
  -DgroupId=net.blackcat64 \
  -DartifactId=jnbt \
  -Dversion=1.6.0 \
  -Dpackaging=jar

# Build the application
RUN ./mvnw clean package -DskipTests

# Use JDK version 21
FROM eclipse-temurin:21-jdk-jammy

WORKDIR /app

# Copy the built JAR from the previous stage
COPY --from=builder /app/target/*.jar app.jar

# Port to run the site on
EXPOSE 8080

# Run command 'java -jar <jarfile>' to run the spring boot app
ENTRYPOINT ["java", "-jar", "app.jar"]
