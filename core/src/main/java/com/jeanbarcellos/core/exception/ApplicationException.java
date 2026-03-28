package com.jeanbarcellos.core.exception;

/**
 * Application Exception - Classe base obrigatória para todas as exceções da
 * aplicação (Unchecked Exception).
 * <p>
 * Todas as exceções customizadas devem obrigatoriamente herdar desta classe.
 * Ela é a responsável por transportar o estado comum, mensagens e metadados
 * para a camada de tratamento global.
 * </p>
 * <p>
 * <b>Extensibilidade:</b> Se a exceção representar um erro técnico, de sistema
 * ou
 * de infraestrutura genérico, ela deve estender diretamente esta classe.
 * </p>
 *
 * @author Jean Barcellos (jeanbarcellos@hotmail.com)
 */
public class ApplicationException extends RuntimeException {
    /**
     * Constrói uma nova exceção com a mensagem detalhada especificada.
     *
     * @param message A mensagem de erro detalhada.
     */
    public ApplicationException(String message) {
        super(message);
    }

    /**
     * Constrói uma nova exceção com a mensagem detalhada e a causa raiz.
     *
     * @param message A mensagem de erro detalhada.
     * @param cause   A exceção original que causou este erro.
     */
    public ApplicationException(String message, Throwable cause) {
        super(message, cause);
    }
}
