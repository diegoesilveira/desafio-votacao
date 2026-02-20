package com.br.votacao.repository.entity;

import com.br.votacao.repository.entity.enums.TipoVoto;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Index;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.time.Instant;
import java.util.UUID;

/**
 * Índice em (pauta_id, associadoId): garante unicidade e acelera existsByPautaIdAndAssociadoId.
 * Índice em pauta_id: otimiza contarPorTipo (agregação por pauta sem full scan).
 */
@Entity
@Table(name = "votos",
        indexes = @Index(name = "idx_voto_pauta_id", columnList = "pauta_id"),
        uniqueConstraints = @UniqueConstraint(columnNames = {"pauta_id", "associadoId"}))
public class Voto {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "pauta_id", nullable = false)
    private Pauta pauta;

    @Column(nullable = false)
    private String associadoId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoVoto votoEnum;

    @Column(nullable = false)
    private Instant dataVoto;

    public Voto() {}

    public Voto(Pauta pauta, String associadoId, TipoVoto votoEnum, Instant dataVoto) {
        this.pauta = pauta;
        this.associadoId = associadoId;
        this.votoEnum = votoEnum;
        this.dataVoto = dataVoto;
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

    public String getAssociadoId() {
        return associadoId;
    }

    public void setAssociadoId(String associadoId) {
        this.associadoId = associadoId;
    }

    public TipoVoto getVoto() {
        return votoEnum;
    }

    public void setVoto(TipoVoto votoEnum) {
        this.votoEnum = votoEnum;
    }

    public Instant getDataVoto() {
        return dataVoto;
    }

    public void setDataVoto(Instant dataVoto) {
        this.dataVoto = dataVoto;
    }
}
