package com.teatroabc.admin.aplicacao.interfaces;


import com.teatroabc.admin.dominio.entidades.BilheteVendido;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Interface para o repositório de bilhetes.
 * Define o contrato para operações de acesso a dados de bilhetes.
 * Porta de saída na arquitetura hexagonal.
 */
public interface IRepositorioBilhete {
    
    /**
     * Busca todos os bilhetes disponíveis.
     * @return Lista com todos os bilhetes
     */
    List<BilheteVendido> buscarTodos();
    
    /**
     * Busca um bilhete pelo seu ID.
     * @param id ID do bilhete
     * @return Optional contendo o bilhete se encontrado, vazio caso contrário
     */
    Optional<BilheteVendido> buscarPorId(String id);
    
    /**
     * Busca bilhetes pelo CPF do cliente.
     * @param cpf CPF do cliente
     * @return Lista de bilhetes associados ao CPF
     */
    List<BilheteVendido> buscarPorCpf(String cpf);
    
    /**
     * Busca bilhetes pelo nome da peça.
     * @param nomePeca Nome da peça
     * @return Lista de bilhetes para a peça especificada
     */
    List<BilheteVendido> buscarPorPeca(String nomePeca);
    
    /**
     * Atualiza o status de reembolso de um bilhete.
     * @param idBilhete ID do bilhete
     * @param reembolsado Flag indicando se foi reembolsado
     * @param dataReembolso Data e hora do reembolso
     * @param motivo Motivo do reembolso
     * @return true se a atualização foi bem-sucedida, false caso contrário
     */
    boolean atualizarStatusReembolso(String idBilhete, boolean reembolsado, 
                                    LocalDateTime dataReembolso, String motivo);
    
    /**
     * Busca bilhetes por status de reembolso.
     * @param reembolsado Flag indicando status de reembolso a ser filtrado
     * @return Lista de bilhetes com o status de reembolso especificado
     */
    List<BilheteVendido> buscarPorStatusReembolso(boolean reembolsado);
    
    /**
     * Busca bilhetes por período de compra.
     * @param inicio Data inicial
     * @param fim Data final
     * @return Lista de bilhetes comprados no período
     */
    List<BilheteVendido> buscarPorPeriodo(LocalDateTime inicio, LocalDateTime fim);
    
    /**
     * Salva as alterações em um bilhete.
     * @param bilhete Bilhete a ser salvo
     * @return true se a operação foi bem-sucedida, false caso contrário
     */
    boolean salvar(BilheteVendido bilhete);
}