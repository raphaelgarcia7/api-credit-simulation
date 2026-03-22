# API Credit Simulation

API REST desenvolvida com Java 21 e Spring Boot para cadastro de clientes e gerenciamento de simulacoes de credito.

O projeto contempla:

- CRUD de clientes
- Criacao de simulacoes vinculadas a um cliente
- Listagem de simulacoes por cliente
- Paginacao da listagem
- Exportacao das simulacoes em `CSV` e `TXT`
- Testes automatizados para os principais endpoints

## Tecnologias utilizadas

- Java 21
- Spring Boot 3.4.3
- Spring Web
- Spring Data JPA
- Lombok
- PostgreSQL
- JUnit 5
- MockMvc
- Gradle

## Arquitetura do projeto

O projeto esta organizado em camadas para manter responsabilidades separadas:

- `controller`: exposicao dos endpoints REST
- `service`: regras de negocio e orquestracao
- `repository`: acesso aos dados com Spring Data JPA
- `dto`: objetos de entrada e saida da API
- `entities`: entidades persistidas no banco
- `exception`: excecoes de dominio e tratamento global

## Modelo de dominio

### Cliente

Campos:

- `id`
- `cpf`
- `nome`
- `endereco`

### Endereco

Campos:

- `rua`
- `numero`
- `bairro`
- `cep`
- `cidade`
- `estado`

No estado atual do projeto, `Endereco` esta modelado como `@Embedded` dentro de `Cliente`.

### Simulacao

Campos:

- `id`
- `cliente`
- `dataHoraSimulacao`
- `valorSolicitado`
- `valorGarantia`
- `quantidadeMeses`
- `taxaJurosMensal`

## Requisitos para executar

- Java 21 instalado
- PostgreSQL em execucao local
- Banco `credit_simulation` criado

Configuracao atual em `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/credit_simulation
spring.datasource.username=admin
spring.datasource.password=admin
```

Se necessario, ajuste usuario, senha e URL conforme o seu ambiente.

## Como executar

1. Suba o PostgreSQL localmente
2. Garanta que o banco `credit_simulation` exista
3. Execute a aplicacao

```bash
./gradlew bootRun
```

No Windows PowerShell:

```powershell
.\gradlew.bat bootRun
```

A aplicacao sera iniciada, por padrao, em:

```text
http://localhost:8080
```

## Como rodar os testes

```bash
./gradlew test
```

No Windows PowerShell:

```powershell
.\gradlew.bat test
```

## Endpoints

Base URL:

```text
http://localhost:8080
```

### 1. Criar cliente

`POST /clientes`

#### Request

```json
{
  "cpf": "12345678901",
  "nome": "Joao Silva",
  "endereco": {
    "rua": "Rua das Flores",
    "numero": "123",
    "bairro": "Centro",
    "cep": "01001000",
    "cidade": "Sao Paulo",
    "estado": "SP"
  }
}
```

#### Response esperada

- `201 Created`

#### Exemplo de retorno

```json
{
  "id": 1,
  "cpf": "12345678901",
  "nome": "Joao Silva",
  "endereco": {
    "rua": "Rua das Flores",
    "numero": "123",
    "bairro": "Centro",
    "cep": "01001000",
    "cidade": "Sao Paulo",
    "estado": "SP"
  }
}
```

### 2. Listar todos os clientes

`GET /clientes`

#### Response esperada

- `200 OK`

### 3. Buscar cliente por ID

`GET /clientes/{id}`

#### Response esperada

- `200 OK`
- `404 Not Found` quando o cliente nao existir

### 4. Atualizar cliente

`PUT /clientes/{id}`

#### Request

```json
{
  "cpf": "12345678901",
  "nome": "Joao da Silva",
  "endereco": {
    "rua": "Rua Nova",
    "numero": "500",
    "bairro": "Centro",
    "cep": "01001000",
    "cidade": "Sao Paulo",
    "estado": "SP"
  }
}
```

#### Response esperada

- `200 OK`
- `404 Not Found` quando o cliente nao existir

### 5. Deletar cliente

`DELETE /clientes/{id}`

#### Response esperada

- `204 No Content`
- `404 Not Found` quando o cliente nao existir

### 6. Criar simulacao

`POST /simulacoes`

#### Request

```json
{
  "clienteId": 1,
  "dataHoraSimulacao": "2024-06-15T10:30:26",
  "valorSolicitado": 300000.00,
  "valorGarantia": 1000000.00,
  "quantidadeMeses": 150,
  "taxaJurosMensal": 0.02
}
```

#### Response esperada

- `201 Created`
- `404 Not Found` quando o cliente nao existir

### 7. Listar simulacoes de um cliente

`GET /simulacoes/cliente/{clienteId}`

#### Response esperada

- `200 OK`
- `404 Not Found` quando o cliente nao existir

### 8. Listar simulacoes de um cliente com paginacao

`GET /simulacoes/cliente/{clienteId}/paginado?page=0&size=10`

#### Response esperada

- `200 OK`
- `404 Not Found` quando o cliente nao existir

#### Exemplo de uso

```text
GET /simulacoes/cliente/1/paginado?page=0&size=5
```

### 9. Exportar simulacoes em CSV

`GET /simulacoes/cliente/{clienteId}/exportar/csv`

#### Response esperada

- `200 OK`
- Header `Content-Type: text/csv`
- Header `Content-Disposition: attachment; filename=simulacoes.csv`

### 10. Exportar simulacoes em TXT

`GET /simulacoes/cliente/{clienteId}/exportar/txt`

#### Response esperada

- `200 OK`
- Header `Content-Type: text/plain`
- Header `Content-Disposition: attachment; filename=simulacoes.txt`

## Exemplo rapido com cURL

### Criar cliente

```bash
curl --request POST "http://localhost:8080/clientes" \
  --header "Content-Type: application/json" \
  --data "{\"cpf\":\"12345678901\",\"nome\":\"Joao Silva\",\"endereco\":{\"rua\":\"Rua das Flores\",\"numero\":\"123\",\"bairro\":\"Centro\",\"cep\":\"01001000\",\"cidade\":\"Sao Paulo\",\"estado\":\"SP\"}}"
```

### Criar simulacao

```bash
curl --request POST "http://localhost:8080/simulacoes" \
  --header "Content-Type: application/json" \
  --data "{\"clienteId\":1,\"dataHoraSimulacao\":\"2024-06-15T10:30:26\",\"valorSolicitado\":300000.00,\"valorGarantia\":1000000.00,\"quantidadeMeses\":150,\"taxaJurosMensal\":0.02}"
```

### Listar simulacoes paginadas

```bash
curl --request GET "http://localhost:8080/simulacoes/cliente/1/paginado?page=0&size=10"
```

## Exemplos de consultas SQL

### Listar clientes

```sql
select * from cliente;
```

### Listar simulacoes

```sql
select * from simulacao;
```

### Listar simulacoes com dados do cliente

```sql
select s.id,
       s.data_hora_simulacao,
       s.valor_solicitado,
       s.valor_garantia,
       s.quantidade_meses,
       s.taxa_juros_mensal,
       c.id as cliente_id,
       c.nome,
       c.cpf
from simulacao s
join cliente c on c.id = s.cliente_id;
```

## Tratamento de erros

Atualmente a API trata globalmente:

- `404 Not Found` para recursos nao encontrados
- `409 Conflict` para CPF duplicado no cadastro

## Observacoes

- O projeto utiliza `BigDecimal` para valores monetarios e taxa de juros, evitando problemas de precisao.
- O Hibernate esta configurado com `ddl-auto=update` para facilitar a execucao local.
- Os testes automatizados usam MockMvc para validacao dos endpoints.

## Melhorias futuras

- Adicionar validacoes com Bean Validation
- Melhorar o padrao de resposta de erro
- Isolar o ambiente de testes com H2 ou Testcontainers
- Revisar a modelagem de endereco para uma relacao `OneToOne`, se necessario para aderencia literal ao requisito
- Ampliar a cobertura de testes

## Autor

Projeto desenvolvido por Raphael Garcia para estudo e preparacao de entrevista tecnica Java Backend.
