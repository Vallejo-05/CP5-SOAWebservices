package br.com.fiap3esr.autoescola3esr.instrucao;

import br.com.fiap3esr.autoescola3esr.adapter.out.repository.InstrucaoRepositoryImpl;
import br.com.fiap3esr.autoescola3esr.adapter.out.repository.entity.AlunoEntity;
import br.com.fiap3esr.autoescola3esr.adapter.out.repository.entity.InstrutorEntity;
import br.com.fiap3esr.autoescola3esr.application.core.domain.Instrucao;
import br.com.fiap3esr.autoescola3esr.application.port.out.InstrucaoRepository;
import br.com.fiap3esr.autoescola3esr.shared.vo.enumeration.MotivoCancelamento;
import br.com.fiap3esr.autoescola3esr.support.DadosTeste;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
@Import(InstrucaoRepositoryImpl.class)
class InstrucaoRepositoryTest {
    @Autowired
    InstrucaoRepository repository;

    @Autowired
    TestEntityManager testEntity;

    @Test
    @DisplayName("Expectativa: Salvar instrução e recuperá-la com aluno e instrutor.")
    void saveCenario1() {
        Instrucao salva = agendar(DadosTeste.proximaSegundaAs10());

        assertThat(salva.getId()).isNotNull();
        assertThat(repository.findById(salva.getId()))
                .hasValueSatisfying(i -> assertThat(i.getAluno().getNome()).isEqualTo("Aluno Teste"));
    }

    @Test
    @DisplayName("Expectativa: Instruções canceladas não ocupam o horário do instrutor nem contam no limite diário do aluno.")
    void cancelamentoCenario1() {
        LocalDateTime dataHora = DadosTeste.proximaSegundaAs10();
        Instrucao instrucao = agendar(dataHora);
        Long idAluno = instrucao.getAluno().getId();
        Long idInstrutor = instrucao.getInstrutor().getId();

        assertThat(repository.existsByInstrutorIdAndDataHora(idInstrutor, dataHora)).isTrue();
        assertThat(repository.countByAlunoIdAndDataHoraBetween(
                idAluno, dataHora.toLocalDate().atStartOfDay(), dataHora.plusHours(12))).isEqualTo(1);

        instrucao.cancelar(MotivoCancelamento.ALUNO_DESISTIU);
        repository.save(instrucao);

        assertThat(repository.existsByInstrutorIdAndDataHora(idInstrutor, dataHora)).isFalse();
        assertThat(repository.countByAlunoIdAndDataHoraBetween(
                idAluno, dataHora.toLocalDate().atStartOfDay(), dataHora.plusHours(12))).isZero();
    }

    private Instrucao agendar(LocalDateTime dataHora) {
        AlunoEntity aluno = testEntity.persist(AlunoEntity.fromDomain(DadosTeste.aluno(null, true)));
        InstrutorEntity instrutor = testEntity.persist(InstrutorEntity.fromDomain(DadosTeste.instrutor(null, true)));
        return repository.save(new Instrucao(null, aluno.toDomain(), instrutor.toDomain(), dataHora));
    }
}
