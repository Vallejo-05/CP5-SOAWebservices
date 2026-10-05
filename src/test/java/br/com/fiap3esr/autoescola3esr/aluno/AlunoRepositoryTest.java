package br.com.fiap3esr.autoescola3esr.aluno;

import br.com.fiap3esr.autoescola3esr.adapter.out.repository.AlunoRepositoryImpl;
import br.com.fiap3esr.autoescola3esr.application.core.domain.Aluno;
import br.com.fiap3esr.autoescola3esr.application.port.out.AlunoRepository;
import br.com.fiap3esr.autoescola3esr.support.DadosTeste;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
@Import(AlunoRepositoryImpl.class)
class AlunoRepositoryTest {
    @Autowired
    AlunoRepository repository;

    @Test
    @DisplayName("Expectativa: Salvar aluno e recuperá-lo pelo ID.")
    void saveCenario1() {
        Aluno salvo = repository.save(DadosTeste.aluno(null, true));

        assertThat(salvo.getId()).isNotNull();
        assertThat(repository.findById(salvo.getId()))
                .hasValueSatisfying(aluno -> assertThat(aluno.getCpf()).isEqualTo("12345678901"));
    }

    @Test
    @DisplayName("Expectativa: Identificar aluno inativo.")
    void existsByIdAndAtivoFalseCenario1() {
        Aluno aluno = repository.save(DadosTeste.aluno(null, true));
        aluno.excluir();
        repository.save(aluno);

        assertThat(repository.existsByIdAndAtivoFalse(aluno.getId())).isTrue();
    }

    @Test
    @DisplayName("Expectativa: Listagem não deve trazer alunos inativos.")
    void findAllByAtivoTrueCenario1() {
        repository.save(DadosTeste.aluno(null, true));
        repository.save(new Aluno(null, "Inativo", "inativo@email.com.br", "(11) 90000-0000",
                "98765432100", DadosTeste.endereco(), false));

        Page<Aluno> pagina = repository.findAllByAtivoTrue(PageRequest.of(0, 10, Sort.by("nome")));

        assertThat(pagina.getContent()).extracting(Aluno::getNome).containsExactly("Aluno Teste");
    }
}
