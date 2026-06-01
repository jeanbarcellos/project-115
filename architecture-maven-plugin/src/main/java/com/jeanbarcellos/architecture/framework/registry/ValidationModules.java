package com.jeanbarcellos.architecture.framework.registry;

import java.util.List;

import com.jeanbarcellos.architecture.framework.validator.ValidatorModule;
import com.jeanbarcellos.architecture.governance.error.validator.ErrorCatalogValidator;
import com.jeanbarcellos.architecture.governance.external.validator.ExternalErrorCatalogValidator;

/**
 * Registro central dos módulos
 * de validação arquitetural.
 *
 * @author Jean Barcellos <jeanbarcellos@hotmail.com>
 */
public final class ValidationModules {

    private ValidationModules() {
    }

    /**
     * Retorna todos os módulos registrados.
     *
     * @return módulos ativos
     */
    public static List<ValidatorModule> all() {

        return List.of(
                new ErrorCatalogValidator(),
                new ExternalErrorCatalogValidator());
    }

}