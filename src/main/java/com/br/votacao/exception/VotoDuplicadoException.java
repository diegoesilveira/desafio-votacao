package com.br.votacao.exception;

public class VotoDuplicadoException extends RuntimeException {

    public VotoDuplicadoException() {
        super("Associado já votou nesta pauta");
    }
}
