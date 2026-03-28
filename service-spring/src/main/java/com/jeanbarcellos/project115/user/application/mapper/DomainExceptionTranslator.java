package com.jeanbarcellos.project115.user.application.mapper;

import com.jeanbarcellos.core.error.ApiErrorType;
import com.jeanbarcellos.core.exception.BusinessException;
import com.jeanbarcellos.core.exception.DomainException;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DomainExceptionTranslator {

    public static BusinessException map(DomainException ex, ApiErrorType type) {

        // ⚠️ Aqui é onde você decide o erro externo
        // Exemplo simples (você pode evoluir isso depois)
        return new BusinessException(
                type,
                ex.getMessage(),
                ex.getContext() // opcional expor / aqui você pode filtrar se quiser
        );
    }
}