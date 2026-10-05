package br.com.fiap3esr.autoescola3esr.usuario;

import br.com.fiap3esr.autoescola3esr.adapter.out.repository.UsuarioRepositoryImpl;
import br.com.fiap3esr.autoescola3esr.application.core.domain.Usuario;
import br.com.fiap3esr.autoescola3esr.application.port.out.UsuarioRepository;
import br.com.fiap3esr.autoescola3esr.shared.vo.enumeration.Role;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
@Import(UsuarioRepositoryImpl.class)
class UsuarioRepositoryTest {
    @Autowired
    UsuarioRepository repository;

    @Test
    @DisplayName("Expectativa: Existir o usuário administrador inicial criado pela migration, com senha criptografada.")
    void findByLoginCenario1() {
        Usuario admin = repository.findByLogin("admin").orElseThrow();

        assertThat(admin.getPerfil()).isEqualTo(Role.ADMIN);
        assertThat(admin.getSenha()).isNotEqualTo("admin");
        assertThat(new BCryptPasswordEncoder().matches("admin", admin.getSenha())).isTrue();
    }

    @Test
    @DisplayName("Expectativa: Salvar, atualizar perfil e excluir usuário.")
    void crudUsuarioCenario1() {
        Usuario salvo = repository.save(new Usuario(null, "maria", "hash", Role.USER));
        assertThat(repository.existsByLogin("maria")).isTrue();

        salvo.atualizarPerfil(Role.ADMIN);
        repository.save(salvo);
        assertThat(repository.findById(salvo.getId()))
                .hasValueSatisfying(u -> assertThat(u.getPerfil()).isEqualTo(Role.ADMIN));

        repository.deleteById(salvo.getId());
        assertThat(repository.existsByLogin("maria")).isFalse();
    }
}
