FROM mcr.microsoft.com/openjdk/jdk:21-ubuntu

WORKDIR /app

COPY gradlew build.gradle settings.gradle ./
COPY gradle ./gradle
COPY src ./src

RUN apt-get update && apt-get install -y dos2unix \
    && dos2unix gradlew \
    && chmod +x gradlew

# Build fat jar bằng bootJar, bỏ test
RUN ./gradlew clean bootJar -x test

# Copy jar fat jar và rename
RUN for f in build/libs/*.jar; do cp "$f" basic.jar; break; done

EXPOSE 8082
CMD ["java", "-jar", "basic.jar"]
