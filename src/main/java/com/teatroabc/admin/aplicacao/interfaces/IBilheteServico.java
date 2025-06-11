import com.teatroabc.admin.aplicacao.dto.ReembolsoDTO;
import com.teatroabc.admin.dominio.entidades.BilheteVendido;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Interface que define as operações de negócio para gerenciamento de bilhetes.
 * Porta de entrada na arquitetura hexagonal.
 */
public interface IBilheteServico {
    
    /**
     * Inicializa o cache carregando todos os bilhetes do banco de dados
     */
    void inicializarCache();
    
    /**
     * Busca todos os bilhetes (do cache)
     */
    List<BilheteVendido> buscarTodos();
    
    /**
     * Busca um bilhete específico por ID
     */
    Optional<BilheteVendido> buscarPorId(String idIngresso);
    
    /**
     * Busca bilhetes por CPF do cliente
     */
    List<BilheteVendido> buscarPorCpf(String cpf);
    
    /**
     * Busca bilhetes por nome da peça
     */
    List<BilheteVendido> buscarPorPeca(String nomePeca);
    
    /**
     * Processa o reembolso de um bilhete
     * @param idIngresso ID do bilhete a ser reembolsado
     * @param motivo Motivo do reembolso
     * @return DTO com resultado da operação
     */
    ReembolsoDTO processarReembolso(String idIngresso, String motivo);
    
    /**
     * Sincroniza o cache com o banco de dados
     * @return true se sincronização foi bem sucedida
     */
    boolean sincronizarComBancoDados();
    
    /**
     * Conta total de bilhetes reembolsados
     */
    int contarBilhetesReembolsados();
    
    /**
     * Calcula o total de vendas (excluindo reembolsos)
     */
    BigDecimal calcularTotalVendas();
    
    /**
     * Calcula o total de valores reembolsados
     */
    BigDecimal calcularTotalReembolsos();
}