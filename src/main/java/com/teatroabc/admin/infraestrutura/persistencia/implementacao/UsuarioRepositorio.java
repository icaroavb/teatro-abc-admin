
package com.teatroabc.admin.infraestrutura.persistencia.implementacao;

import com.teatroabc.admin.dominio.entidades.Usuario;
import com.teatroabc.admin.infraestrutura.persistencia.conexao.ConexaoDB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Implementação do repositório de usuários.
 * Gerencia operações de acesso a dados de usuários no banco de dados.
 */
public class UsuarioRepositorio {
    
    private final ConexaoDB conexaoDB;
    
    /**
     * Construtor padrão que inicializa a conexão com o banco de dados.
     */
    public UsuarioRepositorio() {
        this.conexaoDB = new ConexaoDB();
    }
    
    /**
     * Construtor para injeção de conexão (usado em testes).
     * @param conexaoDB Conexão com o banco de dados
     */
    public UsuarioRepositorio(ConexaoDB conexaoDB) {
        this.conexaoDB = conexaoDB;
    }
    
    /**
     * Busca todos os usuários.
     * @return Lista de usuários
     */
    public List<Usuario> buscarTodos() {
        List<Usuario> usuarios = new ArrayList<>();
        String sql = "SELECT id, nome, nome_usuario, admin FROM usuarios";
        
        try (Connection conn = conexaoDB.obterConexao();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                usuarios.add(mapearUsuario(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Erro ao buscar todos os usuários: " + e.getMessage());
            e.printStackTrace();
        }
        
        return usuarios;
    }
    
    /**
     * Busca um usuário pelo seu ID.
     * @param id ID do usuário
     * @return Optional contendo o usuário se encontrado, vazio caso contrário
     */
    public Optional<Usuario> buscarPorId(String id) {
        String sql = "SELECT id, nome, nome_usuario, admin FROM usuarios WHERE id = ?";
        
        try (Connection conn = conexaoDB.obterConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapearUsuario(rs));
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Erro ao buscar usuário por ID: " + e.getMessage());
            e.printStackTrace();
        }
        
        return Optional.empty();
    }
    
    /**
     * Busca um usuário pelo seu nome de usuário.
     * @param nomeUsuario Nome de usuário
     * @return Optional contendo o usuário se encontrado, vazio caso contrário
     */
    public Optional<Usuario> buscarPorNomeUsuario(String nomeUsuario) {
        String sql = "SELECT id, nome, nome_usuario, admin FROM usuarios WHERE nome_usuario = ?";
        
        try (Connection conn = conexaoDB.obterConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, nomeUsuario);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapearUsuario(rs));
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Erro ao buscar usuário por nome de usuário: " + e.getMessage());
            e.printStackTrace();
        }
        
        return Optional.empty();
    }
    
    /**
     * Verifica se as credenciais de login são válidas.
     * @param nomeUsuario Nome de usuário
     * @param senha Senha
     * @return Optional contendo o usuário se as credenciais forem válidas, vazio caso contrário
     */
    public Optional<Usuario> validarCredenciais(String nomeUsuario, String senha) {
        String sql = "SELECT id, nome, nome_usuario, admin FROM usuarios WHERE nome_usuario = ? AND senha = ?";
        
        try (Connection conn = conexaoDB.obterConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, nomeUsuario);
            stmt.setString(2, senha); // Em um sistema real, usaríamos hash da senha
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapearUsuario(rs));
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Erro ao validar credenciais: " + e.getMessage());
            e.printStackTrace();
        }
        
        return Optional.empty();
    }
    
    /**
     * Salva um usuário no banco de dados.
     * @param usuario Usuário a ser salvo
     * @param senha Senha do usuário
     * @return true se a operação foi bem-sucedida, false caso contrário
     */
    public boolean salvar(Usuario usuario, String senha) {
        String sql;
        boolean isNovo = usuario.getId() == null || usuario.getId().isEmpty();
        
        if (isNovo) {
            sql = "INSERT INTO usuarios (id, nome, nome_usuario, senha, admin) VALUES (?, ?, ?, ?, ?)";
        } else {
            sql = "UPDATE usuarios SET nome = ?, nome_usuario = ?, senha = ?, admin = ? WHERE id = ?";
        }
        
        try (Connection conn = conexaoDB.obterConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            if (isNovo) {
                stmt.setString(1, usuario.getId());
                stmt.setString(2, usuario.getNome());
                stmt.setString(3, usuario.getNomeUsuario());
                stmt.setString(4, senha); // Em um sistema real, usaríamos hash da senha
                stmt.setBoolean(5, usuario.isAdmin());
            } else {
                stmt.setString(1, usuario.getNome());
                stmt.setString(2, usuario.getNomeUsuario());
                stmt.setString(3, senha); // Em um sistema real, usaríamos hash da senha
                stmt.setBoolean(4, usuario.isAdmin());
                stmt.setString(5, usuario.getId());
            }
            
            int linhasAfetadas = stmt.executeUpdate();
            return linhasAfetadas > 0;
            
        } catch (SQLException e) {
            System.err.println("Erro ao salvar usuário: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Exclui um usuário do banco de dados.
     * @param id ID do usuário a ser excluído
     * @return true se a operação foi bem-sucedida, false caso contrário
     */
    public boolean excluir(String id) {
        String sql = "DELETE FROM usuarios WHERE id = ?";
        
        try (Connection conn = conexaoDB.obterConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, id);
            
            int linhasAfetadas = stmt.executeUpdate();
            return linhasAfetadas > 0;
            
        } catch (SQLException e) {
            System.err.println("Erro ao excluir usuário: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Mapeia um ResultSet para um objeto Usuario.
     * @param rs ResultSet com dados do usuário
     * @return Objeto Usuario preenchido
     * @throws SQLException Em caso de erro no acesso aos dados
     */
    private Usuario mapearUsuario(ResultSet rs) throws SQLException {
        String id = rs.getString("id");
        String nome = rs.getString("nome");
        String nomeUsuario = rs.getString("nome_usuario");
        boolean admin = rs.getBoolean("admin");
        
        return new Usuario(id, nome, nomeUsuario, admin);
    }
    
    /**
     * Verifica se já existe um usuário com o nome de usuário especificado.
     * @param nomeUsuario Nome de usuário a verificar
     * @return true se já existe, false caso contrário
     */
    public boolean existeNomeUsuario(String nomeUsuario) {
        String sql = "SELECT COUNT(*) FROM usuarios WHERE nome_usuario = ?";
        
        try (Connection conn = conexaoDB.obterConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, nomeUsuario);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Erro ao verificar existência de nome de usuário: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
}