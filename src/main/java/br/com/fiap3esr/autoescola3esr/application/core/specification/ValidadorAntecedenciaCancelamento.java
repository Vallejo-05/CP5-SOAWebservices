package br.com.fiap3esr.autoescola3esr.application.core.specification;

import br.com.fiap3esr.autoescola3esr.adapter.in.controller.request.instrucao.DadosCancelamento;
import br.com.fiap3esr.autoescola3esr.application.core.domain.Instrucao;
import br.com.fiap3esr.autoescola3esr.application.core.specification.contract.ValidadorCancelamento;
import br.com.fiap3esr.autoescola3esr.exception.type.ValidacaoException;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;

@Component
public class ValidadorAntecedenciaCancelamento implements ValidadorCancelamento {
    @Override
    public void validar(Instrucao instrucao, DadosCancelamento dados) {
        long antecedencia = Duration.between(LocalDateTime.now(), instrucao.getDataHora()).toMinutes();

        if (antecedencia < 24 * 60) {
            throw new ValidacaoException("A instrução somente pode ser cancelada com antecedência mínima de 24 horas!");
        }
    }
}
