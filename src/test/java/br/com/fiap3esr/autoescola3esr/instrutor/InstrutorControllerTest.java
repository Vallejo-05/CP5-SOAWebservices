package br.com.fiap3esr.autoescola3esr.instrutor;

import br.com.fiap3esr.autoescola3esr.adapter.in.controller.response.instrutor.DadosDetalhamentoInstrutor;
import br.com.fiap3esr.autoescola3esr.application.service.InstrutorService;
import br.com.fiap3esr.autoescola3esr.exception.type.InstrutorNotFoundException;
import br.com.fiap3esr.autoescola3esr.support.DadosTeste;
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
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class InstrutorControllerTest {
    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    InstrutorService service;

    private static final String JSON_CADASTRO = """
            {
                "nome": "Instrutor Teste",
                "email": "instrutorteste@email.com.br",
                "telefone": "(11) 91234-5678",
                "cnh": "01234567890",
                "especialidade": "MOTOS",
                "endereco": %s
            }
            """.formatted(DadosTeste.JSON_ENDERECO);

    @Test
    @DisplayName("Expectativa: Retornar código 201 ao cadastrar instrutor com dados válidos.")
    @WithMockUser(roles = "ADMIN")
    void cadastrarInstrutorCenario1() throws Exception {
        when(service.cadastrarInstrutor(any()))
                .thenReturn(new DadosDetalhamentoInstrutor(DadosTeste.instrutor(1L, true)));

        mockMvc.perform(post("/instrutores")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JSON_CADASTRO))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/instrutores/1"))
                .andExpect(jsonPath("$.nome").value("Instrutor Teste"))
                .andExpect(jsonPath("$.ativo").value(true));
    }

    @Test
    @DisplayName("Expectativa: Retornar código 400 ao cadastrar instrutor com dados inválidos.")
    @WithMockUser(roles = "ADMIN")
    void cadastrarInstrutorCenario2() throws Exception {
        mockMvc.perform(post("/instrutores")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
        verify(service, never()).cadastrarInstrutor(any());
    }

    @Test
    @DisplayName("Expectativa: Retornar código 403 quando um usuário sem perfil ADMIN tentar cadastrar instrutor.")
    @WithMockUser(roles = "USER")
    void cadastrarInstrutorCenario3() throws Exception {
        mockMvc.perform(post("/instrutores")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JSON_CADASTRO))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Expectativa: Retornar código 401 quando a requisição não possuir token.")
    void listarInstrutoresCenario1() throws Exception {
        mockMvc.perform(get("/instrutores"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Expectativa: Retornar código 400 ao tentar alterar o e-mail do instrutor.")
    @WithMockUser(roles = "ADMIN")
    void atualizarInstrutorCenario1() throws Exception {
        mockMvc.perform(put("/instrutores")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                { "id": 1, "email": "novo@email.com.br" }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].campo").value("email"));
        verify(service, never()).atualizarInstrutor(any());
    }

    @Test
    @DisplayName("Expectativa: Retornar código 404 ao detalhar instrutor inexistente.")
    @WithMockUser(roles = "ADMIN")
    void detalharInstrutorCenario1() throws Exception {
        when(service.detalharInstrutor(99L))
                .thenThrow(new InstrutorNotFoundException("ID do Instrutor informado não existe!"));

        mockMvc.perform(get("/instrutores/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Expectativa: Retornar código 204 ao excluir instrutor.")
    @WithMockUser(roles = "ADMIN")
    void excluirInstrutorCenario1() throws Exception {
        mockMvc.perform(delete("/instrutores/1"))
                .andExpect(status().isNoContent());
        verify(service).excluirInstrutor(1L);
    }
}
