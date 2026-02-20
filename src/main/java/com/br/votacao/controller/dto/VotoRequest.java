package com.br.votacao.controller.dto;

import com.br.votacao.repository.entity.enums.TipoVoto;
import jakarta.validation.constraints.NotNull;

public record VotoRequest(
        @NotNull(message = "associadoId é obrigatório")
        String associadoId,
        @NotNull(message = "voto é obrigatório")
        TipoVoto voto
) {}
