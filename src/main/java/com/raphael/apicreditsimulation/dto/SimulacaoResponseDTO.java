package com.raphael.apicreditsimulation.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record SimulacaoResponseDTO(
        Long id,
        Long clienteId,
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime dataHoraSimulacao,
        BigDecimal valorSolicitado,
        BigDecimal valorGarantia,
        Integer quantidadeMeses,
        BigDecimal taxaJurosMensal
) {
}
