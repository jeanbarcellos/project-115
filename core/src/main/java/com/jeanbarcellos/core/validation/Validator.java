package com.jeanbarcellos.core.validation;

import java.util.Comparator;
import java.util.List;
import java.util.Set;

import com.jeanbarcellos.core.constants.MessageConstants;
import com.jeanbarcellos.core.error.ValidationError;
import com.jeanbarcellos.core.exception.ValidationException;

import jakarta.validation.ConstraintViolation;
import lombok.RequiredArgsConstructor;

/**
 * Componente principal (Facade) para validação de objetos utilizando as anotações do ecossistema
 * Jakarta Bean Validation.
 *
 * <p>
 * Esta classe atua como um wrapper em torno do {@link jakarta.validation.Validator} padrão, com o
 * objetivo de capturar violações de restrições (Constraints) e traduzi-las automaticamente para o
 * modelo de exceções rico da nossa aplicação ({@link ValidationException} e
 * {@link ValidationError}).
 * </p>
 *
 * @author Jean Barcellos <jeanbarcellos@hotmail.com>
 */
@RequiredArgsConstructor
public class Validator {

    /**
     * Motor de validação interno do ecossistema Jakarta.
     */
    protected final jakarta.validation.Validator innerValidator;

    /**
     * Avalia as restrições (anotações) de um objeto e obtém a coleção bruta de
     * violações.
     *
     * @param model objeto a ser validado (não deve ser nulo)
     * @param <T>   tipo do objeto
     * @return um {@link Set} contendo as violações encontradas. Caso não haja
     *         erros,
     *         retorna um conjunto vazio.
     * @throws IllegalArgumentException caso o objeto fornecido seja nulo.
     */
    public <T> Set<ConstraintViolation<T>> getViolations(T model) {
        return this.innerValidator.validate(model);
    }

    /**
     * Avalia as restrições (anotações) de múltiplos objetos e obtém a coleção
     * bruta combinada de violações.
     *
     * @param models array de objetos a serem validados (varargs)
     * @param <T>    tipo dos objetos
     * @return um {@link Set} contendo as violações encontradas em todos os objetos.
     */
    @SafeVarargs
    public final <T> Set<ConstraintViolation<T>> getViolations(T... models) {
        Set<ConstraintViolation<T>> allViolations = new java.util.LinkedHashSet<>();
        if (models != null) {
            for (T model : models) {
                if (model != null) {
                    allViolations.addAll(this.getViolations(model));
                }
            }
        }
        return allViolations;
    }

    /**
     * Avalia as restrições (anotações) de uma coleção de objetos e obtém a coleção
     * bruta combinada de violações.
     *
     * @param models coleção (Lista, Set, etc) de objetos a serem validados
     * @param <T>    tipo dos objetos
     * @return um {@link Set} contendo as violações encontradas em todos os objetos.
     */
    public <T> Set<ConstraintViolation<T>> getViolations(Iterable<T> models) {
        Set<ConstraintViolation<T>> allViolations = new java.util.LinkedHashSet<>();
        if (models != null) {
            for (T model : models) {
                if (model != null) {
                    allViolations.addAll(this.getViolations(model));
                }
            }
        }
        return allViolations;
    }

    /**
     * Valida múltiplos objetos e, em caso de inconsistências em qualquer um deles,
     * interrompe o fluxo lançando uma única exceção contendo todos os erros agrupados.
     *
     * @param models array de objetos a serem validados (varargs)
     * @param <T>    tipo dos objetos
     * @throws ValidationException se uma ou mais restrições forem violadas.
     */
    @SafeVarargs
    public final <T> void validate(T... models) {
        Set<ConstraintViolation<T>> violations = this.getViolations(models);

        if (!violations.isEmpty()) {
            throw createValidationException(violations);
        }
    }

    /**
     * Valida o objeto e, em caso de inconsistências, interrompe o fluxo lançando
     * uma exceção de validação contendo os detalhes dos campos rejeitados.
     *
     * @param model objeto a ser validado
     * @param <T>   tipo do objeto
     * @throws ValidationException se uma ou mais restrições do objeto forem
     *                             violadas.
     */
    public <T> void validate(T model) {
        Set<ConstraintViolation<T>> violations = this.getViolations(model);

        if (!violations.isEmpty()) {
            throw createValidationException(violations);
        }
    }

    /**
     * Cria uma {@link ValidationException} a partir de um conjunto de restrições
     * violadas,utilizando a mensagem de erro padrão da classe.
     *
     * @param violations conjunto de violações retornadas pelo Jakarta Validation
     * @param <T>        tipo do objeto validado
     * @return uma instância configurada de {@link ValidationException}
     */
    public static <T> ValidationException createValidationException(
            Set<ConstraintViolation<T>> violations) {
        return createValidationException(MessageConstants.ERRO_VALIDACAO_DADOS, violations);
    }

    /**
     * Cria uma {@link ValidationException} a partir de um conjunto de restrições
     * violadas, permitindo a customização da mensagem principal do erro.
     *
     * @param message    mensagem principal descritiva do erro
     * @param violations conjunto de violações retornadas pelo Jakarta Validation
     * @param <T>        tipo do objeto validado
     * @return uma instância configurada de {@link ValidationException}
     */
    public static <T> ValidationException createValidationException(
            String message, Set<ConstraintViolation<T>> violations) {
        return new ValidationException(message, createValidationErrors(violations));
    }

    /**
     * Converte um conjunto de violações nativas do Jakarta
     * ({@link ConstraintViolation}) em uma lista padronizada de erros de nossa
     * aplicação ({@link ValidationError}).
     *
     * @param violations conjunto de violações capturadas
     * @param <T>        tipo do objeto validado
     * @return lista imutável contendo os erros de validação traduzidos
     */
    public static <T> List<ValidationError> createValidationErrors(
            Set<ConstraintViolation<T>> violations) {
        return violations.stream()
                .map(Validator::createValidationError)
                .sorted(Comparator.comparing(ValidationError::getField))
                .toList();
    }

    /**
     * Converte uma única violação nativa ({@link ConstraintViolation}) no modelo
     * de erro detalhado da aplicação ({@link ValidationError}).
     *
     * @param violation violação individual capturada
     * @param <T>       tipo do objeto validado
     * @return instância de {@link ValidationError} com campo, mensagem e valor
     *         rejeitado
     */
    public static <T> ValidationError createValidationError(ConstraintViolation<T> violation) {
        // Conforme JSR 380, getPropertyPath() nunca é nulo.
        String propertyPath = violation.getPropertyPath().toString();

        // getInvalidValue() é nulo se a restrição falhou devido a um valor nulo (ex: @NotNull)
        Object invalidValue = violation.getInvalidValue();
        String rejectedValue = invalidValue != null ? String.valueOf(invalidValue) : null;

        return ValidationError.of(propertyPath, violation.getMessage(), rejectedValue);
    }

}
