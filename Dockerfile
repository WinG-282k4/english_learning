# Stage 1: Build Frontend
FROM node:20-alpine AS frontend-builder
WORKDIR /app/fe
COPY FE/package*.json ./
RUN npm install
COPY FE/ ./
RUN npm run build

# Stage 2: Build Backend
FROM maven:3.9.8-eclipse-temurin-21 AS backend-builder
WORKDIR /app/be
COPY BE/pom.xml ./
RUN mvn dependency:go-offline -B
COPY BE/src ./src
COPY --from=frontend-builder /app/fe/dist ./src/main/resources/static
RUN mvn clean package -DskipTests

# Stage 3: Runtime
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=backend-builder /app/be/target/*.jar app.jar

ENV SERVER_PORT=8080
VOLUME /app/data
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
