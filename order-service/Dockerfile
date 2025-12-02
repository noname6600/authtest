# ============================
# Stage 1: Build (Gradle)
# ============================
FROM gradle:8.8-jdk21 AS build

WORKDIR /home/gradle/project

COPY . .

RUN gradle clean bootJar -x test

# ============================
# Stage 2: Run
# ============================
FROM eclipse-temurin:21-jre

WORKDIR /app

COPY --from=build /home/gradle/project/build/libs/*.jar app.jar

EXPOSE 8082

ENTRYPOINT ["java", "-jar", "app.jar"]
