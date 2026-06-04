package com.jeanbarcellos.core.error.resolver;

/**
 * Classe base para resolvers de erro.
 *
 * <p>
 * Disponibiliza operações auxiliares para navegação
 * na hierarquia de exceções sem dependências diretas
 * de bibliotecas opcionais.
 * </p>
 *
 * @author Jean Barcellos
 */
public abstract class AbstractErrorResolver implements ErrorResolver {

    /**
     * Verifica se uma classe pertence à hierarquia de uma determinada classe
     * informada pelo nome totalmente qualificado.
     *
     * <p>
     * O algoritmo percorre:
     * </p>
     *
     * <ul>
     * <li>A própria classe;</li>
     * <li>Suas interfaces;</li>
     * <li>Toda a cadeia de superclasses.</li>
     * </ul>
     *
     * <p>
     * Esta abordagem permite identificar tipos de bibliotecas opcionais
     * sem criar dependências de compilação diretas.
     * </p>
     *
     * @param clazz           classe a ser analisada.
     * @param targetClassName nome totalmente qualificado do tipo esperado.
     * @return {@code true} quando o tipo for encontrado na hierarquia.
     */
    protected boolean isInstanceOf(Class<?> clazz, String targetClassName) {

        while (clazz != null) {

            if (clazz.getName().equals(targetClassName)) {
                return true;
            }

            for (Class<?> iface : clazz.getInterfaces()) {

                if (isInstanceOf(iface, targetClassName)) {
                    return true;
                }
            }

            clazz = clazz.getSuperclass();
        }

        return false;
    }

    /**
     * Verifica se a exceção ou qualquer uma de suas causas pertence à hierarquia de
     * um tipo
     * informado.
     *
     * @param throwable       exceção inicial.
     * @param targetClassName nome totalmente qualificado do tipo procurado.
     * @return true quando encontrado na cadeia de causas.
     */
    protected boolean hasCause(Throwable throwable, String targetClassName) {

        while (throwable != null) {

            if (isInstanceOf(
                    throwable.getClass(),
                    targetClassName)) {
                return true;
            }

            throwable = throwable.getCause();
        }

        return false;
    }

}