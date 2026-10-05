package br.com.fiap3esr.autoescola3esr.adapter.out.repository;

import br.com.fiap3esr.autoescola3esr.adapter.out.repository.entity.UsuarioEntity;
import br.com.fiap3esr.autoescola3esr.adapter.out.repository.persistence.UsuarioJpaRepository;
import br.com.fiap3esr.autoescola3esr.application.core.domain.Usuario;
import br.com.fiap3esr.autoescola3esr.application.port.out.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UsuarioRepositoryImpl implements UsuarioRepository {
    private final UsuarioJpaRepository jpaRepository;

    @Override
    public Usuario save(Usuario usuario) {
        return jpaRepository.save(UsuarioEntity.fromDomain(usuario)).toDomain();
    }

    @Override
    public Optional<Usuario> findById(Long id) {
        return jpaRepository.findById(id).map(UsuarioEntity::toDomain);
    }

    @Override
    public Optional<Usuario> findByLogin(String login) {
        return jpaRepository.findByLogin(login).map(UsuarioEntity::toDomain);
    }

    @Override
    public boolean existsByLogin(String login) {
        return jpaRepository.existsByLogin(login);
    }

    @Override
    public Page<Usuario> findAll(Pageable paginacao) {
        return jpaRepository.findAll(paginacao).map(UsuarioEntity::toDomain);
    }

    @Override
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }
}
