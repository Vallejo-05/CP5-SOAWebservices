package br.com.fiap3esr.autoescola3esr.adapter.in.controller.request.instrutor;

import br.com.fiap3esr.autoescola3esr.shared.vo.endereco.dto.DadosEndereco;
import br.com.fiap3esr.autoescola3esr.shared.vo.enumeration.Especialidade;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;

public record DadosAtualizacaoInstrutor(
        @NotNull
        Long id,
        String nome,
        String telefone,
        DadosEndereco endereco,

        // Regras de negócio: campos que NÃO podem ser alterados
        @Null(message = "Não é permitido alterar o e-mail do instrutor")
        String email,

        @Null(message = "Não é permitido alterar a CNH do instrutor")
        String cnh,

        @Null(message = "Não é permitido alterar a especialidade do instrutor")
        Especialidade especialidade) {
}
