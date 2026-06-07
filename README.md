# API ORDENA SPACE

API REST em Java 17 com Maven, HTTP server embutido do JDK e JDBC puro para simular telemetria GPS de viaturas e fornecer a ultima posicao por `tabletSatelital` para consumo do sistema ORDENA SPACE.

## Stack

- Java 17
- Maven
- HTTP Server embutido do JDK (`com.sun.net.httpserver.HttpServer`)
- JDBC puro
- Jackson
- H2 Database
- Oracle Database

## Estrutura do projeto

```text
src/main/java/br/com/apiordenaspace
├── ApiOrdenaSpaceApplication.java
├── config
│   ├── AppConfig.java
│   ├── ConnectionFactory.java
│   └── DatabaseInitializer.java
├── controller
│   ├── OpenApiHandler.java
│   ├── SwaggerUiHandler.java
│   └── TelemetriaHandler.java
├── dto
│   ├── ErrorResponse.java
│   └── GpsPosicaoResponse.java
├── entity
│   └── GpsPosicao.java
├── exception
│   ├── BadRequestException.java
│   └── TabletSatelitalNotFoundException.java
├── http
│   ├── ApiServer.java
│   └── HttpJsonResponse.java
├── repository
│   └── GpsPosicaoRepository.java
└── service
    └── GpsPosicaoService.java

src/main/resources
├── application-example.yml
├── data.sql
├── db/oracle
├── public
│   ├── openapi.json
│   └── swagger-ui.html
└── schema.sql
```

Arquivos de deploy:

```text
Dockerfile
.env.example
.env.local.example
```

## Como rodar localmente

### Requisitos

- Java 17
- Maven 3.9+ ou usar `./mvnw`

### Subir com banco H2 local

```bash
./mvnw exec:java
```

A API sobe em `http://localhost:8080`.

O banco H2 em arquivo e inicializado automaticamente com os scripts:
- `src/main/resources/schema.sql`
- `src/main/resources/data.sql`

Arquivo fisico do banco H2:
- `./data/ordena_space_db.mv.db`

## Subir com Oracle usando variaveis de ambiente

Defina as variaveis abaixo na IDE ou no terminal:

```text
APP_PROFILE=oracle
ORACLE_DB_URL=jdbc:oracle:thin:@//HOST:PORT/SERVICE_NAME
ORACLE_DB_USERNAME=SEU_USUARIO
ORACLE_DB_PASSWORD=SUA_SENHA
PORT=8080
```

Exemplo:

```bash
export APP_PROFILE=oracle
export ORACLE_DB_URL=jdbc:oracle:thin:@//localhost:1521/XEPDB1
export ORACLE_DB_USERNAME=seu_usuario
export ORACLE_DB_PASSWORD=sua_senha
export PORT=8080
./mvnw exec:java
```

Observacao:
- as credenciais ficam fora do repositorio
- voce tambem pode usar um arquivo local `.env.local`, que ja esta ignorado no Git
- no perfil `oracle`, a aplicacao nao executa `schema.sql` nem `data.sql`
- voce deve criar a tabela e os inserts no Oracle antes de subir a API

### Usando `.env.local`

1. Copie `.env.local.example` para `.env.local`
2. Preencha com sua URL Oracle completa, usuario e senha
3. Rode:

```bash
./mvnw exec:java
```

## Deploy no Railway

Sim, voce consegue subir essa API no Railway.

O projeto agora possui um `Dockerfile` na raiz, entao o Railway pode construir e executar a aplicacao a partir dele.

Fluxo recomendado:

1. Suba o codigo para o GitHub
2. No Railway, crie um novo projeto
3. Escolha `Deploy from GitHub Repo`
4. Selecione este repositorio
5. No service criado, adicione as variaveis:

```text
APP_PROFILE=oracle
ORACLE_DB_URL=jdbc:oracle:thin:@//HOST:1521/SERVICE_NAME
ORACLE_DB_USERNAME=SEU_USUARIO
ORACLE_DB_PASSWORD=SUA_SENHA
PORT=8080
```

6. Faça o deploy

Depois disso, o Railway vai publicar uma URL publica para a sua API.

### Ponto critico do Oracle externo

O Railway consegue fazer conexoes de saida para destinos externos pela internet, conforme a documentacao oficial de outbound networking.

Mas a sua API so vai conseguir consultar o Oracle se:

- o host Oracle estiver acessivel publicamente
- a porta do Oracle estiver liberada
- o banco aceitar conexoes vindas da infraestrutura do Railway

Se o Oracle exigir whitelist de IP, o Railway documenta `Static Outbound IPs` para clientes Pro.

### Observacao sobre variaveis

O Railway documenta que as variaveis podem ser adicionadas na aba `Variables`, inclusive via `RAW Editor`, e que arquivos `.env` na raiz podem ser detectados como sugestao de importacao.

Por isso foi criado o arquivo `.env.example` na raiz.

## Endpoint principal

```http
GET /api/telemetria/tablets/{tabletSatelital}/ultima-posicao
Accept: application/json
```

Exemplo:

```bash
curl -s http://localhost:8080/api/telemetria/tablets/80001/ultima-posicao
```

### Resposta 200

```json
{
  "tabletSatelital": "80001",
  "latitude": -23.561684,
  "longitude": -46.625378,
  "capturadoEm": "2026-06-07T18:40:00Z",
  "observacao": "simulada dentro da area"
}
```

### Resposta 404

```json
{
  "error": "Tablet satelital nao encontrado."
}
```

## OpenAPI

- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/openapi.json`

Observacao:
- a pagina `swagger-ui.html` usa o Swagger UI via CDN

## Banco e seed

- A tabela `gps_posicoes` guarda apenas a ultima posicao de cada tablet.
- O seed H2 contem 6 tablets: `80001` a `80006`.
- Metade dos registros estao com observacao de “dentro da area” e metade “fora da area”.

## Scripts Oracle

- Criacao da tabela: `src/main/resources/db/oracle/01-create-gps_posicoes.sql`
- Inserts de seed: `src/main/resources/db/oracle/02-seed-gps_posicoes.sql`

## Teste local no navegador com Oracle

Depois de subir com o perfil `oracle`, abra:

```text
http://localhost:8080/api/telemetria/tablets/80001/ultima-posicao
```

Se o tablet existir no Oracle, o navegador vai mostrar o JSON.

## Executar testes

```bash
./mvnw test
```
