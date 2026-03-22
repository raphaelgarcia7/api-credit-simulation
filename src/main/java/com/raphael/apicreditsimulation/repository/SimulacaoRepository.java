package com.raphael.apicreditsimulation.repository;

import com.raphael.apicreditsimulation.entities.Simulacao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SimulacaoRepository extends JpaRepository<Simulacao, Long> {

    boolean existsByClienteId(Long clienteId);

    List<Simulacao> findAllByClienteIdOrderByDataHoraSimulacaoDesc(Long clienteId);

    Page<Simulacao> findAllByClienteId(Long clienteId, Pageable pageable);
}
