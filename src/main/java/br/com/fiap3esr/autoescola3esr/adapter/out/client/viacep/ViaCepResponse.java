package br.com.fiap3esr.autoescola3esr.adapter.out.client.viacep;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

// Representa o JSON retornado pela API pública ViaCEP (https://viacep.com.br)
@JsonIgnoreProperties(ignoreUnknown = true)
public record ViaCepResponse(
        String cep,
        String logradouro,
        String complemento,
        String bairro,
        String localidade,
        String uf,
        String erro) {
    public boolean cepInexistente() {
        return "true".equalsIgnoreCase(erro);
    }
}
