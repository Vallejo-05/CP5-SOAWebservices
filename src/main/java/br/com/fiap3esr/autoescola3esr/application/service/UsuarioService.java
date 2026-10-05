package br.com.fiap3esr.autoescola3esr.application.service;

import br.com.fiap3esr.autoescola3esr.adapter.in.controller.request.usuario.DadosAlteracaoSenha;
import br.com.fiap3esr.autoescola3esr.adapter.in.controller.request.usuario.DadosAtualizacaoPerfil;
import br.com.fiap3esr.autoescola3esr.adapter.in.controller.request.usuario.DadosCadastroUsuario;
import br.com.fiap3esr.autoescola3esr.adapter.in.controller.response.usuario.DadosDetalhamentoUsuario;
import br.com.fiap3esr.autoescola3esr.application.core.domain.Usuario;
import br.com.fiap3esr.autoescola3esr.application.port.out.UsuarioRepository;
import br.com.fiap3esr.autoescola3esr.exception.type.UsuarioNotFoundException;
import br.com.fiap3esr.autoescola3esr.exception.type.ValidacaoException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UsuarioService {
    private final UsuarioRepository repository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public DadosDetalhamentoUsuario cadastrarUsuario(DadosCadastroUsuario dados) {
        if (repository.existsByLogin(dados.login())) {
            throw new ValidacaoException("Já existe um usuário cadastrado com o login informado!");
        }
        // A senha nunca é armazenada em texto puro: é criptografada com BCrypt
        Usuario usuario = new Usuario(
                null,
                dados.login(),
                passwordEncoder.encode(dados.senha()),
                dados.perfil()
        );
        return new DadosDetalhamentoUsuario(repository.save(usuario));
    }

    @Transactional(readOnly = true)
    public Page<DadosDetalhamentoUsuario> listarUsuarios(Pageable paginacao) {
        return repository.findAll(paginacao).map(DadosDetalhamentoUsuario::new);
    }

    @Transactional(readOnly = true)
    public DadosDetalhamentoUsuario detalharUsuario(Long id) {
        return new DadosDetalhamentoUsuario(buscarUsuario(id));
    }

    @Transactional
    public DadosDetalhamentoUsuario atualizarPerfil(Long id, DadosAtualizacaoPerfil dados) {
        Usuario usuario = buscarUsuario(id);
        usuario.atualizarPerfil(dados.perfil());
        return new DadosDetalhamentoUsuario(repository.save(usuario));
    }

    @Transactional
    public void excluirUsuario(Long id, String loginSolicitante) {
        Usuario usuario = buscarUsuario(id);
        if (usuario.getLogin().equals(loginSolicitante)) {
            throw new ValidacaoException("Um administrador não pode excluir o próprio usuário!");
        }
        repository.deleteById(id);
    }

    @Transactional
    public void alterarPropriaSenha(String login, DadosAlteracaoSenha dados) {
        Usuario usuario = repository
                .findByLogin(login)
                .orElseThrow(() -> new UsuarioNotFoundException("Usuário não encontrado!"));
        if (!passwordEncoder.matches(dados.senhaAtual(), usuario.getSenha())) {
            throw new ValidacaoException("Senha atual incorreta!");
        }
        usuario.alterarSenha(passwordEncoder.encode(dados.novaSenha()));
        repository.save(usuario);
    }

    private Usuario buscarUsuario(Long id) {
        return repository
                .findById(id)
                .orElseThrow(() -> new UsuarioNotFoundException("ID do Usuário informado não existe!"));
    }
}
