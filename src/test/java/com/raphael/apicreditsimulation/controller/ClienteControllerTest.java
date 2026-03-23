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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.LocalDateTime;

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
        ClienteRequestDTO dto = criarClienteRequest("11122233344", "Joao Silva");

        mockMvc.perform(post("/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nome").value("Joao Silva"))
                .andExpect(jsonPath("$.cpf").value("11122233344"))
                .andExpect(jsonPath("$.endereco.cidade").value("Sao Paulo"));
    }

    @Test
    @DisplayName("Deve listar todos os clientes e retornar 200")
    void deveListarTodosOsClientes() throws Exception {
        mockMvc.perform(get("/clientes"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Deve buscar cliente por id e retornar 200")
    void deveBuscarClientePorId() throws Exception {
        Long id = criarClienteERetornarId("99988877766", "Maria Silva");

        mockMvc.perform(get("/clientes/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.nome").value("Maria Silva"));
    }

    @Test
    @DisplayName("Deve atualizar cliente e retornar 200")
    void deveAtualizarCliente() throws Exception {
        Long id = criarClienteERetornarId("22233344455", "Cliente Original");
        ClienteRequestDTO dtoAtualizacao = criarClienteRequest("22233344455", "Cliente Atualizado");

        mockMvc.perform(put("/clientes/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dtoAtualizacao)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.nome").value("Cliente Atualizado"));
    }

    @Test
    @DisplayName("Deve deletar cliente com simulacoes e retornar 204")
    void deveDeletarCliente() throws Exception {
        Long id = criarClienteERetornarId("55544433322", "Cliente Removivel");
        SimulacaoRequestDTO simulacaoRequestDTO = new SimulacaoRequestDTO(
                LocalDateTime.of(2024, 6, 15, 10, 30, 26),
                new BigDecimal("300000.00"),
                new BigDecimal("1000000.00"),
                150,
                new BigDecimal("2.00")
        );

        mockMvc.perform(post("/clientes/{clienteId}/simulacoes", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(simulacaoRequestDTO)))
                .andExpect(status().isCreated());

        mockMvc.perform(delete("/clientes/{id}", id))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/clientes/{id}", id))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Deve retornar 404 ao buscar cliente inexistente")
    void deveRetornar404QuandoClienteNaoEncontrado() throws Exception {
        mockMvc.perform(get("/clientes/9999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Cliente nao encontrado."));
    }

    private Long criarClienteERetornarId(String cpf, String nome) throws Exception {
        ClienteRequestDTO dto = criarClienteRequest(cpf, nome);

        String response = mockMvc.perform(post("/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        return objectMapper.readTree(response).get("id").asLong();
    }

    private ClienteRequestDTO criarClienteRequest(String cpf, String nome) {
        EnderecoRequestDTO endereco = new EnderecoRequestDTO(
                "Rua das Flores",
                "123",
                "Centro",
                "01001000",
                "Sao Paulo",
                "SP"
        );

        return new ClienteRequestDTO(cpf, nome, endereco);
    }
}
