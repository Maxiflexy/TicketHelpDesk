FROM eclipse-temurin:17

LABEL mentainer="onyemax247@gmail.com"

WORKDIR /app
COPY target/helpdesk-1.jar /app/spring-boot-helpdesk.jar

ENTRYPOINT ["java", "-jar", "spring-boot-helpdesk.jar"]