#!/bin/bash

# Derruba os containers existentes
docker-compose down

# Construção das imagens Docker
docker build -t backend-cvpronto-backend:latest ./cvbackend
docker build -t backend-cvpronto-pronto:latest ./cvpronto

# Levanta os containers com Docker Compose
docker-compose up --build --force-recreate --remove-orphans -d

# Define o host MySQL como argumento ou "mysql" por padrão
host="${1:-mysql}"

# Espera até que o MySQL esteja pronto (na porta 3306)
echo "Waiting for MySQL at $host..."
until nc -z "$host" 3306; do
  echo "MySQL is unavailable - waiting..."
  sleep 2
done

echo "MySQL is up - executing command"

# Inicia o aplicativo Java dentro do container específico
docker exec -it <backend-container-name> java -jar /Users/davidsonfelix/cvbackend/target/cvbackend-0.0.1-SNAPSHOT.jar