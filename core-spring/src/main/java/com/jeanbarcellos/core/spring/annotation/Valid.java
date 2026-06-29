package com.jeanbarcellos.core.spring.annotation;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indica que o elemento deve ser validado pelo motor do Cadastro Core. Caso a validação falhe, uma
 * ValidationException será lançada.
 *
 * <p>
 * <b>Nota sobre uso em Fields:</b> Para validação em cascata (objetos aninhados), o motor interno
 * ainda exige o uso de {@link jakarta.validation.Valid} em conjunto.
 * </p>
 *
 * @author Jean Barcellos <jeanbarcellos@hotmail.com>
 */
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.CONSTRUCTOR, ElementType.PARAMETER, ElementType.TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Valid {
}
