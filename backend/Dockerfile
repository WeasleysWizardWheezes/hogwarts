# Stage 1: Build
FROM eclipse-temurin:25-jdk-alpine AS build

WORKDIR /app

# Copy build files
COPY backend/.mvn/ .mvn/
COPY backend/mvnw backend/pom.xml ./
COPY openapi/ /openapi/

# Download dependencies (openapi spec is available for the generator plugin)
RUN chmod +x mvnw && ./mvnw dependency:go-offline --batch-mode --no-transfer-progress

# Copy source and build
COPY backend/src/ src/
COPY backend/config/ config/
RUN ./mvnw package -DskipTests --batch-mode --no-transfer-progress

# Stage 2: Runtime
FROM eclipse-temurin:25-jre-alpine

WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
