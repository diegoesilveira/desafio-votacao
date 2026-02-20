package com.br.votacao.service;

import com.br.votacao.exception.SessaoJaAbertaException;
import com.br.votacao.repository.SessaoRepository;
import com.br.votacao.repository.entity.Pauta;
import com.br.votacao.repository.entity.SessaoVotacao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Service
public class SessaoService {

    private static final Logger log = LoggerFactory.getLogger(SessaoService.class);

    private final SessaoRepository repository;
    private final Clock clock;

    public SessaoService(SessaoRepository repository, Clock clock) {
        this.repository = repository;
        this.clock = clock;
    }

    public void abrirSessao(Pauta pauta, Long duracao) {
        UUID pautaId = pauta.getId();

        if (repository.findByPautaId(pautaId).isPresent()) {
            throw new SessaoJaAbertaException(pautaId);
        }

        long minutos = duracao != null ? duracao : 1L;
        Instant inicio = Instant.now(clock);
        Instant fim = inicio.plus(minutos, ChronoUnit.MINUTES);
        repository.save(new SessaoVotacao(pauta, inicio, fim));
        log.info("Sessão aberta: pautaId={}, duracaoMinutos={}", pautaId, minutos);
    }
}
