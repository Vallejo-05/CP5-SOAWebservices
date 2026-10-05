package br.com.fiap3esr.autoescola3esr.support;

import br.com.fiap3esr.autoescola3esr.application.core.domain.Aluno;
import br.com.fiap3esr.autoescola3esr.application.core.domain.Instrucao;
import br.com.fiap3esr.autoescola3esr.application.core.domain.Instrutor;
import br.com.fiap3esr.autoescola3esr.shared.vo.endereco.Endereco;
import br.com.fiap3esr.autoescola3esr.shared.vo.enumeration.Especialidade;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;

// Massa de dados reutilizada pelos testes automatizados
public final class DadosTeste {
    private DadosTeste() {
    }

    public static final String JSON_ENDERECO = """
            {
                "logradouro": "Rua Teste",
                "numero": "000",
                "complemento": "Apto. 00",
                "bairro": "Vila Teste",
                "cidade": "Test City",
                "uf": "TS",
                "cep": "01234-567"
            }
            """;

    public static Endereco endereco() {
        return new Endereco(
                "Rua Teste",
                "000",
                "Apto. 00",
                "Vila Teste",
                "Test City",
                "TS",
                "01234-567"
        );
    }

    public static Instrutor instrutor(Long id, boolean ativo) {
        return new Instrutor(
                id,
                "Instrutor Teste",
                "instrutorteste@email.com.br",
                "(11) 91234-5678",
                "01234567890",
                Especialidade.MOTOS,
                endereco(),
                ativo
        );
    }

    public static Aluno aluno(Long id, boolean ativo) {
        return new Aluno(
                id,
                "Aluno Teste",
                "alunoteste@email.com.br",
                "(11) 98765-4321",
                "12345678901",
                endereco(),
                ativo
        );
    }

    public static Instrucao instrucao(Long id, LocalDateTime dataHora) {
        return new Instrucao(id, aluno(1L, true), instrutor(1L, true), dataHora);
    }

    // Segunda-feira da semana que vem (sempre mais de 24 horas no futuro) às 10:00
    public static LocalDateTime proximaSegundaAs10() {
        return LocalDateTime
                .now()
                .with(TemporalAdjusters.next(DayOfWeek.MONDAY))
                .plusWeeks(1)
                .withHour(10)
                .withMinute(0)
                .withSecond(0)
                .withNano(0);
    }
}
