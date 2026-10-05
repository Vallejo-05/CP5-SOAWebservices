package br.com.fiap3esr.autoescola3esr.adapter.out.repository.persistence;

import br.com.fiap3esr.autoescola3esr.adapter.out.repository.entity.InstrucaoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface InstrucaoJpaRepository extends JpaRepository<InstrucaoEntity, Long> {
    boolean existsByInstrutorIdAndDataHoraAndMotivoCancelamentoIsNull(Long idInstrutor, LocalDateTime dataHora);

    long countByAlunoIdAndDataHoraBetweenAndMotivoCancelamentoIsNull(
            Long idAluno,
            LocalDateTime inicio,
            LocalDateTime fim);
}
