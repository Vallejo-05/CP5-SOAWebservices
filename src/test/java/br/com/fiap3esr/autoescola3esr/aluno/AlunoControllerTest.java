package br.com.fiap3esr.autoescola3esr.aluno;

import br.com.fiap3esr.autoescola3esr.adapter.in.controller.response.aluno.DadosDetalhamentoAluno;
import br.com.fiap3esr.autoescola3esr.adapter.in.controller.response.aluno.DadosListagemAluno;
import br.com.fiap3esr.autoescola3esr.application.service.AlunoService;
import br.com.fiap3esr.autoescola3esr.support.DadosTeste;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AlunoControllerTest {
    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    AlunoService service;

    @Test
    @DisplayName("Expectativa: Retornar código 201 ao cadastrar aluno com dados válidos.")
    @WithMockUser(roles = "ADMIN")
    void cadastrarAlunoCenario1() throws Exception {
        when(service.cadastrarAluno(any()))
                .thenReturn(new DadosDetalhamentoAluno(DadosTeste.aluno(1L, true)));

        mockMvc.perform(post("/alunos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "nome": "Aluno Teste",
                                    "email": "alunoteste@email.com.br",
                                    "telefone": "(11) 98765-4321",
                                    "cpf": "12345678901",
                                    "endereco": %s
                                }
                                """.formatted(DadosTeste.JSON_ENDERECO)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/alunos/1"))
                .andExpect(jsonPath("$.cpf").value("12345678901"));
    }

    @Test
    @DisplayName("Expectativa: Retornar código 400 ao cadastrar aluno com CPF inválido.")
    @WithMockUser(roles = "ADMIN")
    void cadastrarAlunoCenario2() throws Exception {
        mockMvc.perform(post("/alunos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "nome": "Aluno Teste",
                                    "email": "alunoteste@email.com.br",
                                    "telefone": "(11) 98765-4321",
                                    "cpf": "123",
                                    "endereco": %s
                                }
                                """.formatted(DadosTeste.JSON_ENDERECO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].campo").value("cpf"));
        verify(service, never()).cadastrarAluno(any());
    }

    @Test
    @DisplayName("Expectativa: Listagem deve exibir nome, e-mail e CPF dos alunos.")
    @WithMockUser(roles = "USER")
    void listarAlunosCenario1() throws Exception {
        when(service.listarAlunos(any()))
                .thenReturn(new PageImpl<>(List.of(new DadosListagemAluno(DadosTeste.aluno(1L, true)))));

        mockMvc.perform(get("/alunos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].nome").value("Aluno Teste"))
                .andExpect(jsonPath("$.content[0].email").value("alunoteste@email.com.br"))
                .andExpect(jsonPath("$.content[0].cpf").value("12345678901"));
    }

    @Test
    @DisplayName("Expectativa: Retornar código 400 ao tentar alterar o CPF do aluno.")
    @WithMockUser(roles = "ADMIN")
    void atualizarAlunoCenario1() throws Exception {
        mockMvc.perform(put("/alunos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                { "id": 1, "cpf": "99999999999" }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].campo").value("cpf"));
        verify(service, never()).atualizarAluno(any());
    }

    @Test
    @DisplayName("Expectativa: Retornar código 204 ao excluir aluno.")
    @WithMockUser(roles = "ADMIN")
    void excluirAlunoCenario1() throws Exception {
        mockMvc.perform(delete("/alunos/1"))
                .andExpect(status().isNoContent());
        verify(service).excluirAluno(1L);
    }
}
