package com.br.votacao.exception;

public class UsuarioNaoHabilitadoParaVotarException extends RuntimeException {

    public UsuarioNaoHabilitadoParaVotarException() {
        super("Usuário não habilitado para votar");
    }
}
