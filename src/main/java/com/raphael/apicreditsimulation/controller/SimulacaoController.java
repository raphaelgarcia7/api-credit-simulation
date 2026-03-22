package com.raphael.apicreditsimulation.controller;

import com.raphael.apicreditsimulation.dto.SimulacaoRequestDTO;
import com.raphael.apicreditsimulation.dto.SimulacaoResponseDTO;
import com.raphael.apicreditsimulation.service.SimulacaoService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/simulacoes")
@RequiredArgsConstructor
public class SimulacaoController {

    private final SimulacaoService simulacaoService;

    @PostMapping
    public ResponseEntity<SimulacaoResponseDTO> criar(@RequestBody SimulacaoRequestDTO dto) {
        SimulacaoResponseDTO response = simulacaoService.criar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<SimulacaoResponseDTO>> listarPorCliente(@PathVariable Long clienteId) {
        List<SimulacaoResponseDTO> response = simulacaoService.listarPorCliente(clienteId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/cliente/{clienteId}/paginado")
    public ResponseEntity<Page<SimulacaoResponseDTO>> listarPorClientePaginado(
            @PathVariable Long clienteId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<SimulacaoResponseDTO> response = simulacaoService.listarPorClientePaginado(clienteId, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/cliente/{clienteId}/exportar/csv")
    public ResponseEntity<byte[]> exportarCsv(@PathVariable Long clienteId) {
        String conteudo = simulacaoService.exportarCsv(clienteId);
        byte[] bytes = conteudo.getBytes();

        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=simulacoes.csv")
                .header("Content-Type", "text/csv")
                .body(bytes);
    }

    @GetMapping("/cliente/{clienteId}/exportar/txt")
    public ResponseEntity<byte[]> exportarTxt(@PathVariable Long clienteId) {
        String conteudo = simulacaoService.exportarTxt(clienteId);
        byte[] bytes = conteudo.getBytes();

        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=simulacoes.txt")
                .header("Content-Type", "text/plain")
                .body(bytes);
    }
}
