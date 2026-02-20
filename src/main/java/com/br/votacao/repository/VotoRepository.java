package com.br.votacao.repository;

import com.br.votacao.repository.entity.Voto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface VotoRepository extends JpaRepository<Voto, UUID> {
    boolean existsByPautaIdAndAssociadoId(UUID pautaId, String associadoId);
    @Query("""
    SELECT v.votoEnum, COUNT(v)
    FROM Voto v
    WHERE v.pauta.id = :pautaId
    GROUP BY v.votoEnum
""")
    List<Object[]> contarPorTipo(@Param("pautaId") UUID pautaId);

}
