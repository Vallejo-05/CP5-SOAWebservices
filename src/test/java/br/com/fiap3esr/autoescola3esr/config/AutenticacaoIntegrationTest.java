package br.com.fiap3esr.autoescola3esr.config;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// Teste de integração: login real com o usuário admin criado pela migration e uso do token JWT
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AutenticacaoIntegrationTest {
    @Autowired
    MockMvc mockMvc;

    @Test
    @DisplayName("Expectativa: Login válido gera token JWT que permite acessar endpoints protegidos.")
    void loginCenario1() throws Exception {
        String resposta = mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                { "login": "admin", "senha": "admin" }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tokenJWT").isNotEmpty())
                .andReturn()
                .getResponse()
                .getContentAsString();
        String token = JsonPath.read(resposta, "$.tokenJWT");

        mockMvc.perform(get("/usuarios").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].login").value("admin"));
    }

    @Test
    @DisplayName("Expectativa: Login com senha incorreta deve retornar 401.")
    void loginCenario2() throws Exception {
        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                { "login": "admin", "senha": "errada" }
                                """))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Expectativa: Token inválido deve retornar 401.")
    void tokenInvalidoCenario1() throws Exception {
        mockMvc.perform(get("/instrutores").header("Authorization", "Bearer token-invalido"))
                .andExpect(status().isUnauthorized());
    }
}
