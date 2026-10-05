package br.com.fiap3esr.autoescola3esr.application.service;

import br.com.fiap3esr.autoescola3esr.adapter.in.controller.response.endereco.DadosConsultaCep;
import br.com.fiap3esr.autoescola3esr.application.port.out.ConsultaCepPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ConsultaEnderecoService {
    private final ConsultaCepPort consultaCep;

    public DadosConsultaCep consultarPorCep(String cep) {
        return new DadosConsultaCep(consultaCep.consultar(cep));
    }
}
