package main.java.com.teatroabc.admin.aplicacao.servicos;

import com.teatroabc.admin.aplicacao.dto.ReembolsoDTO;
import com.teatroabc.admin.dominio.entidades.BilheteVendido;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Interface que define as operações de serviço para bilhetes.
 * Porta de entrada na arquitetura hexagonal.
 */
public interface IBilheteServico {
    
    /**
     * Inicializa o cache de bilhetes.
     * Carrega todos os bilhetes do repositório para memória.
     */
    void inicializarCache();
    
    /**
     * Busca todos os bilhetes disponíveis.
     * @return Lista com todos os bilhetes
     */
    List<BilheteVendido> buscarTodos();
    
    /**
     * Busca um bilhete pelo seu ID.
     * @param idIngresso ID do bilhete
     * @return Optional contendo o bilhete se encontrado, vazio caso contrário
     */
    Optional<BilheteVendido> buscarPorId(String idIngresso);
    
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
     * Processa o reembolso de um bilhete.
     * @param idIngresso ID do bilhete
     * @param motivo Motivo do reembolso
     * @return DTO com resultado do processamento do reembolso
     */
    ReembolsoDTO processarReembolso(String idIngresso, String motivo);
    
    /**
     * Sincroniza o cache com o banco de dados.
     * @return true se a sincronização foi bem-sucedida, false caso contrário
     */
    boolean sincronizarComBancoDados();
    
    /**
     * Conta quantos bilhetes foram reembolsados.
     * @return Quantidade de bilhetes reembolsados
     */
    int contarBilhetesReembolsados();
    
    /**
     * Calcula o valor total de vendas (bilhetes não reembolsados).
     * @return Valor total de vendas
     */
    BigDecimal calcularTotalVendas();
    
    /**
     * Calcula o valor total de reembolsos.
     * @return Valor total de reembolsos
     */
    BigDecimal calcularTotalReembolsos();
}