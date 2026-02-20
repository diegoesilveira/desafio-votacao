package com.br.votacao.controller.v1;

import com.br.votacao.controller.dto.AbrirSessaoRequest;
import com.br.votacao.controller.dto.CriarPautaRequest;
import com.br.votacao.controller.dto.ResultadoResponse;
import com.br.votacao.controller.dto.VotoRequest;
import com.br.votacao.exception.PautaNotFoundException;
import com.br.votacao.exception.SessaoJaAbertaException;
import com.br.votacao.exception.SessaoNaoEncontradaException;
import com.br.votacao.exception.VotoDuplicadoException;
import com.br.votacao.exception.GlobalExceptionHandler;
import com.br.votacao.repository.entity.Pauta;
import com.br.votacao.repository.entity.enums.TipoVoto;
import com.br.votacao.service.PautaService;
import com.br.votacao.service.SessaoService;
import com.br.votacao.service.VotacaoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PautaController.class)
@Import(GlobalExceptionHandler.class)
class PautaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PautaService pautaService;

    @MockBean
    private SessaoService sessaoService;

    @MockBean
    private VotacaoService votacaoService;

    private static final UUID PAUTA_ID = UUID.fromString("11111111-1111-1111-1111-111111111111");
    private static final Instant NOW = Instant.parse("2025-01-15T10:00:00Z");

    @Nested
    @DisplayName("POST /api/v1/pautas - criar pauta")
    class CriarPauta {

        @Test
        @DisplayName("deve retornar 201 e UUID no body quando request válido")
        void deveRetornar201ComUuid() throws Exception {
            Pauta pauta = new Pauta(PAUTA_ID, "Pauta Teste", "Descrição", NOW);
            when(pautaService.criar("Pauta Teste", "Descrição")).thenReturn(pauta);

            CriarPautaRequest request = new CriarPautaRequest("Pauta Teste", "Descrição");

            mockMvc.perform(post("/api/v1/pautas")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(content().string("\"" + PAUTA_ID + "\""));

            verify(pautaService).criar("Pauta Teste", "Descrição");
        }

        @Test
        @DisplayName("deve retornar 422 quando titulo em branco")
        void deveRetornar422QuandoTituloEmBranco() throws Exception {
            CriarPautaRequest request = new CriarPautaRequest("", "Descrição");

            mockMvc.perform(post("/api/v1/pautas")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isUnprocessableEntity())
                    .andExpect(jsonPath("$.error").value(containsString("titulo")));
        }

        @Test
        @DisplayName("deve retornar 422 quando titulo nulo")
        void deveRetornar422QuandoTituloNulo() throws Exception {
            String json = "{\"titulo\":null,\"descricao\":\"Desc\"}";

            mockMvc.perform(post("/api/v1/pautas")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json))
                    .andExpect(status().isUnprocessableEntity());
        }
    }

    @Nested
    @DisplayName("POST /api/v1/pautas/{id}/sessao - abrir sessão")
    class AbrirSessao {

        @Test
        @DisplayName("deve retornar 201 quando request com duração")
        void deveRetornar201ComDuracao() throws Exception {
            Pauta pauta = new Pauta(PAUTA_ID, "Pauta", "Desc", NOW);
            when(pautaService.buscar(PAUTA_ID)).thenReturn(pauta);
            doNothing().when(sessaoService).abrirSessao(pauta, 5L);

            AbrirSessaoRequest request = new AbrirSessaoRequest(5L);

            mockMvc.perform(post("/api/v1/pautas/" + PAUTA_ID + "/sessao")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated());

            verify(pautaService).buscar(PAUTA_ID);
            verify(sessaoService).abrirSessao(pauta, 5L);
        }

        @Test
        @DisplayName("deve retornar 201 quando body vazio (usa duração default)")
        void deveRetornar201ComBodyVazio() throws Exception {
            Pauta pauta = new Pauta(PAUTA_ID, "Pauta", "Desc", NOW);
            when(pautaService.buscar(PAUTA_ID)).thenReturn(pauta);
            doNothing().when(sessaoService).abrirSessao(pauta, null);

            mockMvc.perform(post("/api/v1/pautas/" + PAUTA_ID + "/sessao")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{}"))
                    .andExpect(status().isCreated());

            verify(sessaoService).abrirSessao(pauta, null);
        }

        @Test
        @DisplayName("deve retornar 404 quando pauta não existe")
        void deveRetornar404QuandoPautaNaoExiste() throws Exception {
            when(pautaService.buscar(PAUTA_ID)).thenThrow(new PautaNotFoundException(PAUTA_ID));

            mockMvc.perform(post("/api/v1/pautas/" + PAUTA_ID + "/sessao")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(new AbrirSessaoRequest(1L))))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error").value(containsString("não encontrada")));
        }

        @Test
        @DisplayName("deve retornar 400 quando sessão já aberta")
        void deveRetornar400QuandoSessaoJaAberta() throws Exception {
            Pauta pauta = new Pauta(PAUTA_ID, "Pauta", "Desc", NOW);
            when(pautaService.buscar(PAUTA_ID)).thenReturn(pauta);
            doThrow(new SessaoJaAbertaException(PAUTA_ID)).when(sessaoService).abrirSessao(pauta, 1L);

            mockMvc.perform(post("/api/v1/pautas/" + PAUTA_ID + "/sessao")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(new AbrirSessaoRequest(1L))))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("POST /api/v1/pautas/{id}/votos - votar")
    class Votar {

        @Test
        @DisplayName("deve retornar 201 quando request válido")
        void deveRetornar201() throws Exception {
            doNothing().when(votacaoService).votar(eq(PAUTA_ID), eq("12345678901"), eq(TipoVoto.SIM));

            VotoRequest request = new VotoRequest("12345678901", TipoVoto.SIM);

            mockMvc.perform(post("/api/v1/pautas/" + PAUTA_ID + "/votos")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated());

            verify(votacaoService).votar(PAUTA_ID, "12345678901", TipoVoto.SIM);
        }

        @Test
        @DisplayName("deve retornar 422 quando associadoId nulo")
        void deveRetornar422QuandoAssociadoIdNulo() throws Exception {
            String json = "{\"associadoId\":null,\"voto\":\"SIM\"}";

            mockMvc.perform(post("/api/v1/pautas/" + PAUTA_ID + "/votos")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json))
                    .andExpect(status().isUnprocessableEntity());
        }

        @Test
        @DisplayName("deve retornar 422 quando voto nulo")
        void deveRetornar422QuandoVotoNulo() throws Exception {
            String json = "{\"associadoId\":\"12345678901\",\"voto\":null}";

            mockMvc.perform(post("/api/v1/pautas/" + PAUTA_ID + "/votos")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json))
                    .andExpect(status().isUnprocessableEntity());
        }

        @Test
        @DisplayName("deve retornar 409 quando voto duplicado")
        void deveRetornar409QuandoVotoDuplicado() throws Exception {
            doThrow(new VotoDuplicadoException()).when(votacaoService).votar(eq(PAUTA_ID), eq("12345678901"), eq(TipoVoto.SIM));
            VotoRequest request = new VotoRequest("12345678901", TipoVoto.SIM);

            mockMvc.perform(post("/api/v1/pautas/" + PAUTA_ID + "/votos")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isConflict());
        }
    }

    @Nested
    @DisplayName("GET /api/v1/pautas/{id}/resultado - resultado")
    class Resultado {

        @Test
        @DisplayName("deve retornar 200 e ResultadoResponse")
        void deveRetornar200ComResultado() throws Exception {
            ResultadoResponse response = new ResultadoResponse(2L, 1L, "APROVADA");
            when(votacaoService.resultado(PAUTA_ID)).thenReturn(response);

            mockMvc.perform(get("/api/v1/pautas/" + PAUTA_ID + "/resultado"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.totalSim").value(2))
                    .andExpect(jsonPath("$.totalNao").value(1))
                    .andExpect(jsonPath("$.resultado").value("APROVADA"));

            verify(votacaoService).resultado(PAUTA_ID);
        }

        @Test
        @DisplayName("deve retornar 404 quando sessão não encontrada")
        void deveRetornar404QuandoSessaoNaoEncontrada() throws Exception {
            when(votacaoService.resultado(PAUTA_ID)).thenThrow(new SessaoNaoEncontradaException(PAUTA_ID));

            mockMvc.perform(get("/api/v1/pautas/" + PAUTA_ID + "/resultado"))
                    .andExpect(status().isNotFound());
        }
    }
}
