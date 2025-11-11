# ----- Build Stage -----
FROM maven:3.9.6-eclipse-temurin-21 AS builder

# Diretório de trabalho
WORKDIR /app

# Copia apenas o pom.xml para baixar dependências (cache eficiente)
COPY notificationapi/pom.xml .

# Baixa dependências offline para agilizar builds futuros
RUN mvn dependency:go-offline

# Copia o código fonte
COPY notificationapi/src ./src

# Compila o projeto e gera o jar
RUN mvn clean package -DskipTests

# ----- Runtime Stage -----
FROM eclipse-temurin:21-jre

# Diretório de trabalho
WORKDIR /app

# Copia o jar do build stage
COPY --from=builder /app/target/*.jar app.jar

# Expõe a porta da aplicação
EXPOSE 8080

# Comando para rodar a aplicação
ENTRYPOINT ["java", "-jar", "app.jar"]
