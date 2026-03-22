package com.raphael.apicreditsimulation.entities;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "simulacao")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Simulacao {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    @Column(nullable = false)
    private LocalDateTime dataHoraSimulacao;

    @Column(nullable = false)
    private BigDecimal valorSolicitado;

    @Column(nullable = false)
    private BigDecimal valorGarantia;

    @Column(nullable = false)
    private Integer quantidadeMeses;

    @Column(nullable = false)
    private BigDecimal taxaJurosMensal;
}
