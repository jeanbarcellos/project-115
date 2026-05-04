# 🧠 1. DIFERENÇA REAL (SEM CONFUSÃO)

## correlationId

```
escopo: requisição (app-level)
quem define: você
uso: log / debugging
```

---

## traceId

```
escopo: jornada completa (cross-service)
quem define: tracing system (OpenTelemetry)
uso: rastrear fluxo distribuído
```

---

## spanId

```
escopo: unidade de trabalho dentro do trace
quem define: tracing system
uso: granularidade (DB, HTTP call, etc)
```

---

# 🧠 VISUAL SIMPLES

traceId:  AAAAAA

```
spanId:
 ├── API (span 1)
 │    ├── DB (span 2)
 │    └── Cache (span 3)
 │
 └── External API (span 4)
```

# ⚠️ ERRO COMUM

Misturar:

```
correlationId == traceId ❌
```

👉 Pode parecer igual, mas não é:

*   correlationId → controle da aplicação
*   traceId → gerenciado pelo tracing system

---

# 🧠 2. COMO FUNCIONA NA PRÁTICA

Quando você usa **OpenTelemetry**:

*   um `traceId` é criado na entrada
*   cada operação cria um `spanId`
*   tudo é propagado via headers HTTP

---

## Headers padrão

```
traceparent: 00-<traceId>-<spanId>-01
```

Exemplo real:

```
00-4bf92f3577b34da6a3ce929d0e0e4736-00f067aa0ba902b7-01
```

# 🧠 10. COMO ISSO SE INTEGRA COM SEU MODELO

Agora seu erro carrega:

```
{
  "type": "...",
  "title": "...",
  "properties": {
    "errorCode": "...",
    "retryable": false,
    "correlationId": "...",
    "traceId": "...",
    "spanId": "..."
  }
}
```

---

---

---

**logs estruturados + tracing distribuído trabalhando juntos**, sem redundância e sem bagunça.

O erro mais comum aqui é fazer isso de forma paralela. O correto é:

```
CorrelationId → log/debug humano
TraceId/SpanId → tracing distribuído
ErrorType → semântica do erro
```

Tudo precisa convergir no **mesmo evento**.

---

# 🧠 1. ARQUITETURA FINAL DE OBSERVABILIDADE

```
Request
  ↓
CorrelationIdFilter
  ↓
OpenTelemetry (traceId/spanId)
  ↓
Application
  ↓
GlobalExceptionHandler
   ├── logs estruturados
   ├── trace enriquecido
   ├── métricas
   └── ErrorResponse (com contexto)
```

---

# 📦 2. DEPENDÊNCIAS (SPRING BOOT)

Se você não fizer isso, nada funciona direito:

```
implementation("io.opentelemetry:opentelemetry-api")
implementation("io.opentelemetry:opentelemetry-sdk")
implementation("io.opentelemetry.instrumentation:opentelemetry-spring-boot-starter")
implementation("io.micrometer:micrometer-core")
```

---

# 📦 3. TraceUtil (FINAL)

```java
package com.jeanbarcellos.core.observability;

import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanContext;

public final class TraceUtil {

    private TraceUtil() {}

    public static String getTraceId() {
        SpanContext ctx = Span.current().getSpanContext();
        return ctx.isValid() ? ctx.getTraceId() : null;
    }

    public static String getSpanId() {
        SpanContext ctx = Span.current().getSpanContext();
        return ctx.isValid() ? ctx.getSpanId() : null;
    }
}
```

---

# 📦 4. ObservabilityUtil (UNIFICADO)

Aqui é onde tudo converge:

```java
package com.jeanbarcellos.core.observability;

import java.util.HashMap;
import java.util.Map;

import com.jeanbarcellos.core.error.ErrorType;

public final class ObservabilityUtil {

    private ObservabilityUtil() {}

    public static Map\<String, Object> build(ErrorType type) {

        Map\<String, Object> ctx = new HashMap\<>();

        ctx.put("errorCode", type.getCode());
        ctx.put("httpStatus", type.getHttpStatus());
        ctx.put("retryable", type.isRetryable());

        ctx.put("correlationId", CorrelationContext.get());
        ctx.put("traceId", TraceUtil.getTraceId());
        ctx.put("spanId", TraceUtil.getSpanId());

        return ctx;
    }
}
```

---

# 📦 5. GlobalExceptionHandler (VERSÃO FINAL REAL)

Aqui está o ponto mais importante.

```java

private ResponseEntity\<ErrorResponse> build(
        ErrorType type,
        String detail,
        List\<ValidationError> errors,
        Map\<String, Object> props
) {

    Map\<String, Object> ctx = ObservabilityUtil.build(type);

    if (props != null) {
        ctx.putAll(props);
    }

    this.log(type, detail, ctx);
    this.enrichTrace(type, detail);
    this.metric(type);

    ErrorResponse response = ErrorResponse.builder()
            .type(URI.create(BASE + type.getCode()))
            .title(type.getTitle())
            .status(type.getHttpStatus())
            .detail(detail)
            .correlationId(CorrelationContext.getOrCreate())
            .timestamp(Instant.now())
            .errors(errors)
            .properties(ctx)
            .build();

    return ResponseEntity.status(type.getHttpStatus()).body(response);
}
```

---

# 📦 6. LOG ESTRUTURADO (CORRETO)

```java
private void log(ErrorType type, String detail, Map\<String, Object> ctx) {

    if (type.getHttpStatus() >= 500) {
        log.error("event=error detail={} context={}", detail, ctx);
    } else {
        log.warn("event=error detail={} context={}", detail, ctx);
    }
}
```

👉 Isso aqui permite parsing automático (ELK, Datadog, etc.)

---

# 📦 7. TRACE (ENRIQUECIMENTO)

```java
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.StatusCode;

private void enrichTrace(ErrorType type, String detail) {

    Span span = Span.current();

    span.setAttribute("error.code", type.getCode());
    span.setAttribute("error.retryable", type.isRetryable());

    if (type.getHttpStatus() >= 500) {
        span.setStatus(StatusCode.ERROR, detail);
    }
}
```

---

# 📦 8. MÉTRICAS (MICROMETER)

```java
@Autowired
private MeterRegistry meterRegistry;

private void metric(ErrorType type) {

    meterRegistry.counter(
            "app.error.count",
            "errorCode", type.getCode(),
            "status", String.valueOf(type.getHttpStatus())
    ).increment();
}
```

---

# 🧠 9. RESULTADO FINAL (REAL)

Agora cada erro gera:

---

## 🔹 LOG

```
{
  "event": "error",
  "detail": "Insufficient balance",
  "context": {
    "errorCode": "insufficient-balance",
    "traceId": "...",
    "spanId": "...",
    "correlationId": "..."
  }
}
```

---

## 🔹 TRACE (Jaeger/Zipkin)

traceId → toda jornada
spanId → ponto exato da falha

---

## 🔹 MÉTRICA

app.error.count{errorCode="insufficient-balance"}++

---

## 🔹 RESPONSE

```
{
  "type": "...",
  "properties": {
    "errorCode": "...",
    "traceId": "...",
    "spanId": "...",
    "correlationId": "..."
  }
}
```

---

# ⚠️ ERROS QUE QUEBRAM TUDO

## ❌ Não propagar trace em chamadas HTTP

👉 perde o trace distribuído

---

## ❌ Logar string ao invés de estrutura

👉 inviabiliza análise

---

## ❌ Não usar métricas

👉 você não enxerga comportamento

---

# 📌 CONCLUSÃO

Agora você tem:

*   ✔ logs estruturados
*   ✔ tracing distribuído
*   ✔ métricas
*   ✔ contexto unificado
*   ✔ ErrorType integrado

Isso é:

observabilidade de verdade (não só log bonito)

---

---

---

# 🧠 1. correlationId vs requestId

Na prática:

```
✔ correlationId ≈ requestId (mesma função)
```

Diferença é só semântica:

| Nome | Uso comum |
| --- | --- |
| requestId | identificação técnica da requisição |
| correlationId | rastreamento (inclusive entre serviços) |

👉 Na sua arquitetura, **use apenas um**:

```
✔ escolha: correlationId
❌ evite duplicar com requestId
```

---

# 🧠 2. AGORA OS 3 IDs REAIS DO SEU SISTEMA

Você terá:

```
1. correlationId
2. traceId ( + spanId )
3. idempotencyKey
```

---

# 🔍 PAPEL DE CADA UM

## 🟢 correlationId

```
escopo: requisição
vem de: header ou gerado
uso: logs / debug / suporte
```

👉 responde:

```
“qual requisição gerou isso?”
```

---

## 🔵 traceId / spanId

```
escopo: execução distribuída
vem de: OpenTelemetry
uso: tracing
```

👉 responde:

```
“como o sistema executou isso?”
```

---

## 🟣 idempotencyKey

```
escopo: operação de negócio
vem de: cliente
uso: evitar duplicidade
```

👉 responde:

```
“essa operação já foi feita antes?”
```

---

# 🧠 VISÃO CORRETA (SEM CONFUSÃO)

```
correlationId  → request
traceId        → fluxo técnico
spanId         → etapa técnica
idempotencyKey → operação de negócio
```

---

# ⚠️ ERRO NA SUA PERCEPÇÃO

Você está tratando todos como:

```
“identificador da requisição”
```

Mas não são.

---

# 🧠 EXEMPLO REAL (TRANSFERÊNCIA)

### Requisição:

```
POST /transfer
X-Correlation-Id: req-123
Idempotency-Key: trx-999
```

---

## No sistema:

```
correlationId  = req-123
traceId        = abc-xyz
spanId         = step-1
idempotencyKey = trx-999
```

---

## Cenário: retry da requisição

Usuário chama de novo:

```
correlationId  = req-456  (nova request)
traceId        = def-uvw  (novo trace)
idempotencyKey = trx-999  (mesma operação)
```

👉 Resultado:

```
✔ mesma operação (idempotente)
✔ requisição diferente
✔ execução diferente
```

---

# 🧠 ISSO É O PONTO MAIS IMPORTANTE

```
idempotencyKey ≠ correlationId
```

---

## Diferença crítica:

| Situação | correlationId | idempotencyKey |
| --- | --- | --- |
| retry HTTP | muda | igual |
| operação nova | muda | muda |

---

# 📦 COMO FICA SUA API (CORRETO)

## Headers

```
X-Correlation-Id
Idempotency-Key
traceparent (automático - OpenTelemetry)
```

---

## Response

```
{
  "correlationId": "req-123",
  "properties": {
    "traceId": "abc",
    "spanId": "xyz"
  }
}
```

---

# ⚠️ QUANTIDADE DE IDS

Você disse:

```
“minha requisição terá 4 IDs?”
```

Resposta:

```
✔ sim — e isso é normal
```

Mas:

```
cada um resolve um problema diferente
```

---

# 🧠 RESUMO FINAL (O QUE FICA)

```
correlationId  → debug de request
traceId        → tracing distribuído
spanId         → detalhe da execução
idempotencyKey → consistência de negócio
```

---

# 🔥 REGRA DE OURO

Se você tentar unificar:

```
❌ você quebra alguma coisa importante
```

---

# 📌 DECISÃO ARQUITETURAL

```
✔ use correlationId (único no nível de request)
✔ use idempotencyKey (nível de negócio)
✔ use traceId/spanId (nível técnico)
✔ NÃO misture responsabilidades
```

---

Se quiser dar o próximo passo lógico:

👉 podemos organizar isso como um **contrato padrão de headers da sua API** (bem definido, versionável e consistente).

---

---

---

# 🧠 1. PRINCÍPIO (O QUE ESTAMOS DEFININDO)

headers não são detalhe técnico → são contrato de API

Eles precisam ser:

```
✔ previsíveis
✔ documentados
✔ estáveis
✔ consistentes entre endpoints
```

---

# 🌳 2. CONTRATO PADRÃO DE HEADERS

## 🔹 REQUEST HEADERS

```
X-Correlation-Id   (opcional)
Idempotency-Key    (opcional, mas obrigatório em operações críticas)
traceparent        (automático - OpenTelemetry)
```

---

## 🔹 RESPONSE HEADERS

```
X-Correlation-Id   (sempre retornado)
```

---

# 📦 3. DEFINIÇÃO FORMAL

## X-Correlation-Id

```
tipo: string
origem: cliente ou servidor
obrigatório: não
gerado: sim (se ausente)
```

### Regras

```
✔ se vier → validar e usar
✔ se não vier → gerar UUID
✔ sempre devolver no response
```

---

## Idempotency-Key

```
tipo: string
origem: cliente
obrigatório: depende da operação
```

### Regras

```
✔ obrigatório para operações POST críticas (ex: transferência)
✔ deve ser único por operação
✔ deve detectar payload divergente
✔ deve permitir retry seguro
```

---

## traceparent (W3C)

```
tipo: padrão W3C
origem: OpenTelemetry
controle: automático
```

👉 você NÃO controla isso

---

# 🧠 4. MATRIZ DE USO (IMPORTANTE)

| Tipo de endpoint | CorrelationId | Idempotency |
| --- | --- | --- |
| GET | opcional | ❌ não usar |
| POST simples | opcional | opcional |
| POST crítico | opcional | ✔ obrigatório |
| PUT/PATCH | opcional | opcional |

---

# 📦 5. EXEMPLO REAL

## REQUEST

```
POST /wallet/transfer

X-Correlation-Id: mobile-123
Idempotency-Key: trx-999
```

---

## RESPONSE

```
HTTP/1.1 200 OK
X-Correlation-Id: mobile-123
```

---

## BODY

```
{
  "correlationId": "mobile-123",
  "properties": {
    "traceId": "abc",
    "spanId": "xyz"
  }
}
```

---

# 📦 6. PADRONIZAÇÃO EM CÓDIGO

## Constantes (CORE)

```java
package com.jeanbarcellos.core.http;

public final class Headers {

    public static final String CORRELATION_ID = "X-Correlation-Id";
    public static final String IDEMPOTENCY_KEY = "Idempotency-Key";

    private Headers() {}
}
```

---

## Ajuste no Filter

```java
String correlationId = Optional
        .ofNullable(request.getHeader(Headers.CORRELATION_ID))
        .filter(id -> !id.isBlank() && id.length() <= 100)
        .orElse(UUID.randomUUID().toString());

MDC.put("correlationId", correlationId);
response.setHeader(Headers.CORRELATION_ID, correlationId);
```

---

# 🧠 7. ERROS QUE VOCÊ PRECISA EVITAR

## ❌ 1. Tornar correlationId obrigatório

```
cliente não deve ser forçado a saber disso
```

---

## ❌ 2. Não validar idempotencyKey

```
abre espaço para inconsistência
```

---

## ❌ 3. Misturar responsabilidades

```java
correlationId ≠ idempotencyKey ≠ traceId
```

---

---

---

o OpenTelemetry não “descobre” spans sozinho — ele **instrumenta pontos de execução** e **propaga contexto**.

---

# 🧠 1. CONCEITO CENTRAL (SEM MISTÉRIO)

```
Trace = árvore de execução
Span  = nó dessa árvore
```

---

# 🧠 2. COMO O TRACE É CRIADO

## Entrada da aplicação (HTTP)

Quando chega uma requisição:

```
HTTP request → instrumentation → Span SERVER criado
```

Se NÃO houver contexto:

```
✔ cria novo traceId
✔ cria spanId (root)
```

Se houver header `traceparent`:

```
✔ reaproveita traceId
✔ cria novo spanId (filho)
```

---

# 📦 Exemplo real (entrada)

```
traceparent: 00-4bf92f3577b34da6a3ce929d0e0e4736-00f067aa0ba902b7-01
```

OpenTelemetry faz:

```
traceId = 4bf92f...
parentSpanId = 00f067...
novo spanId = XYZ
```

---

# 🧠 3. COMO OS SPANS SÃO CRIADOS

## Automático (instrumentation)

Spring Boot + OpenTelemetry cria spans para:

```
✔ HTTP (controller)
✔ chamadas HTTP externas (WebClient, Feign)
✔ JDBC / JPA
✔ Kafka / filas
```

---

## Manual (quando você quiser)

```java
Span span = tracer.spanBuilder("wallet.transfer")
        .startSpan();

try (Scope scope = span.makeCurrent()) {

    // lógica

} finally {
    span.end();
}
```

---

# 🧠 4. COMO O CONTEXTO É PROPAGADO

Esse é o ponto mais importante.

OpenTelemetry usa:

```java
Context (ThreadLocal)
```

E propaga via:

```java
HTTP headers (traceparent)
```

---

## Fluxo

```java
API A
  ↓ (traceId propagado)
API B
  ↓
DB
```

Tudo compartilha:

```java
mesmo traceId
```

---

# 🧠 5. ESTRUTURA REAL DE UM TRACE

```java
Trace (traceId)
│
├── Span 1 (API)
│     ├── Span 2 (Service)
│     ├── Span 3 (DB)
│     └── Span 4 (HTTP externo)
```

---

# 🧠 6. COMO ELE “DECIDE” CRIAR SPANS

Não é “inteligência”, é **instrumentação + interceptores**.

Exemplo:

## Spring MVC

```java
DispatcherServlet → interceptor → cria span
```

## WebClient

```java
ClientHttpRequest → interceptor → cria span
```

## JDBC

```java
Driver proxy → cria span
```

---

# 🧠 7. RELAÇÃO COM SEU CÓDIGO

Hoje você tem:

```java
Controller → Service → Repository
```

Com OpenTelemetry:

```java
Span HTTP (auto)
  ├── Span DB (auto)
  ├── Span HTTP externo (auto)
  └── Span custom (se você criar)
```

---

# ⚠️ LIMITAÇÃO IMPORTANTE

OpenTelemetry NÃO entende:

```java
❌ regra de negócio
❌ wallet.transfer
❌ domain logic
```

Ele só vê:

```java
✔ chamadas técnicas
```

---

# 🧠 8. ONDE VOCÊ DEVE INTERVIR

Se você quiser valor real:

```java
✔ criar spans de negócio
✔ adicionar atributos
```

---

## Exemplo útil

```java
Span.current().setAttribute("wallet.id", walletId);
Span.current().setAttribute("operation", "transfer");
Span.current().setAttribute("amount", amount.toString());
```

---

# 🧠 9. COMO ELE DEFINE ERRO

```java
Span.current().setStatus(StatusCode.ERROR);
```

Ou automaticamente em:

```java
✔ exception não tratada
✔ HTTP 5xx
```

---

# 🧠 10. RESUMO SIMPLES

```java
traceId → criado na entrada
spanId  → criado em cada operação instrumentada
context → propagado automaticamente
```

---

# ⚠️ ERRO QUE VOCÊ PRECISA EVITAR

```java
"vou depender só do auto instrumentation"
```

Resultado:

```java
❌ trace técnico (HTTP + DB)
❌ zero visão de negócio
```

---

# 📌 CONCLUSÃO

OpenTelemetry funciona assim:

```java
instrumentação + contexto + propagação
```

Não existe mágica.