# Relátorio Fitness com AI
Este projeto é uma aplicação de eCommerce construída com uma arquitetura de microserviços utilizando Spring Boot, RabbitMQ, Eureka, Config Server, Keycloak para autenticação e uma aplicação frontend moderna integrada com OAuth 2.0 (PKCE Flow).
### Arquitetura de solução
![Captura de Tela (1432)](https://github.com/user-attachments/assets/ef6e3c57-5b6b-4fa5-89aa-e3f994036a5e)
###  Etapas de Desenvolvimento
🔧 Backend

User Service
* Configuração do serviço e banco de dados
* Criação dos endpoints REST
Activity Service
* Configuração do serviço e banco de dados
* Comunicação com User Service via REST e RabbitMQ

AI Service
* Geração de recomendações com base nas atividades
* Integração com API externa (Gemini)
Eureka Server
* Registro e descoberta de microserviços
* RabbitMQ
* Comunicação assíncrona entre serviços
Config Server
* Centralização das configurações dos serviços
API Gateway
* Roteamento de requisições para os serviços

Keycloak

* Gerenciamento de usuários e autenticação com OAuth 2.0 PKCE

💻 Frontend

* Setup da aplicação
* Autenticação com OAuth 2.0 (PKCE)
* Integração com Redux Store
* Formulário de atividades e dashboard
* Página de detalhes da atividade
* Integração com APIs do backend

### Ferramentas 
* Java 17+
* Maven 
* Node.js (para frontend)
* RabbitMQ
* Keycloak
* PostgreSQL
* Docker 

