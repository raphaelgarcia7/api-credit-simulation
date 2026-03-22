package com.raphael.apicreditsimulation.service;

import com.raphael.apicreditsimulation.dto.SimulacaoRequestDTO;
import com.raphael.apicreditsimulation.dto.SimulacaoResponseDTO;
import com.raphael.apicreditsimulation.entities.Cliente;
import com.raphael.apicreditsimulation.entities.Simulacao;
import com.raphael.apicreditsimulation.exception.NotFoundException;
import com.raphael.apicreditsimulation.repository.ClienteRepository;
import com.raphael.apicreditsimulation.repository.SimulacaoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SimulacaoService {

    private final SimulacaoRepository simulacaoRepository;
    private final ClienteRepository clienteRepository;

    public SimulacaoResponseDTO criar(SimulacaoRequestDTO dto) {
        Cliente cliente = clienteRepository.findById(dto.clienteId())
                .orElseThrow(() -> new NotFoundException("Cliente não encontrado."));

        Simulacao simulacao = Simulacao.builder()
                .cliente(cliente)
                .dataHoraSimulacao(dto.dataHoraSimulacao())
                .valorSolicitado(dto.valorSolicitado())
                .valorGarantia(dto.valorGarantia())
                .quantidadeMeses(dto.quantidadeMeses())
                .taxaJurosMensal(dto.taxaJurosMensal())
                .build();

        Simulacao salvo = simulacaoRepository.save(simulacao);
        return toResponseDTO(salvo);
    }

    public List<SimulacaoResponseDTO> listarPorCliente(Long clienteId) {
        clienteRepository.findById(clienteId)
                .orElseThrow(() -> new NotFoundException("Cliente não encontrado."));

        return simulacaoRepository.findByClienteId(clienteId)
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    public Page<SimulacaoResponseDTO> listarPorClientePaginado(Long clienteId, Pageable pageable) {
        clienteRepository.findById(clienteId)
                .orElseThrow(() -> new NotFoundException("Cliente não encontrado"));

        return simulacaoRepository.findByClienteId(clienteId, pageable)
                .map(this::toResponseDTO);
    }

    public String exportarCsv(Long clienteId) {
        clienteRepository.findById(clienteId)
                .orElseThrow(() -> new NotFoundException("Cliente não encontrado"));

        List<Simulacao> simulacoes = simulacaoRepository.findByClienteId(clienteId);

        StringBuilder sb = new StringBuilder();
        sb.append("id,clienteId,dataHoraSimulacao,valorSolicitado,valorGarantia,quantidadeMeses,taxaJurosMensal\n");

        for (Simulacao s : simulacoes) {
            sb.append(s.getId()).append(",")
                    .append(s.getCliente().getId()).append(",")
                    .append(s.getDataHoraSimulacao()).append(",")
                    .append(s.getValorSolicitado()).append(",")
                    .append(s.getValorGarantia()).append(",")
                    .append(s.getQuantidadeMeses()).append(",")
                    .append(s.getTaxaJurosMensal()).append("\n");
        }

        return sb.toString();
    }

    public String exportarTxt(Long clienteId) {
        clienteRepository.findById(clienteId)
                .orElseThrow(() -> new NotFoundException("Cliente não encontrado"));

        List<Simulacao> simulacoes = simulacaoRepository.findByClienteId(clienteId);

        StringBuilder sb = new StringBuilder();
        sb.append("=== SIMULAÇÕES DO CLIENTE ").append(clienteId).append(" ===\n\n");

        for (Simulacao s : simulacoes) {
            sb.append("ID: ").append(s.getId()).append("\n")
                    .append("Data/Hora: ").append(s.getDataHoraSimulacao()).append("\n")
                    .append("Valor Solicitado: ").append(s.getValorSolicitado()).append("\n")
                    .append("Valor Garantia: ").append(s.getValorGarantia()).append("\n")
                    .append("Quantidade Meses: ").append(s.getQuantidadeMeses()).append("\n")
                    .append("Taxa Juros Mensal: ").append(s.getTaxaJurosMensal()).append("\n")
                    .append("---\n");
        }

        return sb.toString();
    }

    private SimulacaoResponseDTO toResponseDTO(Simulacao simulacao) {
        return new SimulacaoResponseDTO(
                simulacao.getId(),
                simulacao.getCliente().getId(),
                simulacao.getDataHoraSimulacao(),
                simulacao.getValorSolicitado(),
                simulacao.getValorGarantia(),
                simulacao.getQuantidadeMeses(),
                simulacao.getTaxaJurosMensal()
        );
    }
}
