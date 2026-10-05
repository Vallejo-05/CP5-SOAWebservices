package br.com.fiap3esr.autoescola3esr.adapter.out.repository;

import br.com.fiap3esr.autoescola3esr.adapter.out.repository.entity.InstrucaoEntity;
import br.com.fiap3esr.autoescola3esr.adapter.out.repository.persistence.AlunoJpaRepository;
import br.com.fiap3esr.autoescola3esr.adapter.out.repository.persistence.InstrucaoJpaRepository;
import br.com.fiap3esr.autoescola3esr.adapter.out.repository.persistence.InstrutorJpaRepository;
import br.com.fiap3esr.autoescola3esr.application.core.domain.Instrucao;
import br.com.fiap3esr.autoescola3esr.application.port.out.InstrucaoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class InstrucaoRepositoryImpl implements InstrucaoRepository {
    private final InstrucaoJpaRepository jpaRepository;
    private final AlunoJpaRepository alunoJpaRepository;
    private final InstrutorJpaRepository instrutorJpaRepository;

    @Override
    public Instrucao save(Instrucao instrucao) {
        InstrucaoEntity entity = new InstrucaoEntity(
                instrucao.getId(),
                alunoJpaRepository.getReferenceById(instrucao.getAluno().getId()),
                instrutorJpaRepository.getReferenceById(instrucao.getInstrutor().getId()),
                instrucao.getDataHora(),
                instrucao.getMotivoCancelamento()
        );
        return jpaRepository.save(entity).toDomain();
    }

    @Override
    public Optional<Instrucao> findById(Long id) {
        return jpaRepository.findById(id).map(InstrucaoEntity::toDomain);
    }

    @Override
    public Page<Instrucao> findAll(Pageable paginacao) {
        return jpaRepository.findAll(paginacao).map(InstrucaoEntity::toDomain);
    }

    @Override
    public boolean existsByInstrutorIdAndDataHora(Long idInstrutor, LocalDateTime dataHora) {
        return jpaRepository.existsByInstrutorIdAndDataHoraAndMotivoCancelamentoIsNull(idInstrutor, dataHora);
    }

    @Override
    public long countByAlunoIdAndDataHoraBetween(Long idAluno, LocalDateTime inicio, LocalDateTime fim) {
        return jpaRepository.countByAlunoIdAndDataHoraBetweenAndMotivoCancelamentoIsNull(idAluno, inicio, fim);
    }
}
