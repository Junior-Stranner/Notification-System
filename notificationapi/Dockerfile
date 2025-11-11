# ----- Build -----
FROM maven:3.9.6-eclipse-temurin-21 AS builder
WORKDIR /app

# Copia pom.xml e baixa dependências
COPY pom.xml .
RUN mvn dependency:go-offline

# Copia o código fonte
COPY src ./src

# Compila o projeto e gera o jar
RUN mvn clean package -DskipTests

# ----- Runtime -----
FROM eclipse-temurin:21-jre
WORKDIR /app

# Copia o jar gerado pelo builder
COPY --from=builder /app/target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]
