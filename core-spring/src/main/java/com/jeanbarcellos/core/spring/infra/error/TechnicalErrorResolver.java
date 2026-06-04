package com.jeanbarcellos.core.spring.infra.error;

import java.util.List;

import com.jeanbarcellos.core.error.TechnicalErrorType;
import com.jeanbarcellos.core.error.resolver.ErrorResolver;
import com.jeanbarcellos.core.spring.infra.error.resolver.SpringCacheErrorResolver;
import com.jeanbarcellos.core.spring.infra.error.resolver.SpringConflictErrorResolver;
import com.jeanbarcellos.core.spring.infra.error.resolver.SpringDataErrorResolver;
import com.jeanbarcellos.core.spring.infra.error.resolver.SpringFallbackErrorResolver;
import com.jeanbarcellos.core.spring.infra.error.resolver.SpringGenericErrorReolver;
import com.jeanbarcellos.core.spring.infra.error.resolver.SpringInfraestructureErrorResolver;
import com.jeanbarcellos.core.spring.infra.error.resolver.SpringIntegrationErrorResolver;
import com.jeanbarcellos.core.spring.infra.error.resolver.SpringRateLimitErrorResolver;
import com.jeanbarcellos.core.spring.infra.error.resolver.SpringResourceErrorResolver;
import com.jeanbarcellos.core.spring.infra.error.resolver.SpringSecurityErrorResolver;
import com.jeanbarcellos.core.spring.infra.error.resolver.SpringValidationErrorResolver;

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
            new SpringValidationErrorResolver(),
            new SpringResourceErrorResolver(),
            new SpringSecurityErrorResolver(),
            new SpringConflictErrorResolver(),
            new SpringDataErrorResolver(),
            new SpringIntegrationErrorResolver(),
            new SpringInfraestructureErrorResolver(),
            new SpringRateLimitErrorResolver(),
            new SpringCacheErrorResolver(),
            new SpringFallbackErrorResolver(),
            new SpringGenericErrorReolver());

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