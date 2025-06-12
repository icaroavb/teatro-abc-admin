package com.teatroabc.admin.infraestrutura.persistencia.conexao;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Classe responsável por gerenciar a conexão com o banco de dados.
 * Utiliza um arquivo de propriedades para configuração.
 */
public class ConexaoDB {
    
    private String url;
    private String usuario;
    private String senha;
    private static final String ARQUIVO_PROPRIEDADES = "/database.properties";
    private static boolean driverCarregado = false;
    
    /**
     * Construtor que inicializa as propriedades de conexão a partir do arquivo de configuração.
     */
    public ConexaoDB() {
        carregarPropriedades();
        carregarDriver();
    }
    
    /**
     * Carrega o driver JDBC do MySQL.
     */
    private void carregarDriver() {
        if (!driverCarregado) {
            try {
                // Tenta carregar o driver mais recente
                Class.forName("com.mysql.cj.jdbc.Driver");
                driverCarregado = true;
                System.out.println("Driver MySQL carregado com sucesso: com.mysql.cj.jdbc.Driver");
            } catch (ClassNotFoundException e) {
                System.err.println("Driver com.mysql.cj.jdbc.Driver não encontrado. Tentando driver legado...");
                
                try {
                    // Tenta o driver legado
                    Class.forName("com.mysql.jdbc.Driver");
                    driverCarregado = true;
                    System.out.println("Driver MySQL legado carregado: com.mysql.jdbc.Driver");
                } catch (ClassNotFoundException e2) {
                    System.err.println("ERRO CRÍTICO: Nenhum driver MySQL encontrado!");
                    System.err.println("Certifique-se de que o MySQL Connector/J está no classpath.");
                    System.err.println("Adicione a dependência no Maven/Gradle ou baixe o JAR manualmente.");
                    e2.printStackTrace();
                }
            }
        }
    }
    
    /**
     * Carrega as propriedades de conexão a partir do arquivo database.properties.
     */
    private void carregarPropriedades() {
        Properties propriedades = new Properties();
        
        try (InputStream inputStream = ConexaoDB.class.getResourceAsStream(ARQUIVO_PROPRIEDADES)) {
            if (inputStream != null) {
                propriedades.load(inputStream);
                
                this.url = propriedades.getProperty("url");
                this.usuario = propriedades.getProperty("usuario", "root");
                this.senha = propriedades.getProperty("senha", "@Pitoco123");
                
                System.out.println("Propriedades carregadas do arquivo: " + ARQUIVO_PROPRIEDADES);
                System.out.println("URL: " + this.url);
                System.out.println("Usuário: " + this.usuario);
                
            } else {
                System.err.println("Arquivo de configuração não encontrado: " + ARQUIVO_PROPRIEDADES);
                System.out.println("Usando configurações padrão...");
                
                // Valores padrão em caso de falha na leitura do arquivo
                this.url = "jdbc:mysql://localhost:3306/teatro?useSSL=false&serverTimezone=UTC";
                this.usuario = "root";
                this.senha = "";
            }
        } catch (IOException e) {
            System.err.println("Erro ao carregar propriedades de conexão: " + e.getMessage());
            e.printStackTrace();
            
            // Valores padrão em caso de falha na leitura do arquivo
            this.url = "jdbc:mysql://localhost:3306/teatro?useSSL=false&serverTimezone=UTC";
            this.usuario = "root";
            this.senha = "";
        }
    }
    
    /**
     * Obtém uma conexão com o banco de dados.
     * @return Conexão aberta com o banco de dados
     * @throws SQLException Em caso de erro na conexão
     */
    public Connection obterConexao() throws SQLException {
        if (!driverCarregado) {
            throw new SQLException("Driver JDBC não foi carregado. Verifique se o MySQL Connector/J está no classpath.");
        }
        
        try {
            System.out.println("Tentando conectar com: " + url);
            Connection conn = DriverManager.getConnection(url, usuario, senha);
            System.out.println("Conexão estabelecida com sucesso!");
            return conn;
        } catch (SQLException e) {
            System.err.println("Erro ao obter conexão com o banco de dados: " + e.getMessage());
            System.err.println("URL: " + url);
            System.err.println("Usuário: " + usuario);
            
            // Verifica se é erro de driver
            if (e.getMessage().contains("No suitable driver")) {
                System.err.println("SOLUÇÃO: Adicione o MySQL Connector/J ao classpath do projeto");
                System.err.println("Maven: <dependency><groupId>mysql</groupId><artifactId>mysql-connector-java</artifactId><version>8.0.33</version></dependency>");
            }
            
            throw e;
        }
    }
    
    /**
     * Verifica se a conexão com o banco de dados está disponível.
     * @return true se a conexão estiver disponível, false caso contrário
     */
    public boolean testarConexao() {
        System.out.println("Testando conexão com o banco de dados...");
        
        if (!driverCarregado) {
            System.err.println("Driver não foi carregado. Teste de conexão falhou.");
            return false;
        }
        
        try (Connection conn = obterConexao()) {
            boolean sucesso = conn != null && !conn.isClosed();
            if (sucesso) {
                System.out.println("Teste de conexão: SUCESSO");
            } else {
                System.out.println("Teste de conexão: FALHA - Conexão nula ou fechada");
            }
            return sucesso;
        } catch (SQLException e) {
            System.err.println("Teste de conexão: FALHA - " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Verifica se o driver está disponível no classpath.
     * @return true se o driver estiver disponível
     */
    public static boolean isDriverDisponivel() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return true;
        } catch (ClassNotFoundException e) {
            try {
                Class.forName("com.mysql.jdbc.Driver");
                return true;
            } catch (ClassNotFoundException e2) {
                return false;
            }
        }
    }
    
    // Getters e Setters
    public String getUrl() {
        return url;
    }
    
    public void setUrl(String url) {
        this.url = url;
    }
    
    public String getUsuario() {
        return usuario;
    }
    
    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }
    
    public void setSenha(String senha) {
        this.senha = senha;
    }
    
    public boolean isDriverCarregado() {
        return driverCarregado;
    }
}