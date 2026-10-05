package br.com.fiap3esr.autoescola3esr.adapter.in.controller.request.usuario;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record DadosAlteracaoSenha(
        @NotBlank
        @JsonProperty("senha_atual")
        String senhaAtual,

        @NotBlank
        @Size(min = 6, message = "A nova senha deve conter no mínimo 6 caracteres")
        @JsonProperty("nova_senha")
        String novaSenha) {
}
