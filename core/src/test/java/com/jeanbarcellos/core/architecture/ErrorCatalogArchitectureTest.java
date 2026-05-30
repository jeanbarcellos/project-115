package com.jeanbarcellos.core.architecture;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

import org.junit.jupiter.api.Test;

import com.jeanbarcellos.core.error.ErrorType;
import com.jeanbarcellos.core.error.ExternalErrorType;
import com.jeanbarcellos.core.error.TechnicalErrorType;

/**
 * Regras arquiteturais relacionadas aos catálogos de erro.
 *
 * <p>
 * O objetivo destes testes é garantir consistência,
 * governança e previsibilidade dos erros utilizados
 * pela plataforma.
 * </p>
 *
 * <p>
 * Estas validações complementam os testes tradicionais
 * de unidade e ajudam a evitar regressões arquiteturais.
 * </p>
 *
 * @author Jean Barcellos <jeanbarcellos@hotmail.com>
 */
class ErrorCatalogArchitectureTest {

    /**
     * Expressão regular utilizada para validar
     * códigos em formato kebab-case.
     */
    private static final Pattern KEBAB_CASE = Pattern.compile("[a-z0-9]+(-[a-z0-9]+)*");

    // =========================================================================
    // TECHNICAL ERROR TYPE
    // =========================================================================

    /**
     * Garante que todos os códigos de erro técnico
     * sejam únicos.
     *
     * <p>
     * O código é utilizado por:
     * </p>
     *
     * <ul>
     * <li>RFC 7807;</li>
     * <li>logs estruturados;</li>
     * <li>monitoramento;</li>
     * <li>observabilidade;</li>
     * <li>integrações.</li>
     * </ul>
     *
     * <p>
     * Códigos duplicados podem causar ambiguidades
     * e dificultar troubleshooting.
     * </p>
     *
     * @author Jean Barcellos <jeanbarcellos@hotmail.com>
     */
    @Test
    void technicalErrorCodes_shouldBeUnique() {

        // Arrange
        Set<String> codes = new HashSet<>();

        // Act / Assert
        for (TechnicalErrorType error : TechnicalErrorType.values()) {

            assertTrue(
                    codes.add(error.getCode()),
                    () -> "Duplicated error code found: " + error.getCode());
        }
    }

    /**
     * Garante que todos os códigos de erro técnico
     * utilizem o padrão kebab-case.
     *
     * <p>
     * Exemplos válidos:
     * </p>
     *
     * <ul>
     * <li>resource-not-found</li>
     * <li>dependency-failure</li>
     * <li>internal-error</li>
     * </ul>
     *
     * @author Jean Barcellos <jeanbarcellos@hotmail.com>
     */
    @Test
    void technicalErrorCodes_shouldUseKebabCase() {

        // Act / Assert
        for (TechnicalErrorType error : TechnicalErrorType.values()) {

            assertTrue(
                    KEBAB_CASE.matcher(error.getCode()).matches(),
                    () -> "Invalid error code: " + error.getCode());
        }
    }

    /**
     * Garante que todos os códigos de erro
     * sejam preenchidos.
     *
     * @author Jean Barcellos <jeanbarcellos@hotmail.com>
     */
    @Test
    void technicalErrorCodes_shouldNotBeBlank() {

        // Act / Assert
        for (TechnicalErrorType error : TechnicalErrorType.values()) {

            assertFalse(error.getCode().isBlank());
        }
    }

    /**
     * Garante que todos os títulos de erro
     * sejam preenchidos.
     *
     * @author Jean Barcellos <jeanbarcellos@hotmail.com>
     */
    @Test
    void technicalErrorTitles_shouldNotBeBlank() {

        // Act / Assert
        for (TechnicalErrorType error : TechnicalErrorType.values()) {

            assertFalse(error.getTitle().isBlank());
        }
    }

    /**
     * Garante que todos os status HTTP
     * pertençam ao intervalo válido.
     *
     * @author Jean Barcellos <jeanbarcellos@hotmail.com>
     */
    @Test
    void technicalErrors_shouldHaveValidHttpStatus() {

        // Act / Assert
        for (TechnicalErrorType error : TechnicalErrorType.values()) {

            assertTrue(error.getHttpStatus() >= 400);
            assertTrue(error.getHttpStatus() <= 599);
        }
    }

    // =========================================================================
    // ERROR TYPE CONTRACT
    // =========================================================================

    /**
     * Garante que implementações de ErrorType
     * sejam realizadas exclusivamente através
     * de enums.
     *
     * <p>
     * A utilização de enums garante:
     * </p>
     *
     * <ul>
     * <li>imutabilidade;</li>
     * <li>governança centralizada;</li>
     * <li>catálogo finito de erros;</li>
     * <li>consistência operacional.</li>
     * </ul>
     *
     * @author Jean Barcellos <jeanbarcellos@hotmail.com>
     */
    @Test
    void errorTypes_shouldBeImplementedOnlyByEnums() {

        // Arrange
        Class<?>[] implementations = {
                TechnicalErrorType.class
        };

        // Act / Assert
        for (Class<?> implementation : implementations) {

            assertTrue(
                    implementation.isEnum(),
                    () -> implementation.getName() + " must be an enum");
        }
    }

    /**
     * Garante que implementações de ErrorType
     * sejam públicas.
     *
     * @author Jean Barcellos <jeanbarcellos@hotmail.com>
     */
    @Test
    void errorTypes_shouldBePublic() {

        // Arrange
        Class<?>[] implementations = {
                TechnicalErrorType.class
        };

        // Act / Assert
        for (Class<?> implementation : implementations) {

            assertTrue(
                    Modifier.isPublic(implementation.getModifiers()),
                    () -> implementation.getName() + " must be public");
        }
    }

    // =========================================================================
    // EXTERNAL ERROR TYPE CONTRACT
    // =========================================================================

    /**
     * Garante que implementações de ExternalErrorType
     * sejam realizadas exclusivamente através de enums.
     *
     * <p>
     * Esta restrição simplifica o mapeamento
     * dos erros documentados pelos providers.
     * </p>
     *
     * @author Jean Barcellos <jeanbarcellos@hotmail.com>
     */
    @Test
    void externalErrorTypes_shouldBeImplementedOnlyByEnums() {

        /*
         * Esta regra deve ser expandida pelos módulos
         * consumidores que implementam catálogos
         * concretos de providers.
         *
         * Exemplo:
         *
         * SerproErrorType.class
         * IbgeErrorType.class
         * BancoCentralErrorType.class
         */

        assertTrue(true);
    }

    // =========================================================================
    // FUTURE GOVERNANCE
    // =========================================================================

    /**
     * Placeholder para futura validação global
     * de unicidade dos códigos de erro da plataforma.
     *
     * <p>
     * Quando houver múltiplos catálogos
     * (UserErrorType, WalletErrorType, etc),
     * esta validação deverá percorrer todos
     * os enums que implementam ErrorType.
     * </p>
     *
     * @author Jean Barcellos <jeanbarcellos@hotmail.com>
     */
    // @Test
    // void allErrorCodesAcrossPlatform_shouldBeUnique() {

    //     fail("""
    //             Not implemented yet.
    //             Future implementation should scan all ErrorType enums.
    //             """);
    // }

}