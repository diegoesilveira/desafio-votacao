package com.br.votacao.exception;

public class CpfInvalidoException extends RuntimeException {

    public CpfInvalidoException() {
        super("CPF inválido");
    }
}
