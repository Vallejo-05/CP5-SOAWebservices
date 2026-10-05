package br.com.fiap3esr.autoescola3esr.aluno;

import br.com.fiap3esr.autoescola3esr.adapter.in.controller.request.aluno.DadosAtualizacaoAluno;
import br.com.fiap3esr.autoescola3esr.adapter.in.controller.request.aluno.DadosCadastroAluno;
import br.com.fiap3esr.autoescola3esr.application.core.domain.Aluno;
import br.com.fiap3esr.autoescola3esr.application.port.out.AlunoRepository;
import br.com.fiap3esr.autoescola3esr.application.service.AlunoService;
import br.com.fiap3esr.autoescola3esr.exception.type.AlunoNotFoundException;
import br.com.fiap3esr.autoescola3esr.shared.vo.endereco.dto.DadosEndereco;
import br.com.fiap3esr.autoescola3esr.support.DadosTeste;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AlunoServiceTest {
    @Mock
    AlunoRepository repository;

    @InjectMocks
    AlunoService service;

    @Test
    @DisplayName("Expectativa: Aluno recém-cadastrado deve estar ativo.")
    void cadastrarAlunoCenario1() {
        when(repository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        var dto = service.cadastrarAluno(new DadosCadastroAluno(
                "Aluno Teste",
                "alunoteste@email.com.br",
                "(11) 98765-4321",
                "12345678901",
                new DadosEndereco(DadosTeste.endereco())
        ));

        assertThat(dto.ativo()).isTrue();
        assertThat(dto.cpf()).isEqualTo("12345678901");
    }

    @Test
    @DisplayName("Expectativa: Exclusão deve apenas inativar o aluno (exclusão lógica).")
    void excluirAlunoCenario1() {
        when(repository.findById(1L)).thenReturn(Optional.of(DadosTeste.aluno(1L, true)));

        service.excluirAluno(1L);

        ArgumentCaptor<Aluno> captor = ArgumentCaptor.forClass(Aluno.class);
        verify(repository).save(captor.capture());
        assertThat(captor.getValue().isAtivo()).isFalse();
    }

    @Test
    @DisplayName("Expectativa: Atualização não deve alterar e-mail nem CPF do aluno.")
    void atualizarAlunoCenario1() {
        when(repository.findById(1L)).thenReturn(Optional.of(DadosTeste.aluno(1L, true)));
        when(repository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        var dto = service.atualizarAluno(new DadosAtualizacaoAluno(1L, "Novo Nome", null, null, null, null));

        assertThat(dto.nome()).isEqualTo("Novo Nome");
        assertThat(dto.email()).isEqualTo("alunoteste@email.com.br");
        assertThat(dto.cpf()).isEqualTo("12345678901");
    }

    @Test
    @DisplayName("Expectativa: Lançar exceção ao excluir aluno inexistente.")
    void excluirAlunoCenario2() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.excluirAluno(99L))
                .isInstanceOf(AlunoNotFoundException.class);
    }
}
