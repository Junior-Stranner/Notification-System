# Etapa 1: Build da aplicação com Maven + JDK 21
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app

# Copia projeto
COPY . .

# Gera o .jar
RUN mvn clean package -DskipTests


# Etapa 2: Runtime (somente JDK 21)
FROM eclipse-temurin:21-jdk
WORKDIR /app

# Copia o jar compilado
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
