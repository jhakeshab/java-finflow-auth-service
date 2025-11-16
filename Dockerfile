# ============================================
# Dockerfile
# ============================================
FROM maven:3.9.6-eclipse-temurin-17 AS builder
WORKDIR /build
COPY . .
RUN mvn clean package -DskipTests

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=builder /build/target/auth-service-1.0.0.jar app.jar
RUN mkdir -p logs
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]