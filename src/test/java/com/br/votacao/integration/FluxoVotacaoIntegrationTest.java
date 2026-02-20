package com.br.votacao.integration;

import com.br.votacao.controller.dto.AbrirSessaoRequest;
import com.br.votacao.controller.dto.CriarPautaRequest;
import com.br.votacao.controller.dto.ResultadoResponse;
import com.br.votacao.controller.dto.VotoRequest;
import com.br.votacao.repository.entity.enums.TipoVoto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class FluxoVotacaoIntegrationTest {

    @LocalServerPort
    private int port;

    private RestTemplate restTemplate;

    @BeforeEach
    void setUp() {
        restTemplate = new RestTemplate();
        restTemplate.setUriTemplateHandler(new DefaultUriBuilderFactory("http://localhost:" + port));
    }

    @Test
    void fluxoCompleto_criarPauta_abrirSessao_votar_consultarResultado() {
        UUID pautaId = criarPauta("Pauta Teste", "Descrição da pauta");
        abrirSessao(pautaId, 2L);
        votar(pautaId, "12345678901", TipoVoto.SIM);
        votar(pautaId, "98765432100", TipoVoto.NAO);
        votar(pautaId, "11122233344", TipoVoto.SIM);

        ResultadoResponse resultado = consultarResultado(pautaId);

        assertThat(resultado.totalSim()).isEqualTo(2);
        assertThat(resultado.totalNao()).isEqualTo(1);
        assertThat(resultado.resultado()).isEqualTo("APROVADA");
    }

    private UUID criarPauta(String titulo, String descricao) {
        CriarPautaRequest request = new CriarPautaRequest(titulo, descricao);
        var response = restTemplate.postForEntity("/api/v1/pautas", request, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        String content = response.getBody();
        return UUID.fromString(content != null ? content.replace("\"", "").trim() : "");
    }

    private void abrirSessao(UUID pautaId, Long duracaoMinutos) {
        AbrirSessaoRequest request = new AbrirSessaoRequest(duracaoMinutos);
        var response = restTemplate.postForEntity("/api/v1/pautas/" + pautaId + "/sessao", request, Void.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }

    private void votar(UUID pautaId, String associadoId, TipoVoto voto) {
        VotoRequest request = new VotoRequest(associadoId, voto);
        var response = restTemplate.postForEntity("/api/v1/pautas/" + pautaId + "/votos", request, Void.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }

    private ResultadoResponse consultarResultado(UUID pautaId) {
        var response = restTemplate.getForEntity("/api/v1/pautas/" + pautaId + "/resultado", ResultadoResponse.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        return response.getBody();
    }
}
