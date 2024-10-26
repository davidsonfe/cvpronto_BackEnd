# Dockerfile do Back-end
FROM eclipse-temurin:17-jdk AS build

# Copie os arquivos do projeto para a imagem
COPY . .

# Execute o Maven para compilar o projeto, ignorando os testes
RUN ./mvnw clean install -DskipTests

# Defina o ponto de entrada para executar a aplicação
ENTRYPOINT ["java", "-jar", "target/cvbackend-0.0.1-SNAPSHOT.jar"]
EXPOSE 8080
