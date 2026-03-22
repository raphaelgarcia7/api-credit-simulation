package com.raphael.apicreditsimulation.controller;

import com.raphael.apicreditsimulation.dto.PaginaResponseDTO;
import com.raphael.apicreditsimulation.dto.SimulacaoRequestDTO;
import com.raphael.apicreditsimulation.dto.SimulacaoResponseDTO;
import com.raphael.apicreditsimulation.service.SimulacaoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/clientes/{clienteId}/simulacoes")
@RequiredArgsConstructor
public class SimulacaoController {

    private final SimulacaoService simulacaoService;

    @PostMapping
    public ResponseEntity<SimulacaoResponseDTO> criar(
            @PathVariable Long clienteId,
            @Valid @RequestBody SimulacaoRequestDTO dto) {

        SimulacaoResponseDTO response = simulacaoService.criar(clienteId, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<PaginaResponseDTO<SimulacaoResponseDTO>> listarPorCliente(
            @PathVariable Long clienteId,
            @PageableDefault(size = 10, sort = "dataHoraSimulacao", direction = Sort.Direction.DESC) Pageable pageable) {

        return ResponseEntity.ok(PaginaResponseDTO.from(simulacaoService.listarPorCliente(clienteId, pageable)));
    }

    @GetMapping(value = "/exportacao/csv", produces = "text/csv")
    public ResponseEntity<byte[]> exportarCsv(@PathVariable Long clienteId) {
        byte[] bytes = simulacaoService.exportarCsv(clienteId).getBytes(StandardCharsets.UTF_8);

        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=simulacoes.csv")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(bytes);
    }

    @GetMapping(value = "/exportacao/txt", produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<byte[]> exportarTxt(@PathVariable Long clienteId) {
        byte[] bytes = simulacaoService.exportarTxt(clienteId).getBytes(StandardCharsets.UTF_8);

        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=simulacoes.txt")
                .contentType(MediaType.TEXT_PLAIN)
                .body(bytes);
    }
}
