

/**
 * DTO (Data Transfer Object) para encapsular os dados de login.
 * Usado para transferir as credenciais do usuário da interface para o serviço de autenticação.
 */
public class LoginDTO {
    private final String usuario;
    private final String senha;
    
    /**
     * Construtor para criar um objeto LoginDTO.
     * @param usuario Nome de usuário
     * @param senha Senha do usuário
     */
    public LoginDTO(String usuario, String senha) {
        this.usuario = usuario;
        this.senha = senha;
    }
    
    /**
     * Retorna o nome de usuário.
     * @return Nome de usuário
     */
    public String getUsuario() {
        return usuario;
    }
    
    /**
     * Retorna a senha.
     * @return Senha do usuário
     */
    public String getSenha() {
        return senha;
    }
}