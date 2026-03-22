package com.raphael.apicreditsimulation.service;

import com.raphael.apicreditsimulation.dto.ClienteRequestDTO;
import com.raphael.apicreditsimulation.dto.ClienteResponseDTO;
import com.raphael.apicreditsimulation.entities.Cliente;
import com.raphael.apicreditsimulation.entities.Endereco;
import com.raphael.apicreditsimulation.exception.ConflictException;
import com.raphael.apicreditsimulation.exception.NotFoundException;
import com.raphael.apicreditsimulation.repository.ClienteRepository;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ClienteServiceTest {

    @Mock
    private ClienteRepository clienteRepository;

    @InjectMocks
    private ClienteService clienteService;

    @Test
    @DisplayName("Deve criar cliente com sucesso")
    void deveCriarClienteComSucesso() {
        // Arrange
        Endereco endereco = Endereco.builder()
                .rua("Rua das Flores")
                .numero("123")
                .bairro("Centro")
                .cep("12345678")
                .cidade("São Paulo")
                .estado("SP")
                .build();

        ClienteRequestDTO dto = new ClienteRequestDTO("12345678901", "João Silva", endereco);

        Cliente clienteSalvo = Cliente.builder()
                .id(1L)
                .cpf("12345678901")
                .nome("João Silva")
                .endereco(endereco)
                .build();

        when(clienteRepository.findByCpf(dto.cpf())).thenReturn(Optional.empty());
        when(clienteRepository.save(any())).thenReturn(clienteSalvo);

        // Act
        ClienteResponseDTO response = clienteService.criar(dto);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.nome()).isEqualTo("João Silva");
        assertThat(response.cpf()).isEqualTo("12345678901");
        verify(clienteRepository).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção ao criar cliente com CPF duplicado")
    void deveLancarExcecaoQuandoCpfJaCadastrado() {
        // Arrange
        Endereco endereco = Endereco.builder()
                .rua("Rua das Flores")
                .numero("123")
                .bairro("Centro")
                .cep("12345678")
                .cidade("São Paulo")
                .estado("SP")
                .build();

        ClienteRequestDTO dto = new ClienteRequestDTO("12345678901", "João Silva", endereco);

        Cliente clienteExistente = Cliente.builder()
                .id(1L)
                .cpf("12345678901")
                .nome("João Silva")
                .build();

        when(clienteRepository.findByCpf(dto.cpf())).thenReturn(Optional.of(clienteExistente));

        // Act + Assert
        assertThatThrownBy(() -> clienteService.criar(dto))
                .isInstanceOf(ConflictException.class)
                .hasMessage("CPF já cadastrado");
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar cliente inexistente")
    void deveLancarExcecaoQuandoClienteNaoEncontrado() {
        // Arrange
        when(clienteRepository.findById(99L)).thenReturn(Optional.empty());

        // Act + Assert
        assertThatThrownBy(() -> clienteService.buscarPorId(99L))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Cliente não encontrado");
    }
}