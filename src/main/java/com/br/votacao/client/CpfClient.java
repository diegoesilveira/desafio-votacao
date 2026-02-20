package com.br.votacao.client;

/**
 * Contrato para validação de CPF e verificação de habilitação para votar.
 * Implementação fake retorna resultados configuráveis para testes.
 */
public interface CpfClient {

    /**
     * Verifica se o CPF é válido e se o usuário está habilitado a votar.
     *
     * @param cpf CPF do associado (apenas números ou formatado)
     * @return status de habilitação
     * @throws com.br.votacao.exception.CpfInvalidoException se CPF for inválido
     * @throws com.br.votacao.exception.UsuarioNaoHabilitadoParaVotarException se status for UNABLE_TO_VOTE
     */
    void validarHabilitacao(String cpf);
}
