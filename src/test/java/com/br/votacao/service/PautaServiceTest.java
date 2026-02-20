package com.br.votacao.service;

import com.br.votacao.exception.PautaNotFoundException;
import com.br.votacao.repository.PautaRepository;
import com.br.votacao.repository.entity.Pauta;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PautaServiceTest {

    @Mock
    private PautaRepository repository;

    @Mock
    private java.time.Clock clock;

    @InjectMocks
    private PautaService service;

    @Test
    void criar_deveSalvarERetornarPauta() {
        UUID id = UUID.randomUUID();
        Instant now = Instant.parse("2025-01-15T10:00:00Z");
        when(clock.instant()).thenReturn(now);
        Pauta salva = new Pauta(id, "Título", "Descrição", now);
        when(repository.save(any(Pauta.class))).thenReturn(salva);

        Pauta resultado = service.criar("Título", "Descrição");

        assertThat(resultado.getId()).isEqualTo(id);
        assertThat(resultado.getTitulo()).isEqualTo("Título");
        verify(repository).save(any(Pauta.class));
    }

    @Test
    void buscar_quandoNaoExiste_deveLancarPautaNotFoundException() {
        UUID id = UUID.randomUUID();
        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.buscar(id))
                .isInstanceOf(PautaNotFoundException.class)
                .hasMessageContaining(id.toString());
    }

    @Test
    void buscar_quandoExiste_deveRetornarPauta() {
        UUID id = UUID.randomUUID();
        Pauta pauta = new Pauta(id, "T", "D", Instant.now());
        when(repository.findById(id)).thenReturn(Optional.of(pauta));

        Pauta resultado = service.buscar(id);

        assertThat(resultado).isSameAs(pauta);
    }
}
