

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
    
    /**
     * Construtor que inicializa as propriedades de conexão a partir do arquivo de configuração.
     */
    public ConexaoDB() {
        carregarPropriedades();
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
                this.senha = propriedades.getProperty("senha", "");
                
                // Carrega o driver JDBC
                try {
                    Class.forName("com.mysql.cj.jdbc.Driver");
                } catch (ClassNotFoundException e) {
                    System.err.println("Erro ao carregar o driver JDBC: " + e.getMessage());
                    e.printStackTrace();
                }
            } else {
                System.err.println("Arquivo de configuração não encontrado: " + ARQUIVO_PROPRIEDADES);
                
                // Valores padrão em caso de falha na leitura do arquivo
                this.url = "jdbc:mysql://localhost:3306/teatro";
                this.usuario = "root";
                this.senha = "";
            }
        } catch (IOException e) {
            System.err.println("Erro ao carregar propriedades de conexão: " + e.getMessage());
            e.printStackTrace();
            
            // Valores padrão em caso de falha na leitura do arquivo
            this.url = "jdbc:mysql://localhost:3306/teatro";
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
        try {
            return DriverManager.getConnection(url, usuario, senha);
        } catch (SQLException e) {
            System.err.println("Erro ao obter conexão com o banco de dados: " + e.getMessage());
            throw e;
        }
    }
    
    /**
     * Verifica se a conexão com o banco de dados está disponível.
     * @return true se a conexão estiver disponível, false caso contrário
     */
    public boolean testarConexao() {
        try (Connection conn = obterConexao()) {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            System.err.println("Erro ao testar conexão com o banco de dados: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Obtém a URL de conexão.
     * @return URL de conexão
     */
    public String getUrl() {
        return url;
    }
    
    /**
     * Define a URL de conexão.
     * @param url URL de conexão
     */
    public void setUrl(String url) {
        this.url = url;
    }
    
    /**
     * Obtém o nome de usuário para conexão.
     * @return Nome de usuário
     */
    public String getUsuario() {
        return usuario;
    }
    
    /**
     * Define o nome de usuário para conexão.
     * @param usuario Nome de usuário
     */
    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }
    
    /**
     * Define a senha para conexão.
     * @param senha Senha
     */
    public void setSenha(String senha) {
        this.senha = senha;
    }
}