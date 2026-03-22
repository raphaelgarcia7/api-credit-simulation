package com.raphael.apicreditsimulation.service;

import com.raphael.apicreditsimulation.dto.ClienteRequestDTO;
import com.raphael.apicreditsimulation.dto.ClienteResponseDTO;
import com.raphael.apicreditsimulation.entities.Cliente;
import com.raphael.apicreditsimulation.exception.ConflictException;
import com.raphael.apicreditsimulation.exception.NotFoundException;
import com.raphael.apicreditsimulation.repository.ClienteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
public class ClienteService {

    private final ClienteRepository clienteRepository;

    public ClienteResponseDTO criar(ClienteRequestDTO dto) {
        clienteRepository.findByCpf(dto.cpf()).
                ifPresent(cliente -> {
                    throw new ConflictException("CPF já cadastrado");
                });

        Cliente cliente = Cliente.builder()
                .cpf(dto.cpf())
                .nome(dto.nome())
                .endereco(dto.endereco())
                .build();

        Cliente salvo = clienteRepository.save(cliente);

        return toResponseDTO(salvo);
    }

    public List<ClienteResponseDTO> listarTodos() {
        return clienteRepository.findAll()
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    public ClienteResponseDTO buscarPorId(Long id) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Cliente não encontrado"));

        return toResponseDTO(cliente);
    }

    public ClienteResponseDTO atualizar(Long id, ClienteRequestDTO dto) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Cliente não encontrado."));

        cliente.setCpf(dto.cpf());
        cliente.setNome(dto.nome());
        cliente.setEndereco(dto.endereco());

        Cliente salvo = clienteRepository.save(cliente);
        return toResponseDTO(salvo);
    }

    public void deletar(Long id) {
        clienteRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Cliente não encontrado."));

        clienteRepository.deleteById(id);
    }

    private ClienteResponseDTO toResponseDTO(Cliente cliente) {
        return new ClienteResponseDTO(
                cliente.getId(),
                cliente.getCpf(),
                cliente.getNome(),
                cliente.getEndereco()
        );
    }
}
