package com.raphael.apicreditsimulation.repository;

import com.raphael.apicreditsimulation.entities.Simulacao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SimulacaoRepository extends JpaRepository<Simulacao, Long> {
    List<Simulacao> findByClienteId(Long id);
    Page<Simulacao> findByClienteId(Long id, Pageable pageable);
}
