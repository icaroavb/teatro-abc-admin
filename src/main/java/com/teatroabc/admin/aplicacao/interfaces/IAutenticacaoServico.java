package com.teatroabc.admin.aplicacao.interfaces;

import com.teatroabc.admin.aplicacao.interfaces.IAutenticacaoServico;
import com.teatroabc.admin.aplicacao.dto.LoginDTO;
import com.teatroabc.admin.dominio.entidades.Usuario;

/**
 * Interface que define as operações de autenticação.
 * Porta de entrada na arquitetura hexagonal.
 */
public interface IAutenticacaoServico {
    
    /**
     * Autentica um usuário com base nas credenciais fornecidas.
     * @param loginDTO DTO contendo usuário e senha
     * @return Usuário autenticado ou null se falhar
     */
    Usuario autenticar(LoginDTO loginDTO);
    
    /**
     * Valida se uma sessão está ativa.
     * @param token Token da sessão
     * @return true se a sessão for válida, false caso contrário
     */
    boolean validarSessao(String token);
    
    /**
     * Encerra a sessão de um usuário.
     * @param token Token da sessão a ser encerrada
     */
    void encerrarSessao(String token);
}