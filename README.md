# Project 115 - REST API Design

*   REST API Design:
    *   Select
    *   Filtering
    *   Sorting
    *   Pagination
*   Versionamento
*   Projeções
*   Padronização de respostas genéricas

---

## Estrutura definida pela RFC 7807

Um erro **não é um status code**.
O status code continua sendo o **status HTTP**.

O corpo descreve **o problema**.

### Campos padronizados (RFC)

| Campo | Obrigatório | Significado |
| --- | --- | --- |
| `type` | ❌ | URI que identifica o tipo do erro |
| `title` | ❌ | Resumo humano do erro |
| `status` | ❌ | Status HTTP (espelhado) |
| `detail` | ❌ | Descrição específica |
| `instance` | ❌ | URI da ocorrência |

⚠️ **Nenhum campo é obrigatório**, mas:

*   `type`, `title`, `status` e `detail` são o mínimo aceitável em APIs sérias

---

## RFC 7807 — _Problem Details for HTTP APIs_

**Objetivo da RFC:**

Padronizar **respostas de erro legíveis por máquinas**, sem quebrar a semântica do HTTP.

Antes da RFC:

*   Cada API inventava um JSON de erro
*   Clientes tinham `if (code == X)` espalhado
*   Nenhuma interoperabilidade

Depois da RFC:

*   Estrutura mínima previsível
*   Extensível
*   Baseada em HTTP, não em exceções

### Exemplo **correto** segundo a RFC

```
HTTP/1.1 404 Not Found
Content-Type: application/problem+json
```

```
{
  "type": "https://example.com/problems/user-not-found",
  "title": "User not found",
  "status": 404,
  "detail": "User with id 42 does not exist",
  "instance": "/users/42"
}
```

👉 Observe:

*   HTTP continua mandando (`404`)
*   JSON **explica**
*   Cliente pode reagir por `type`, não por string mágica

### Campos principais

```java
problem.setType(URI.create("https://example.com/problems/user-not-found"));
problem.setTitle("User not found");
problem.setDetail("User with id 42 does not exist");
problem.setInstance(URI.create("/users/42"));
```

Mapeamento direto:

| Java | RFC |
| --- | --- |
| setType(URI) | type |
| setTitle(String) | title |
| setStatus(int) | status |
| setDetail(String) | detail |
| setInstance(URI) | instance |

Nada a mais. Nada a menos.

Extensão controlada (ponto crítico)

A RFC permite campos extras.

Spring expõe isso via:

```java
problem.setProperty("timestamp", Instant.now());
problem.setProperty("correlationId", requestId);
```

Resultado:

```
{
  "type": "https://example.com/problems/user-not-found",
  "title": "User not found",
  "status": 404,
  "detail": "User with id 42 does not exist",
  "instance": "/users/42",
  "timestamp": "2026-02-01T20:10:00Z",
  "correlationId": "abc-123"
}
```

👉 Regra dura:

*   Campos extras não podem quebrar os campos padrão.
*   Se você colocar errorCode e ignorar type, você violou o espírito da RFC.