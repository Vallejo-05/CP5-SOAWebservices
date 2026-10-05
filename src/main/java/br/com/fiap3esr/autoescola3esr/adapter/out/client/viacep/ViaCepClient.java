package br.com.fiap3esr.autoescola3esr.adapter.out.client.viacep;

import br.com.fiap3esr.autoescola3esr.application.port.out.ConsultaCepPort;
import br.com.fiap3esr.autoescola3esr.exception.type.CepNotFoundException;
import br.com.fiap3esr.autoescola3esr.exception.type.ServicoExternoException;
import br.com.fiap3esr.autoescola3esr.shared.vo.endereco.Endereco;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

// Adaptador de saída que consome a API externa ViaCEP utilizando o RestClient do Spring
@Component
public class ViaCepClient implements ConsultaCepPort {
    private final RestClient restClient;

    @Autowired
    public ViaCepClient(@Value("${api.externa.viacep.url}") String baseUrl) {
        this(RestClient.builder(), baseUrl);
    }

    // Construtor utilizado nos testes para injetar um servidor simulado (MockRestServiceServer)
    public ViaCepClient(RestClient.Builder builder, String baseUrl) {
        this.restClient = builder.baseUrl(baseUrl).build();
    }

    @Override
    public Endereco consultar(String cep) {
        String cepNumerico = cep == null ? "" : cep.replaceAll("\\D", "");
        if (cepNumerico.length() != 8) {
            throw new CepNotFoundException("CEP informado é inválido: " + cep);
        }

        ViaCepResponse resposta;
        try {
            resposta = restClient
                    .get()
                    .uri("/{cep}/json/", cepNumerico)
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .body(ViaCepResponse.class);
        } catch (RestClientException ex) {
            throw new ServicoExternoException("Não foi possível consultar o CEP no serviço ViaCEP!", ex);
        }

        if (resposta == null || resposta.cepInexistente()) {
            throw new CepNotFoundException("CEP não encontrado: " + cep);
        }

        return new Endereco(
                resposta.logradouro(),
                null,
                resposta.complemento(),
                resposta.bairro(),
                resposta.localidade(),
                resposta.uf(),
                resposta.cep()
        );
    }
}
