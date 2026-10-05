package br.com.fiap3esr.autoescola3esr.usuario;

import br.com.fiap3esr.autoescola3esr.adapter.in.controller.response.usuario.DadosDetalhamentoUsuario;
import br.com.fiap3esr.autoescola3esr.application.service.UsuarioService;
import br.com.fiap3esr.autoescola3esr.shared.vo.enumeration.Role;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class UsuarioControllerTest {
    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    UsuarioService service;

    private static final String JSON_CADASTRO = """
            { "login": "novo.usuario", "senha": "senha123", "perfil": "USER" }
            """;

    @Test
    @DisplayName("Expectativa: Administrador consegue cadastrar usuário (201) e a senha não é devolvida.")
    @WithMockUser(roles = "ADMIN")
    void cadastrarUsuarioCenario1() throws Exception {
        when(service.cadastrarUsuario(any()))
                .thenReturn(new DadosDetalhamentoUsuario(2L, "novo.usuario", Role.USER));

        mockMvc.perform(post("/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JSON_CADASTRO))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.login").value("novo.usuario"))
                .andExpect(jsonPath("$.senha").doesNotExist());
    }

    @Test
    @DisplayName("Expectativa: Usuário sem perfil ADMIN não pode cadastrar usuários (403).")
    @WithMockUser(roles = "USER")
    void cadastrarUsuarioCenario2() throws Exception {
        mockMvc.perform(post("/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JSON_CADASTRO))
                .andExpect(status().isForbidden());
        verify(service, never()).cadastrarUsuario(any());
    }

    @Test
    @DisplayName("Expectativa: Usuário sem perfil ADMIN não pode listar usuários (403).")
    @WithMockUser(roles = "USER")
    void listarUsuariosCenario1() throws Exception {
        mockMvc.perform(get("/usuarios"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Expectativa: Administrador consegue atualizar o perfil de um usuário.")
    @WithMockUser(roles = "ADMIN")
    void atualizarPerfilCenario1() throws Exception {
        when(service.atualizarPerfil(eq(2L), any()))
                .thenReturn(new DadosDetalhamentoUsuario(2L, "novo.usuario", Role.ADMIN));

        mockMvc.perform(put("/usuarios/2/perfil")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                { "perfil": "ADMIN" }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.perfil").value("ADMIN"));
    }

    @Test
    @DisplayName("Expectativa: Qualquer usuário autenticado pode alterar a própria senha (204).")
    @WithMockUser(username = "joao", roles = "USER")
    void alterarPropriaSenhaCenario1() throws Exception {
        mockMvc.perform(put("/usuarios/senha")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                { "senha_atual": "senha123", "nova_senha": "novaSenha456" }
                                """))
                .andExpect(status().isNoContent());
        verify(service).alterarPropriaSenha(eq("joao"), any());
    }

    @Test
    @DisplayName("Expectativa: Administrador consegue excluir usuário (204).")
    @WithMockUser(username = "admin", roles = "ADMIN")
    void excluirUsuarioCenario1() throws Exception {
        mockMvc.perform(delete("/usuarios/2"))
                .andExpect(status().isNoContent());
        verify(service).excluirUsuario(2L, "admin");
    }
}
