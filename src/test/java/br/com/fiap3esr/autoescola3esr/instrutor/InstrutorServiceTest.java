package br.com.fiap3esr.autoescola3esr.instrutor;

import br.com.fiap3esr.autoescola3esr.adapter.in.controller.request.instrutor.DadosAtualizacaoInstrutor;
import br.com.fiap3esr.autoescola3esr.application.core.domain.Instrutor;
import br.com.fiap3esr.autoescola3esr.application.port.out.InstrutorRepository;
import br.com.fiap3esr.autoescola3esr.application.service.InstrutorService;
import br.com.fiap3esr.autoescola3esr.exception.type.InstrutorNotFoundException;
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
class InstrutorServiceTest {
    @Mock
    InstrutorRepository repository;

    @InjectMocks
    InstrutorService service;

    @Test
    @DisplayName("Expectativa: Exclusão deve apenas inativar o instrutor (exclusão lógica).")
    void excluirInstrutorCenario1() {
        when(repository.findById(1L)).thenReturn(Optional.of(DadosTeste.instrutor(1L, true)));

        service.excluirInstrutor(1L);

        ArgumentCaptor<Instrutor> captor = ArgumentCaptor.forClass(Instrutor.class);
        verify(repository).save(captor.capture());
        assertThat(captor.getValue().isAtivo()).isFalse();
    }

    @Test
    @DisplayName("Expectativa: Atualização deve alterar nome e telefone, mantendo e-mail, CNH e especialidade.")
    void atualizarInstrutorCenario1() {
        Instrutor original = DadosTeste.instrutor(1L, true);
        when(repository.findById(1L)).thenReturn(Optional.of(original));
        when(repository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        var dto = service.atualizarInstrutor(new DadosAtualizacaoInstrutor(
                1L, "Novo Nome", "(11) 90000-0000", null, null, null, null));

        assertThat(dto.nome()).isEqualTo("Novo Nome");
        assertThat(dto.telefone()).isEqualTo("(11) 90000-0000");
        assertThat(dto.email()).isEqualTo(original.getEmail());
        assertThat(dto.cnh()).isEqualTo(original.getCnh());
        assertThat(dto.especialidade()).isEqualTo(original.getEspecialidade());
    }

    @Test
    @DisplayName("Expectativa: Lançar exceção ao detalhar instrutor inexistente.")
    void detalharInstrutorCenario1() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.detalharInstrutor(99L))
                .isInstanceOf(InstrutorNotFoundException.class);
    }
}
