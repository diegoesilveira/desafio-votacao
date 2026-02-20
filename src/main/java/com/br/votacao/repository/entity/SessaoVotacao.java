package com.br.votacao.repository.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "sessoes", indexes = @Index(name = "idx_sessao_pauta_id", columnList = "pauta_id"))
public class SessaoVotacao {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne
    @JoinColumn(name = "pauta_id", nullable = false)
    private Pauta pauta;

    @Column(nullable = false)
    private Instant dataInicio;

    @Column(nullable = false)
    private Instant dataFim;

    public SessaoVotacao() {}

    public SessaoVotacao(Pauta pauta, Instant inicio, Instant fim) {
        this.pauta = pauta;
        this.dataInicio = inicio;
        this.dataFim = fim;
    }

    /** Verifica se a sessão está aberta na data de referência (permite testes com Clock fixo). */
    public boolean isAberta(Instant referencia) {
        return !referencia.isAfter(this.dataFim);
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Pauta getPauta() {
        return pauta;
    }

    public void setPauta(Pauta pauta) {
        this.pauta = pauta;
    }

    public Instant getDataInicio() {
        return dataInicio;
    }

    public void setDataInicio(Instant dataInicio) {
        this.dataInicio = dataInicio;
    }

    public Instant getDataFim() {
        return dataFim;
    }

    public void setDataFim(Instant dataFim) {
        this.dataFim = dataFim;
    }
}
