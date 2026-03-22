package com.raphael.apicreditsimulation.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ClienteRequestDTO(
        @NotBlank(message = "CPF e obrigatorio.")
        @Pattern(regexp = "\\d{11}", message = "CPF deve conter exatamente 11 digitos.")
        String cpf,

        @NotBlank(message = "Nome e obrigatorio.")
        @Size(max = 150, message = "Nome deve ter no maximo 150 caracteres.")
        String nome,

        @NotNull(message = "Endereco e obrigatorio.")
        @Valid
        EnderecoRequestDTO endereco
) {
}
