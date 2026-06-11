# 1. Addım: Build mərhələsi
FROM alpine:3.19 AS build
RUN apk add --no-cache openjdk17
COPY . /app
WORKDIR /app
RUN ./gradlew bootJar --no-daemon

# 2. Addım: Run mərhələsi
FROM alpine:3.19
RUN apk add --no-cache openjdk17
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]