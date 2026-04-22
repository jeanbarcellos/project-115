# Project 115 - REST API Design

- REST API Design:
  - Select
  - Filtering
  - Sorting
  - Pagination
- Versionamento
- Projeções
- Padronização de respostas genéricas

---

## Estrutura definida pela RFC 7807

Um erro **não é um status code**.
O status code continua sendo o **status HTTP**.

O corpo descreve **o problema**.

### Campos padronizados (RFC)

| Campo      | Obrigatório | Significado                       |
| ---------- | ----------- | --------------------------------- |
| `type`     | ❌          | URI que identifica o tipo do erro |
| `title`    | ❌          | Resumo humano do erro             |
| `status`   | ❌          | Status HTTP (espelhado)           |
| `detail`   | ❌          | Descrição específica              |
| `instance` | ❌          | URI da ocorrência                 |

⚠️ **Nenhum campo é obrigatório**, mas:

- `type`, `title`, `status` e `detail` são o mínimo aceitável em APIs sérias

---

## RFC 7807 — _Problem Details for HTTP APIs_

**Objetivo da RFC:**

Padronizar **respostas de erro legíveis por máquinas**, sem quebrar a semântica do HTTP.

Antes da RFC:

- Cada API inventava um JSON de erro
- Clientes tinham `if (code == X)` espalhado
- Nenhuma interoperabilidade

Depois da RFC:

- Estrutura mínima previsível
- Extensível
- Baseada em HTTP, não em exceções

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

- HTTP continua mandando (`404`)
- JSON **explica**
- Cliente pode reagir por `type`, não por string mágica

### Campos principais

```java
problem.setType(URI.create("https://example.com/problems/user-not-found"));
problem.setTitle("User not found");
problem.setDetail("User with id 42 does not exist");
problem.setInstance(URI.create("/users/42"));
```

Mapeamento direto:

| Java              | RFC      |
| ----------------- | -------- |
| setType(URI)      | type     |
| setTitle(String)  | title    |
| setStatus(int)    | status   |
| setDetail(String) | detail   |
| setInstance(URI)  | instance |

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

- Campos extras não podem quebrar os campos padrão.
- Se você colocar errorCode e ignorar type, você violou o espírito da RFC.

---

## Diferença entre DomainException e BusinessException

A diferença não é semântica superficial, é arquitetural.

E muita gente usa os dois nomes como sinônimos — errado.

### DomainException

> Exceção que representa uma violação do domínio, independentemente de quem consome.

- Vive no core / domínio
- Não conhece HTTP, API, UI, fila, batch
- Pode ocorrer em qualquer contexto de execução
- É agnóstica de canal
- Carrega significado de negócio, não contrato externo

👉 É linguagem ubíqua do domínio, não da API.

### BusinessException

> Exceção que representa um erro de negócio já projetado para um boundary específico (normalmente API).

- Geralmente nasce no adapter ou application layer
- Normalmente já assume que vai virar:
  - HTTP 4xx
  - mensagem para cliente
- Costuma carregar:
  - código
  - mensagem “user friendly”
  - às vezes status HTTP

👉 É erro de negócio do ponto de vista do consumidor, não do domínio puro.

### Diferença resumida

| Aspecto                  | DomainException    | BusinessException |
| ------------------------ | ------------------ | ----------------- |
| Onde nasce               | Domínio / Core     | Application / API |
| Conhece HTTP             | ❌                 | ⚠️ geralmente sim |
| Conhece contrato externo | ❌                 | ✔️                |
| Reutilizável fora da API | ✔️                 | ❌                |
| Linguagem                | Domínio            | Cliente / API     |
| Papel                    | Sinalizar violação | Traduzir violação |

### Arquitetura correta (camadas claras)

```
[ Domain ]
   └─ DomainException

[ Application / Adapter ]
   └─ BusinessException (opcional)

[ API ]
   └─ ProblemDetail / ApiError
```

E, atenção:

- 👉 **BusinessException é opcional**
- 👉 **DomainException é fundamental**

### Regra simples (guarde isso)

- Se a exceção faz sentido fora da API → DomainException
- Se só faz sentido para o consumidor → BusinessException
- Se carrega HTTP → nunca DomainException
- Se está no core → nunca BusinessException

### Veredito arquitetural

Se você usa apenas DomainException + tradução no adapter:

- ✔️ arquitetura limpa
- ✔️ menos classes
- ✔️ menos acoplamento
- ✔️ mais reutilização

Se você cria BusinessException no core:

- ❌ você acoplou domínio ao delivery
- ❌ perdeu agnosticismo
- ❌ dificultou evolução

### Em uma frase (sem anestesia)

- **DomainException** é linguagem do domínio.
- **BusinessException** é linguagem da borda.

Se quiser, no próximo passo posso:

- mostrar um projeto sem BusinessException nenhuma
- mostrar quando BusinessException é inevitável
- ou desmontar um design real que mistura as duas

```
[ Domain ]
   └─ throw DomainException

[ Application ]
   └─ Domain → BusinessException

[ API ]
   └─ BusinessException → ApiError (RFC 7807)
```

## **400 vs 422 — critério objetivo (não opinativo)**

A confusão comum é tratar tudo como 400. Isso empobrece o contrato.

## Regra prática

- **400 Bad Request**
  - Problema de **sintaxe/estrutura** da requisição
  - JSON inválido, tipo errado, header ausente, query malformada
  - O servidor **não consegue processar semanticamente**
- **422 Unprocessable Entity**
  - Estrutura válida, mas **violação de regra/validação**
  - Bean Validation, invariantes simples
  - O servidor **entende**, mas **rejeita**


## Separação de Erros por Canal

```
core → DomainException
application → BusinessException

adapter REST → ErrorResponse (RFC 7807)
adapter EVENT → ErrorEvent
```
