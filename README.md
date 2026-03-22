# API Credit Simulation

API REST desenvolvida com Java 21 e Spring Boot 3 para cadastro de clientes e simulação de crédito.

O projeto foi estruturado para atender ao desafio técnico com foco em:

- CRUD completo de clientes
- relacionamento `1:1` entre cliente e endereço
- criação de simulações vinculadas a clientes
- listagem paginada de simulações por cliente
- exportação das simulações em `CSV` e `TXT`
- testes automatizados para todos os endpoints REST
- separação de responsabilidades entre controller, service, repository e DTOs

## Tecnologias utilizadas

- Java 21
- Spring Boot 3.4.3
- Spring Web
- Spring Data JPA
- Spring Validation
- Lombok
- PostgreSQL
- H2
- JUnit 5
- MockMvc
- Maven

## Estrutura do projeto

O projeto está organizado em camadas:

- `controller`: expõe os endpoints REST
- `service`: concentra as regras de negócio
- `repository`: realiza o acesso ao banco de dados
- `dto`: define os contratos de entrada e saída da API
- `entities`: representa as entidades persistidas
- `exception`: centraliza exceções e tratamento global de erros

## Modelo de domínio

### Cliente

Campos:

- `id`
- `cpf`
- `nome`
- `endereco`

### Endereço

Campos:

- `id`
- `rua`
- `numero`
- `bairro`
- `cep`
- `cidade`
- `estado`

### Simulação

Campos:

- `id`
- `cliente`
- `dataHoraSimulacao`
- `valorSolicitado`
- `valorGarantia`
- `quantidadeMeses`
- `taxaJurosMensal`

## Relacionamentos

- `Cliente` possui um `Endereço` em relacionamento `OneToOne`
- `Simulação` pertence a um `Cliente` em relacionamento `ManyToOne`

## Banco de dados

### Ambiente principal

O projeto está configurado para usar PostgreSQL localmente:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/credit_simulation
spring.datasource.username=admin
spring.datasource.password=admin
```

Se necessário, ajuste os valores em [application.properties](/C:/PROJETOS/api-credit-simulation/src/main/resources/application.properties).

### Ambiente de testes

Os testes automatizados utilizam H2 em memória, sem dependência do PostgreSQL local.

## Como executar o projeto

### Pré-requisitos

- Java 21 instalado
- Maven instalado
- PostgreSQL em execução
- banco `credit_simulation` criado

### Executar a aplicação

Com Maven instalado:

```bash
mvn spring-boot:run
```

Com Maven Wrapper:

```bash
./mvnw spring-boot:run
```

A aplicação será iniciada em:

```text
http://localhost:8080
```

## Como rodar os testes

Com Maven instalado:

```bash
mvn test
```

Com Maven Wrapper:

```bash
./mvnw test
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
  "nome": "João Silva",
  "endereco": {
    "rua": "Rua das Flores",
    "numero": "123",
    "bairro": "Centro",
    "cep": "01001000",
    "cidade": "São Paulo",
    "estado": "SP"
  }
}
```

#### Responses esperadas

- `201 Created`
- `400 Bad Request` para payload inválido
- `409 Conflict` para CPF já cadastrado

### 2. Listar clientes

`GET /clientes`

#### Responses esperadas

- `200 OK`

### 3. Buscar cliente por ID

`GET /clientes/{id}`

#### Responses esperadas

- `200 OK`
- `404 Not Found` se o cliente não existir

### 4. Atualizar cliente

`PUT /clientes/{id}`

#### Request

```json
{
  "cpf": "12345678901",
  "nome": "João da Silva",
  "endereco": {
    "rua": "Rua Nova",
    "numero": "500",
    "bairro": "Centro",
    "cep": "01001000",
    "cidade": "São Paulo",
    "estado": "SP"
  }
}
```

#### Responses esperadas

- `200 OK`
- `400 Bad Request` para payload inválido
- `404 Not Found` se o cliente não existir
- `409 Conflict` se o CPF já estiver em uso por outro cliente

### 5. Deletar cliente

`DELETE /clientes/{id}`

#### Responses esperadas

- `204 No Content`
- `404 Not Found` se o cliente não existir
- `409 Conflict` se o cliente possuir simulações vinculadas

### 6. Criar simulação para um cliente

`POST /clientes/{clienteId}/simulacoes`

#### Request

Os dados abaixo refletem exatamente os valores pedidos no enunciado:

```json
{
  "dataHoraSimulacao": "2024-06-15T10:30:26",
  "valorSolicitado": 300000.00,
  "valorGarantia": 1000000.00,
  "quantidadeMeses": 150,
  "taxaJurosMensal": 2.00
}
```

#### Responses esperadas

- `201 Created`
- `400 Bad Request` para payload inválido
- `404 Not Found` se o cliente não existir

### 7. Listar simulações paginadas de um cliente

`GET /clientes/{clienteId}/simulacoes?page=0&size=10`

#### Exemplo de resposta

```json
{
  "content": [
    {
      "id": 1,
      "clienteId": 1,
      "dataHoraSimulacao": "2024-06-15T10:30:26",
      "valorSolicitado": 300000.00,
      "valorGarantia": 1000000.00,
      "quantidadeMeses": 150,
      "taxaJurosMensal": 2.00
    }
  ],
  "page": 0,
  "size": 10,
  "totalElements": 1,
  "totalPages": 1,
  "first": true,
  "last": true
}
```

#### Responses esperadas

- `200 OK`
- `404 Not Found` se o cliente não existir

### 8. Exportar simulações em CSV

`GET /clientes/{clienteId}/simulacoes/exportacao/csv`

#### Responses esperadas

- `200 OK`
- `404 Not Found` se o cliente não existir

### 9. Exportar simulações em TXT

`GET /clientes/{clienteId}/simulacoes/exportacao/txt`

#### Responses esperadas

- `200 OK`
- `404 Not Found` se o cliente não existir

## Exemplo rápido com cURL

### Criar cliente

```bash
curl --request POST "http://localhost:8080/clientes" \
  --header "Content-Type: application/json" \
  --data "{\"cpf\":\"12345678901\",\"nome\":\"João Silva\",\"endereco\":{\"rua\":\"Rua das Flores\",\"numero\":\"123\",\"bairro\":\"Centro\",\"cep\":\"01001000\",\"cidade\":\"São Paulo\",\"estado\":\"SP\"}}"
```

### Criar simulação

```bash
curl --request POST "http://localhost:8080/clientes/1/simulacoes" \
  --header "Content-Type: application/json" \
  --data "{\"dataHoraSimulacao\":\"2024-06-15T10:30:26\",\"valorSolicitado\":300000.00,\"valorGarantia\":1000000.00,\"quantidadeMeses\":150,\"taxaJurosMensal\":2.00}"
```

### Listar simulações paginadas

```bash
curl --request GET "http://localhost:8080/clientes/1/simulacoes?page=0&size=10"
```

## Exemplo de consultas SQL

### Listar clientes

```sql
select * from clientes;
```

### Listar endereços

```sql
select * from enderecos;
```

### Listar simulações

```sql
select * from simulacoes;
```

### Listar simulações com dados do cliente

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
from simulacoes s
join clientes c on c.id = s.cliente_id;
```

## Tratamento de erros

O projeto possui tratamento global de exceções com respostas padronizadas.

Exemplo:

```json
{
  "timestamp": "2026-03-22T19:30:00",
  "status": 404,
  "error": "Not Found",
  "message": "Cliente nao encontrado.",
  "path": "/clientes/9999",
  "validationErrors": null
}
```

## Qualidade e boas práticas aplicadas

- uso de DTOs para não expor entidades diretamente
- validação de entrada com Bean Validation
- tratamento global de exceções
- retorno consistente de status HTTP
- regras de negócio centralizadas em services
- testes isolados do banco principal
- paginação com contrato de resposta estável

## Melhorias futuras possíveis

- incluir documentação OpenAPI/Swagger
- adicionar migrations com Flyway ou Liquibase
- incluir coleção do Postman no repositório
- criar testes de integração mais detalhados para cenários de erro

## Autor

Projeto desenvolvido por Raphael Garcia para preparação de entrevista técnica de Backend Java.
