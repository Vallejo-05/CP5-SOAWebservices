package br.com.fiap3esr.autoescola3esr.adapter.out.repository;

import br.com.fiap3esr.autoescola3esr.adapter.out.repository.entity.AlunoEntity;
import br.com.fiap3esr.autoescola3esr.adapter.out.repository.persistence.AlunoJpaRepository;
import br.com.fiap3esr.autoescola3esr.application.core.domain.Aluno;
import br.com.fiap3esr.autoescola3esr.application.port.out.AlunoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class AlunoRepositoryImpl implements AlunoRepository {
    private final AlunoJpaRepository jpaRepository;

    @Override
    public Aluno save(Aluno aluno) {
        return jpaRepository.save(AlunoEntity.fromDomain(aluno)).toDomain();
    }

    @Override
    public Optional<Aluno> findById(Long id) {
        return jpaRepository.findById(id).map(AlunoEntity::toDomain);
    }

    @Override
    public boolean existsById(Long id) {
        return jpaRepository.existsById(id);
    }

    @Override
    public Page<Aluno> findAllByAtivoTrue(Pageable paginacao) {
        return jpaRepository.findAllByAtivoTrue(paginacao).map(AlunoEntity::toDomain);
    }

    @Override
    public boolean existsByIdAndAtivoFalse(Long id) {
        return jpaRepository.existsByIdAndAtivoFalse(id);
    }
}
