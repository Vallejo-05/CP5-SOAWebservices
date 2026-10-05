package br.com.fiap3esr.autoescola3esr.application.core.specification;

import br.com.fiap3esr.autoescola3esr.adapter.in.controller.request.instrucao.DadosCancelamento;
import br.com.fiap3esr.autoescola3esr.application.core.domain.Instrucao;
import br.com.fiap3esr.autoescola3esr.application.core.specification.contract.ValidadorCancelamento;
import br.com.fiap3esr.autoescola3esr.exception.type.ValidacaoException;
import org.springframework.stereotype.Component;

@Component
public class ValidadorInstrucaoJaCancelada implements ValidadorCancelamento {
    @Override
    public void validar(Instrucao instrucao, DadosCancelamento dados) {
        if (instrucao.isCancelada()) {
            throw new ValidacaoException("A instrução informada já foi cancelada!");
        }
    }
}
