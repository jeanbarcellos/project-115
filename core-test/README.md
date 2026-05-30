8. Contract Tests

Eu considero a principal lacuna hoje.

Vocês têm:

```
Spring
Quarkus
```

Ambos deveriam responder:

```json
{
  "type": "...",
  "title": "...",
  "status": 404,
  "detail": "...",
  "instance": "...",
  "timestamp": "...",
  "correlationId": "...",
  "properties": {}
}
```

Eu criaria um módulo:

```
core-test
```

com asserts reutilizáveis.

Exemplo:

```
ProblemAssertions.assertProblem(response);
```

Garantindo que Spring e Quarkus nunca divergem.