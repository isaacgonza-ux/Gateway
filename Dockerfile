FROM maven:3.8.6-eclipse-temurin-17 AS build
WORKDIR /app


COPY pom.xml .
COPY src ./src

# Construimos el JAR (sin tests)
RUN mvn clean package -DskipTests


FROM eclipse-temurin:17-jre

WORKDIR /app 
# Copiamos el jar y le damos un nombre estándar
COPY --from=build /app/target/*.jar gateway.jar


EXPOSE 8080

# Comando para ejecutar
ENTRYPOINT ["java","-jar","gateway.jar"]