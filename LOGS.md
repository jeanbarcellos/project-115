> Isso não conflita com logback serializando json no file, por exemplo ?

Mostrar maisMostrar menos

Conflita — dependendo de como você fizer.

Você está perto de cometer um erro comum: **serializar JSON na aplicação E no Logback ao mesmo tempo**.

---

# 🧠 O PROBLEMA

Se você fizer isso:

```java
String json = objectMapper.writeValueAsString(event);
log.error(json);
```

E também configurar o Logback para JSON:

```
logback → JSON encoder
```

Você gera:

```
{
  "message": "{\"event\":\"error\",\"errorCode\":\"...\"}"
}
```

👉 JSON dentro de JSON → lixo para observabilidade

---

# 🧠 REGRA CORRETA

```
OU a aplicação serializa
OU o Logback serializa
NUNCA os dois
```

---

# 🧠 QUAL ESCOLHER?

## ❌ Serializar na aplicação (o que te sugeri antes)

Problemas:

```
❌ acoplamento com JSON
❌ difícil evoluir formato
❌ quebra com appenders estruturados
```

---

## ✔ Deixar o Logback serializar (CORRETO)

A aplicação só faz:

```
log.error("error event", event);
```

E o Logback transforma isso em JSON.

---

# 📦 1. DTO DE LOG (permanece igual)

```java
@Builder
@Getter
public class ErrorLogEvent {

    private String event;
    private String errorCode;
    private int httpStatus;
    private boolean retryable;

    private String message;
    private String exception;

    private String correlationId;

    private String path;
    private String method;

    private Instant timestamp;
}
```

---

# 📦 2. LOG CORRETO (SEM SERIALIZAR)

```java
private void log(ErrorType type, ErrorLogEvent event, Exception ex) {

    if (type.getHttpStatus() >= 500) {
        log.error("error", event, ex);
    } else {
        log.warn("error", event);
    }
}
```

---

# ⚠️ MAS AQUI TEM UM DETALHE IMPORTANTE

O Logback padrão NÃO entende objetos.

Você precisa de:

👉 encoder JSON (ex: logstash-logback-encoder)

---

# 📦 3. CONFIGURAÇÃO LOGBACK (JSON)

Dependência:

```
implementation("net.logstash.logback:logstash-logback-encoder:7.4")
```

---

## logback-spring.xml

```xml
<configuration>

    <appender name="JSON" class="ch.qos.logback.core.ConsoleAppender">
        <encoder class="net.logstash.logback.encoder.LogstashEncoder"/>
    </appender>

    <root level="INFO">
        <appender-ref ref="JSON"/>
    </root>

</configuration>
```

---

# 📦 4. COMO O LOG SAI

Agora isso:

```java
log.error("error", event);
```

vira:

```
{
  "message": "error",
  "errorCode": "invalid-amount",
  "httpStatus": 422,
  "correlationId": "req-123",
  "path": "/wallet/deposit",
  ...
}
```

👉 **sem double JSON**
👉 **parseável**
👉 **compatível com ELK / Datadog**

---

# 🧠 5. MELHOR AINDA (ARGUMENTO ESTRUTURADO)

Se quiser fazer direito mesmo:

```java
import net.logstash.logback.argument.StructuredArguments;

log.error(
    "error",
    StructuredArguments.keyValue("event", event)
);
```

---

# ⚠️ ERROS QUE VOCÊ PRECISA EVITAR

## ❌ 1. JSON manual + encoder JSON

```
→ JSON duplicado (quebra tudo)
```

---

## ❌ 2. toString() no DTO

```java
log.error(event.toString()); ❌
```

→ perde estrutura

---

## ❌ 3. Map + DTO misturado

```
→ log inconsistente
```

---

# 🧠 CONCLUSÃO

```
✔ DTO de log é correto
✔ mas NÃO serialize manualmente
✔ deixe o Logback cuidar disso
```

---

# 🔥 RESUMO FINAL

```
Application → cria objeto
Logger      → recebe objeto
Logback     → serializa JSON
```

---

# 👉 Próximo passo (se quiser evoluir certo)

```
👉 adicionar correlationId automaticamente via MDC no log
👉 padronizar campos globais (app, env, version)
👉 separar log de negócio vs técnico
```

Se não fizer isso, você tem JSON — mas não tem **observabilidade útil**.

Esses três pontos são exatamente onde o log deixa de ser “JSON bonito” e vira **observabilidade útil**. Vou direto ao que importa e como implementar sem complicar.

---

# 🧠 1. correlationId automático via MDC

Hoje você já faz:

```java
MDC.put("correlationId", correlationId);
```

Mas isso **não aparece automaticamente no JSON** — a menos que o Logback saiba extrair.

---

## ✔ Como resolver (Logback)

No `logback-spring.xml`:

```xml
<encoder class="net.logstash.logback.encoder.LogstashEncoder">
    <includeMdc>true</includeMdc>
</encoder>
```

---

## Resultado

Sem você mudar seu log:

```
log.error("error", event);
```

Sai:

```
{
  "message": "error",
  "correlationId": "req-123",
  "errorCode": "invalid-amount"
}
```

👉 você não precisa mais colocar correlationId no DTO
👉 ele entra automaticamente

---

# ⚠️ erro comum

Duplicar:

```
DTO tem correlationId + MDC tem correlationId ❌
```

👉 escolha UM (use MDC)

---

# 🧠 2. campos globais (app, env, version)

Isso resolve um problema que você ainda não percebeu:

```
logs de múltiplos serviços misturados
```

---

## ✔ configuração no Logback

```xml
<encoder class="net.logstash.logback.encoder.LogstashEncoder">

    <customFields>
        {
          "app":"wallet-service",
          "env":"prod",
          "version":"1.0.0"
        }
    </customFields>

</encoder>
```

---

## Resultado

```
{
  "app": "wallet-service",
  "env": "prod",
  "version": "1.0.0",
  "correlationId": "req-123",
  "errorCode": "invalid-amount"
}
```

---

# ⚠️ erro comum

Colocar isso no código:

```java
event.setApp("wallet-service"); ❌
```

👉 isso é infra, não domínio

---

# 🧠 3. separar log técnico vs log de negócio

Hoje você está misturando:

```
erro técnico + contexto de negócio + fluxo
```

Isso vira ruído.

---

## ✔ separação correta

### 🔴 Log técnico (GlobalExceptionHandler)

```
✔ erros
✔ exceções
✔ falhas
✔ HTTP 4xx/5xx
```

Exemplo:

```java
log.error("error", event);
```

---

### 🟢 Log de negócio (Application Service)

```
✔ operações importantes
✔ mudanças de estado
✔ decisões de negócio
```

Exemplo:

```java
log.info("wallet.transfer.executed",
    StructuredArguments.keyValue("walletId", walletId),
    StructuredArguments.keyValue("amount", amount)
);
```

---

# 🧠 diferença prática

## técnico

```
{
  "event": "error",
  "errorCode": "database-error"
}
```

---

## negócio

```
{
  "event": "wallet.transfer",
  "walletId": 123,
  "amount": 100
}
```

---

# ⚠️ erro comum (grave)

```
logar tudo como error ❌
```

ou

```
misturar negócio no exception handler ❌
```

---

# 🧠 REGRA SIMPLES

```
GlobalExceptionHandler → falhas
Service                → eventos de negócio
```

---

# 📌 CONCLUSÃO

Se você aplicar só esses três pontos:

```
✔ MDC → correlationId automático
✔ Logback → campos globais
✔ separação → técnico vs negócio
```

Você sai de:

```
log estruturado
```

para:

```
log realmente observável
```

---

# 🔥 ajuste final (que pouca gente faz)

Padronize o campo:

```
event
```

Exemplos:

```
error
wallet.transfer
wallet.deposit
wallet.withdraw
```

Sem isso, você não consegue:

```
filtrar / agrupar / criar métricas
```

---

---

---

Agora ficou claro — e isso está no nível certo.

Você quer um padrão:

```
[level][categoria] campos estruturados
```

Sem abstração, sem DTO, sem framework.

---

# 🧠 AJUSTE FINO NO SEU FORMATO

O que você propôs:

```
[error][business-exception] code=invalid-amount status=422 correlationId=req-123 message=Invalid amount
```

Está bom, mas dá pra melhorar levemente:

```
[level][category] code=... status=... correlationId=... message=...
```

👉 padronize “category” (não misture nomes depois)

---

# 📦 IMPLEMENTAÇÃO FINAL

## Método central no GlobalExceptionHandler

```java
private void log(
        String category,
        ErrorType type,
        String detail,
        String correlationId,
        Exception ex
) {

    String pattern = "[error][{}] code={} status={} correlationId={} message={}";

    if (type.getHttpStatus() >= 500) {
        log.error(
                pattern,
                category,
                type.getCode(),
                type.getHttpStatus(),
                correlationId,
                detail,
                ex
        );
    } else {
        log.warn(
                pattern,
                category,
                type.getCode(),
                type.getHttpStatus(),
                correlationId,
                detail
        );
    }
}
```

---

# 📦 USO NOS HANDLERS

## BusinessException

```java
this.log(
        "business",
        ex.getErrorType(),
        ex.getMessage(),
        correlationId,
        ex
);
```

---

## ValidationException

```java
this.log(
        "validation",
        TechnicalErrorType.INPUT_VALIDATION_ERROR,
        ex.getMessage(),
        correlationId,
        ex
);
```

---

## DomainException (vazou)

```java
this.log(
        "domain",
        TechnicalErrorType.SYSTEM_VALIDATION_ERROR,
        ex.getMessage(),
        correlationId,
        ex
);
```

---

## Exception genérica

```java
this.log(
        "technical",
        TechnicalErrorType.INTERNAL_ERROR,
        "Unexpected error",
        correlationId,
        ex
);
```

---

# 🧠 RESULTADO FINAL

### 4xx

```
[error][business] code=invalid-amount status=422 correlationId=req-123 message=Invalid amount
```

---

### 5xx

```
[error][technical] code=database-error status=500 correlationId=req-123 message=Database error
java.sql.SQLException: ...
```

---

# 🧠 POR QUE ISSO FUNCIONA

```
✔ grep por [error]
✔ filtra por [business], [technical]
✔ agrupa por code
✔ rastreia por correlationId
✔ sem dependência externa
✔ sem complexidade
```

---

# ⚠️ ÚNICO CUIDADO

Padronize as categorias e NÃO invente depois:

```
business
validation
technical
domain
```

Se começar a criar:

```
business-error
biz
err
```

→ você perde o benefício

---

# 📌 CONCLUSÃO

Você chegou em um padrão:

```
✔ simples
✔ legível
✔ consistente
✔ sustentável
```

Sem cair em overengineering.

---