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
