package com.jeanbarcellos.core.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Constantes de mensagens genéricas para o ecossistema do MS.
 *
 * <p>Centraliza strings usadas em configurações, exceptions, validações comuns, etc.
 * Prepara o terreno para uma futura extração para biblioteca core ou uso de
 * MessageSource (i18n).
 *
 * @author Jean Barcellos (jeanbarcellos@hotmail.com)
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class MessageConstants {

    // ==============================================================================
    // MENSAGENS GERAIS / SISTEMA (500)
    // ==============================================================================

    public static final String ERRO_SERVICO_INESPERADO = "Ocorreu um erro inesperado no serviço. Se o problema persistir entre em contato com o administrador.";

    // ==============================================================================
    // MENSAGENS DE REQUISIÇÃO E VALIDAÇÃO (400 / 422)
    // ==============================================================================

    public static final String ERRO_REQUISICAO_INVALIDA = "A requisição contém parâmetros ou payload inválidos. Verifique os detalhes do erro.";
    public static final String ERRO_VALIDACAO_DADOS = "Os dados informados não são válidos. Por favor, verifique os campos destacados.";
    public static final String ERRO_PARAMETRO_TIPO_INVALIDO = "O parâmetro '%s' recebeu um valor inválido. Tipo esperado: %s.";
    public static final String ERRO_PARAMETRO_OBRIGATORIO_AUSENTE = "O parâmetro de query obrigatório '%s' está ausente na requisição.";
    public static final String ERRO_CORPO_REQUISICAO_MALFORMADO = "O corpo da requisição (payload) está ausente, malformado ou contém tipos de dados incompatíveis.";

    // ==============================================================================
    // MENSAGENS DE RECURSO / ROTEAMENTO (404 / 405 / 415)
    // ==============================================================================

    public static final String ERRO_RECURSO_NAO_ENCONTRADO = "Recurso não encontrado.";
    public static final String ERRO_ROTA_NAO_ENCONTRADA = "A rota '%s %s' não foi encontrada nesta API.";
    public static final String ERRO_METODO_NAO_SUPORTADO = "O método HTTP '%s' não é suportado para esta rota.";
    public static final String ERRO_MEDIA_TYPE_NAO_SUPORTADO = "O Media Type '%s' não é suportado. Verifique o cabeçalho 'Content-Type'.";

    // ==============================================================================
    // MENSAGENS DE INTEGRAÇÃO / REDE
    // ==============================================================================

    public static final String ERRO_CONEXAO_RECUSADA = "Conexão recusada: O serviço destino pode estar fora do ar ou bloqueado por firewall.";
    public static final String ERRO_HOST_DESCONHECIDO = "Host desconhecido: Falha de DNS ao tentar resolver o endereço do serviço destino.";
    public static final String ERRO_TEMPO_LIMITE_EXCEDIDO = "Tempo limite excedido (Timeout): O serviço destino demorou muito para responder.";
    public static final String ERRO_COMUNICACAO_REDE = "Erro de comunicação na camada de rede com o serviço externo: %s";
    public static final String ERRO_RESPOSTA_EXTERNA_MALFORMADA = "Erro ao processar a resposta do serviço externo. O formato dos dados (payload) recebidos é inválido ou incompatível.";

    // ==============================================================================
    // MENSAGENS DE AUTORIZAÇÃO / SEGURANÇA
    // ==============================================================================

    public static final String ERRO_FALHA_AUTORIZACAO = "Falha de autorização na comunicação com o provedor de identidade ou serviço protegido.";

    // ==============================================================================
    // MENSAGENS EXCLUSIVAS DE LOG ESTRUTURADO (Não expostas ao cliente)
    // ==============================================================================

    public static final String ERRO_LOG_VIOLACAO_CONSTRAINTS = "Violação de constraints de validação (%d erros).";
    public static final String ERRO_LOG_PAYLOAD_INVALIDO = "Payload inválido (%d erros).";
    public static final String ERRO_LOG_PARSER_JSON = "Erro no parser do JSON/Payload: %s";

}
