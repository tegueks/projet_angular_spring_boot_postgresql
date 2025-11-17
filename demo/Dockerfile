# Etape 1: Build avec maven multi stage

FROM maven:3.9.6-eclipse-temurin-21 AS build 
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# Étape 2 : Image d'exécution (légère)

FROM eclipse-temurin:21-jdk
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
# Healthcheck against Spring actuator
HEALTHCHECK --interval=30s --timeout=5s --start-period=20s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1
  
ENTRYPOINT ["java", "-jar", "app.jar"]