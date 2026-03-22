package com.raphael.apicreditsimulation.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record SimulacaoRequestDTO(
        @NotNull(message = "Data e hora da simulacao sao obrigatorias.")
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime dataHoraSimulacao,

        @NotNull(message = "Valor solicitado e obrigatorio.")
        @DecimalMin(value = "0.01", message = "Valor solicitado deve ser maior que zero.")
        @Digits(integer = 13, fraction = 2, message = "Valor solicitado deve possuir no maximo 13 inteiros e 2 decimais.")
        BigDecimal valorSolicitado,

        @NotNull(message = "Valor da garantia e obrigatorio.")
        @DecimalMin(value = "0.01", message = "Valor da garantia deve ser maior que zero.")
        @Digits(integer = 13, fraction = 2, message = "Valor da garantia deve possuir no maximo 13 inteiros e 2 decimais.")
        BigDecimal valorGarantia,

        @NotNull(message = "Quantidade de meses e obrigatoria.")
        @Positive(message = "Quantidade de meses deve ser maior que zero.")
        Integer quantidadeMeses,

        @NotNull(message = "Taxa de juros mensal e obrigatoria.")
        @DecimalMin(value = "0.01", message = "Taxa de juros mensal deve ser maior que zero.")
        @Digits(integer = 3, fraction = 2, message = "Taxa de juros mensal deve possuir no maximo 3 inteiros e 2 decimais.")
        BigDecimal taxaJurosMensal
) {
}
