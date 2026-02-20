package com.br.votacao.service;

import com.br.votacao.client.CpfClient;
import com.br.votacao.controller.dto.ResultadoResponse;
import com.br.votacao.exception.SessaoEncerradaException;
import com.br.votacao.exception.SessaoNaoEncontradaException;
import com.br.votacao.exception.VotoDuplicadoException;
import com.br.votacao.repository.SessaoRepository;
import com.br.votacao.repository.VotoRepository;
import com.br.votacao.repository.entity.Pauta;
import com.br.votacao.repository.entity.SessaoVotacao;
import com.br.votacao.repository.entity.enums.TipoVoto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VotacaoServiceTest {

    private static final Instant AGORA = Instant.parse("2025-01-15T12:00:00Z");
    private static final Instant FIM_SESSAO = Instant.parse("2025-01-15T12:10:00Z");

    @Mock
    private VotoRepository votoRepository;

    @Mock
    private SessaoRepository sessaoRepository;

    @Mock
    private CpfClient cpfClient;

    @Mock
    private java.time.Clock clock;

    @InjectMocks
    private VotacaoService service;

    @Test
    void votar_quandoSessaoNaoExiste_deveLancarSessaoNaoEncontradaException() {
        UUID pautaId = UUID.randomUUID();
        when(sessaoRepository.findByPautaId(pautaId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.votar(pautaId, "12345678901", TipoVoto.SIM))
                .isInstanceOf(SessaoNaoEncontradaException.class);
    }

    @Test
    void votar_quandoSessaoEncerrada_deveLancarSessaoEncerradaException() {
        UUID pautaId = UUID.randomUUID();
        Pauta pauta = new Pauta(pautaId, "T", "D", Instant.now());
        SessaoVotacao sessao = new SessaoVotacao(pauta, AGORA.minus(15, java.time.temporal.ChronoUnit.MINUTES), AGORA.minus(1, java.time.temporal.ChronoUnit.MINUTES));
        when(sessaoRepository.findByPautaId(pautaId)).thenReturn(Optional.of(sessao));
        when(clock.instant()).thenReturn(AGORA);

        assertThatThrownBy(() -> service.votar(pautaId, "12345678901", TipoVoto.SIM))
                .isInstanceOf(SessaoEncerradaException.class);
    }

    @Test
    void votar_quandoVotoDuplicado_deveLancarVotoDuplicadoException() {
        UUID pautaId = UUID.randomUUID();
        Pauta pauta = new Pauta(pautaId, "T", "D", Instant.now());
        SessaoVotacao sessao = new SessaoVotacao(pauta, AGORA, FIM_SESSAO);
        when(sessaoRepository.findByPautaId(pautaId)).thenReturn(Optional.of(sessao));
        when(clock.instant()).thenReturn(AGORA);
        when(votoRepository.existsByPautaIdAndAssociadoId(pautaId, "12345678901")).thenReturn(true);

        assertThatThrownBy(() -> service.votar(pautaId, "12345678901", TipoVoto.SIM))
                .isInstanceOf(VotoDuplicadoException.class);
        verify(cpfClient, never()).validarHabilitacao(any());
    }

    @Test
    void votar_quandoSessaoAberta_deveSalvarVoto() {
        UUID pautaId = UUID.randomUUID();
        Pauta pauta = new Pauta(pautaId, "T", "D", Instant.now());
        SessaoVotacao sessao = new SessaoVotacao(pauta, AGORA, FIM_SESSAO);
        when(sessaoRepository.findByPautaId(pautaId)).thenReturn(Optional.of(sessao));
        when(clock.instant()).thenReturn(AGORA);
        when(votoRepository.existsByPautaIdAndAssociadoId(pautaId, "12345678901")).thenReturn(false);
        when(votoRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        service.votar(pautaId, "12345678901", TipoVoto.SIM);

        verify(cpfClient).validarHabilitacao("12345678901");
        verify(votoRepository).save(any());
    }

    @Test
    void resultado_quandoSimMaiorQueNao_deveRetornarAprovada() {
        UUID pautaId = UUID.randomUUID();
        when(votoRepository.contarPorTipo(pautaId)).thenReturn(List.of(
                new Object[]{TipoVoto.SIM, 10L},
                new Object[]{TipoVoto.NAO, 4L}
        ));

        ResultadoResponse r = service.resultado(pautaId);

        assertThat(r.totalSim()).isEqualTo(10);
        assertThat(r.totalNao()).isEqualTo(4);
        assertThat(r.resultado()).isEqualTo("APROVADA");
    }

    @Test
    void resultado_quandoEmpate_deveRetornarRejeitada() {
        UUID pautaId = UUID.randomUUID();
        when(votoRepository.contarPorTipo(pautaId)).thenReturn(List.of(
                new Object[]{TipoVoto.SIM, 5L},
                new Object[]{TipoVoto.NAO, 5L}
        ));

        ResultadoResponse r = service.resultado(pautaId);

        assertThat(r.totalSim()).isEqualTo(5);
        assertThat(r.totalNao()).isEqualTo(5);
        assertThat(r.resultado()).isEqualTo("REJEITADA");
    }
}
