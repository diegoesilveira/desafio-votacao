package com.br.votacao.exception;

import java.util.UUID;

public class PautaNotFoundException extends RuntimeException {

    public PautaNotFoundException(UUID id) {
        super("Pauta não encontrada: " + id);
    }
}
