package br.com.fiap3esr.autoescola3esr.usuario;

import br.com.fiap3esr.autoescola3esr.adapter.in.controller.request.usuario.DadosAlteracaoSenha;
import br.com.fiap3esr.autoescola3esr.adapter.in.controller.request.usuario.DadosCadastroUsuario;
import br.com.fiap3esr.autoescola3esr.application.core.domain.Usuario;
import br.com.fiap3esr.autoescola3esr.application.port.out.UsuarioRepository;
import br.com.fiap3esr.autoescola3esr.application.service.UsuarioService;
import br.com.fiap3esr.autoescola3esr.exception.type.ValidacaoException;
import br.com.fiap3esr.autoescola3esr.shared.vo.enumeration.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {
    @Mock
    UsuarioRepository repository;

    PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    UsuarioService service;

    @BeforeEach
    void setUp() {
        service = new UsuarioService(repository, passwordEncoder);
    }

    @Test
    @DisplayName("Expectativa: A senha do usuário deve ser armazenada criptografada.")
    void cadastrarUsuarioCenario1() {
        when(repository.existsByLogin("novo.usuario")).thenReturn(false);
        when(repository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        service.cadastrarUsuario(new DadosCadastroUsuario("novo.usuario", "senha123", Role.USER));

        ArgumentCaptor<Usuario> captor = ArgumentCaptor.forClass(Usuario.class);
        verify(repository).save(captor.capture());
        assertThat(captor.getValue().getSenha()).isNotEqualTo("senha123");
        assertThat(passwordEncoder.matches("senha123", captor.getValue().getSenha())).isTrue();
    }

    @Test
    @DisplayName("Expectativa: Não permitir cadastro de login duplicado.")
    void cadastrarUsuarioCenario2() {
        when(repository.existsByLogin("admin")).thenReturn(true);

        assertThatThrownBy(() -> service.cadastrarUsuario(new DadosCadastroUsuario("admin", "senha123", Role.USER)))
                .isInstanceOf(ValidacaoException.class);
        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("Expectativa: Alterar a própria senha quando a senha atual estiver correta.")
    void alterarPropriaSenhaCenario1() {
        Usuario usuario = new Usuario(2L, "joao", passwordEncoder.encode("senha123"), Role.USER);
        when(repository.findByLogin("joao")).thenReturn(Optional.of(usuario));

        service.alterarPropriaSenha("joao", new DadosAlteracaoSenha("senha123", "novaSenha456"));

        verify(repository).save(usuario);
        assertThat(passwordEncoder.matches("novaSenha456", usuario.getSenha())).isTrue();
    }

    @Test
    @DisplayName("Expectativa: Não alterar a senha quando a senha atual estiver incorreta.")
    void alterarPropriaSenhaCenario2() {
        Usuario usuario = new Usuario(2L, "joao", passwordEncoder.encode("senha123"), Role.USER);
        when(repository.findByLogin("joao")).thenReturn(Optional.of(usuario));

        assertThatThrownBy(() -> service.alterarPropriaSenha("joao", new DadosAlteracaoSenha("errada", "novaSenha456")))
                .isInstanceOf(ValidacaoException.class);
        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("Expectativa: Administrador não pode excluir o próprio usuário.")
    void excluirUsuarioCenario1() {
        when(repository.findById(1L)).thenReturn(Optional.of(new Usuario(1L, "admin", "hash", Role.ADMIN)));

        assertThatThrownBy(() -> service.excluirUsuario(1L, "admin"))
                .isInstanceOf(ValidacaoException.class);
        verify(repository, never()).deleteById(any());
    }
}
