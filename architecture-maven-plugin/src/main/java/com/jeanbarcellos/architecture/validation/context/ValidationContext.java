package com.jeanbarcellos.architecture.validation.context;

import java.util.HashSet;
import java.util.Set;

import lombok.Getter;

/**
 * Contexto compartilhado entre as regras de validação.
 *
 * @author Jean Barcellos <jeanbarcellos@hotmail.com>
 */
@Getter
public class ValidationContext {

    private final Set<String> errorCodes = new HashSet<>();
}