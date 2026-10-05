package br.com.fiap3esr.autoescola3esr.adapter.in.controller.request.usuario;

import br.com.fiap3esr.autoescola3esr.shared.vo.enumeration.Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record DadosCadastroUsuario(
        @NotBlank
        @Size(max = 100)
        String login,

        @NotBlank
        @Size(min = 6, message = "A senha deve conter no mínimo 6 caracteres")
        String senha,

        @NotNull
        Role perfil) {
}
