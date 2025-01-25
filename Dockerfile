FROM maven:3.8.6-eclipse-temurin-17 AS build
WORKDIR /build/
COPY pom.xml /build/

COPY src /build/src/
#COPY settings.xml /build/
RUN mvn -B -f ./pom.xml package -DskipTests

# Final stage
FROM eclipse-temurin:17-jdk-jammy AS final_stage
WORKDIR /app
#COPY config/* /app/config/
COPY src/main/resources/application.properties .

COPY --from=build /build/target/helpdesk-1*.jar /app/helpdesk-1.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "helpdesk-1.jar"]