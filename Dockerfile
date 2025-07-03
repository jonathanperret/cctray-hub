# syntax=docker/dockerfile:1.7-labs
FROM eclipse-temurin:21.0.2_13-jdk AS build
COPY --parents gradle/ src/ gradlew/ *.kts /src
WORKDIR /src
RUN ./gradlew build

FROM eclipse-temurin:21.0.2_13-jre AS prod
USER 1001
COPY --from=build /src/build/libs/cctray-hub.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
EXPOSE 8080
EXPOSE 8081
