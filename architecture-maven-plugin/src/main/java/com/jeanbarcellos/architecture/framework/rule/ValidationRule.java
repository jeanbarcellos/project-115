package com.jeanbarcellos.architecture.framework.rule;

/**
 * Contrato base para todas as regras
 * arquiteturais do framework.
 *
 * <p>
 * Cada regra deve possuir um código único
 * e estável utilizado para rastreamento,
 * documentação e identificação em relatórios.
 * </p>
 *
 * @author Jean Barcellos <jeanbarcellos@hotmail.com>
 */
public interface ValidationRule {

    /**
     * Retorna os metadados da regra.
     *
     * @return metadados da regra
     */
    RuleMetadata metadata();

}