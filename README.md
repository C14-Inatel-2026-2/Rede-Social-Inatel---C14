# Rede-Social-Inatel-C14

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
