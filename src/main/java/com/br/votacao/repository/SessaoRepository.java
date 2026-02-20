package com.br.votacao.repository;

import com.br.votacao.repository.entity.SessaoVotacao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface SessaoRepository extends JpaRepository<SessaoVotacao, UUID> {
    Optional<SessaoVotacao> findByPautaId(UUID pautaId);
}
