package com.raphael.apicreditsimulation.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record SimulacaoRequestDTO(
    Long clienteId,
    LocalDateTime dataHoraSimulacao,
    BigDecimal valorSolicitado,
    BigDecimal valorGarantia,
    Integer quantidadeMeses,
    BigDecimal taxaJurosMensal
) { }
