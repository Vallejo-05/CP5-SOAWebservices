package br.com.fiap3esr.autoescola3esr.adapter.in.controller.request.aluno;

import br.com.fiap3esr.autoescola3esr.shared.vo.endereco.dto.DadosEndereco;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;

public record DadosAtualizacaoAluno(
        @NotNull
        Long id,
        String nome,
        String telefone,
        DadosEndereco endereco,

        // Regras de negócio: campos que NÃO podem ser alterados
        @Null(message = "Não é permitido alterar o e-mail do aluno")
        String email,

        @Null(message = "Não é permitido alterar o CPF do aluno")
        String cpf) {
}
