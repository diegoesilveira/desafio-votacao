package com.br.votacao.exception;

import java.util.UUID;

public class SessaoNaoEncontradaException extends RuntimeException {

    public SessaoNaoEncontradaException(UUID pautaId) {
        super("Sessão não encontrada para a pauta: " + pautaId);
    }
}
