package com.jeanbarcellos.architecture.framework.report;

import lombok.Builder;
import lombok.Getter;

/**
 * Representa uma violação arquitetural identificada
 * durante a execução do plugin.
 *
 * <p>
 * Cada violação contém informações suficientes
 * para auxiliar a localização e correção do problema.
 * </p>
 *
 * <p>
 * Atualmente são registradas:
 * </p>
 *
 * <ul>
 * <li>categoria da validação;</li>
 * <li>classe responsável pela violação;</li>
 * <li>arquivo relacionado;</li>
 * <li>mensagem detalhada.</li>
 * </ul>
 *
 * <p>
 * A estrutura já está preparada para futura evolução
 * com suporte a localização precisa em código-fonte
 * (linha e coluna).
 * </p>
 *
 * @author Jean Barcellos <jeanbarcellos@hotmail.com>
 */
@Getter
@Builder
public class ValidationViolation {

    /**
     * Categoria da validação.
     */
    private final ValidationCategory category;

    /**
     * Nome completo da classe relacionada
     * à violação.
     */
    private final String className;

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