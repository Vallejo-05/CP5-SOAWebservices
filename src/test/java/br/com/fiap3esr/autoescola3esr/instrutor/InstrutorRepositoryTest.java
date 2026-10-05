package br.com.fiap3esr.autoescola3esr.instrutor;

import br.com.fiap3esr.autoescola3esr.adapter.out.repository.InstrutorRepositoryImpl;
import br.com.fiap3esr.autoescola3esr.adapter.out.repository.entity.AlunoEntity;
import br.com.fiap3esr.autoescola3esr.adapter.out.repository.entity.InstrucaoEntity;
import br.com.fiap3esr.autoescola3esr.adapter.out.repository.entity.InstrutorEntity;
import br.com.fiap3esr.autoescola3esr.application.core.domain.Instrutor;
import br.com.fiap3esr.autoescola3esr.application.port.out.InstrutorRepository;
import br.com.fiap3esr.autoescola3esr.shared.vo.enumeration.Especialidade;
import br.com.fiap3esr.autoescola3esr.shared.vo.enumeration.MotivoCancelamento;
import br.com.fiap3esr.autoescola3esr.support.DadosTeste;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
@Import(InstrutorRepositoryImpl.class)
class InstrutorRepositoryTest {
    @Autowired
    InstrutorRepository repository;

    @Autowired
    TestEntityManager testEntity;

    @Test
    @DisplayName("Expectativa: Retornar null quando o instrutor estiver ocupado.")
    void escolherInstrutorAleatorioDisponivelCenario1() {
        LocalDateTime proximaSegundaAs10 = DadosTeste.proximaSegundaAs10();
        AlunoEntity aluno = cadastrarAluno();
        InstrutorEntity instrutor = cadastrarInstrutor("instrutor1@email.com.br", "01234567890", true);
        agendarInstrucao(aluno, instrutor, proximaSegundaAs10, null);

        Instrutor instrutorDisponivel = repository.escolherInstrutorAleatorioDisponivel(
                Especialidade.MOTOS,
                proximaSegundaAs10
        );

        assertThat(instrutorDisponivel).isNull();
    }

    @Test
    @DisplayName("Expectativa: Retornar instrutor quando o instrutor estiver disponível.")
    void escolherInstrutorAleatorioDisponivelCenario2() {
        InstrutorEntity instrutor = cadastrarInstrutor("instrutor1@email.com.br", "01234567890", true);

        Instrutor instrutorDisponivel = repository.escolherInstrutorAleatorioDisponivel(
                Especialidade.MOTOS,
                DadosTeste.proximaSegundaAs10()
        );

        assertThat(instrutorDisponivel).isNotNull();
        assertThat(instrutorDisponivel.getId()).isEqualTo(instrutor.getId());
    }

    @Test
    @DisplayName("Expectativa: Instrutor volta a ficar disponível quando a instrução é cancelada.")
    void escolherInstrutorAleatorioDisponivelCenario3() {
        LocalDateTime proximaSegundaAs10 = DadosTeste.proximaSegundaAs10();
        AlunoEntity aluno = cadastrarAluno();
        InstrutorEntity instrutor = cadastrarInstrutor("instrutor1@email.com.br", "01234567890", true);
        agendarInstrucao(aluno, instrutor, proximaSegundaAs10, MotivoCancelamento.ALUNO_DESISTIU);

        Instrutor instrutorDisponivel = repository.escolherInstrutorAleatorioDisponivel(null, proximaSegundaAs10);

        assertThat(instrutorDisponivel).isNotNull();
        assertThat(instrutorDisponivel.getId()).isEqualTo(instrutor.getId());
    }

    @Test
    @DisplayName("Expectativa: Instrutor inativo nunca deve ser escolhido.")
    void escolherInstrutorAleatorioDisponivelCenario4() {
        cadastrarInstrutor("instrutor1@email.com.br", "01234567890", false);

        Instrutor instrutorDisponivel = repository.escolherInstrutorAleatorioDisponivel(
                Especialidade.MOTOS,
                DadosTeste.proximaSegundaAs10()
        );

        assertThat(instrutorDisponivel).isNull();
    }

    @Test
    @DisplayName("Expectativa: Listagem deve trazer apenas instrutores ativos, ordenados por nome.")
    void findAllByAtivoTrueCenario1() {
        cadastrarInstrutor("b@email.com.br", "11111111111", true, "Bruno");
        cadastrarInstrutor("a@email.com.br", "22222222222", true, "Ana");
        cadastrarInstrutor("c@email.com.br", "33333333333", false, "Carlos");

        Page<Instrutor> pagina = repository.findAllByAtivoTrue(PageRequest.of(0, 10, Sort.by("nome")));

        assertThat(pagina.getContent())
                .extracting(Instrutor::getNome)
                .containsExactly("Ana", "Bruno");
    }

    private AlunoEntity cadastrarAluno() {
        AlunoEntity aluno = AlunoEntity.fromDomain(DadosTeste.aluno(null, true));
        return testEntity.persist(aluno);
    }

    private InstrutorEntity cadastrarInstrutor(String email, String cnh, boolean ativo) {
        return cadastrarInstrutor(email, cnh, ativo, "Instrutor Teste");
    }

    private InstrutorEntity cadastrarInstrutor(String email, String cnh, boolean ativo, String nome) {
        InstrutorEntity instrutor = new InstrutorEntity(
                null, nome, email, "(11) 91234-5678", cnh, Especialidade.MOTOS, DadosTeste.endereco(), ativo);
        return testEntity.persist(instrutor);
    }

    private void agendarInstrucao(
            AlunoEntity aluno,
            InstrutorEntity instrutor,
            LocalDateTime dataHora,
            MotivoCancelamento motivoCancelamento) {
        testEntity.persist(new InstrucaoEntity(null, aluno, instrutor, dataHora, motivoCancelamento));
    }
}
