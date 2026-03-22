package com.raphael.apicreditsimulation.dto;

import com.raphael.apicreditsimulation.entities.Endereco;

public record ClienteRequestDTO(
        String cpf,
        String nome,
        Endereco endereco
) { }
