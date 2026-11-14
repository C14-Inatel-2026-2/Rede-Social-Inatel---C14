# Rede-Social-Inatel-C14
# 💬 Mesão chat

O **Mesão chat** é uma rede social baseada em salas de bate-papo, desenvolvida em **Java** com comunicação em tempo real via **sockets**. Os usuários criam uma conta, fazem login e escolhem em quais chats querem entrar para conversar com outras pessoas conectadas ao mesmo tempo.

## 📌 Sobre o projeto

A aplicação segue o modelo cliente-servidor: um servidor central gerencia as conexões e distribui as mensagens para todos os participantes de cada chat, de forma contínua e instantânea. Os dados dos usuários ficam armazenados em um banco de dados, que controla o registro e a autenticação.

## ✨ Funcionalidades

* **Registro de usuários** com e-mail, nome e senha
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

├── ChatZoneFX/         # Interface gráfica JavaFX
│   ├── src/            # Código-fonte da interface
│   ├── Jenkinsfile     # Pipeline do front
│   └── pom.xml         # Configurações e dependências do front-end
│
├── database/           # Arquivo relacionado ao bd
├── jenkins/            # Configurações relacionadas ao Jenkins
│
├── src/                # Código-fonte do back-end

├── .env.exemplo        # Exemplo de variáveis de ambiente
├── .env.front.exemplo  # Exemplo de variáveis do front-end
├── .env.render.exemplo # Exemplo de variáveis do Render
│
├── docker-compose.yml  # Orquestração dos serviços
├── Dockerfile          # Configuração da imagem Docker
│
├── Jenkinsfile         # Pipeline do back-end
├── pom.xml             # Configurações e dependências do back-end
│
├── README.md           # Documentação do projeto
└── render.yaml         # Configuração de deploy no Render

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

### Pipeline do front (ChatZoneFX)

O cliente desktop tem uma pipeline própria, o job projeto-front, que usa o arquivo ChatZoneFX/Jenkinsfile:

Checkout SCM → Source → Build → Análise estática → Test → Package → Publish download page

A publicação é executada somente na branch integration e atualiza a página de download no GitHub Pages:
https://c14-inatel-2026-2.github.io/Rede-Social-Inatel---C14/

#### Estágios

- Checkout SCM: baixa o código-fonte do GitHub para execução da pipeline.
- Source: exibe a branch, o commit e o conteúdo da pasta ChatZoneFX.
- Build: compila o cliente junto com o código do servidor que ele usa (login, cadastro, troca de senha e conexão com o chat).
- Análise estática: executa Checkstyle e SpotBugs no código do cliente.
- Test: executa os testes do cliente em modo headless (JavaFX Monocle, sem interface gráfica), publica os relatórios JUnit e gera a cobertura JaCoCo.
- Package: gera o JAR e um pacote ZIP com as dependências, os scripts abrir-windows.bat e abrir-linux-macos.sh e as instruções de execução.
- Publish download page: publica o ZIP, o JAR e a página de download na branch gh-pages, servida pelo GitHub Pages, somente na integration.

O cliente é desktop (JavaFX), então não é publicado no Render: ele é distribuído pela página de download e se conecta ao servidor e ao banco configurados nas variáveis CHAT_SERVER_HOST, CHAT_SERVER_PORT_BASE, CHAT_SERVER_HTTP_URL, DB_URL, DB_USER e DB_PASSWORD.

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

## 🚀 Instalação e execução

O Mesão chat é composto por duas partes, que são distribuídas juntas no mesmo pacote:

- **Servidor:** banco de dados MySQL e servidor de chat, executados com Docker.
- **Cliente:** aplicativo desktop JavaFX (ChatZoneFX), usado para conversar.

### Pré-requisitos

- [Docker Desktop](https://www.docker.com/products/docker-desktop/) instalado e em execução
- [Java 17 ou superior](https://adoptium.net/)

### 1. Baixar o pacote

Acesse a página de download e clique em **Baixar pacote completo**:

https://c14-inatel-2026-2.github.io/Rede-Social-Inatel---C14/

Extraia o arquivo `ChatZoneFX.zip`. A pasta extraída terá esta estrutura:

```
ChatZoneFX/
├── ChatZoneFX.jar
├── abrir-windows.bat
├── abrir-linux-macos.sh
├── README.txt
├── lib/
├── lib-windows/
└── servidor/
    ├── docker-compose.yml
    ├── Dockerfile
    ├── LEIA-ME.txt
    ├── app.jar
    ├── lib/
    └── database/
```

### 2. Iniciar o servidor

Abra um terminal **dentro da pasta `servidor`** e execute:

```bash
docker compose up -d --build
```

Esse comando cria e inicia dois containers:

| Container | Função | Porta |
|---|---|---|
| `chatzone-servidor-mysql-1` | Banco de dados (usuários e senhas) | 3307 |
| `chatzone-servidor-servidor-1` | Lista de salas (HTTP) e salas de chat | 8089 e 3000 a 3005 |

Para confirmar que o servidor está no ar, acesse no navegador:

```
http://localhost:8089/rooms
```

A página deve exibir a lista das 6 salas disponíveis.

> Na primeira execução, o banco leva de 10 a 20 segundos para criar as tabelas.

### 3. Abrir o cliente

Volte para a pasta principal do pacote e execute:

- **Windows:** dê dois cliques em `abrir-windows.bat`
- **Linux e macOS:** siga a seção [Executar pelo código-fonte](#executar-pelo-código-fonte)

O cliente já vem configurado para se conectar ao servidor e ao banco iniciados no passo anterior.

### 4. Encerrar

Dentro da pasta `servidor`:

```bash
docker compose stop
```

Para remover os containers e **apagar todos os usuários cadastrados**:

```bash
docker compose down -v
```

### Executar pelo código-fonte

Alternativa para Linux, macOS ou para desenvolvimento. Requer também Git e Maven.

```bash
git clone https://github.com/C14-Inatel-2026-2/Rede-Social-Inatel---C14.git
cd Rede-Social-Inatel---C14/servidor-download
```

Inicie o servidor com Docker, como no passo 2, e depois execute o cliente:

```bash
export DB_USER=redesocial
export DB_PASSWORD=redesocial
cd ../ChatZoneFX
mvn javafx:run
```

### Problemas comuns

| Problema | Solução |
|---|---|
| `no configuration file provided: not found` | O comando foi executado fora da pasta `servidor`. Entre nela com `cd servidor`. |
| `port is already allocated` | Outro programa ou container está usando as portas 3307, 8089 ou 3000 a 3005. Encerre-o (veja com `docker ps`) e tente novamente. |
| Cadastro ou login falha logo após iniciar | O banco ainda está sendo criado. Aguarde alguns segundos e tente novamente. |
| Nenhuma sala aparece no lobby | Verifique se o servidor está no ar em `http://localhost:8089/rooms`. |
| `Java nao foi encontrado` | Instale o Java 17 ou superior e abra o cliente novamente. |

Para ver os logs do servidor, dentro da pasta `servidor`:

```bash
docker compose logs servidor
```

## 💡 Como usar

1. Na tela inicial, abra a aba **Cadastrar** e crie uma conta com nome de usuário, e-mail e senha
   (a senha precisa ter 8 ou mais caracteres, com letra maiúscula, letra minúscula, número e símbolo).
2. Entre com o e-mail e a senha na aba **Entrar**.
3. No lobby, escolha uma das salas disponíveis. Salas lotadas aparecem marcadas e não podem ser abertas.
4. No chat, digite a mensagem e pressione **Enter** ou clique em enviar.
   O topo da tela mostra quantos usuários estão online e quem são.
5. Use o botão de voltar para retornar ao lobby.
6. No perfil, é possível **trocar a senha** (informando a senha atual) ou **sair** da conta.

> Para testar a conversa entre dois usuários na mesma máquina, abra o cliente duas vezes e entre
> com contas diferentes na mesma sala.
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
| Parte dos Testes ChatController e LobbyController | Vários testes feitos a mão, porém com largo auxílio da i.a(claude) na configuração para classes do front como label. Mais testes foram requisitados para a i.a para uma maior cobertura |

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
8. Entregue o código completo da classe de testes, preservando os testes existentes e acrescentando apenas os necessários.

---------------------------------------
Relatório de I.A. - Nathan Bispo de Aguiar

## Uso de IA: Cadastro, Login, banco de dados e CI

### Ferramentas

- Claude: apoio direcionado para revisar o código e os testes do cadastro e do login, esclarecer dúvidas técnicas e diagnosticar problemas de configuração (Docker, Maven e Jenkins). O autor escreveu o código de produção e a maior parte dos testes, avaliou as sugestões e escolheu quais aplicar.

### Usos e resultados

1. Revisão de código e testes (Claude)
   Contexto: o autor enviou versões sucessivas das classes `Cadastro`, `Login`, `Usuario`, `CadastroDAO`, `LoginDAO` e `ConnectionDAO` e dos testes `CadastroTest`, `LoginTest` e `UsuarioTest`.
   Resultado: a IA apontou o retorno `null` que escondia o motivo das falhas, a confirmação falsa de sucesso quando o `INSERT` falhava, o `NullPointerException` com o banco fora do ar, o `connectToDb()` que sempre retornava `null` e o `LoginDAO` que descartava o nome consultado. Nos testes, apontou um DAO sem `@Mock`, comparação de objetos sem `equals`, stubs com argumentos diferentes da chamada e um teste que falhava pelo motivo errado, além de explicar o uso de `verify`. O autor substituiu os retornos `null` por exceções específicas, adicionou a verificação de conexão nula, passou a ler as credenciais do banco de variáveis de ambiente, fez o login retornar o nome e corrigiu os testes.

2. Banco de dados com Docker (Claude)
   Contexto: o autor solicitou auxílio na criação do banco.
   Resultado: a IA recomendou o banco local em container e escreveu um passo a passo para adicionar o MySQL ao `docker-compose.yml` do Jenkins. Ao seguir o tutorial, o autor encontrou o conflito da porta 3306 com o MySQL instalado na máquina, e a IA sugeriu expor o container na porta 3307.

3. Dependência vulnerável (Claude)
   Contexto: a IDE marcou CVEs no `mysql-connector-j`.
   Resultado: a IA sugeriu atualizar a versão. A primeira versão indicada também tinha CVEs; após o novo alerta da IDE, a IA pesquisou e indicou a linha de versões atual do conector.

4. Diagnóstico de CI (Claude)
   Contexto: o autor enviou erros do pipeline.
   Resultado: a IA identificou o commit que removeu o `@{argLine}` do `pom.xml`, o que impedia o JaCoCo de gerar o relatório de cobertura. O autor restaurou o `@{argLine}` com uma propriedade `argLine` vazia, para os testes continuarem rodando localmente.

5. Auxílio na escrita do `UsuarioDAOTest` (Claude)
   Contexto: o autor pediu auxílio para escrever os testes do `UsuarioDAO`.
   Resultado: a IA sugeriu testes para a classe. O autor selecionou os pertinentes, reorganizou e adaptou alguns deles, como acrescentar a verificação de fechamento da conexão nos casos de falha do `alterarSenha`.

### O que não foi feito por IA

- Implementações nas classes `Cadastro`, `Login`, `Usuario`, `CadastroDAO`, `LoginDAO` e `ConnectionDAO`
- Escrita dos testes do `CadastroTest`, `LoginTest` e `UsuarioTest`
- Escolha dos cenários pertinentes e organização final do `UsuarioDAOTest`
- Decisão de mover as validações do construtor de `Usuario` para o `Cadastro`
- Decisão de lançar exceções no back-end para que o front capture e exiba as mensagens
- Adição do campo `nome` ao banco e às classes
- Aplicação das correções, execução dos testes e validação no Jenkins

---------------------------------------
relatório de IA: Luis Eduardo Alves Marques

## Uso de IA: Back-end (usuario e banco) e testes

### Ferramentas
* Claude: apoio direcionado para revisar testes ou criar novos esclarecer dúvidas técnicas.
* Chatgpt: apoio direcionado para revisar testes ou criar novos esclarecer dúvidas técnicas.

### Usos e resultados

1. Criação de testes de LoginDAO e CadastroDAO (Claude)
   Contexto: foi pedido a criação de testes a partir do código de LoginDAO e CadastroDAO.
   Resultado: a IA criou uma lista de testes que cobriram boa parte dos códigos e que tiveram que ser feitas poucas mudanças para rodarem na suíte de testes ou retirar testes que não eram unitários.

2. Protocolo de criptografia (Claude)
   Contexto: o autor mandou o código de Cadastro e CadastroDAO e perguntou onde seria ideal ter a criptografia do BCrypto.
   Resultado: a IA respondeu que deveria parar de salvar o hash da senha direto em Usuario e só fazer o hash no momento que fosse adicionar no banco de dados.

3. Revisão e ampliação da suíte de testes (Claude)
   Contexto: o autor pediu a revisão dos testes existentes.
   Resultado: a IA ajudou a identificar casos ausentes ou que não validavam o comportamento pretendido.

4. Comandos no terminal (Chatgpt)
   Contexto: era pedido ao chat o comando ou os comandos que resolviam a necessidade do momento (exemplo: como deletar um commit específico).
   Resultado: a IA sempre mandava o comando a sequência que resolvia ou sequências possíveis para resolver.

5. Melhoria na mensagem de conclusão da pipeline (Chatgpt)
   Contexto: foi pedido como poderia acrescentar detalhes como autor do último commit e em qual branch.
   Resultado: a IA fez a lógica e adicionou as variáveis no  `description` da função do plugin Discord Notification.

### O que não foi feito por IA
* Classe Usuario
* Classe GerenciadorUsuariosBD
* Classe TrocarSenha
* Classe UsuarioDAO
* Regex da classe Login
* Pequenos ajustes nas classes CadastroDAO e LoginDAO
* Escolha de qual dependência de hash usar
* Na pipeline o refactor do Post Acitions e o Source
* Testes de: GerenciadorUsuariosBDTest, TrocarSenhaTest e alguns de LoginTest
* Remoção do teste de sistema, por exigência do projeto de apenas testes unitários
* Execução dos testes e correção dos erros de compilação e de build
---------------------------------------
relatório de IA: Luis Eduardo Alves Marques

## Uso de IA: Back-end (usuario e banco) e testes

### Ferramentas
* Claude: apoio direcionado para revisar testes ou criar novos esclarecer dúvidas técnicas.
* Chatgpt: apoio direcionado para revisar testes ou criar novos esclarecer dúvidas técnicas.

### Usos e resultados

1. Criação de testes de LoginDAO e CadastroDAO (Claude)
   Contexto: foi pedido a criação de testes a partir do código de LoginDAO e CadastroDAO.
   Resultado: a IA criou uma lista de testes que cobriram boa parte dos códigos e que tiveram que ser feitas poucas mudanças para rodarem na suíte de testes ou retirar testes que não eram unitários.

2. Protocolo de criptografia (Claude)
   Contexto: o autor mandou o código de Cadastro e CadastroDAO e perguntou onde seria ideal ter a criptografia do BCrypto.
   Resultado: a IA respondeu que deveria parar de salvar o hash da senha direto em Usuario e só fazer o hash no momento que fosse adicionar no banco de dados.

3. Revisão e ampliação da suíte de testes (Claude)
   Contexto: o autor pediu a revisão dos testes existentes.
   Resultado: a IA ajudou a identificar casos ausentes ou que não validavam o comportamento pretendido.

4. Comandos no terminal (Chatgpt)
   Contexto: era pedido ao chat o comando ou os comandos que resolviam a necessidade do momento (exemplo: como deletar um commit específico).
   Resultado: a IA sempre mandava o comando a sequência que resolvia ou sequências possíveis para resolver.

5. Melhoria na mensagem de conclusão da pipeline (Chatgpt)
   Contexto: foi pedido como poderia acrescentar detalhes como autor do último commit e em qual branch.
   Resultado: a IA fez a lógica e adicionou as variáveis no  `description` da função do plugin Discord Notification.

### O que não foi feito por IA
* Classe Usuario
* Classe GerenciadorUsuariosBD
* Classe TrocarSenha
* Classe UsuarioDAO
* Regex da classe Login
* Pequenos ajustes nas classes CadastroDAO e LoginDAO
* Escolha de qual dependência de hash usar
* Na pipeline o refactor do Post Acitions e o Source
* Testes de: GerenciadorUsuariosBDTest, TrocarSenhaTest e alguns de LoginTest
* Remoção do teste de sistema, por exigência do projeto de apenas testes unitários
* Execução dos testes e correção dos erros de compilação e de build
