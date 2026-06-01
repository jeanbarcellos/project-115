package com.jeanbarcellos.architecture.framework.report;

import com.jeanbarcellos.architecture.framework.rule.ValidationRule;

import lombok.Builder;
import lombok.Getter;

/**
 * Representa uma violação arquitetural
 * encontrada durante o processo de validação.
 *
 * <p>
 * Cada violação contém informações suficientes
 * para identificação e correção do problema.
 * </p>
 *
 * <p>
 * A estrutura já está preparada para futura
 * evolução com suporte a localização em
 * código-fonte (arquivo, linha e coluna).
 * </p>
 *
 * @author Jean Barcellos <jeanbarcellos@hotmail.com>
 */
@Getter
@Builder
public class ValidationViolation {

    /**
     * Módulo de governança responsável pela validação.
     */
    private final ValidationModule module;

    /**
     * Categoria da validação.
     */
    private final ValidationCategory category;

    /**
     * Regra que falhou.
     */
    private final ValidationRule rule;

    /**
     * Nome completo da classe relacionada
     * à violação.
     */
    private final String className;

    /**
     * Elemento específico relacionado
     * à violação.
     *
     * <p>
     * Exemplos:
     * </p>
     *
     * <ul>
     * <li>USER_NOT_FOUND</li>
     * <li>WALLET_NOT_FOUND</li>
     * <li>CREATE_USER_COMMAND</li>
     * </ul>
     */
    private final String element;

    /**
     * Caminho do arquivo relacionado.
     *
     * <p>
     * Pode ser {@code null} quando a informação
     * ainda não estiver disponível.
     * </p>
     */
    private final String filePath;

    /**
     * Linha do código-fonte.
     *
     * <p>
     * Reservado para futura implementação
     * via parser de código-fonte.
     * </p>
     */
    private final Integer lineNumber;

    /**
     * Coluna do código-fonte.
     *
     * <p>
     * Reservado para futura implementação
     * via parser de código-fonte.
     * </p>
     */
    private final Integer columnNumber;

    /**
     * Descrição da violação.
     */
    private final String message;

}