package br.com.fiap3esr.autoescola3esr.instrucao;

import br.com.fiap3esr.autoescola3esr.adapter.in.controller.request.instrucao.DadosAgendamento;
import br.com.fiap3esr.autoescola3esr.adapter.in.controller.request.instrucao.DadosCancelamento;
import br.com.fiap3esr.autoescola3esr.application.core.domain.Instrucao;
import br.com.fiap3esr.autoescola3esr.application.core.specification.*;
import br.com.fiap3esr.autoescola3esr.application.port.out.InstrucaoRepository;
import br.com.fiap3esr.autoescola3esr.exception.type.ValidacaoException;
import br.com.fiap3esr.autoescola3esr.shared.vo.enumeration.MotivoCancelamento;
import br.com.fiap3esr.autoescola3esr.support.DadosTeste;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.DayOfWeek;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ValidadoresInstrucaoTest {
    @Mock
    InstrucaoRepository repository;

    @InjectMocks
    ValidadorLimiteDiarioAluno validadorLimiteDiarioAluno;

    private final ValidadorHorarioFuncionamento validadorHorarioFuncionamento = new ValidadorHorarioFuncionamento();
    private final ValidadorHorarioAntecedencia validadorHorarioAntecedencia = new ValidadorHorarioAntecedencia();
    private final ValidadorAntecedenciaCancelamento validadorAntecedenciaCancelamento = new ValidadorAntecedenciaCancelamento();
    private final ValidadorInstrucaoJaCancelada validadorInstrucaoJaCancelada = new ValidadorInstrucaoJaCancelada();

    private DadosAgendamento agendamento(LocalDateTime dataHora) {
        return new DadosAgendamento(1L, 1L, null, dataHora);
    }

    @Test
    @DisplayName("Expectativa: Não permitir agendamento aos domingos.")
    void horarioFuncionamentoCenario1() {
        LocalDateTime domingo = DadosTeste.proximaSegundaAs10().with(DayOfWeek.SUNDAY);
        assertThatThrownBy(() -> validadorHorarioFuncionamento.validar(agendamento(domingo)))
                .isInstanceOf(ValidacaoException.class);
    }

    @Test
    @DisplayName("Expectativa: Não permitir instrução que termine após as 21:00 (início às 21:00).")
    void horarioFuncionamentoCenario2() {
        LocalDateTime sabadoAs21 = DadosTeste.proximaSegundaAs10().with(DayOfWeek.SATURDAY).withHour(21);
        assertThatThrownBy(() -> validadorHorarioFuncionamento.validar(agendamento(sabadoAs21)))
                .isInstanceOf(ValidacaoException.class);
    }

    @Test
    @DisplayName("Expectativa: Permitir instrução no sábado às 20:00 (última do dia).")
    void horarioFuncionamentoCenario3() {
        LocalDateTime sabadoAs20 = DadosTeste.proximaSegundaAs10().with(DayOfWeek.SATURDAY).withHour(20);
        assertThatCode(() -> validadorHorarioFuncionamento.validar(agendamento(sabadoAs20)))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("Expectativa: Não permitir agendamento com menos de 30 minutos de antecedência.")
    void antecedenciaAgendamentoCenario1() {
        assertThatThrownBy(() -> validadorHorarioAntecedencia.validar(agendamento(LocalDateTime.now().plusMinutes(10))))
                .isInstanceOf(ValidacaoException.class);
    }

    @Test
    @DisplayName("Expectativa: Não permitir mais de duas instruções no mesmo dia para o mesmo aluno.")
    void limiteDiarioCenario1() {
        when(repository.countByAlunoIdAndDataHoraBetween(eq(1L), any(), any())).thenReturn(2L);
        assertThatThrownBy(() -> validadorLimiteDiarioAluno.validar(agendamento(DadosTeste.proximaSegundaAs10())))
                .isInstanceOf(ValidacaoException.class);
    }

    @Test
    @DisplayName("Expectativa: Permitir a segunda instrução do dia para o mesmo aluno.")
    void limiteDiarioCenario2() {
        when(repository.countByAlunoIdAndDataHoraBetween(eq(1L), any(), any())).thenReturn(1L);
        assertThatCode(() -> validadorLimiteDiarioAluno.validar(agendamento(DadosTeste.proximaSegundaAs10())))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("Expectativa: Não permitir cancelamento com menos de 24 horas de antecedência.")
    void antecedenciaCancelamentoCenario1() {
        Instrucao instrucao = DadosTeste.instrucao(1L, LocalDateTime.now().plusHours(23));
        assertThatThrownBy(() -> validadorAntecedenciaCancelamento.validar(
                instrucao, new DadosCancelamento(MotivoCancelamento.OUTROS)))
                .isInstanceOf(ValidacaoException.class);
    }

    @Test
    @DisplayName("Expectativa: Permitir cancelamento com mais de 24 horas de antecedência.")
    void antecedenciaCancelamentoCenario2() {
        Instrucao instrucao = DadosTeste.instrucao(1L, LocalDateTime.now().plusHours(25));
        assertThatCode(() -> validadorAntecedenciaCancelamento.validar(
                instrucao, new DadosCancelamento(MotivoCancelamento.OUTROS)))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("Expectativa: Não permitir cancelar uma instrução já cancelada.")
    void instrucaoJaCanceladaCenario1() {
        Instrucao instrucao = DadosTeste.instrucao(1L, DadosTeste.proximaSegundaAs10());
        instrucao.cancelar(MotivoCancelamento.ALUNO_DESISTIU);
        assertThatThrownBy(() -> validadorInstrucaoJaCancelada.validar(
                instrucao, new DadosCancelamento(MotivoCancelamento.OUTROS)))
                .isInstanceOf(ValidacaoException.class);
    }
}
