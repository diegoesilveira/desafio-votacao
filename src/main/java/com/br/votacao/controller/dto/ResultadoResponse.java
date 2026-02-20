package com.br.votacao.controller.dto;

public record ResultadoResponse(
        long totalSim,
        long totalNao,
        String resultado
) {}