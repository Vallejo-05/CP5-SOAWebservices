package br.com.fiap3esr.autoescola3esr.adapter.out.repository.persistence;

import br.com.fiap3esr.autoescola3esr.adapter.out.repository.entity.UsuarioEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioJpaRepository extends JpaRepository<UsuarioEntity, Long> {
    Optional<UsuarioEntity> findByLogin(String login);

    boolean existsByLogin(String login);
}
