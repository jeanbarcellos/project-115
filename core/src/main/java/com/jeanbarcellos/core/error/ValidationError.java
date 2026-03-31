package com.jeanbarcellos.core.error;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Representa um erro de validação de campo.
 *
 * <p>
 * Utilizado como extensão do {@link ErrorResponse} para detalhar
 * violações de regras de validação.
 * </p>
 *
 * @author Jean Barcellos <jeanbarcellos@hotmail.com>
 */
@Getter
@AllArgsConstructor
public class ValidationError {

    /**
     * O nome do campo, parâmetro ou propriedade que falhou (ex: "usuario.cpf").
     */
    private final String field;

    /**
     * O motivo da falha (ex: "O CPF informado não possui 11 dígitos").
     */
    private final String message;

    /**
     * O valor original enviado pelo cliente que foi rejeitado na validação.
     * <p>
     * <b>Nota sobre SonarQube (S1948):</b> O campo é do tipo Object para suportar
     * diversos tipos de retorno (String, Number, Boolean). Embora Object não seja
     * estritamente serializável, os valores injetados aqui em tempo de execução
     * (provenientes de payloads JSON) são tipos básicos serializáveis.
     * </p>
     */
    private final Object rejectedValue;

    /**
     * Cria uma instância de erro de campo informando apenas o campo e o motivo.
     * O valor rejeitado assumirá {@code null}.
     *
     * @param field   O nome do campo com erro.
     * @param message A mensagem descrevendo o erro.
     * @return Uma nova instância de {@link ValidationError}.
     */
    public static ValidationError of(String field, String message) {
        return new ValidationError(field, message, null);
    }

    /**
     * Cria uma instância de erro de campo completa, informando o valor que foi
     * rejeitado.
     *
     * @param field         O nome do campo com erro.
     * @param message       A mensagem descrevendo o erro.
     * @param rejectedValue O valor inválido enviado na requisição.
     * @return Uma nova instância de {@link ValidationError}.
     */
    public static ValidationError of(String field, String message, Object rejectedValue) {
        return new ValidationError(field, message, rejectedValue);
    }
}