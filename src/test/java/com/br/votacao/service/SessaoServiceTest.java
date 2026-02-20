package com.br.votacao.service;

import com.br.votacao.exception.SessaoJaAbertaException;
import com.br.votacao.repository.SessaoRepository;
import com.br.votacao.repository.entity.Pauta;
import com.br.votacao.repository.entity.SessaoVotacao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SessaoServiceTest {

    private static final Instant FIXED_NOW = Instant.parse("2025-01-15T12:00:00Z");

    @Mock
    private SessaoRepository repository;

    @Mock
    private Clock clock;
    @InjectMocks
    private SessaoService service;

    @Test
    void abrirSessao_comDuracaoNull_deveUsarUmMinuto() {
        Pauta pauta = new Pauta(UUID.randomUUID(), "T", "D", Instant.now());
        when(repository.findByPautaId(pauta.getId())).thenReturn(Optional.empty());
        when(clock.instant()).thenReturn(FIXED_NOW);
        when(repository.save(any(SessaoVotacao.class))).thenAnswer(i -> i.getArgument(0));

        service.abrirSessao(pauta, null);

        ArgumentCaptor<SessaoVotacao> captor = ArgumentCaptor.forClass(SessaoVotacao.class);
        verify(repository).save(captor.capture());
        SessaoVotacao sessao = captor.getValue();
        assertThat(sessao.getDataInicio()).isEqualTo(FIXED_NOW);
        assertThat(sessao.getDataFim()).isEqualTo(FIXED_NOW.plus(1, ChronoUnit.MINUTES));
    }

    @Test
    void abrirSessao_comDuracaoInformada_deveRespeitarMinutos() {
        Pauta pauta = new Pauta(UUID.randomUUID(), "T", "D", Instant.now());
        when(repository.findByPautaId(pauta.getId())).thenReturn(Optional.empty());
        when(clock.instant()).thenReturn(FIXED_NOW);
        when(repository.save(any(SessaoVotacao.class))).thenAnswer(i -> i.getArgument(0));

        service.abrirSessao(pauta, 5L);

        ArgumentCaptor<SessaoVotacao> captor = ArgumentCaptor.forClass(SessaoVotacao.class);
        verify(repository).save(captor.capture());
        assertThat(captor.getValue().getDataFim()).isEqualTo(FIXED_NOW.plus(5, ChronoUnit.MINUTES));
    }

    @Test
    void abrirSessao_quandoSessaoJaExiste_deveLancarSessaoJaAbertaException() {
        UUID pautaId = UUID.randomUUID();
        Pauta pauta = new Pauta(pautaId, "T", "D", Instant.now());
        SessaoVotacao existente = new SessaoVotacao(pauta, FIXED_NOW, FIXED_NOW.plus(1, ChronoUnit.MINUTES));
        when(repository.findByPautaId(pautaId)).thenReturn(Optional.of(existente));

        assertThatThrownBy(() -> service.abrirSessao(pauta, 1L))
                .isInstanceOf(SessaoJaAbertaException.class)
                .hasMessageContaining(pautaId.toString());
    }
}
