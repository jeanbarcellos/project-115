# DTO

## Spring

### Respostas de erro padrão do Spring

Ao executar uma requisição GET neste endpoint, vemos que esta exceção foi lançada, e este é o corpo da resposta:

```json
{
    "timestamp":"2019-09-16T22:14:45.624+0000",
    "status":500,
    "error":"Internal Server Error",
    "message":"No message available",
    "path":"/api/book/1"
}
```

### Respostas mais detalhadas

```json
{
    "error": "auth-0001",
    "message": "Incorrect username and password",
    "detail": "Ensure that the username and password included in the request are correct",
    "help": "https://example.com/help/error/auth-0001"
}
```
- Erro – um identificador único para o erro
- Mensagem – uma mensagem breve e legível para humanos.
- Detalhe – uma explicação mais extensa do erro
- Help - URL de ajuda

### Corpos de Resposta Padronizados

Embora a maioria das APIs REST siga convenções semelhantes, os detalhes geralmente variam, incluindo os nomes dos campos e as informações incluídas no corpo da resposta. Essas diferenças dificultam o tratamento uniforme de erros por bibliotecas e frameworks.

Em um esforço para padronizar o tratamento de erros em APIs REST, o IETF elaborou o RFC 7807 , que cria um esquema generalizado de tratamento de erros .

Este esquema é composto por cinco partes:

1. type (tipo) – um identificador URI que categoriza o erro
2. title (título) – uma mensagem breve e de fácil leitura sobre o erro.
3. status (status) – o código de resposta HTTP (opcional)
4. detalhe (detail) – uma explicação do erro em linguagem acessível a humanos.
5. instance (instância_ – um URI que identifica a ocorrência específica do erro
Em vez de usar nosso corpo de resposta de erro personalizado, podemos converter nosso corpo:

```
{
    "type": "/errors/incorrect-user-pass",
    "title": "Incorrect username or password.",
    "status": 401,
    "detail": "Authentication failed due to incorrect username or password.",
    "instance": "/login/log/abc123"
}
```

Note que o campo "tipo" categoriza o tipo de erro, enquanto "instância" identifica uma ocorrência específica do erro, de forma semelhante a classes e objetos, respectivamente.

## Referencias

- https://medium.com/@aedemirsen/generic-api-response-with-spring-boot-175434952086
- https://www.baeldung.com/rest-api-error-handling-best-practices