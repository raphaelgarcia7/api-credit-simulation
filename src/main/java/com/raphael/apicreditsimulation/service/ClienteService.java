package com.raphael.apicreditsimulation.service;

import com.raphael.apicreditsimulation.dto.ClienteRequestDTO;
import com.raphael.apicreditsimulation.dto.ClienteResponseDTO;
import com.raphael.apicreditsimulation.dto.EnderecoRequestDTO;
import com.raphael.apicreditsimulation.dto.EnderecoResponseDTO;
import com.raphael.apicreditsimulation.entities.Cliente;
import com.raphael.apicreditsimulation.entities.Endereco;
import com.raphael.apicreditsimulation.exception.NotFoundException;
import com.raphael.apicreditsimulation.repository.ClienteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClienteService {

    private final ClienteRepository clienteRepository;

    @Transactional
    public ClienteResponseDTO criar(ClienteRequestDTO dto) {
        Cliente cliente = Cliente.builder()
                .cpf(dto.cpf())
                .nome(dto.nome())
                .endereco(toEndereco(dto.endereco()))
                .build();

        return toResponseDTO(clienteRepository.save(cliente));
    }

    @Transactional(readOnly = true)
    public List<ClienteResponseDTO> listarTodos() {
        return clienteRepository.findAll()
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public ClienteResponseDTO buscarPorId(Long id) {
        return toResponseDTO(buscarEntidadePorId(id));
    }

    @Transactional
    public ClienteResponseDTO atualizar(Long id, ClienteRequestDTO dto) {
        Cliente cliente = buscarEntidadePorId(id);

        cliente.setCpf(dto.cpf());
        cliente.setNome(dto.nome());
        cliente.setEndereco(atualizarEndereco(cliente.getEndereco(), dto.endereco()));

        return toResponseDTO(clienteRepository.save(cliente));
    }

    @Transactional
    public void deletar(Long id) {
        Cliente cliente = buscarEntidadePorId(id);
        clienteRepository.delete(cliente);
    }

    private Cliente buscarEntidadePorId(Long id) {
        return clienteRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Cliente nao encontrado."));
    }

    private Endereco atualizarEndereco(Endereco enderecoAtual, EnderecoRequestDTO dto) {
        if (enderecoAtual == null) {
            return toEndereco(dto);
        }

        enderecoAtual.setRua(dto.rua());
        enderecoAtual.setNumero(dto.numero());
        enderecoAtual.setBairro(dto.bairro());
        enderecoAtual.setCep(dto.cep());
        enderecoAtual.setCidade(dto.cidade());
        enderecoAtual.setEstado(dto.estado());
        return enderecoAtual;
    }

    private Endereco toEndereco(EnderecoRequestDTO dto) {
        return Endereco.builder()
                .rua(dto.rua())
                .numero(dto.numero())
                .bairro(dto.bairro())
                .cep(dto.cep())
                .cidade(dto.cidade())
                .estado(dto.estado())
                .build();
    }

    private ClienteResponseDTO toResponseDTO(Cliente cliente) {
        return new ClienteResponseDTO(
                cliente.getId(),
                cliente.getCpf(),
                cliente.getNome(),
                toEnderecoResponseDTO(cliente.getEndereco())
        );
    }

    private EnderecoResponseDTO toEnderecoResponseDTO(Endereco endereco) {
        return new EnderecoResponseDTO(
                endereco.getId(),
                endereco.getRua(),
                endereco.getNumero(),
                endereco.getBairro(),
                endereco.getCep(),
                endereco.getCidade(),
                endereco.getEstado()
        );
    }
}
