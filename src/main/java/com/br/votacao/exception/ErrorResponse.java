package com.br.votacao.exception;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.Instant;

/**
 * DTO padronizado para respostas de erro da API.
 */
public record ErrorResponse(
        @JsonFormat(shape = JsonFormat.Shape.STRING, timezone = "UTC")
        Instant timestamp,
        int status,
        String error,
        String path
) {}
