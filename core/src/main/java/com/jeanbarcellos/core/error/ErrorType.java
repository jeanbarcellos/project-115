package com.jeanbarcellos.core.error;

/**
 * Representa um tipo de erro conhecido pela aplicação.
 *
 * <p>
 * O {@code code} identifica o erro de forma única e será utilizado
 * para resolver a URI do problema no adapter (ex: /problems/{code}).
 * </p>
 *
 * @author Jean Barcellos <jeanbarcellos@hotmail.com>
 */
public interface ErrorType {

    /**
     * Código único do erro.
     *
     * @return código do erro
     */
    String getCode();

    /**
     * Título curto do erro.
     *
     * @return título do erro
     */
    String getTitle();

    /**
     * Status HTTP associado ao erro.
     *
     * @return status HTTP
     */
    int getHttpStatus();

    /**
     * Indica se a operação pode ser tentada novamente.
     *
     * Ex:
     * - timeout → true
     * - validação → false
     */
    boolean isRetryable();
}