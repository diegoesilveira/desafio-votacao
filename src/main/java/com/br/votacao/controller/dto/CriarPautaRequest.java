package com.br.votacao.controller.dto;

import jakarta.validation.constraints.NotBlank;

public record CriarPautaRequest(
        @NotBlank(message = "titulo é obrigatório")
        String titulo,
        String descricao
) {}
