public interface IAutenticacaoServico {
    /**
     * Autentica um usuário com as credenciais fornecidas.
     * @param loginDTO DTO com as credenciais do usuário
     * @return Objeto Usuario se autenticado com sucesso, null caso contrário
     */
    Usuario autenticar(LoginDTO loginDTO);
    
    /**
     * Valida se um token de sessão é válido.
     * @param token Token de sessão
     * @return true se o token for válido, false caso contrário
     */
    boolean validarSessao(String token);
    
    /**
     * Encerra a sessão de um usuário.
     * @param token Token de sessão a ser invalidado
     */
    void encerrarSessao(String token);
}