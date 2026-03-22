package com.raphael.apicreditsimulation.service;

import com.raphael.apicreditsimulation.dto.ClienteRequestDTO;
import com.raphael.apicreditsimulation.dto.ClienteResponseDTO;
import com.raphael.apicreditsimulation.dto.EnderecoRequestDTO;
import com.raphael.apicreditsimulation.entities.Cliente;
import com.raphael.apicreditsimulation.entities.Endereco;
import com.raphael.apicreditsimulation.exception.ConflictException;
import com.raphael.apicreditsimulation.exception.NotFoundException;
import com.raphael.apicreditsimulation.repository.ClienteRepository;
import com.raphael.apicreditsimulation.repository.SimulacaoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ClienteServiceTest {

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private SimulacaoRepository simulacaoRepository;

    @InjectMocks
    private ClienteService clienteService;

    @Test
    @DisplayName("Deve criar cliente com sucesso")
    void deveCriarClienteComSucesso() {
        ClienteRequestDTO dto = criarClienteRequest();
        Cliente clienteSalvo = Cliente.builder()
                .id(1L)
                .cpf(dto.cpf())
                .nome(dto.nome())
                .endereco(criarEndereco())
                .build();

        when(clienteRepository.findByCpf(dto.cpf())).thenReturn(Optional.empty());
        when(clienteRepository.save(any(Cliente.class))).thenReturn(clienteSalvo);

        ClienteResponseDTO response = clienteService.criar(dto);

        assertThat(response).isNotNull();
        assertThat(response.nome()).isEqualTo("Joao Silva");
        assertThat(response.cpf()).isEqualTo("12345678901");
        assertThat(response.endereco().cidade()).isEqualTo("Sao Paulo");
        verify(clienteRepository).save(any(Cliente.class));
    }

    @Test
    @DisplayName("Deve lancar excecao ao criar cliente com CPF duplicado")
    void deveLancarExcecaoQuandoCpfJaCadastrado() {
        ClienteRequestDTO dto = criarClienteRequest();
        Cliente clienteExistente = Cliente.builder().id(1L).cpf(dto.cpf()).nome(dto.nome()).build();

        when(clienteRepository.findByCpf(dto.cpf())).thenReturn(Optional.of(clienteExistente));

        assertThatThrownBy(() -> clienteService.criar(dto))
                .isInstanceOf(ConflictException.class)
                .hasMessage("CPF ja cadastrado.");
    }

    @Test
    @DisplayName("Deve lancar excecao ao buscar cliente inexistente")
    void deveLancarExcecaoQuandoClienteNaoEncontrado() {
        when(clienteRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> clienteService.buscarPorId(99L))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Cliente nao encontrado.");
    }

    @Test
    @DisplayName("Deve impedir exclusao de cliente com simulacoes")
    void deveImpedirExclusaoDeClienteComSimulacoes() {
        Cliente cliente = Cliente.builder()
                .id(1L)
                .cpf("12345678901")
                .nome("Joao Silva")
                .endereco(criarEndereco())
                .build();

        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
        when(simulacaoRepository.existsByClienteId(1L)).thenReturn(true);

        assertThatThrownBy(() -> clienteService.deletar(1L))
                .isInstanceOf(ConflictException.class)
                .hasMessage("Nao e possivel remover um cliente com simulacoes cadastradas.");

        verify(clienteRepository, never()).delete(any(Cliente.class));
    }

    private ClienteRequestDTO criarClienteRequest() {
        return new ClienteRequestDTO(
                "12345678901",
                "Joao Silva",
                new EnderecoRequestDTO("Rua das Flores", "123", "Centro", "01001000", "Sao Paulo", "SP")
        );
    }

    private Endereco criarEndereco() {
        return Endereco.builder()
                .id(1L)
                .rua("Rua das Flores")
                .numero("123")
                .bairro("Centro")
                .cep("01001000")
                .cidade("Sao Paulo")
                .estado("SP")
                .build();
    }
}
