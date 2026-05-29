package com.jeanbarcellos.core.error;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Representa uma violação de regra ou invariante de domínio.
 *
 * <p>
 * Diferentemente de {@link ValidationError},
 * esta classe representa inconsistências detectadas
 * dentro do modelo de domínio e não erros de validação
 * de entrada.
 * </p>
 *
 * <p>
 * Exemplos:
 * </p>
 *
 * <ul>
 * <li>saldo insuficiente;</li>
 * <li>conta bloqueada;</li>
 * <li>limite excedido;</li>
 * <li>estado inválido para a operação.</li>
 * </ul>
 *
 * @author Jean Barcellos <jeanbarcellos@hotmail.com>
 */
@Getter
@AllArgsConstructor
public class DomainViolation {

    /**
     * Nome do atributo ou regra violada.
     */
    private final String field;

    /**
     * Descrição da violação.
     */
    private final String message;

    /**
     * Valor rejeitado.
     */
    private final Object rejectedValue;

    /**
     * Cria uma violação de domínio sem valor rejeitado.
     *
     * @param field   atributo ou regra violada
     * @param message descrição da violação
     * @return nova instância de {@link DomainViolation}
     */
    public static DomainViolation of(String field, String message) {
        return new DomainViolation(field, message, null);
    }

    /**
     * Cria uma violação de domínio completa.
     *
     * @param field         atributo ou regra violada
     * @param message       descrição da violação
     * @param rejectedValue valor rejeitado
     * @return nova instância de {@link DomainViolation}
     */
    public static DomainViolation of(String field, String message, Object rejectedValue) {
        return new DomainViolation(field, message, rejectedValue);
    }
}