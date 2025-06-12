package com.teatroabc.admin.aplicacao.servicos;



import com.teatroabc.admin.aplicacao.dto.LoginDTO;
import com.teatroabc.admin.aplicacao.interfaces.IAutenticacaoServico;
import com.teatroabc.admin.dominio.entidades.Usuario;
import com.teatroabc.admin.infraestrutura.persistencia.implementacao.UsuarioRepositorio;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * Serviço de aplicação responsável pela autenticação de usuários.
 * Implementa a lógica de negócio para login e validação de sessões.
 */
public class AutenticacaoServico implements IAutenticacaoServico {
    
    private final UsuarioRepositorio usuarioRepositorio;
    private final Map<String, SessaoUsuario> sessoes;
    
    /**
     * Classe interna para representar uma sessão de usuário.
     */
    private static class SessaoUsuario {
        final String idUsuario;
        final LocalDateTime expiracao;
        
        SessaoUsuario(String idUsuario, LocalDateTime expiracao) {
            this.idUsuario = idUsuario;
            this.expiracao = expiracao;
        }
    }
    
    /**
     * Construtor do serviço de autenticação.
     * @param usuarioRepositorio Repositório de usuários
     */
    public AutenticacaoServico(UsuarioRepositorio usuarioRepositorio) {
        this.usuarioRepositorio = usuarioRepositorio;
        this.sessoes = new HashMap<>();
        
        // Para desenvolvimento, criar um usuário admin se o repositório estiver vazio
        // Em produção, isso seria removido e os usuários seriam criados previamente
        criarUsuarioAdminPadrao();
    }
    
    /**
     * Cria um usuário admin padrão para facilitar o desenvolvimento.
     */
    private void criarUsuarioAdminPadrao() {
        // Verifica se já existe um usuário admin
        if (usuarioRepositorio.buscarPorNomeUsuario("admin").isEmpty()) {
            // Cria um usuário admin padrão
            Usuario admin = new Usuario(
                UUID.randomUUID().toString(),
                "Administrador",
                "admin",
                true
            );
            
            // Salva o usuário com senha padrão (em produção, usaríamos uma senha forte)
            usuarioRepositorio.salvar(admin, "admin");
            
            System.out.println("Usuário admin criado com sucesso!");
        }
    }
    
    @Override
    public Usuario autenticar(LoginDTO loginDTO) {
        if (loginDTO == null || loginDTO.getUsuario() == null || loginDTO.getSenha() == null) {
            return null;
        }
        
        // Validar credenciais no repositório
        Optional<Usuario> usuarioOpt = usuarioRepositorio.validarCredenciais(
            loginDTO.getUsuario(), 
            loginDTO.getSenha()
        );
        
        if (usuarioOpt.isEmpty()) {
            // Para desenvolvimento, permitir login com credenciais padrão
            if ("admin".equals(loginDTO.getUsuario()) && "admin".equals(loginDTO.getSenha())) {
                return new Usuario("1", "Administrador", "admin", true);
            }
            
            return null;
        }
        
        Usuario usuario = usuarioOpt.get();
        
        // Gerar token de sessão
        String token = UUID.randomUUID().toString();
        usuario.setToken(token);
        
        // Registrar sessão
        sessoes.put(token, new SessaoUsuario(usuario.getId(), LocalDateTime.now().plusHours(8)));
        
        return usuario;
    }
    
    @Override
    public boolean validarSessao(String token) {
        if (token == null || token.isEmpty()) {
            return false;
        }
        
        SessaoUsuario sessao = sessoes.get(token);
        if (sessao == null) {
            return false;
        }
        
        // Verificar se a sessão expirou
        if (sessao.expiracao.isBefore(LocalDateTime.now())) {
            sessoes.remove(token);
            return false;
        }
        
        return true;
    }
    
    @Override
    public void encerrarSessao(String token) {
        if (token != null) {
            sessoes.remove(token);
        }
    }
}