package br.com.fiap3esr.autoescola3esr.application.core.specification;

import br.com.fiap3esr.autoescola3esr.application.core.specification.contract.ValidadorAgendamento;
import br.com.fiap3esr.autoescola3esr.adapter.in.controller.request.instrucao.DadosAgendamento;
import br.com.fiap3esr.autoescola3esr.application.port.out.InstrucaoRepository;
import br.com.fiap3esr.autoescola3esr.exception.type.ValidacaoException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Component
public class ValidadorLimiteDiarioAluno implements ValidadorAgendamento {
    private static final int LIMITE_DIARIO = 2;

    @Autowired
    private InstrucaoRepository repository;

    @Override
    public void validar(DadosAgendamento dados) {
        LocalDateTime inicioDia = dados.dataHora().toLocalDate().atStartOfDay();
        LocalDateTime fimDia = dados.dataHora().toLocalDate().atTime(LocalTime.MAX);

        long instrucoesNoDia = repository.countByAlunoIdAndDataHoraBetween(
                dados.idAluno(),
                inicioDia,
                fimDia
        );

        if (instrucoesNoDia >= LIMITE_DIARIO) {
            throw new ValidacaoException("Permitido o agendamento de no máximo duas instruções diárias por aluno!");
        }
    }
}
