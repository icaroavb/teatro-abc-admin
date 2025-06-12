package com.teatroabc.admin.aplicacao.interfaces;


import com.teatroabc.admin.aplicacao.dto.EstatisticaDTO;
import java.util.Map;
import java.math.BigDecimal;

/**
 * Interface que define as operações de geração de estatísticas.
 * Porta de entrada na arquitetura hexagonal.
 */
public interface IEstatisticaServico {
    
    /**
     * Gera estatísticas completas sobre vendas e reembolsos.
     * @return Objeto EstatisticaDTO com as estatísticas
     */
    EstatisticaDTO gerarEstatisticasGerais();
    
    /**
     * Calcula estatísticas de vendas por peça.
     * @return Mapa com valores de venda por peça
     */
    Map<String, BigDecimal> calcularVendasPorPeca();
    
    /**
     * Calcula estatísticas de vendas por turno.
     * @return Mapa com valores de venda por turno
     */
    Map<String, BigDecimal> calcularVendasPorTurno();
    
    /**
     * Calcula estatísticas de vendas por mês.
     * @param ano Ano para o cálculo (se for 0, usa o ano atual)
     * @return Mapa com valores de venda por mês
     */
    Map<Integer, BigDecimal> calcularVendasPorMes(int ano);
    
    /**
     * Calcula o valor total de bilhetes vendidos excluindo reembolsos.
     * @return Valor total de vendas
     */
    BigDecimal calcularTotalVendas();
    
    /**
     * Calcula o valor total de bilhetes reembolsados.
     * @return Valor total de reembolsos
     */
    BigDecimal calcularTotalReembolsos();
    
    /**
     * Calcula a peça com maior receita.
     * @return Array com nome da peça e valor de vendas
     */
    Object[] calcularPecaMaisLucrativa();
    
    /**
     * Calcula o turno com maior receita.
     * @return Array com nome do turno e valor de vendas
     */
    Object[] calcularTurnoMaisLucrativo();
}