package com.br.votacao.controller.v1;

import com.br.votacao.controller.dto.AbrirSessaoRequest;
import com.br.votacao.controller.dto.CriarPautaRequest;
import com.br.votacao.controller.dto.ResultadoResponse;
import com.br.votacao.controller.dto.VotoRequest;
import com.br.votacao.service.PautaService;
import com.br.votacao.service.SessaoService;
import com.br.votacao.service.VotacaoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * API de pautas, sessões e votação.
 * Versionamento via path (/api/v1/)
 */

@Tag(name = "Pautas", description = "API para gerenciamento de pautas e votação")
@RestController
@RequestMapping("/api/v1/pautas")
public class PautaController {

    private final PautaService pautaService;
    private final SessaoService sessaoService;
    private final VotacaoService votacaoService;

    public PautaController(PautaService pautaService,
                           SessaoService sessaoService,
                           VotacaoService votacaoService) {
        this.pautaService = pautaService;
        this.sessaoService = sessaoService;
        this.votacaoService = votacaoService;
    }

    @Operation(summary = "Criar nova pauta")
    @PostMapping
    public ResponseEntity<UUID> criar(@Valid @RequestBody CriarPautaRequest request) {
        UUID id = pautaService.criar(request.titulo(), request.descricao()).getId();
        return ResponseEntity.status(HttpStatus.CREATED).body(id);
    }

    @Operation(summary = "Abrir sessão para pauta")
    @PostMapping("/{id}/sessao")
    public ResponseEntity<Void> abrirSessao(@PathVariable UUID id,
                                            @RequestBody(required = false) AbrirSessaoRequest request) {
        var pauta = pautaService.buscar(id);
        Long duracao = request != null ? request.duracaoEmMinutos() : null;
        sessaoService.abrirSessao(pauta, duracao);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(summary = "Votar em pauta")
    @PostMapping("/{id}/votos")
    public ResponseEntity<Void> votar(@PathVariable UUID id,
                                      @Valid @RequestBody VotoRequest request) {
        votacaoService.votar(id, request.associadoId(), request.voto());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(summary = "Obter resultado da votação")
    @GetMapping("/{id}/resultado")
    public ResponseEntity<ResultadoResponse> resultado(@PathVariable UUID id) {
        return ResponseEntity.ok(votacaoService.resultado(id));
    }
}
