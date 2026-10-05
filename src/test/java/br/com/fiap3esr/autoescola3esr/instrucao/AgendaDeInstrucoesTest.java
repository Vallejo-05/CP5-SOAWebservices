package br.com.fiap3esr.autoescola3esr.instrucao;

import br.com.fiap3esr.autoescola3esr.adapter.in.controller.request.instrucao.DadosAgendamento;
import br.com.fiap3esr.autoescola3esr.adapter.in.controller.request.instrucao.DadosCancelamento;
import br.com.fiap3esr.autoescola3esr.application.core.domain.Instrucao;
import br.com.fiap3esr.autoescola3esr.application.core.specification.contract.ValidadorAgendamento;
import br.com.fiap3esr.autoescola3esr.application.core.specification.contract.ValidadorCancelamento;
import br.com.fiap3esr.autoescola3esr.application.port.out.AlunoRepository;
import br.com.fiap3esr.autoescola3esr.application.port.out.InstrucaoRepository;
import br.com.fiap3esr.autoescola3esr.application.port.out.InstrutorRepository;
import br.com.fiap3esr.autoescola3esr.application.service.AgendaDeInstrucoes;
import br.com.fiap3esr.autoescola3esr.exception.type.AlunoNotFoundException;
import br.com.fiap3esr.autoescola3esr.exception.type.ValidacaoException;
import br.com.fiap3esr.autoescola3esr.shared.vo.enumeration.MotivoCancelamento;
import br.com.fiap3esr.autoescola3esr.support.DadosTeste;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AgendaDeInstrucoesTest {
    @Mock
    AlunoRepository alunoRepository;

    @Mock
    InstrutorRepository instrutorRepository;

    @Mock
    InstrucaoRepository repository;

    @Mock
    ValidadorAgendamento validadorAgendamento;

    @Mock
    ValidadorCancelamento validadorCancelamento;

    AgendaDeInstrucoes agenda;

    @BeforeEach
    void setUp() {
        agenda = new AgendaDeInstrucoes(
                alunoRepository,
                instrutorRepository,
                repository,
                List.of(validadorAgendamento),
                List.of(validadorCancelamento)
        );
    }

    @Test
    @DisplayName("Expectativa: Escolher instrutor aleatório quando o instrutor não for informado.")
    void agendarCenario1() {
        LocalDateTime dataHora = DadosTeste.proximaSegundaAs10();
        DadosAgendamento dados = new DadosAgendamento(1L, null, null, dataHora);
        when(alunoRepository.findById(1L)).thenReturn(Optional.of(DadosTeste.aluno(1L, true)));
        when(instrutorRepository.escolherInstrutorAleatorioDisponivel(null, dataHora))
                .thenReturn(DadosTeste.instrutor(7L, true));
        when(repository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        var dto = agenda.agendar(dados);

        assertThat(dto.idInstrutor()).isEqualTo(7L);
        verify(validadorAgendamento).validar(dados);
    }

    @Test
    @DisplayName("Expectativa: Lançar exceção quando não houver instrutor disponível.")
    void agendarCenario2() {
        LocalDateTime dataHora = DadosTeste.proximaSegundaAs10();
        when(alunoRepository.findById(1L)).thenReturn(Optional.of(DadosTeste.aluno(1L, true)));
        when(instrutorRepository.escolherInstrutorAleatorioDisponivel(null, dataHora)).thenReturn(null);

        assertThatThrownBy(() -> agenda.agendar(new DadosAgendamento(1L, null, null, dataHora)))
                .isInstanceOf(ValidacaoException.class);
        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("Expectativa: Não agendar quando o aluno não existir.")
    void agendarCenario3() {
        when(alunoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> agenda.agendar(new DadosAgendamento(99L, 1L, null, DadosTeste.proximaSegundaAs10())))
                .isInstanceOf(AlunoNotFoundException.class);
        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("Expectativa: Cancelar instrução registrando o motivo.")
    void cancelarCenario1() {
        Instrucao instrucao = DadosTeste.instrucao(1L, DadosTeste.proximaSegundaAs10());
        when(repository.findById(1L)).thenReturn(Optional.of(instrucao));
        when(repository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        var dto = agenda.cancelar(1L, new DadosCancelamento(MotivoCancelamento.INSTRUTOR_CANCELOU));

        assertThat(dto.cancelada()).isTrue();
        assertThat(dto.motivoCancelamento()).isEqualTo(MotivoCancelamento.INSTRUTOR_CANCELOU);
    }

    @Test
    @DisplayName("Expectativa: Não cancelar quando um validador reprovar o cancelamento.")
    void cancelarCenario2() {
        Instrucao instrucao = DadosTeste.instrucao(1L, DadosTeste.proximaSegundaAs10());
        DadosCancelamento dados = new DadosCancelamento(MotivoCancelamento.OUTROS);
        when(repository.findById(1L)).thenReturn(Optional.of(instrucao));
        doThrow(new ValidacaoException("erro")).when(validadorCancelamento).validar(instrucao, dados);

        assertThatThrownBy(() -> agenda.cancelar(1L, dados)).isInstanceOf(ValidacaoException.class);
        assertThat(instrucao.isCancelada()).isFalse();
        verify(repository, never()).save(any());
    }
}
