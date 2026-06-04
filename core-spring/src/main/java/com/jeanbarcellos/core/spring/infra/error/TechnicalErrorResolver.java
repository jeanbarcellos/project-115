package com.jeanbarcellos.core.spring.infra.error;

import java.util.List;

import com.jeanbarcellos.core.error.TechnicalErrorType;
import com.jeanbarcellos.core.error.resolver.*;
import com.jeanbarcellos.core.spring.infra.error.resolver.*;

/**
 * Utilitário responsável por traduzir exceções técnicas (nativas, de frameworks
 * ou bibliotecas)
 * para o domínio padronizado de erros da aplicação
 * ({@link TechnicalErrorType}).
 *
 * <p>
 * <b>Estratégia de Desacoplamento (Bibliotecas Opcionais):</b><br>
 * Como esta classe pertence a uma biblioteca genérica (core/commons), ela evita
 * importar
 * dependências pesadas e opcionais (ex: Spring Security, OpenFeign,
 * Resilience4j, JJWT). Para
 * capturar exceções dessas bibliotecas sem causar erros de compilação
 * ({@code ClassNotFound}),
 * utilizamos reflexão ({@link #isInstanceOf(Class, String)}) para verificar a
 * árvore de herança.
 * </p>
 *
 * @author Jean Barcellos
 */
public final class TechnicalErrorResolver {

    private static final List<ErrorResolver> RESOLVERS = List.of(
            new CommonValidationErrorResolver(),
            new SpringValidationErrorResolver(),

            new CommonResourceErrorResolver(),
            new SpringResourceErrorResolver(),

            new CommonSecurityErrorResolver(),
            new SpringSecurityErrorResolver(),

            new CommonConflictErrorResolver(),
            new SpringConflictErrorResolver(),

            new CommonDataErrorResolver(),
            new SpringDataErrorResolver(),

            new CommonIntegrationErrorResolver(),
            new SpringIntegrationErrorResolver(),

            new CommonInfraestructureErrorResolver(),
            new SpringInfraestructureErrorResolver(),

            new CommonRateLimitErrorResolver(),
            new SpringRateLimitErrorResolver(),

            new CommonCacheErrorResolver(),
            new SpringCacheErrorResolver(),

            new CommonFallbackErrorResolver(),
            new SpringFallbackErrorResolver(),

            new CommonGenericErrorResolver(),
            new SpringGenericErrorResolver());

    /**
     * Construtor privado para ocultar o construtor público implícito,
     * garantindo que esta classe utilitária não seja instanciada.
     */
    private TechnicalErrorResolver() {
    }

    /**
     * Analisa uma exceção ({@link Throwable}) e determina qual é o
     * {@link TechnicalErrorType}
     * mais adequado correspondente.
     * <p>
     * A ordem de verificação segue o agrupamento de categorias definidas no enum.
     * </p>
     *
     * @param ex a exceção capturada que precisa ser traduzida.
     * @return o {@link TechnicalErrorType} correspondente ou {@code INTERNAL_ERROR}
     *         se não houver mapeamento.
     */
    public static TechnicalErrorType resolveType(Throwable ex) {

        if (ex == null) {
            return TechnicalErrorType.INTERNAL_ERROR;
        }

        for (ErrorResolver resolver : RESOLVERS) {

            TechnicalErrorType type = resolver.resolve(ex);

            if (type != null) {
                return type;
            }
        }

        // Se nenhuma verificação for atendida, retorna erro genérico 500
        return TechnicalErrorType.INTERNAL_ERROR;
    }

}