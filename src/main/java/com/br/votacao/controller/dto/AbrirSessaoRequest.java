package com.br.votacao.controller.dto;

import jakarta.validation.constraints.Positive;

public record AbrirSessaoRequest(
        @Positive(message = "duracaoEmMinutos deve ser positivo")
        Long duracaoEmMinutos
) {}
