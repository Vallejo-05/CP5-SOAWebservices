# CP5 - SOA e WebServices | API Auto Escola 3ESR

API REST em **Java 21 + Spring Boot 4** para agendamento de instruções de uma auto-escola.

## Integrantes

| Nome | RM |
|------|----|
| Fernando Gonzales Alexandre | RM555045 |
| Gabriel Guerreiro Escobosa Vallejo | RM554973 |
| Lucas Catroppa Piratininga Dias | RM555450 |
| Luiz Felipe Coelho Ramos | RM555074 |
| Vitor Musolino Teixeira| RM555012 |

## O que foi entregue

### Checkpoint 5
| Item pedido | Onde está |
|-------------|-----------|
| **Consumo de API/WebService externo** | Integração com a API pública **ViaCEP**: `adapter/out/client/viacep/ViaCepClient.java`, que usa o `RestClient` do Spring e implementa a porta `ConsultaCepPort`. O endpoint é `GET /enderecos/cep/{cep}` e serve para preencher automaticamente o endereço no cadastro de alunos e instrutores. CEP inexistente retorna 404; ViaCEP fora do ar retorna 503. |
| **Documentação automática com Swagger** | `config/documentation/SwaggerConfig.java` (springdoc-openapi) e anotações `@Tag`, `@Operation` e `@ApiResponse` nos controllers, com autenticação Bearer JWT. Swagger UI em **http://localhost:8081/swagger-ui.html**, JSON em `/v3/api-docs` e YAML em `/v3/api-docs.yaml`. Uma cópia offline da especificação está em `SwaggerOffLine.yaml`. |
| **Configuração de CORS** | `config/cors/CorsConfig.java` define as origens, métodos e cabeçalhos permitidos, além de expor o cabeçalho `Location`. As origens ficam na propriedade `api.cors.allowed-origins` (ou na variável de ambiente `CORS_ALLOWED_ORIGINS`), e o `SecurityConfig` aplica a configuração com `.cors(Customizer.withDefaults())`. |
| **Testes automatizados para cada entidade** | `src/test/java`, com 75 testes (veja abaixo). |

## Arquitetura (hexagonal / portas e adaptadores)

```
adapter/in/controller        -> controllers REST + DTOs de request/response
application/core/domain      -> domínio (Instrutor, Aluno, Usuario, Instrucao)
application/core/specification -> validadores das regras de agendamento/cancelamento
application/port/out         -> portas de saída (repositórios, consulta de CEP)
application/service          -> casos de uso
adapter/out/repository       -> entidades JPA, Spring Data e implementações das portas
adapter/out/client/viacep    -> cliente da API externa ViaCEP
config                       -> segurança (JWT), CORS e Swagger
```

## Testes automatizados

Os testes usam **H2 em memória (modo MySQL)** com as mesmas migrations Flyway, então rodam sem nenhum banco instalado:

```bash
./mvnw test
```

| Entidade / recurso | Testes |
|--------------------|--------|
| Instrutor | `InstrutorControllerTest`, `InstrutorServiceTest`, `InstrutorRepositoryTest` |
| Aluno | `AlunoControllerTest`, `AlunoServiceTest`, `AlunoRepositoryTest` |
| Usuário | `UsuarioControllerTest`, `UsuarioServiceTest`, `UsuarioRepositoryTest` |
| Instrução | `InstrucaoControllerTest`, `AgendaDeInstrucoesTest`, `ValidadoresInstrucaoTest`, `InstrucaoRepositoryTest` |
| API externa (ViaCEP) | `ViaCepClientTest` (com `MockRestServiceServer`), `EnderecoControllerTest` |
| CORS / Swagger / JWT | `CorsConfigTest`, `SwaggerConfigTest`, `AutenticacaoIntegrationTest` |

## Como executar

1. Suba um MySQL (ou use um que já esteja instalado):
   ```bash
   docker compose up -d
   ```
2. Rode a aplicação. As tabelas são criadas pelo Flyway.
   ```bash
   ./mvnw spring-boot:run
   ```
3. Acesse **http://localhost:8081/swagger-ui.html**, faça `POST /login` com `{"login": "admin", "senha": "admin"}`, clique em **Authorize** e informe o token.

Configurações que podem ser sobrescritas por variáveis de ambiente: `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`, `JWT_SECRET`, `CORS_ALLOWED_ORIGINS` e `VIACEP_URL`.

### Exemplos de requisição

```json
POST /instrucoes
{ "id_aluno": 1, "id_instrutor": 1, "data_hora": "12/10/2026 - 10:00" }

POST /instrucoes/1/cancelamento
{ "motivo": "ALUNO_DESISTIU" }

PUT /usuarios/senha
{ "senha_atual": "admin", "nova_senha": "novaSenha123" }
```
