FROM maven:3.8.6-eclipse-temurin-17 AS build
WORKDIR /app

# Copiamos archivos de Maven
COPY pom.xml .
COPY src ./src

# Construimos el JAR (sin tests)
RUN mvn clean package -DskipTests

# Imagen runtime
FROM eclipse-temurin:17-jre
COPY --from=build /app/target/*.jar gateway.jar

# Puerto que usará el gateway
EXPOSE 8080

# Comando para ejecutar
ENTRYPOINT ["java","-jar","/app/gateway.jar"]
