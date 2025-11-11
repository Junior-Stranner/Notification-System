 Use uma imagem base com Java (escolha a versão adequada)

FROM openjdk:17-jdk-slim
# Defina o diretório de trabalho dentro do container

WORKDIR /app
# Copie o arquivo JAR construído (certifique-se de ajustar o nome)

COPY target/*.jar app.jar
# Exponha a porta que sua aplicação Spring Boot utiliza (geralmente 8080)

EXPOSE 8080
# Comando para executar a aplicação

ENTRYPOINT ["java", "-jar", "app.jar"]