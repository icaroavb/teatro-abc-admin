package com.teatroabc.admin.dominio.interfaces;



/**
 * Interface para autenticação no domínio.
 * Define o contrato para validação de credenciais e acesso.
 */
public interface IAutenticacao {
    
    /**
     * Verifica se um usuário tem permissão para uma determinada ação.
     * @param idUsuario ID do usuário
     * @param acao Nome da ação a verificar
     * @return true se tiver permissão, false caso contrário
     */
    boolean temPermissao(String idUsuario, String acao);
    
    /**
     * Valida as credenciais de um usuário.
     * @param nomeUsuario Nome de usuário
     * @param senha Senha do usuário
     * @return true se as credenciais forem válidas, false caso contrário
     */
    boolean validarCredenciais(String nomeUsuario, String senha);
    
    /**
     * Valida um token de autenticação.
     * @param token Token de autenticação
     * @return true se o token for válido, false caso contrário
     */
    boolean validarToken(String token);
    
    /**
     * Obtém o perfil de acesso de um usuário.
     * @param idUsuario ID do usuário
     * @return Código do perfil de acesso
     */
    int obterPerfilAcesso(String idUsuario);
}