# Etapa 1: Construir a aplicação utilizando Spring Boot
FROM openjdk:22-jdk-slim-bullseye as build

WORKDIR /app

# Copie o JAR da aplicação (se já foi gerado com o maven ou gradle)
COPY target/notificationapi-0.0.1-SNAPSHOT.jar notificationapi-0.0.1-SNAPSHOT.jar

# Execute o layertools para extrair as camadas
RUN java -Dspring-boot.jarmode=layertools -jar notificationapi-0.0.1-SNAPSHOT.jar extract --destination /extract/

# Etapa 2: Criar a imagem de produção
FROM bellsoft/liberica-openjdk-alpine:22 as production

WORKDIR /app

# Copie as camadas extraídas do build para a imagem final
COPY --from=build /extract/dependencies/ ./dependencies/
COPY --from=build /extract/spring-boot-loader/ ./spring-boot-loader/
COPY --from=build /extract/snapshot-dependencies/ ./snapshot-dependencies/
COPY --from=build /extract/application/ ./application/

# Defina o ponto de entrada para a execução da aplicação
ENTRYPOINT ["java",
            "-cp",
            "spring-boot-loader:dependencies/*:snapshot-dependencies/*:application",
            "org.springframework.boot.loader.JarLauncher"]
