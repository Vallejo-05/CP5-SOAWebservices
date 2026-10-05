package br.com.fiap3esr.autoescola3esr.adapter.in.controller.request.usuario;

import br.com.fiap3esr.autoescola3esr.shared.vo.enumeration.Role;
import jakarta.validation.constraints.NotNull;

public record DadosAtualizacaoPerfil(
        @NotNull
        Role perfil) {
}
