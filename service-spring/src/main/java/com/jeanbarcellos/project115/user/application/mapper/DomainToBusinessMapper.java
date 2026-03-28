package com.jeanbarcellos.project115.user.application.mapper;

import com.jeanbarcellos.core.exception.BusinessException;
import com.jeanbarcellos.core.exception.DomainException;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DomainToBusinessMapper {

    public static BusinessException map(DomainException ex) {

        return new BusinessException(
                ex.getType(),
                ex.getMessage(),
                ex.getType().httpStatus(),
                ex.getContext() // aqui você pode filtrar se quiser
        );
    }
}