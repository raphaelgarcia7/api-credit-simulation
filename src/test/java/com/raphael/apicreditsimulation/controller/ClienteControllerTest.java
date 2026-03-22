package com.raphael.apicreditsimulation.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.raphael.apicreditsimulation.dto.ClienteRequestDTO;
import com.raphael.apicreditsimulation.entities.Endereco;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
class ClienteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Deve criar cliente e retornar 201")
    void deveCriarCliente() throws Exception {
        Endereco endereco = Endereco.builder()
                .rua("Rua das Flores")
                .numero("123")
                .bairro("Centro")
                .cep("12345678")
                .cidade("São Paulo")
                .estado("SP")
                .build();

        ClienteRequestDTO dto = new ClienteRequestDTO("11122233344", "João Silva", endereco);

        mockMvc.perform(post("/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nome").value("João Silva"))
                .andExpect(jsonPath("$.cpf").value("11122233344"));
    }

    @Test
    @DisplayName("Deve listar todos os clientes e retornar 200")
    void deveListarTodosOsClientes() throws Exception {
        mockMvc.perform(get("/clientes"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Deve retornar 404 ao buscar cliente inexistente")
    void deveRetornar404QuandoClienteNaoEncontrado() throws Exception {
        mockMvc.perform(get("/clientes/9999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Deve deletar cliente e retornar 204")
    void deveDeletarCliente() throws Exception {
        Endereco endereco = Endereco.builder()
                .rua("Rua das Flores")
                .numero("123")
                .bairro("Centro")
                .cep("12345678")
                .cidade("São Paulo")
                .estado("SP")
                .build();

        ClienteRequestDTO dto = new ClienteRequestDTO("99988877766", "Maria Silva", endereco);

        String response = mockMvc.perform(post("/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Long id = objectMapper.readTree(response).get("id").asLong();

        mockMvc.perform(delete("/clientes/" + id))
                .andExpect(status().isNoContent());
    }
}