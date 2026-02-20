package com.br.votacao.repository;

import com.br.votacao.repository.entity.Pauta;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PautaRepository extends JpaRepository<Pauta, UUID> {
}
