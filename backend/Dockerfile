# Stage 1: Build
FROM eclipse-temurin:25-jdk-alpine AS build

WORKDIR /app

COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
RUN chmod +x mvnw && ./mvnw dependency:go-offline --batch-mode

COPY src/ src/
RUN ./mvnw package -DskipTests --batch-mode --no-transfer-progress

# Stage 2: Runtime
FROM eclipse-temurin:25-jre-alpine

WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
