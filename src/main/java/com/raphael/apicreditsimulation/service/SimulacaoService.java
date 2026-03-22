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
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SimulacaoService {

    private final SimulacaoRepository simulacaoRepository;
    private final ClienteRepository clienteRepository;

    @Transactional
    public SimulacaoResponseDTO criar(Long clienteId, SimulacaoRequestDTO dto) {
        Cliente cliente = buscarClientePorId(clienteId);

        Simulacao simulacao = Simulacao.builder()
                .cliente(cliente)
                .dataHoraSimulacao(dto.dataHoraSimulacao())
                .valorSolicitado(dto.valorSolicitado())
                .valorGarantia(dto.valorGarantia())
                .quantidadeMeses(dto.quantidadeMeses())
                .taxaJurosMensal(dto.taxaJurosMensal())
                .build();

        return toResponseDTO(simulacaoRepository.save(simulacao));
    }

    @Transactional(readOnly = true)
    public Page<SimulacaoResponseDTO> listarPorCliente(Long clienteId, Pageable pageable) {
        buscarClientePorId(clienteId);

        return simulacaoRepository.findAllByClienteId(clienteId, pageable)
                .map(this::toResponseDTO);
    }

    @Transactional(readOnly = true)
    public String exportarCsv(Long clienteId) {
        List<Simulacao> simulacoes = buscarSimulacoesDoCliente(clienteId);

        StringBuilder sb = new StringBuilder();
        sb.append("id,clienteId,dataHoraSimulacao,valorSolicitado,valorGarantia,quantidadeMeses,taxaJurosMensal\n");

        for (Simulacao simulacao : simulacoes) {
            sb.append(simulacao.getId()).append(",")
                    .append(simulacao.getCliente().getId()).append(",")
                    .append(simulacao.getDataHoraSimulacao()).append(",")
                    .append(simulacao.getValorSolicitado()).append(",")
                    .append(simulacao.getValorGarantia()).append(",")
                    .append(simulacao.getQuantidadeMeses()).append(",")
                    .append(simulacao.getTaxaJurosMensal()).append("\n");
        }

        return sb.toString();
    }

    @Transactional(readOnly = true)
    public String exportarTxt(Long clienteId) {
        List<Simulacao> simulacoes = buscarSimulacoesDoCliente(clienteId);

        StringBuilder sb = new StringBuilder();
        sb.append("=== SIMULACOES DO CLIENTE ").append(clienteId).append(" ===\n\n");

        for (Simulacao simulacao : simulacoes) {
            sb.append("ID: ").append(simulacao.getId()).append("\n")
                    .append("Data/Hora: ").append(simulacao.getDataHoraSimulacao()).append("\n")
                    .append("Valor Solicitado: ").append(simulacao.getValorSolicitado()).append("\n")
                    .append("Valor Garantia: ").append(simulacao.getValorGarantia()).append("\n")
                    .append("Quantidade Meses: ").append(simulacao.getQuantidadeMeses()).append("\n")
                    .append("Taxa Juros Mensal: ").append(simulacao.getTaxaJurosMensal()).append("\n")
                    .append("---\n");
        }

        return sb.toString();
    }

    private Cliente buscarClientePorId(Long clienteId) {
        return clienteRepository.findById(clienteId)
                .orElseThrow(() -> new NotFoundException("Cliente nao encontrado."));
    }

    private List<Simulacao> buscarSimulacoesDoCliente(Long clienteId) {
        buscarClientePorId(clienteId);
        return simulacaoRepository.findAllByClienteIdOrderByDataHoraSimulacaoDesc(clienteId);
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
