package br.com.fiap3esr.autoescola3esr.application.port.out;

import br.com.fiap3esr.autoescola3esr.application.core.domain.Instrucao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Optional;

public interface InstrucaoRepository {
    Instrucao save(Instrucao instrucao);

    Optional<Instrucao> findById(Long id);

    Page<Instrucao> findAll(Pageable paginacao);

    // Considera apenas instruções não canceladas
    boolean existsByInstrutorIdAndDataHora(Long idInstrutor, LocalDateTime dataHora);

    // Considera apenas instruções não canceladas
    long countByAlunoIdAndDataHoraBetween(Long idAluno, LocalDateTime inicio, LocalDateTime fim);
}
