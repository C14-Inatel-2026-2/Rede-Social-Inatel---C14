# Rede-Social-Inatel-C14
# 💬 Mesão chat

O **Mesão chat** é uma rede social baseada em salas de bate-papo, desenvolvida em **Java** com comunicação em tempo real via **sockets**. Os usuários criam uma conta, fazem login e escolhem em quais chats querem entrar para conversar com outras pessoas conectadas ao mesmo tempo.

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
* Java sockets (comunicação cliente-servidor)
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

## Render e o front JavaFX

O diretório `ChatZoneFX` é um cliente desktop JavaFX. Ele não é um front-end web
e não pode ser publicado como Render Web Service. A pipeline `projeto-front`
compila, testa e arquiva o JAR do cliente no Jenkins.

O arquivo `render.yaml` configura o serviço HTTP Docker da raiz (`Main`), que é
o componente compatível com um Web Service do Render. Cadastre `DB_URL`,
`DB_USER` e `DB_PASSWORD` no painel do Render. O chat TCP atual ainda exige uma
arquitetura diferente (backend HTTP/WebSocket e cliente usando hostname público),
pois o Render não fornece seis portas TCP públicas para o modelo atual.

As variáveis necessárias estão documentadas em `.env.render.exemplo`. O Render
injeta `PORT` automaticamente; não é necessário cadastrá-la manualmente.

## Pipeline de CI/CD

O projeto usa Jenkins, executado por Docker Compose, para automatizar o fluxo:

`Checkout SCM` → `Source` → `Build` → `Análise estática` → `Test` → `Package` → `Deploy` → `Post Actions`

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

- `Checkout SCM`: baixa o código-fonte do GitHub para execução da pipeline.
- `Source`: exibe a branch e o commit que serão processados.
- `Build`: compila o projeto sem executar os testes.
- `Análise estática`: identifica possíveis erros e problemas no código.
- `Test`: executa os testes, publica os relatórios JUnit e gera a cobertura JaCoCo.
- `Package`: gera o JAR e verifica se o artefato foi criado.
- `Deploy`: dispara o deploy no Render somente na `main`.
- `Post Actions`: envia no discord o resultado da pipeline, qual a branch onde foi feita e quem deu o último commit.

Nunca versione o arquivo `.env`; use `.env.example` como modelo.

## Requisitos do Sistema

### Requisitos Funcionais

Os requisitos funcionais descrevem as funcionalidades que o Mesão chat deverá oferecer aos seus usuários.

| ID | Requisito | Descrição |
|---|---|---|
| RF01 | Cadastro de usuário | O sistema deverá permitir o cadastro de novos usuários por meio de e-mail e senha. |
| RF02 | Autenticação | O sistema deverá permitir que usuários cadastrados realizem login utilizando suas credenciais. |
| RF03 | Redefinição de senha | O usuário deverá conseguir alterar sua senha, desde que informe a senha atual e atenda aos critérios de segurança. |
| RF04 | Visualização de salas | O sistema deverá apresentar uma lista com as salas de conversa disponíveis. |
| RF05 | Criação de salas | O sistema deverá permitir a criação de novas salas de bate-papo. |
| RF06 | Entrada em salas | O usuário deverá conseguir entrar em uma sala que possua vagas disponíveis. |
| RF07 | Limite de participantes | O sistema deverá controlar a quantidade de participantes por sala e impedir novas entradas quando a capacidade máxima for atingida. |
| RF08 | Envio e recebimento de mensagens | O sistema deverá permitir a troca de mensagens entre usuários conectados à mesma sala. |
| RF09 | Visualização de senha | O usuário deverá ter a opção de visualizar ou ocultar a senha digitada na tela de login. |
| RF10 | Notificações de participantes | O sistema deverá informar aos participantes quando um usuário entrar ou sair da sala. 

### Requisitos Não Funcionais

Os requisitos não funcionais estabelecem características de qualidade, segurança e funcionamento esperadas para a aplicação.

| ID | Requisito | Descrição |
|---|---|---|
| RNF01 | Desempenho | As mensagens deverão ser transmitidas em tempo real, buscando minimizar atrasos na comunicação. |
| RNF02 | Segurança | As credenciais dos usuários deverão ser protegidas, e o sistema não deverá armazenar senhas em texto puro. |
| RNF03 | Usabilidade | A interface deverá ser simples e intuitiva, permitindo que os usuários naveguem entre as salas e utilizem o chat com facilidade. |
| RNF04 | Confiabilidade | O sistema deverá tratar falhas de comunicação sem comprometer o funcionamento dos demais usuários conectados. |
| RNF05 | Concorrência | O servidor deverá permitir múltiplos usuários conectados simultaneamente, respeitando os limites de cada sala. |
| RNF06 | Manutenibilidade | O código deverá manter uma organização que facilite correções, testes e implementação de novas funcionalidades. |
| RNF07 | Compatibilidade | A aplicação deverá funcionar em ambientes compatíveis com Java 21 e JavaFX, considerando as dependências necessárias. |
| RNF08 | Integridade dos dados | As informações de cadastro e autenticação deverão ser mantidas de maneira consistente no banco de dados. |

