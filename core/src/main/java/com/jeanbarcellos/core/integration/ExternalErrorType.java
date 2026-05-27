package com.jeanbarcellos.core.integration;

/**
 * Representa erros vindos de sistemas externos.
 * *
 * <p>
 * NOTA: Esta interface é de uso interno para mapeamento de integrações baseadas
 * na documentação oficial do provider e NÃO deve ser exposta diretamente nas
 * respostas da API.
 * </p>
 * *
 * <p>
 * <b>Orientação ao Desenvolvedor:</b><br>
 * Ao implementar esta interface (geralmente através de um {@code enum}), é
 * fundamental ler
 * atentamente a documentação oficial da API externa que está sendo integrada.
 * Certifique-se de
 * mapear todos os possíveis códigos e descrições de erro documentados pelo
 * provedor.
 * Um mapeamento exaustivo e fiel à documentação garante um tratamento de falhas
 * mais
 * preciso, facilita a análise de logs e evita comportamentos inesperados na
 * nossa aplicação.
 * </p>
 */
public interface ExternalErrorType {

    /**
     * Obtém o código ou status de erro informado na documentação oficial da API
     * externa.
     *
     * @return O código/status de erro do provider.
     */
    // int getCode();
    String getCode(); // Código do provider

    /**
     * Obtém a descrição do erro exatamente como descrita na documentação oficial da
     * API externa.
     *
     * @return A descrição oficial do erro.
     */
    String getDescription(); // getDescription
    // String getTitle();       // descrição do provider
    // String getMessage();     // mensagem original


    /**
     * Indica se a operação que resultou neste erro pode ser executada novamente
     * de forma segura (retentativa).
     * *
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
    boolean isRetryable();   // decidido com base na documentação do provider

}