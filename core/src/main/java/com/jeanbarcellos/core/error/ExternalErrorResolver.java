package com.jeanbarcellos.core.error;

public interface ExternalErrorResolver<T extends ExternalErrorType> {

    T resolve(Integer status, String code);
}