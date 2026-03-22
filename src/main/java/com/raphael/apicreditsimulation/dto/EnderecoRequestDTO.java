package com.raphael.apicreditsimulation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record EnderecoRequestDTO(
        @NotBlank(message = "Rua e obrigatoria.")
        @Size(max = 150, message = "Rua deve ter no maximo 150 caracteres.")
        String rua,

        @NotBlank(message = "Numero e obrigatorio.")
        @Size(max = 20, message = "Numero deve ter no maximo 20 caracteres.")
        String numero,

        @NotBlank(message = "Bairro e obrigatorio.")
        @Size(max = 100, message = "Bairro deve ter no maximo 100 caracteres.")
        String bairro,

        @NotBlank(message = "CEP e obrigatorio.")
        @Pattern(regexp = "\\d{8}", message = "CEP deve conter exatamente 8 digitos.")
        String cep,

        @NotBlank(message = "Cidade e obrigatoria.")
        @Size(max = 100, message = "Cidade deve ter no maximo 100 caracteres.")
        String cidade,

        @NotBlank(message = "Estado e obrigatorio.")
        @Pattern(regexp = "[A-Z]{2}", message = "Estado deve conter a sigla com 2 letras maiusculas.")
        String estado
) {
}
