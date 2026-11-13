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

Relatório da I.A - Gilberto Cezar Silveira Júnior

## Uso de IA — Front-end (JavaFX) e integração

### Ferramentas
* *Amazon Q Developer:* geração do front e integração login/cadastro
* *Claude:* testes de autenticação, ChatConfig e ChatController, apoio na depuração de erros da pipeline (Jenkins) e esclarecimento de dúvida sobre o tipo de serviço no Render

### Prompts e resultados

*1. Geração do front (Amazon Q Developer)*
Contexto: zip com o que já existia do projeto.
Prompt: "Gere arquivos de frontend utilizando o JavaFX com um visual moderno, é um
aplicativo de chats online com telas para cadastro e login, lobby de salas e o próprio
chat. O usuário possui email, nome e senha. Faça de uma maneira simples para integrar
as funcionalidades disponíveis no back end"
Um exemplo do que foi ignorado é que a i.a desenvolveu um novo regex, o que já havia sido feito no back end, então alteramos depois manualmente para excluir essas funções redundantes.
*AJUSTADO*

*2. Integração com o back-end (Amazon Q Developer)*
Antes da IA: a função de login no AuthController já estava feita à mão.
Prompt: "Analisando o que já foi implementado, como a questão do login, analise as
demais funções do back end e comece a implementá-las para o front end, levando em conta
o banco de dados e sua visualização para o usuário."
Resultado: a IA completou as demais funções (cadastro) e usou as exceptions que já
existiam, foi desenvolvido sem muitos erros já que essas funções já funcionavam no back end.
*ACEITO*

*3. Testes de autenticação (Claude)*
Contexto: enviamos nossos primeiros testes (ex.: loginValido_navegaParaLobby), os
slides de testes unitários e o repositório.
Prompt: "Preciso de testes unitarios para testar as funcionalidades de autentificação,
usando mockito em alguns e podendo utilizar injeção de dependencia. Siga os padrões de
testes unitarios dos slides. Os códigos devem seguir o padrão dos powerpoints e os que
já foram implementados, sem alterar o código. Devem ser apenas testes para o front e
suas funcionalidades"
Resultado: o Claude sugeriu um toolkit para testar o front e aplicamos a dependência JavaFX Monocle para testar o ambiente em headless, sem interface gráfica, isso foi aplicado manualmente.
Mesmo tendo sido requisitado utilizando apenas testes unitários e mock a i.a ainda desenvolveu alguns testes que foram corrigidos posteriormente por declarar e utilizar métodos de classe sem utilizar do mock. O que acabava gerando um teste de integração, que não era permitido. A solução foi mockar manualmente e desenvolver a classe Navigator para isso.
*AJUSTADO*

*4. Mensagens no ChatController (Claude)*
Antes da IA: método conectar() (versão inicial) e início do init (e-mail).
Prompt: "Observe o que já foi implementado do back end no front end e me auxilie em como
fazer o encaminhamento de mensagens entre usuários, visando principalmente o que já foi
feito no back. Veja como o método de conexão está sendo feito"
Resultado: o Claude devolveu ChatConfig, ChatController e LobbyController atualizados.
Além disso, no LobbyController o método buscarSalas teve que ser implementado manualmente, já que a i.a definiu toda a integração de salas no loadRooms e isso impossibilitava a utilização do mock. A i.a desenvolveu alguns trechos utilizando string ao invés da própria classe MensagemCliente que havia sido feita com esse propósito, outro erro corrigido manualmente.
*AJUSTADO*

### Quem fez o quê
https://github.com/C14-Inatel-2026-2/Rede-Social-Inatel---C14.git
| Parte | Origem |
|---|---|
| Visual (.fxml e o código em chatzone que aplica funções às telas) | IA (Amazon Q) e revisada pelo Claude |
| Função de login no AuthController (versão inicial) | À mão |
| Cadastro e demais funções do AuthController | IA, com ajustes à mão para integração (incluindo a remoção do regex redundante gerado pela IA) |
| Primeiros testes (loginValido_navegaParaLobby, loginEmailInvalido_naoNavega) | À mão |
| Demais testes de login e cadastro | IA (Claude), com ajustes à mão |
| conectar() do ChatController | À mão (versão inicial), depois modificado pela IA (Claude) |
| init (e-mail) | Feito à mão a princípio, assim como o início do conectar(). A IA (Claude) modificou o init ao alterar o código, por exemplo no envio da mensagem ao apertar Enter |
| Restante do ChatController e ChatConfig | IA (Claude), com ajustes à mão |
| Pipeline Jenkins | Feita sem IA a partir de material de base; a IA (Claude) só ajudou a resolver erros causados no deploy do front no Render |
| Testes ChatController e LobbyController | Vários testes feitos a mão, porém com largo auxílio da i.a(claude) na configuração para classes do front como label. A i.a também ajudou na ideia de alguns testes dessas classes e algumas aplicações |

### O que não foi feito por IA
* Função de login inicial no AuthController
* Primeiros testes (loginValido_navegaParaLobby, loginEmailInvalido_naoNavega)
* Versão inicial do conectar() e do init (e-mail) no ChatController
* Remoção do regex redundante gerado pela IA no front
* Dependência JavaFX Monocle para testes em headless
* Classe Navigator e mocks manuais que evitaram testes de integração
* Método buscarSalas no LobbyController
* Correção dos trechos que usavam string em vez da classe MensagemCliente
* Pipeline Jenkins (a IA só ajudou na depuração de erros)
* Base dos testes de LobbyControllerTest e ChatControllerTest

### Como usamos a IA
Individualmente. Sempre enviamos o código existente como contexto, pedimos que a IA
seguisse o padrão do que já estava feito e revisamos o resultado antes do commit.
A IA não foi usada na revisão de PRs. Antes do commit, o código era verificado
pelos testes da pipeline.

---------------------------------------
relatório Thiago Villela
## Uso de IA: Back-end (servidor de chat) e testes

### Ferramentas
* Claude: apoio direcionado para revisar testes, completar a partir do padrão de testes humanos anteriores, esclarecer dúvidas técnicas e realizar algumas refatorações a partir do comando do autor
* Copilot: apoio direcionado para revisar testes, esclarecer dúvidas técnicas e realizar uma refatoração específica. O autor definiu os objetivos, avaliou as sugestões e escolheu quais cenários eram pertinentes; a IA não foi usada para gerar todo o código nem para substituir a revisão do autor

### Usos e resultados

1. Revisão de código (Claude)
   Resultado: apontou problemas como o `HashSet` de escritores sem proteção de concorrência, exceção não tratada e falta de `finally` no `ServidorHandler`, campos `static` indevidos e os bugs da `Sala` (`addUsuario` com lógica invertida, `nome` não guardado e contador decrementado sem remoção).
   O autor aplicou as correções do `addUsuario` e do construtor da `Sala`.

2. Protocolo de mensagens (Claude)
   Contexto: o autor perguntou se o uso de texto pelo socket era uma opção fraca.
   Resultado: a IA discutiu comandos em texto pelo socket e sugeriu mensagens em JSON, uma por linha (Gson), com uma classe `Mensagem`.

3. Testes unitários (Claude)
   Contexto: o autor pediu testes que seguissem o padrão feito por ele em outros testes, classe por classe (`Servidor`, `GerenciadorSalas`, `Sala`, `ServidorSocket`, `ServidorHandler`, `ServidorFactoryImp`), e decidiu quais cenários eram pertinentes. Pediu, por exemplo, testes só do gerenciador, sem depender da `Sala`, e testes só com mocks.
   Resultado: a IA gerou versões mais complexas, que foram descartadas porque o autor definiu que os testes deveriam ser simples, sem comentários, sem salas auxiliares como "sala pequena" e com nomes de usuário fixos ("thiago", "beto"). O autor também removeu por conta própria certos testes que verificavam comportamentos muito próximos e, portanto, redundantes.

4. Revisão e ampliação da suíte de testes (Copilot)
   Contexto: o autor pediu a revisão dos testes existentes.
   Resultado: a IA ajudou a identificar casos ausentes ou que não validavam o comportamento pretendido. Em `TestServidorHandler`, isso incluiu cenários como sala lotada ou inexistente, dados inválidos na criação de sala, mensagem sem texto e fechamento do socket. Em `TestSala`, ajudou a adaptar os testes à API atual e a cobrir adição de usuário nulo, remoção por e-mail, e-mail desconhecido, listagem de usuários e liberação de vagas. Também auxiliou na preparação de mensagens JSON de teste, evitando a montagem manual repetitiva dos dados.

5. Revisão do isolamento dos testes (Copilot)
   o autor especificou a revisão dos testes para conferir isolamento e se existiam somente testes unitários.
   Resultado: a IA usou mocks para sockets e streams aonde estava faltando e removeu o caso que abria um `ServerSocket` real. Também ajudou a isolar testes que alteram `System.out` e `System.err` ou que compartilham estado estático, usando `@ResourceLock`. Um teste de sistema proposto pela IA não foi usado, porque o projeto exige apenas testes de unidade.

6. Refatoração do `ServidorHandler` (Copilot)
   Contexto: pedido do autor para separar o tratamento em métodos próprios.
   Resultado: a IA extraiu o tratamento de entrada em sala, criação de sala e envio de mensagens em métodos próprios.

7. Mock no programa, não só nos testes (Claude)
   Contexto: a ideia de ter o `GerenciadorUsuarios` simulado, com a implementação real a ser feita depois (já concluida), partiu do autor.
   Resultado: a IA apenas comparou as alternativas (interface com mock manual e fachada

### O que não foi feito por IA
* Arquitetura do chat (salas, usuários, capacidade, fluxo de entrada e saída)
* Integração final do código
* Padrão dos testes usado como base para a IA
* Correções do `addUsuario`, do construtor da `Sala` e escolha do `indexOf` no `removeUsuario`
* Ideia do `GerenciadorUsuarios` simulado em código temporário por um mock
* Criação de cada tipo de envio de mensagem que executa uma função (apenas separado em métodos pela IA)
* a estrutura das classes, servidor e protocolo.
* Remoção dos testes redundantes
* Remoção do teste de sistema, por exigência do projeto de apenas testes unitários
* Execução dos testes e correção dos erros de compilação e de build


### Como usamos a IA
Individualmente. Sempre enviamos o código existente como contexto, pedimos que a IA
seguisse o padrão do que já estava feito e revisamos o resultado antes do commit.
A IA não foi usada na revisão de PRs. Antes do commit, o código era verificado
pelos testes da pipeline.

---------------------------------------
relatório Leonardo Cruz Zanin

### Ferramentas
.Chatgpt

### Uso de IA na depuração e integração:
- ClassNotFoundException.
- Problemas com Maven e Surefire.
- Configuração de pacotes no IntelliJ.
- Problemas de integração entre cliente e servidor.
- Problemas com Mockito e dependências.
- Conflitos e operações Git.
- Integração do Jenkins com Docker.

#### Uso de IA com testes:
*Analise a classe `Cliente` e desenvolva testes unitários utilizando JUnit 5 e Mockito.
Requisitos:
1. Teste cada comportamento público da classe.
2. Utilize mocks para isolar dependências externas.
3. Mocke métodos estáticos de utilitários, como `IdentificarComandoChat`, utilizando `MockedStatic`.
4. Não teste novamente a lógica de outras classes dentro dos testes de `Cliente`.
5. Verifique chamadas, argumentos, retornos e exceções.
6. Siga o padrão de nomenclatura e organização dos testes existentes.
7. Evite testes redundantes e não altere a implementação original da classe.
   8.Entregue o código completo da classe de testes, preservando os testes existentes e acrescentando apenas os necessários.
