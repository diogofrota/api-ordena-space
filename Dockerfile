FROM maven:3.9.9-eclipse-temurin-17 AS build

WORKDIR /app

COPY pom.xml mvnw ./
COPY .mvn .mvn
RUN chmod +x mvnw

COPY src src

RUN ./mvnw -DskipTests package dependency:copy-dependencies

FROM eclipse-temurin:17-jre

WORKDIR /app

COPY --from=build /app/target/api-ordena-space-1.0.0.jar /app/app.jar
COPY --from=build /app/target/dependency /app/lib

CMD ["sh", "-c", "java -cp /app/app.jar:/app/lib/* br.com.apiordenaspace.ApiOrdenaSpaceApplication"]
