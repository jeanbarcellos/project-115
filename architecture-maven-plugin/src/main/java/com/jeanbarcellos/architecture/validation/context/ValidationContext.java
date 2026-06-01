package com.jeanbarcellos.architecture.validation.context;

import java.util.HashSet;
import java.util.Set;

import lombok.Getter;

/**
 * Contexto compartilhado entre todas as regras
 * executadas durante o processo de validação.
 *
 * <p>
 * Permite compartilhar informações entre
 * diferentes validadores sem acoplamento direto.
 * </p>
 *
 * <p>
 * Atualmente é utilizado para controle
 * de unicidade global dos códigos de erro.
 * </p>
 *
 * @author Jean Barcellos <jeanbarcellos@hotmail.com>
 */
@Getter
public class ValidationContext {

    /**
     * Códigos de erro já processados.
     */
    private final Set<String> errorCodes = new HashSet<>();

}