package com.jeanbarcellos.core.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Representa a categoria arquitetural/operacional
 * de um erro na aplicação.
 *
 * <p>
 * Utilizada principalmente para:
 * </p>
 *
 * <ul>
 * <li>logging estruturado;</li>
 * <li>observabilidade;</li>
 * <li>telemetria;</li>
 * <li>dashboards;</li>
 * <li>classificação operacional;</li>
 * <li>agrupamento de falhas.</li>
 * </ul>
 *
 * <p>
 * Esta enumeração NÃO representa:
 * </p>
 *
 * <ul>
 * <li>criticidade operacional;</li>
 * <li>nível de severidade;</li>
 * <li>nível de log;</li>
 * <li>política de alerta.</li>
 * </ul>
 *
 * <p>
 * Tais características dependem do contexto
 * da falha e devem ser resolvidas dinamicamente.
 * </p>
 *
 * @author Jean Barcellos <jeanbarcellos@hotmail.com>
 */
@Getter
@RequiredArgsConstructor
public enum ErrorCategory {

    /**
     * Erros técnicos/internos da aplicação.
     */
    TECHNICAL("technical"),

    /**
     * Erros de negócio.
     */
    BUSINESS("business"),

    /**
     * Erros de validação.
     */
    VALIDATION("validation"),

    /**
     * Erros de integração externa.
     */
    INTEGRATION("integration"),

    /**
     * Erros relacionados ao domínio.
     */
    DOMAIN("domain"),

    /**
     * Erros genéricos da aplicação.
     */
    APPLICATION("application");

    /**
     * Código textual da categoria.
     *
     * <p>
     * Utilizado principalmente em logs,
     * métricas e observabilidade.
     * </p>
     */
    private final String code;
}