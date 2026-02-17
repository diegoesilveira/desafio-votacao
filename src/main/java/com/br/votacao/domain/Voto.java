package com.br.votacao.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.time.LocalDateTime;

@Entity
@Table(name = "voto", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"sessao_id", "associado_id"})
})
public class Voto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sessao_id", nullable = false)
    private Sessao sessao;

    @Column(name = "associado_id", nullable = false)
    private String associadoId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VotoOpcao opcao;

    @Column(name = "data_voto", nullable = false)
    private LocalDateTime dataVoto = LocalDateTime.now();

    protected Voto() {
    }

    public Voto(Sessao sessao, String associadoId, VotoOpcao opcao) {
        if (sessao == null) {
            throw new IllegalArgumentException("Sessão é obrigatória");
        }
        if (associadoId == null || associadoId.isBlank()) {
            throw new IllegalArgumentException("Identificador do associado é obrigatório");
        }
        if (opcao == null) {
            throw new IllegalArgumentException("Opção do voto é obrigatória");
        }
        this.sessao = sessao;
        this.associadoId = associadoId.trim();
        this.opcao = opcao;
    }

    public Long getId() {
        return id;
    }

    public Sessao getSessao() {
        return sessao;
    }

    public String getAssociadoId() {
        return associadoId;
    }

    public VotoOpcao getOpcao() {
        return opcao;
    }

    public LocalDateTime getDataVoto() {
        return dataVoto;
    }
}
