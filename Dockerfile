# Stage 1: Build Stage
FROM maven:3.9.9-amazoncorretto-17-alpine AS builder
WORKDIR /app

# Cache dependencies for faster builds
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Install necessary tools for jlink
RUN apk add --no-cache binutils

# Copy source code and build the application
COPY src ./src
RUN mvn clean package -DskipTests

# Create a custom Java runtime using jlink
RUN $JAVA_HOME/bin/jlink \
    --module-path $JAVA_HOME/jmods \
    --add-modules java.base,java.desktop,java.logging,java.sql,java.naming,java.management,java.security.jgss,java.instrument,jdk.unsupported \
    --output /custom-java-runtime \
    --strip-debug \
    --compress=2 \
    --no-header-files \
    --no-man-pages

# Stage 2: Runtime Stage
FROM alpine:3.21
WORKDIR /app

# Update apk and install necessary packages (e.g., libc compatibility for Java if needed)
RUN apk update && apk upgrade && apk add --no-cache libc6-compat

# Copy the custom Java runtime and application
COPY --from=builder /custom-java-runtime /custom-java-runtime
COPY --from=builder /app/target/drinkwater-api.jar ./drinkwater-api.jar

# Expose port for the application
EXPOSE 8081

# Define the entry point for the container
ENTRYPOINT ["/custom-java-runtime/bin/java", "-jar", "drinkwater-api.jar"]
