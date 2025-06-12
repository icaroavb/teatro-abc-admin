package com.teatroabc.admin.infraestrutura.persistencia.implementacao;

import com.teatroabc.admin.dominio.entidades.BilheteVendido;
import com.teatroabc.admin.dominio.interfaces.IRepositorioBilhete;
import com.teatroabc.admin.infraestrutura.persistencia.conexao.ConexaoDB;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Implementação do repositório de bilhetes.
 * Gerencia operações de acesso a dados de bilhetes no banco de dados.
 */
public class BilheteRepositorio implements IRepositorioBilhete {
    
    private final ConexaoDB conexaoDB;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    /**
     * Construtor padrão que inicializa a conexão com o banco de dados.
     */
    public BilheteRepositorio() {
        this.conexaoDB = new ConexaoDB();
    }
    
    /**
     * Construtor para injeção de conexão (usado em testes).
     * @param conexaoDB Conexão com o banco de dados
     */
    public BilheteRepositorio(ConexaoDB conexaoDB) {
        this.conexaoDB = conexaoDB;
    }
    
    @Override
    public List<BilheteVendido> buscarTodos() {
        List<BilheteVendido> bilhetes = new ArrayList<>();
        String sql = "SELECT id_ingresso, cpf, nome_peca, turno, numero_poltrona, nome_sessao, capacidade, preco, " +
                     "reembolsado, data_reembolso FROM vw_bilhetes";
        
        try (Connection conn = conexaoDB.obterConexao();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                bilhetes.add(mapearBilhete(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Erro ao buscar todos os bilhetes: " + e.getMessage());
            e.printStackTrace();
        }
        
        return bilhetes;
    }
    
    @Override
    public Optional<BilheteVendido> buscarPorId(String id) {
        String sql = "SELECT id_ingresso, cpf, nome_peca, turno, numero_poltrona, nome_sessao, capacidade, preco, " +
                     "reembolsado, data_reembolso FROM vw_bilhetes WHERE id_ingresso = ?";
        
        try (Connection conn = conexaoDB.obterConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapearBilhete(rs));
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Erro ao buscar bilhete por ID: " + e.getMessage());
            e.printStackTrace();
        }
        
        return Optional.empty();
    }
    
    @Override
    public List<BilheteVendido> buscarPorCpf(String cpf) {
        List<BilheteVendido> bilhetes = new ArrayList<>();
        String sql = "SELECT id_ingresso, cpf, nome_peca, turno, numero_poltrona, nome_sessao, capacidade, preco, " +
                     "reembolsado, data_reembolso FROM vw_bilhetes WHERE cpf = ?";
        
        try (Connection conn = conexaoDB.obterConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, cpf);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    bilhetes.add(mapearBilhete(rs));
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Erro ao buscar bilhetes por CPF: " + e.getMessage());
            e.printStackTrace();
        }
        
        return bilhetes;
    }
    
    @Override
    public List<BilheteVendido> buscarPorPeca(String nomePeca) {
        List<BilheteVendido> bilhetes = new ArrayList<>();
        String sql = "SELECT id_ingresso, cpf, nome_peca, turno, numero_poltrona, nome_sessao, capacidade, preco, " +
                     "reembolsado, data_reembolso FROM vw_bilhetes WHERE nome_peca LIKE ?";
        
        try (Connection conn = conexaoDB.obterConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, "%" + nomePeca + "%");
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    bilhetes.add(mapearBilhete(rs));
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Erro ao buscar bilhetes por peça: " + e.getMessage());
            e.printStackTrace();
        }
        
        return bilhetes;
    }
    
    @Override
    public boolean atualizarStatusReembolso(String idBilhete, boolean reembolsado, 
                                           LocalDateTime dataReembolso, String motivo) {
        String sql = "UPDATE bilhetes SET reembolsado = ?, data_reembolso = ?, motivo_reembolso = ? WHERE id_ingresso = ?";
        
        try (Connection conn = conexaoDB.obterConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setBoolean(1, reembolsado);
            
            if (dataReembolso != null) {
                stmt.setString(2, dataReembolso.format(FORMATTER));
            } else {
                stmt.setNull(2, Types.TIMESTAMP);
            }
            
            stmt.setString(3, motivo);
            stmt.setString(4, idBilhete);
            
            int linhasAfetadas = stmt.executeUpdate();
            return linhasAfetadas > 0;
            
        } catch (SQLException e) {
            System.err.println("Erro ao atualizar status de reembolso: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    @Override
    public List<BilheteVendido> buscarPorStatusReembolso(boolean reembolsado) {
        List<BilheteVendido> bilhetes = new ArrayList<>();
        String sql = "SELECT id_ingresso, cpf, nome_peca, turno, numero_poltrona, nome_sessao, capacidade, preco, " +
                     "reembolsado, data_reembolso FROM vw_bilhetes WHERE reembolsado = ?";
        
        try (Connection conn = conexaoDB.obterConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setBoolean(1, reembolsado);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    bilhetes.add(mapearBilhete(rs));
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Erro ao buscar bilhetes por status de reembolso: " + e.getMessage());
            e.printStackTrace();
        }
        
        return bilhetes;
    }
    
    @Override
    public List<BilheteVendido> buscarPorPeriodo(LocalDateTime inicio, LocalDateTime fim) {
        List<BilheteVendido> bilhetes = new ArrayList<>();
        String sql = "SELECT id_ingresso, cpf, nome_peca, turno, numero_poltrona, nome_sessao, capacidade, preco, " +
                     "reembolsado, data_reembolso FROM vw_bilhetes WHERE data_compra BETWEEN ? AND ?";
        
        try (Connection conn = conexaoDB.obterConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, inicio.format(FORMATTER));
            stmt.setString(2, fim.format(FORMATTER));
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    bilhetes.add(mapearBilhete(rs));
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Erro ao buscar bilhetes por período: " + e.getMessage());
            e.printStackTrace();
        }
        
        return bilhetes;
    }
    
    @Override
    public boolean salvar(BilheteVendido bilhete) {
        // No contexto deste sistema administrativo, não é necessário implementar
        // a criação de novos bilhetes, apenas atualizações como reembolsos
        return atualizarStatusReembolso(
            bilhete.getIdIngresso(),
            bilhete.isReembolsado(),
            bilhete.getDataReembolso(),
            "Atualizado pelo sistema administrativo"
        );
    }
    
    /**
     * Mapeia um ResultSet para um objeto BilheteVendido.
     * @param rs ResultSet com dados do bilhete
     * @return Objeto BilheteVendido preenchido
     * @throws SQLException Em caso de erro no acesso aos dados
     */
    private BilheteVendido mapearBilhete(ResultSet rs) throws SQLException {
        String idIngresso = rs.getString("id_ingresso");
        String cpf = rs.getString("cpf");
        String nomePeca = rs.getString("nome_peca");
        String turno = rs.getString("turno");
        String numeroPoltrona = rs.getString("numero_poltrona");
        String nomeSessao = rs.getString("nome_sessao");
        int capacidade = rs.getInt("capacidade");
        BigDecimal preco = rs.getBigDecimal("preco");
        
        BilheteVendido bilhete = new BilheteVendido(
            idIngresso, cpf, nomePeca, turno, numeroPoltrona, nomeSessao, capacidade, preco
        );
        
        // Configura o status de reembolso se for o caso
        boolean reembolsado = rs.getBoolean("reembolsado");
        if (reembolsado) {
            bilhete.realizarReembolso();
            
            // Verifica se há data de reembolso no resultado
            if (rs.getObject("data_reembolso") != null) {
                // Código para definir a data do reembolso no objeto
                // Em um ambiente real, provavelmente usaríamos um campo na entidade
                // Ou uma subclasse específica para bilhetes reembolsados
            }
        }
        
        return bilhete;
    }
}