

import java.math.BigDecimal;
import java.util.Map;

/**
 * DTO (Data Transfer Object) para encapsular estatísticas de vendas.
 * Transfere informações consolidadas sobre vendas e reembolsos entre as camadas da aplicação.
 */
public class EstatisticaDTO {
    private final BigDecimal totalVendas;
    private final BigDecimal totalReembolsos;
    private final int quantidadeBilhetes;
    private final int quantidadeReembolsados;
    private final Map<String, BigDecimal> vendasPorPeca;
    private final Map<String, BigDecimal> vendasPorTurno;
    
    /**
     * Construtor para criar um objeto EstatisticaDTO.
     * @param totalVendas Valor total de vendas
     * @param totalReembolsos Valor total de reembolsos
     * @param quantidadeBilhetes Quantidade total de bilhetes
     * @param quantidadeReembolsados Quantidade de bilhetes reembolsados
     * @param vendasPorPeca Mapa com valores de venda por peça
     * @param vendasPorTurno Mapa com valores de venda por turno
     */
    public EstatisticaDTO(BigDecimal totalVendas, BigDecimal totalReembolsos, 
                          int quantidadeBilhetes, int quantidadeReembolsados,
                          Map<String, BigDecimal> vendasPorPeca, Map<String, BigDecimal> vendasPorTurno) {
        this.totalVendas = totalVendas != null ? totalVendas : BigDecimal.ZERO;
        this.totalReembolsos = totalReembolsos != null ? totalReembolsos : BigDecimal.ZERO;
        this.quantidadeBilhetes = quantidadeBilhetes;
        this.quantidadeReembolsados = quantidadeReembolsados;
        this.vendasPorPeca = vendasPorPeca;
        this.vendasPorTurno = vendasPorTurno;
    }
    
    /**
     * Retorna o valor total de vendas.
     * @return Valor total de vendas
     */
    public BigDecimal getTotalVendas() {
        return totalVendas;
    }
    
    /**
     * Retorna o valor total de reembolsos.
     * @return Valor total de reembolsos
     */
    public BigDecimal getTotalReembolsos() {
        return totalReembolsos;
    }
    
    /**
     * Retorna a quantidade total de bilhetes.
     * @return Quantidade total de bilhetes
     */
    public int getQuantidadeBilhetes() {
        return quantidadeBilhetes;
    }
    
    /**
     * Retorna a quantidade de bilhetes reembolsados.
     * @return Quantidade de bilhetes reembolsados
     */
    public int getQuantidadeReembolsados() {
        return quantidadeReembolsados;
    }
    
    /**
     * Retorna o mapa com valores de venda por peça.
     * @return Mapa com valores de venda por peça
     */
    public Map<String, BigDecimal> getVendasPorPeca() {
        return vendasPorPeca;
    }
    
    /**
     * Retorna o mapa com valores de venda por turno.
     * @return Mapa com valores de venda por turno
     */
    public Map<String, BigDecimal> getVendasPorTurno() {
        return vendasPorTurno;
    }
    
    /**
     * Calcula a taxa de reembolso.
     * @return Taxa de reembolso (quantidade reembolsada / quantidade total)
     */
    public double getTaxaReembolso() {
        if (quantidadeBilhetes == 0) {
            return 0.0;
        }
        return (double) quantidadeReembolsados / quantidadeBilhetes;
    }
    
    /**
     * Calcula o percentual de reembolso em valor.
     * @return Percentual de reembolso em valor (valor reembolsado / valor total)
     */
    public double getPercentualReembolsoValor() {
        if (totalVendas.compareTo(BigDecimal.ZERO) == 0) {
            return 0.0;
        }
        return totalReembolsos.divide(totalVendas, 4, BigDecimal.ROUND_HALF_UP).doubleValue();
    }
    
    /**
     * Calcula o valor médio por bilhete.
     * @return Valor médio por bilhete
     */
    public BigDecimal getValorMedioPorBilhete() {
        if (quantidadeBilhetes == 0) {
            return BigDecimal.ZERO;
        }
        return totalVendas.divide(BigDecimal.valueOf(quantidadeBilhetes), 2, BigDecimal.ROUND_HALF_UP);
    }
}