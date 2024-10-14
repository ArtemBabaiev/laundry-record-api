FROM maven:3.9.7-eclipse-temurin-21-alpine AS build

WORKDIR /app

COPY . .

RUN mvn --batch-mode -DskipTests clean package

FROM eclipse-temurin:21-jre-alpine

COPY --from=build /app/target/*.jar /opt/laudry-api.jar

ENTRYPOINT ["java", "-jar", "/opt/laudry-api.jar"]
