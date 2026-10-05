package br.com.fiap3esr.autoescola3esr.endereco;

import br.com.fiap3esr.autoescola3esr.application.port.out.ConsultaCepPort;
import br.com.fiap3esr.autoescola3esr.exception.type.CepNotFoundException;
import br.com.fiap3esr.autoescola3esr.shared.vo.endereco.Endereco;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class EnderecoControllerTest {
    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    ConsultaCepPort consultaCep;

    @Test
    @DisplayName("Expectativa: Retornar o endereço do CEP consultado.")
    @WithMockUser
    void consultarCepCenario1() throws Exception {
        when(consultaCep.consultar("01310100")).thenReturn(new Endereco(
                "Avenida Paulista", null, null, "Bela Vista", "São Paulo", "SP", "01310-100"));

        mockMvc.perform(get("/enderecos/cep/01310100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.logradouro").value("Avenida Paulista"))
                .andExpect(jsonPath("$.cidade").value("São Paulo"));
    }

    @Test
    @DisplayName("Expectativa: Retornar 404 para CEP inexistente.")
    @WithMockUser
    void consultarCepCenario2() throws Exception {
        when(consultaCep.consultar("99999999")).thenThrow(new CepNotFoundException("CEP não encontrado"));

        mockMvc.perform(get("/enderecos/cep/99999999"))
                .andExpect(status().isNotFound());
    }
}
