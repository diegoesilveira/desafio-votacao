package com.br.votacao.exception;

public class SessaoEncerradaException extends RuntimeException {

    public SessaoEncerradaException() {
        super("Sessão de votação encerrada");
    }
}
