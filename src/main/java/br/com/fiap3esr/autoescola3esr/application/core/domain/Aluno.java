package br.com.fiap3esr.autoescola3esr.application.core.domain;

import br.com.fiap3esr.autoescola3esr.shared.vo.endereco.Endereco;
import br.com.fiap3esr.autoescola3esr.shared.vo.endereco.dto.DadosEndereco;

public class Aluno {
    private Long id;
    private String nome;
    private String email;
    private String telefone;
    private String cpf;
    private Endereco endereco;
    private boolean ativo = true;

    public Aluno() {
    }

    public Aluno(
            Long id,
            String nome,
            String email,
            String telefone,
            String cpf,
            Endereco endereco,
            boolean ativo) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.telefone = telefone;
        this.cpf = cpf;
        this.endereco = endereco;
        this.ativo = ativo;
    }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getEmail() {
        return email;
    }

    public String getTelefone() {
        return telefone;
    }

    public String getCpf() {
        return cpf;
    }

    public Endereco getEndereco() {
        return endereco;
    }

    public boolean isAtivo() {
        return ativo;
    }

    // Regra de negócio: apenas nome, telefone e endereço podem ser atualizados
    public void atualizarInformacoes(String nome, String telefone, DadosEndereco endereco) {
        if (nome != null && !nome.isBlank()) {
            this.nome = nome;
        }
        if (telefone != null && !telefone.isBlank()) {
            this.telefone = telefone;
        }
        if (endereco != null) {
            this.endereco.atualizarInformacoes(endereco);
        }
    }

    // Regra de negócio: a exclusão não apaga os dados, apenas inativa o aluno
    public void excluir() {
        this.ativo = false;
    }
}
