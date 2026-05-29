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
     * Título curto, estável e legível do erro.
     *
     * <p>
     * Este valor normalmente é utilizado no campo
     * {@code title} do Problem Details (RFC 7807).
     * </p>
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
     * Indica se a operação associada ao erro
     * pode ser tentada novamente.
     *
     * <p>
     * Este atributo representa uma recomendação
     * operacional e não uma garantia de sucesso
     * em uma nova tentativa.
     * </p>
     *
     * @return {@code true} quando uma nova tentativa
     *         é recomendada; caso contrário {@code false}
     */
    boolean isRetryable();
}