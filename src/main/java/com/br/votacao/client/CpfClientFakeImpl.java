package com.br.votacao.client;

import com.br.votacao.exception.CpfInvalidoException;
import com.br.votacao.exception.UsuarioNaoHabilitadoParaVotarException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Implementação fake do client de CPF para desenvolvimento e testes.
 * CPF inválido: não possui 11 dígitos numéricos.
 * CPF válido: retorna aleatoriamente ABLE_TO_VOTE ou UNABLE_TO_VOTE.
 */
@Component
@ConditionalOnProperty(name = "app.cpf-client.fake.enabled", havingValue = "true", matchIfMissing = true)
public class CpfClientFakeImpl implements CpfClient {

    @Override
    public void validarHabilitacao(String cpf) {
        String apenasNumeros = cpf != null ? cpf.replaceAll("\\D", "") : "";
        if (apenasNumeros.length() != 11) {
            throw new CpfInvalidoException();
        }
        CpfStatus status = ThreadLocalRandom.current().nextBoolean() ? CpfStatus.ABLE_TO_VOTE : CpfStatus.UNABLE_TO_VOTE;
        if (status == CpfStatus.UNABLE_TO_VOTE) {
            throw new UsuarioNaoHabilitadoParaVotarException();
        }
    }
}
