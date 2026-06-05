package com.jeanbarcellos.core.quarkus.infra.error;

import java.util.List;

import com.jeanbarcellos.core.error.TechnicalErrorType;
import com.jeanbarcellos.core.error.resolver.*;
import com.jeanbarcellos.core.error.resolver.ErrorResolver;
import com.jeanbarcellos.core.quarkus.infra.error.resolver.*;

/**
 * Utilitário responsável por traduzir exceções técnicas (nativas, de frameworks ou bibliotecas)
 * para o domínio padronizado de erros da aplicação ({@link TechnicalErrorType}).
 *
 * <p>
 * <b>Contexto Quarkus / Jakarta EE:</b><br>
 * Este resolver é focado nas exceções lançadas pelo JAX-RS (RESTEasy), JPA (Hibernate),
 * Bean Validation e extensões nativas do ecossistema Quarkus.
 * </p>
 *
 * <p>
 * <b>Estratégia de Desacoplamento (Bibliotecas Opcionais):</b><br>
 * Como esta classe pertence a uma biblioteca genérica (core/commons), ela evita importar
 * dependências pesadas e opcionais (ex: JWT, Redis, Fault Tolerance).
 * Para capturar exceções dessas bibliotecas sem causar erros de compilação ({@code ClassNotFound}),
 * utilizamos reflexão ({@link #isInstanceOf(Class, String)}) para verificar a árvore de herança.
 * </p>
 *
 * @author Jean Barcellos
 */
public class TechnicalErrorResolver {

    private static final List<ErrorResolver> RESOLVERS = List.of(
            new CommonValidationErrorResolver(),
            new QuarkusValidationErrorResolver(),

            new CommonResourceErrorResolver(),
            new QuarkusResourceErrorResolver(),

            new CommonSecurityErrorResolver(),
            new QuarkusSecurityErrorResolver(),

            new CommonConflictErrorResolver(),
            new QuarkusConflictErrorResolver(),

            new CommonDataErrorResolver(),
            new QuarkusDataErrorResolver(),

            new CommonIntegrationErrorResolver(),
            new QuarkusIntegrationErrorResolver(),

            new CommonInfrastructureErrorResolver(),
            new QuarkusInfrastructureErrorResolver(),

            new CommonRateLimitErrorResolver(),
            new QuarkusRateLimitErrorResolver(),

            new CommonCacheErrorResolver(),
            new QuarkusCacheErrorResolver(),

            new CommonFallbackErrorResolver(),
            new QuarkusFallbackErrorResolver(),

            new CommonGenericErrorResolver(),
            new QuarkusGenericErrorResolver());


    /**
     * Construtor privado para ocultar o construtor público implícito,
     * garantindo que esta classe utilitária não seja instanciada.
     */
    private TechnicalErrorResolver() { }

    /**
     * Analisa uma exceção ({@link Throwable}) e determina qual é o {@link TechnicalErrorType}
     * mais adequado correspondente.
     * <p>
     * A ordem de verificação segue o agrupamento de categorias definidas no enum.
     * </p>
     *
     * @param ex a exceção capturada que precisa ser traduzida.
     * @return o {@link TechnicalErrorType} correspondente ou {@code INTERNAL_ERROR} se não houver mapeamento.
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

        // #endregion

        // Se nenhuma verificação for atendida, retorna erro genérico 500
        return TechnicalErrorType.INTERNAL_ERROR;
    }

}
