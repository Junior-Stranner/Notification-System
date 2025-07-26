# Etapa 1: Build da aplicação com Maven e JDK 22
FROM maven:3.9.6-eclipse-temurin-22 AS build
WORKDIR /app

# Copia os arquivos do projeto
COPY . .

# Executa o build, ignorando os testes
RUN mvn clean package -DskipTests

# Etapa 2: Runtime usando Temurin JDK 22 com Alpine (mais leve)
FROM eclipse-temurin:22-jre-alpine
WORKDIR /app

# Copia o JAR gerado na etapa de build
COPY --from=build /app/target/*.jar app.jar

# Expõe a porta padrão do Spring Boot
EXPOSE 8080

# Comando para rodar o JAR
ENTRYPOINT ["java", "-jar", "app.jar"]

