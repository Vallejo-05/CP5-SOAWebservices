package br.com.fiap3esr.autoescola3esr.adapter.in.controller;

import br.com.fiap3esr.autoescola3esr.adapter.in.controller.request.instrucao.DadosAgendamento;
import br.com.fiap3esr.autoescola3esr.adapter.in.controller.request.instrucao.DadosCancelamento;
import br.com.fiap3esr.autoescola3esr.adapter.in.controller.response.instrucao.DadosDetalhamentoAgendamento;
import br.com.fiap3esr.autoescola3esr.application.service.AgendaDeInstrucoes;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/instrucoes")
@SecurityRequirement(name = "bearer-key")
@Tag(name = "Instruções", description = "Agendamento, consulta e cancelamento de instruções")
@RequiredArgsConstructor
public class InstrucaoController {
    private final AgendaDeInstrucoes agenda;

    @PostMapping
    @Operation(
            summary = "Agendar instrução",
            description = "Formato da data/hora: dd/MM/yyyy - HH:mm. Se o instrutor não for informado, " +
                    "um instrutor disponível (opcionalmente da especialidade informada) é escolhido aleatoriamente.")
    @ApiResponse(responseCode = "201", description = "Instrução agendada")
    @ApiResponse(responseCode = "400", description = "Dados inválidos ou regra de negócio violada")
    @ApiResponse(responseCode = "404", description = "Aluno ou instrutor não encontrado")
    public ResponseEntity<DadosDetalhamentoAgendamento> agendarInstrucao(
            @RequestBody @Valid DadosAgendamento dados,
            UriComponentsBuilder uriBuilder) {
        DadosDetalhamentoAgendamento dto = agenda.agendar(dados);
        URI uri = uriBuilder
                .path("/instrucoes/{id}")
                .buildAndExpand(dto.id())
                .toUri();
        return ResponseEntity.created(uri).body(dto);
    }

    @GetMapping
    @Operation(summary = "Listar instruções", description = "Ordenado por data/hora, 10 registros por página.")
    public ResponseEntity<Page<DadosDetalhamentoAgendamento>> listarInstrucoes(
            @ParameterObject @PageableDefault(size = 10, sort = "dataHora") Pageable paginacao) {
        return ResponseEntity.ok(agenda.listar(paginacao));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Detalhar instrução")
    @ApiResponse(responseCode = "404", description = "Instrução não encontrada")
    public ResponseEntity<DadosDetalhamentoAgendamento> detalharInstrucao(@PathVariable Long id) {
        return ResponseEntity.ok(agenda.detalhar(id));
    }

    @PostMapping("/{id}/cancelamento")
    @Operation(
            summary = "Cancelar instrução",
            description = "Motivo obrigatório (ALUNO_DESISTIU, INSTRUTOR_CANCELOU ou OUTROS). " +
                    "Somente com antecedência mínima de 24 horas.")
    @ApiResponse(responseCode = "200", description = "Instrução cancelada")
    @ApiResponse(responseCode = "400", description = "Motivo não informado ou regra de negócio violada")
    @ApiResponse(responseCode = "404", description = "Instrução não encontrada")
    public ResponseEntity<DadosDetalhamentoAgendamento> cancelarInstrucao(
            @PathVariable Long id,
            @RequestBody @Valid DadosCancelamento dados) {
        return ResponseEntity.ok(agenda.cancelar(id, dados));
    }
}
