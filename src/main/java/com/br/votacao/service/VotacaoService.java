package com.br.votacao.service;

import com.br.votacao.client.CpfClient;
import com.br.votacao.controller.dto.ResultadoResponse;
import com.br.votacao.exception.SessaoEncerradaException;
import com.br.votacao.exception.SessaoNaoEncontradaException;
import com.br.votacao.exception.VotoDuplicadoException;
import com.br.votacao.repository.SessaoRepository;
import com.br.votacao.repository.VotoRepository;
import com.br.votacao.repository.entity.Voto;
import com.br.votacao.repository.entity.enums.TipoVoto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class VotacaoService {

    private static final Logger log = LoggerFactory.getLogger(VotacaoService.class);

    private final VotoRepository votoRepository;
    private final SessaoRepository sessaoRepository;
    private final CpfClient cpfClient;
    private final Clock clock;

    public VotacaoService(VotoRepository votoRepository,
                          SessaoRepository sessaoRepository,
                          CpfClient cpfClient,
                          Clock clock) {
        this.votoRepository = votoRepository;
        this.sessaoRepository = sessaoRepository;
        this.cpfClient = cpfClient;
        this.clock = clock;
    }

    public void votar(UUID pautaId, String associadoId, TipoVoto tipoVoto) {
        var sessao = sessaoRepository.findByPautaId(pautaId)
                .orElseThrow(() -> new SessaoNaoEncontradaException(pautaId));

        Instant now = Instant.now(clock);
        if (!sessao.isAberta(now)) {
            log.warn("Tentativa de voto com sessão encerrada: pautaId={}", pautaId);
            throw new SessaoEncerradaException();
        }

        if (votoRepository.existsByPautaIdAndAssociadoId(pautaId, associadoId)) {
            log.warn("Tentativa de voto duplicado: pautaId={}, associadoId={}", pautaId, associadoId);
            throw new VotoDuplicadoException();
        }

        cpfClient.validarHabilitacao(associadoId);

        votoRepository.save(new Voto(sessao.getPauta(), associadoId, tipoVoto, now));
        log.info("Voto registrado: pautaId={}", pautaId);
    }
    public ResultadoResponse resultado(UUID pautaId) {

        Map<TipoVoto, Long> votosPorTipo = votoRepository.contarPorTipo(pautaId)
                .stream()
                .collect(Collectors.toUnmodifiableMap(
                        linha -> (TipoVoto) linha[0],
                        linha -> ((Number) linha[1]).longValue()
                ));

        long sim = votosPorTipo.getOrDefault(TipoVoto.SIM, 0L);
        long nao = votosPorTipo.getOrDefault(TipoVoto.NAO, 0L);

        String status = sim > nao ? "APROVADA" : "REJEITADA";

        log.info("Resultado apurado: pautaId={}, sim={}, nao={}, resultado={}",
                pautaId, sim, nao, status);

        return new ResultadoResponse(sim, nao, status);
    }

}
