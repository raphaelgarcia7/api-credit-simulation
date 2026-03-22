package com.raphael.apicreditsimulation.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.raphael.apicreditsimulation.dto.ClienteRequestDTO;
import com.raphael.apicreditsimulation.dto.SimulacaoRequestDTO;
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

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
class SimulacaoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private Long criarClienteERetornarId() throws Exception {
        Endereco endereco = Endereco.builder()
                .rua("Rua das Flores")
                .numero("123")
                .bairro("Centro")
                .cep("12345678")
                .cidade("São Paulo")
                .estado("SP")
                .build();

        ClienteRequestDTO dto = new ClienteRequestDTO("11122233344", "João Silva", endereco);

        String response = mockMvc.perform(post("/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andReturn().getResponse().getContentAsString();

        return objectMapper.readTree(response).get("id").asLong();
    }

    @Test
    @DisplayName("Deve criar simulação e retornar 201")
    void deveCriarSimulacao() throws Exception {
        Long clienteId = criarClienteERetornarId();

        SimulacaoRequestDTO dto = new SimulacaoRequestDTO(
                clienteId,
                LocalDateTime.of(2024, 6, 15, 10, 30, 26),
                new BigDecimal("300000.00"),
                new BigDecimal("1000000.00"),
                150,
                new BigDecimal("0.02")
        );

        mockMvc.perform(post("/simulacoes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.clienteId").value(clienteId))
                .andExpect(jsonPath("$.quantidadeMeses").value(150));
    }

    @Test
    @DisplayName("Deve listar simulações por cliente e retornar 200")
    void deveListarSimulacoesPorCliente() throws Exception {
        Long clienteId = criarClienteERetornarId();

        mockMvc.perform(get("/simulacoes/cliente/" + clienteId))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Deve retornar 404 ao listar simulações de cliente inexistente")
    void deveRetornar404QuandoClienteNaoEncontrado() throws Exception {
        mockMvc.perform(get("/simulacoes/cliente/9999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Deve exportar CSV e retornar 200")
    void deveExportarCsv() throws Exception {
        Long clienteId = criarClienteERetornarId();

        mockMvc.perform(get("/simulacoes/cliente/" + clienteId + "/exportar/csv"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Deve exportar TXT e retornar 200")
    void deveExportarTxt() throws Exception {
        Long clienteId = criarClienteERetornarId();

        mockMvc.perform(get("/simulacoes/cliente/" + clienteId + "/exportar/txt"))
                .andExpect(status().isOk());
    }
}