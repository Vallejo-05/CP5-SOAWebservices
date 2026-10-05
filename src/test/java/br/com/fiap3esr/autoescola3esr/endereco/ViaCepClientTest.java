package br.com.fiap3esr.autoescola3esr.endereco;

import br.com.fiap3esr.autoescola3esr.adapter.out.client.viacep.ViaCepClient;
import br.com.fiap3esr.autoescola3esr.exception.type.CepNotFoundException;
import br.com.fiap3esr.autoescola3esr.exception.type.ServicoExternoException;
import br.com.fiap3esr.autoescola3esr.shared.vo.endereco.Endereco;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;
import static org.springframework.http.HttpMethod.GET;

// Testa o consumo da API externa ViaCEP simulando as respostas do serviço (sem acesso à internet)
class ViaCepClientTest {
    private static final String BASE_URL = "https://viacep.com.br/ws";

    MockRestServiceServer server;
    ViaCepClient client;

    @BeforeEach
    void setUp() {
        RestClient.Builder builder = RestClient.builder();
        server = MockRestServiceServer.bindTo(builder).build();
        client = new ViaCepClient(builder, BASE_URL);
    }

    @Test
    @DisplayName("Expectativa: Converter a resposta do ViaCEP em um endereço.")
    void consultarCenario1() {
        server.expect(requestTo(BASE_URL + "/01310100/json/"))
                .andExpect(method(GET))
                .andRespond(withSuccess("""
                        {
                          "cep": "01310-100",
                          "logradouro": "Avenida Paulista",
                          "complemento": "de 612 a 1510 - lado par",
                          "bairro": "Bela Vista",
                          "localidade": "São Paulo",
                          "uf": "SP",
                          "ibge": "3550308"
                        }
                        """, MediaType.APPLICATION_JSON));

        Endereco endereco = client.consultar("01310-100");

        assertThat(endereco.getLogradouro()).isEqualTo("Avenida Paulista");
        assertThat(endereco.getCidade()).isEqualTo("São Paulo");
        assertThat(endereco.getUf()).isEqualTo("SP");
        assertThat(endereco.getCep()).isEqualTo("01310-100");
        server.verify();
    }

    @Test
    @DisplayName("Expectativa: Lançar CepNotFoundException quando o ViaCEP indicar CEP inexistente.")
    void consultarCenario2() {
        server.expect(requestTo(BASE_URL + "/99999999/json/"))
                .andRespond(withSuccess("""
                        { "erro": "true" }
                        """, MediaType.APPLICATION_JSON));

        assertThatThrownBy(() -> client.consultar("99999-999"))
                .isInstanceOf(CepNotFoundException.class);
    }

    @Test
    @DisplayName("Expectativa: Lançar CepNotFoundException para CEP com formato inválido, sem chamar o serviço.")
    void consultarCenario3() {
        assertThatThrownBy(() -> client.consultar("123"))
                .isInstanceOf(CepNotFoundException.class);
        server.verify();
    }

    @Test
    @DisplayName("Expectativa: Lançar ServicoExternoException quando o ViaCEP estiver indisponível.")
    void consultarCenario4() {
        server.expect(requestTo(BASE_URL + "/01310100/json/"))
                .andRespond(withServerError());

        assertThatThrownBy(() -> client.consultar("01310100"))
                .isInstanceOf(ServicoExternoException.class);
    }
}
