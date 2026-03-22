package com.raphael.apicreditsimulation.dto;

public record EnderecoResponseDTO(
        Long id,
        String rua,
        String numero,
        String bairro,
        String cep,
        String cidade,
        String estado
) {
}
