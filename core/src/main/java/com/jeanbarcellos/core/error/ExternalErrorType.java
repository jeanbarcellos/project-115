package com.jeanbarcellos.core.error;

/**
 * Representa um erro oficialmente documentado e retornado
 * por um sistema externo integrado à aplicação.
 *
 * <p>
 * Esta interface define um catálogo padronizado de erros de integração,
 * funcionando como uma camada de tradução semântica entre os códigos
 * retornados pelo provider externo e os erros internos da plataforma.
 * </p>
 *
 * <p>
 * Implementações desta interface normalmente são representadas através
 * de {@code enum}s específicos por integração
 * (ex: {@code SerproErrorType}, {@code StripeErrorType},
 * {@code BacenErrorType}).
 * </p>
 *
 * <h2>Objetivos</h2>
 *
 * <ul>
 *   <li>Centralizar o catálogo de erros documentados pelo provider;</li>
 *   <li>Evitar mapeamentos procedurais espalhados pela aplicação;</li>
 *   <li>Padronizar a tradução de falhas externas para erros internos;</li>
 *   <li>Facilitar troubleshooting e observabilidade;</li>
 *   <li>Preservar rastreabilidade entre erro externo e erro interno;</li>
 *   <li>Reduzir acoplamento entre regras de integração e domínio.</li>
 * </ul>
 *
 * <h2>Importante</h2>
 *
 * <p>
 * Esta interface deve representar apenas erros oficialmente retornados
 * e documentados pelo provider externo.
 * </p>
 *
 * <p>
 * Falhas locais de infraestrutura ou comunicação da própria aplicação,
 * como:
 * </p>
 *
 * <ul>
 *   <li>{@link java.net.SocketTimeoutException}</li>
 *   <li>falhas DNS</li>
 *   <li>problemas SSL/TLS</li>
 *   <li>falhas de conexão TCP</li>
 *   <li>timeouts do client HTTP</li>
 * </ul>
 *
 * <p>
 * não devem ser modeladas como {@code ExternalErrorType},
 * pois não representam respostas reais do provider.
 * Tais cenários devem ser tratados diretamente através
 * dos erros internos da plataforma
 * (ex: {@code TechnicalErrorType}).
 * </p>
 *
 * <h2>Política de Retry</h2>
 *
 * <p>
 * O método {@link #isRetryable()} representa apenas uma sugestão
 * operacional baseada na documentação oficial do provider.
 * </p>
 *
 * <p>
 * O valor retornado não deve ser interpretado como uma decisão
 * definitiva de retentativa pela aplicação, já que a política
 * final de retry depende de fatores internos como:
 * </p>
 *
 * <ul>
 *   <li>idempotência da operação;</li>
 *   <li>criticidade da transação;</li>
 *   <li>regras de negócio;</li>
 *   <li>estratégias de resiliência;</li>
 *   <li>circuit breakers;</li>
 *   <li>configuração operacional do sistema.</li>
 * </ul>
 *
 * <h2>Exemplo</h2>
 *
 * <pre>{@code
 * NOT_FOUND(
 *     404,
 *     "NOT_FOUND",
 *     "Resource not found",
 *     false,
 *     TechnicalErrorType.RESOURCE_NOT_FOUND
 * )
 * }</pre>
 *
 * @author Jean Barcellos (jeanbarcellos@hotmail.com)
 */
public interface ExternalErrorType {

    /**
     * Status retornado pelo provider externo.
     *
     * <p>
     * Normalmente representa o HTTP Status da integração,
     * mas pode assumir outros significados dependendo
     * do protocolo utilizado.
     * </p>
     *
     * @return status retornado pelo provider
     */
    Integer getStatus();

    /**
     * Código específico retornado pelo provider externo.
     *
     * <p>
     * Este código representa a identificação semântica
     * do erro na integração e deve refletir fielmente
     * a documentação oficial do sistema externo.
     * </p>
     *
     * <p>
     * Exemplos:
     * </p>
     *
     * <ul>
     *   <li>{@code INVALID_DOCUMENT}</li>
     *   <li>{@code RATE_LIMIT}</li>
     *   <li>{@code USER_NOT_FOUND}</li>
     *   <li>{@code P200-001}</li>
     * </ul>
     *
     * @return código do erro retornado pelo provider
     */
    String getCode();

    /**
     * Descrição oficial do erro retornado pelo provider.
     *
     * <p>
     * Sempre que possível, esta descrição deve refletir
     * fielmente a documentação técnica da integração,
     * facilitando troubleshooting e rastreabilidade.
     * </p>
     *
     * @return descrição oficial do erro
     */
    String getDescription(); // getDescription

    /**
     * Indica se a documentação do provider sugere que
     * a falha possa ser temporária e uma nova tentativa
     * tenha chance de sucesso.
     *
     * <p>
     * Este método representa apenas um hint operacional
     * fornecido pelo sistema externo e não deve ser usado
     * isoladamente como política definitiva de retry.
     * </p>
     * <p>
     * O valor deste método é decidido com base na documentação técnica do provider
     * (ex: falhas de rede ou indisponibilidade temporária geralmente retornam
     * {@code true},
     * enquanto erros de negócio ou de credenciais inválidas retornam
     * {@code false}).
     * </p>
     *
     * @return {@code true} se for seguro realizar uma nova tentativa, {@code false}
     *         caso contrário.
     */
    boolean isRetryable();

    /**
     * Obtém o erro interno da plataforma associado
     * ao erro retornado pelo provider externo.
     *
     * <p>
     * Este mapeamento funciona como uma camada
     * anti-corrupção (ACL), traduzindo os erros
     * específicos da integração para a taxonomia
     * padronizada da aplicação.
     * </p>
     *
     * <p>
     * O erro retornado pode pertencer tanto ao catálogo
     * técnico compartilhado do core quanto a catálogos
     * específicos do microserviço.
     * </p>
     *
     * @return erro interno mapeado
     */
    ErrorType getErrorType();

}