FROM eclipse-temurin:17-jre

WORKDIR /app

COPY target/*.jar integracion-springboot.jar

EXPOSE 8080

ENTRYPOINT ["java","-jar","integracion-springboot.jar"]