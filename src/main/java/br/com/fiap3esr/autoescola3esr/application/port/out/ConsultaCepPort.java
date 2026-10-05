package br.com.fiap3esr.autoescola3esr.application.port.out;

import br.com.fiap3esr.autoescola3esr.shared.vo.endereco.Endereco;

// Porta de saída para consulta de endereços em um serviço externo (ex.: ViaCEP)
public interface ConsultaCepPort {
    Endereco consultar(String cep);
}
