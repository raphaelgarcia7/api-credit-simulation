package com.raphael.apicreditsimulation.dto;

import com.raphael.apicreditsimulation.entities.Endereco;

public record ClienteResponseDTO(
        Long id,
        String cpf,
        String nome,
        Endereco endereco
) { }
