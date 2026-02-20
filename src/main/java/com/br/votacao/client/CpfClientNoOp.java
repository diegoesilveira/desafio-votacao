package com.br.votacao.client;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * Implementação que não valida CPF (uso quando integração externa está desabilitada).
 */
@Component
@ConditionalOnProperty(name = "app.cpf-client.fake.enabled", havingValue = "false")
public class CpfClientNoOp implements CpfClient {

    @Override
    public void validarHabilitacao(String cpf) {

    }
}