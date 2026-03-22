package com.raphael.apicreditsimulation.dto;

public record ClienteResponseDTO(
        Long id,
        String cpf,
        String nome,
        EnderecoResponseDTO endereco
) {
}
