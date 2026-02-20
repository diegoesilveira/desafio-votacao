package com.br.votacao.service;

import com.br.votacao.exception.PautaNotFoundException;
import com.br.votacao.repository.PautaRepository;
import com.br.votacao.repository.entity.Pauta;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Instant;
import java.util.UUID;

@Service
public class PautaService {

    private static final Logger log = LoggerFactory.getLogger(PautaService.class);

    private final PautaRepository repository;
    private final Clock clock;

    public PautaService(PautaRepository repository, Clock clock) {
        this.repository = repository;
        this.clock = clock;
    }

    public Pauta criar(String titulo, String descricao) {
        Pauta pauta = repository.save(new Pauta(null, titulo, descricao, Instant.now(clock)));
        log.info("Pauta criada: id={}", pauta.getId());
        return pauta;
    }

    public Pauta buscar(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new PautaNotFoundException(id));
    }
}
