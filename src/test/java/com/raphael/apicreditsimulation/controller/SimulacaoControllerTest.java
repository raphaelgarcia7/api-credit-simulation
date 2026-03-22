package com.raphael.apicreditsimulation.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.raphael.apicreditsimulation.dto.ClienteRequestDTO;
import com.raphael.apicreditsimulation.dto.EnderecoRequestDTO;
import com.raphael.apicreditsimulation.dto.SimulacaoRequestDTO;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
class SimulacaoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Deve criar simulacao e retornar 201")
    void deveCriarSimulacao() throws Exception {
        Long clienteId = criarClienteERetornarId();

        mockMvc.perform(post("/clientes/{clienteId}/simulacoes", clienteId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(criarSimulacaoRequest())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.clienteId").value(clienteId))
                .andExpect(jsonPath("$.quantidadeMeses").value(150))
                .andExpect(jsonPath("$.taxaJurosMensal").value(2.00));
    }

    @Test
    @DisplayName("Deve listar simulacoes paginadas por cliente e retornar 200")
    void deveListarSimulacoesPaginadasPorCliente() throws Exception {
        Long clienteId = criarClienteERetornarId();
        criarSimulacao(clienteId);

        mockMvc.perform(get("/clientes/{clienteId}/simulacoes?page=0&size=10", clienteId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].clienteId").value(clienteId))
                .andExpect(jsonPath("$.content[0].quantidadeMeses").value(150));
    }

    @Test
    @DisplayName("Deve exportar simulacoes em CSV e retornar 200")
    void deveExportarCsv() throws Exception {
        Long clienteId = criarClienteERetornarId();
        criarSimulacao(clienteId);

        mockMvc.perform(get("/clientes/{clienteId}/simulacoes/exportacao/csv", clienteId))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition", "attachment; filename=simulacoes.csv"))
                .andExpect(content().contentType("text/csv"))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("valorSolicitado")));
    }

    @Test
    @DisplayName("Deve exportar simulacoes em TXT e retornar 200")
    void deveExportarTxt() throws Exception {
        Long clienteId = criarClienteERetornarId();
        criarSimulacao(clienteId);

        mockMvc.perform(get("/clientes/{clienteId}/simulacoes/exportacao/txt", clienteId))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition", "attachment; filename=simulacoes.txt"))
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_PLAIN))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("SIMULACOES DO CLIENTE")));
    }

    @Test
    @DisplayName("Deve retornar 404 ao listar simulacoes de cliente inexistente")
    void deveRetornar404QuandoClienteNaoEncontrado() throws Exception {
        mockMvc.perform(get("/clientes/9999/simulacoes"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Cliente nao encontrado."));
    }

    private void criarSimulacao(Long clienteId) throws Exception {
        mockMvc.perform(post("/clientes/{clienteId}/simulacoes", clienteId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(criarSimulacaoRequest())))
                .andExpect(status().isCreated());
    }

    private Long criarClienteERetornarId() throws Exception {
        ClienteRequestDTO dto = new ClienteRequestDTO(
                "12345678901",
                "Joao Silva",
                new EnderecoRequestDTO("Rua das Flores", "123", "Centro", "01001000", "Sao Paulo", "SP")
        );

        String response = mockMvc.perform(post("/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        return objectMapper.readTree(response).get("id").asLong();
    }

    private SimulacaoRequestDTO criarSimulacaoRequest() {
        return new SimulacaoRequestDTO(
                LocalDateTime.of(2024, 6, 15, 10, 30, 26),
                new BigDecimal("300000.00"),
                new BigDecimal("1000000.00"),
                150,
                new BigDecimal("2.00")
        );
    }
}
