package br.com.fiap3esr.autoescola3esr.adapter.in.controller.request.instrucao;

import br.com.fiap3esr.autoescola3esr.shared.vo.enumeration.MotivoCancelamento;
import jakarta.validation.constraints.NotNull;

public record DadosCancelamento(
        @NotNull(message = "É obrigatório informar o motivo do cancelamento (ALUNO_DESISTIU, INSTRUTOR_CANCELOU ou OUTROS)")
        MotivoCancelamento motivo) {
}
