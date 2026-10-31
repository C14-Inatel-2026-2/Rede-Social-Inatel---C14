# Rede-Social-Inatel-C14

# 💬 Mesão Chat

O **Mesão Chat** é uma rede social baseada em salas de bate-papo, desenvolvida em **Java** com comunicação em tempo real via **Sockets**. Os usuários criam uma conta, fazem login e escolhem em quais chats querem entrar para conversar com outras pessoas conectadas ao mesmo tempo.

## 📌 Sobre o projeto

A aplicação segue o modelo cliente-servidor: um servidor central gerencia as conexões e distribui as mensagens para todos os participantes de cada chat, de forma contínua e instantânea. Os dados dos usuários ficam armazenados em um banco de dados, que controla o registro e a autenticação.

## ✨ Funcionalidades

* **Registro de usuários** com e-mail e senha
* **Login** com validação das credenciais no banco de dados
* **Menu de chats** com todas as salas disponíveis
* **Entrada em salas**, com ou sem outros usuários online no momento
* **Troca de mensagens em tempo real** entre os participantes
* **Múltiplos usuários conectados simultaneamente**

## 🛠️ Tecnologias utilizadas

* Java [21]
* Java Sockets (comunicação cliente-servidor)
* Banco de dados: MySQL
* Interface: JavaFX 
* Gerenciador de dependências: Maven

## 📁 Estrutura

├── front/   # interface JavaFX
├── src/     # servidor e lógica da aplicação
└── pom.xml

### Pré-requisitos
* JDK instalado
* SQL configurado e em execução

## Pipeline de CI/CD

O projeto usa Jenkins, executado por Docker Compose, para automatizar o fluxo:

`Source` → `Build` → `Test` → `Package` → `Deploy`

O deploy é executado somente para a branch `main` e dispara o Deploy Hook do Render.

### Configuração local

1. Copie `.env.example` para `.env`.
2. Preencha as credenciais do Jenkins, do GitHub e o `RENDER_DEPLOY_HOOK`.
3. Inicie o Jenkins:

```bash
docker compose up --build -d
```

O Jenkins ficará disponível em `http://localhost:8080`. O job multibranch é criado automaticamente a partir da configuração em `jenkins/casc.yaml`.

### Estágios

- `Source`: exibe a branch e o commit que serão processados.
- `Build`: compila o projeto sem executar os testes.
- `Test`: executa os testes, publica os relatórios JUnit e gera a cobertura JaCoCo.
- `Package`: gera o JAR e verifica se o artefato foi criado.
- `Deploy`: dispara o deploy no Render somente na `main`.

Nunca versione o arquivo `.env`; use `.env.example` como modelo.
