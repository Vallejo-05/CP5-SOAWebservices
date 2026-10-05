package br.com.fiap3esr.autoescola3esr.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class SwaggerConfigTest {
    @Autowired
    MockMvc mockMvc;

    @Test
    @DisplayName("Expectativa: Documentação OpenAPI gerada automaticamente e acessível sem autenticação.")
    void apiDocsCenario1() throws Exception {
        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.info.title").value("Auto Escola 3ESR"))
                .andExpect(jsonPath("$.components.securitySchemes.bearer-key.scheme").value("bearer"))
                .andExpect(jsonPath("$.paths['/instrutores']").exists())
                .andExpect(jsonPath("$.paths['/alunos']").exists())
                .andExpect(jsonPath("$.paths['/usuarios']").exists())
                .andExpect(jsonPath("$.paths['/instrucoes']").exists())
                .andExpect(jsonPath("$.paths['/enderecos/cep/{cep}']").exists());
    }

    @Test
    @DisplayName("Expectativa: Swagger UI acessível sem autenticação.")
    void swaggerUiCenario1() throws Exception {
        mockMvc.perform(get("/swagger-ui/index.html"))
                .andExpect(status().isOk());
    }
}
