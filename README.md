🛠️ Como Executar
🔹 Opção 1 – Rodar Localmente (Dev com H2)
mvn spring-boot:run -Dspring-boot.run.profiles=dev


Swagger:

http://localhost:8080/swagger-ui/index.html

🔹 Opção 2 – Rodar com Docker (Recomendado)
Build
docker compose build

Subir aplicação
docker compose up


Acessar:

http://localhost:8080/swagger-ui/index.html

🐳 Docker

O projeto possui:

Dockerfile multi-stage

docker-compose.yml com:

Aplicação

PostgreSQL


📦 Estrutura do Projeto

Arquitetura em camadas, separando responsabilidades:

Controller → Camada de entrada HTTP

Service → Regras de negócio

Repository → Persistência

Client → Integração externa

Exception → Tratamento centralizado