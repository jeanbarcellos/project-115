Vamos fazer alguns ajustes e melhorias:

- módulo `core` possui o pacote base `com.jeanbarcellos.core`. Ele é o core agnóstico a framework e permanecerá assim

- módulo `service-spring` possui o pacote base `com.jeanbarcellos.project115` no qual possui o pacote de dominio `user`, ou seja, está em `com.jeanbarcellos.project115.user`. dentro deste pacote deverá estar a organização das nossas camadas do dominio user

- Adicione java doc em TODAs as classes, implementações de `core`

- Quando for exibir os códigos-fonte pra mim, mostre-me completo, incluindo pacote e importações

- O mapeamento de erros de negócio de user, deve estar neste módulo, não em `BusinessErrorType` de core, pois haverão erros específicos deste módulo. Mais tarde criaremos outros módulos e faremoz a mesma estratégia

- `ApiError` não deve ter `URI type`, a URL deverá ser gerada/resolvida no Handler Exception

- Altere a ordem dos nomes dos dtos, deixe o dominio primeito e a ação na sequencia. por exemplo: `UserCreateRequest` e `UserUpdateRequest`.

--

- Porque você deixei UserErrorType em domain, sendo que ele tem httpStatus no tipo? O ideal não seria ficar no appplication?

- ApiError deve continuar com URI type, pois vai retornar no corpo da requisição de erro.

- Resolve URI no GlobalExceptionHandler. No qual o endereço da URL deve ser "parametrizável" pois ela pode ser dinâmica e mudar.

- mostre-me GlobalExceptionHandler completo e adaptado

- em `core.exception` devemos ter uma exception chamada `ApplicationException`que extenderá `RuntimeException`, que será a Classe base obrigatória para todas as exceções da
 aplicação. Todas as exceções customizadas devem obrigatoriamente herdar desta classe. Ela é a responsável por transportar o estado comum, mensagens e metadados para a camada de tratamento global

---

Application Exception deve ser o mais "basico possivel", assim:

```java
/**
 * Application Exception - Classe base obrigatória para todas as exceções da
 * aplicação (Unchecked Exception).
 * <p>
 * Todas as exceções customizadas devem obrigatoriamente herdar desta classe.
 * Ela é a responsável por transportar o estado comum, mensagens e metadados
 * para a camada de tratamento global.
 * </p>
 * <p>
 * <b>Extensibilidade:</b> Se a exceção representar um erro técnico, de sistema
 * ou
 * de infraestrutura genérico, ela deve estender diretamente esta classe.
 * </p>
 */
public class ApplicationException extends RuntimeException {
    /**
     * Constrói uma nova exceção com a mensagem detalhada especificada.
     *
     * @param message A mensagem de erro detalhada.
     */
    public ApplicationException(String message) {
        super(message);
    }

    /**
     * Constrói uma nova exceção com a mensagem detalhada e a causa raiz.
     *
     * @param message A mensagem de erro detalhada.
     * @param cause   A exceção original que causou este erro.
     */
    public ApplicationException(String message, Throwable cause) {
        super(message, cause);
    }
}
```

Também deve ser criado ValidationException, no qual mais tarde eu entrarei em detalhes do porque

```java
package com.jeanbarcellos.core.exception;

import java.util.Collections;
import java.util.List;

import com.jeanbarcellos.core.error.ValidationError;

/**
 * Validation Exception - Especialização utilizada para falhas de contrato ou
 * erros detectados pela Validação Agregada (múltiplos erros simultâneos).
 * <p>
 * Deve ser mapeada pela camada de tratamento global para o código
 * <b>HTTP 422 (Unprocessable Entity)</b> e carrega obrigatoriamente
 * uma lista de mensagens de erro específicas.
 * </p>
 * <p>
 * <b>Alinhamento com RFC 9457 (Problem Details for HTTP APIs):</b><br>
 * Esta exceção fornece os metadados necessários para que a camada de tratamento
 * global monte um JSON padronizado, expondo uma
 * lista detalhada de {@link ValidationError}s (propriedade de extensão).
 * </p>
 *
 * @author Jean Barcellos (jeanbarcellos@hotmail.com)
 */
public class ValidationException extends ApplicationException {

    /**
     * Uma lista imutável das falhas de validação encontradas
     * ( Campo + Mensagem + Valor Rejeitado)
     */
    private final List<ValidationError> errors;

    /**
     * Construtor para falhas de validação com múltiplos campos inválidos.
     *
     * @param detail Mensagem específica e legível sobre o contexto do erro (RFC
     *               9457 'detail').
     * @param errors Lista de campos que falharam na validação e seus motivos.
     */
    public ValidationException(String detail, List<ValidationError> errors) {
        super(detail);
        this.errors = errors != null ? errors : Collections.emptyList();
    }

    /**
     * Construtor para quando há apenas um erro de campo a ser reportado.
     *
     * @param detail Mensagem específica do contexto do erro.
     * @param error  O erro único detectado.
     */
    public ValidationException(String detail, ValidationError error) {
        super(detail);
        this.errors = error != null ? List.of(error) : Collections.emptyList();
    }

    /**
     * @return Uma lista imutável das falhas de validação encontradas (Campo +
     *         Mensagem + Valor Rejeitado).
     */
    public List<ValidationError> getErrors() {
        return errors;
    }
}
```

- Mova VALIDATION_ERROR para TechnicalErrorType, pois trataremos como "erro genérico" dos dados de entrada

```
VALIDATION_ERROR("validation-error", 422, "Validation failed");
```

- Corrija e deixe da melhor forma o GlobalExcepitionHandler. Não esqueça que já temos TechnicalErrorResolver criado anteriormente para que possamos usar também.


---

Vamos seguir para opção B.

Deixe DomainException puro e BusinessException como boundary

DomainException anteriormente tinha contexto, creio que podemos deixar estas duas (message) e message (context)