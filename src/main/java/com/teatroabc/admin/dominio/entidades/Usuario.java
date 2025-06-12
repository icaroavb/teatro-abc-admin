package com.teatroabc.admin.dominio.entidades;




import com.teatroabc.admin.dominio.enums.TipoUsuario;
import java.util.Objects;

/**
 * Entidade de domínio que representa um usuário administrativo do sistema.
 * Possui informações de autenticação e perfil de acesso.
 */
public class Usuario {
    private final String id;
    private final String nome;
    private final String nomeUsuario;
    private final boolean admin;
    private String token;
    
    /**
     * Construtor para criar uma instância de Usuário.
     * @param id ID único do usuário
     * @param nome Nome completo do usuário
     * @param nomeUsuario Nome de login do usuário
     * @param admin Flag que indica se o usuário é administrador
     */
    public Usuario(String id, String nome, String nomeUsuario, boolean admin) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("ID do usuário não pode ser nulo ou vazio");
        }
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome do usuário não pode ser nulo ou vazio");
        }
        if (nomeUsuario == null || nomeUsuario.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome de usuário não pode ser nulo ou vazio");
        }
        
        this.id = id;
        this.nome = nome;
        this.nomeUsuario = nomeUsuario;
        this.admin = admin;
    }
    
    /**
     * Verifica se o usuário tem permissão para uma ação específica.
     * @param acao Nome da ação
     * @return true se o usuário tem permissão, false caso contrário
     */
    public boolean temPermissao(String acao) {
        // Se for admin, tem todas as permissões
        if (admin) {
            return true;
        }
        
        // Implementar lógica de permissões específicas para usuários não-admin
        // Por exemplo, um funcionário pode ter permissão para visualizar bilhetes
        // mas não para fazer reembolsos
        switch (acao) {
            case "visualizarBilhetes":
            case "visualizarEstatisticas":
                return true;
            case "reembolsarBilhete":
            case "gerenciarUsuarios":
                return false;
            default:
                return false;
        }
    }
    
    /**
     * Obtém o tipo de usuário (ADMIN ou FUNCIONARIO).
     * @return Enum TipoUsuario
     */
    public TipoUsuario getTipo() {
        return admin ? TipoUsuario.ADMIN : TipoUsuario.FUNCIONARIO;
    }
    
    // Getters
    public String getId() { return id; }
    public String getNome() { return nome; }
    public String getNomeUsuario() { return nomeUsuario; }
    public boolean isAdmin() { return admin; }
    
    /**
     * Obtém o token de sessão do usuário.
     * @return Token de sessão
     */
    public String getToken() {
        return token;
    }
    
    /**
     * Define o token de sessão do usuário.
     * @param token Token de sessão
     */
    public void setToken(String token) {
        this.token = token;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Usuario usuario = (Usuario) o;
        return Objects.equals(id, usuario.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    
    @Override
    public String toString() {
        return "Usuario{" +
                "id='" + id + '\'' +
                ", nome='" + nome + '\'' +
                ", nomeUsuario='" + nomeUsuario + '\'' +
                ", tipo=" + getTipo() +
                '}';
    }
}