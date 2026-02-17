package com.br.votacao.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "sessao")
public class Sessao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pauta_id", nullable = false, unique = true)
    private Pauta pauta;

    @Column(name = "data_abertura", nullable = false)
    private LocalDateTime dataAbertura;

    @Column(name = "duracao_minutos", nullable = false)
    private Integer duracaoMinutos;

    @Column(name = "data_encerramento", nullable = false)
    private LocalDateTime dataEncerramento;

    protected Sessao() {
    }

    public Sessao(Pauta pauta, LocalDateTime dataAbertura, int duracaoMinutos) {
        if (pauta == null) {
            throw new IllegalArgumentException("Pauta é obrigatória");
        }
        if (duracaoMinutos <= 0) {
            throw new IllegalArgumentException("Duração deve ser maior que zero");
        }
        this.pauta = pauta;
        this.dataAbertura = dataAbertura;
        this.duracaoMinutos = duracaoMinutos;
        this.dataEncerramento = dataAbertura.plusMinutes(duracaoMinutos);
    }

    public boolean isAberta() {
        return LocalDateTime.now().isBefore(dataEncerramento) || LocalDateTime.now().equals(dataEncerramento);
    }

    public Long getId() {
        return id;
    }

    public Pauta getPauta() {
        return pauta;
    }

    public LocalDateTime getDataAbertura() {
        return dataAbertura;
    }

    public Integer getDuracaoMinutos() {
        return duracaoMinutos;
    }

    public LocalDateTime getDataEncerramento() {
        return dataEncerramento;
    }
}
