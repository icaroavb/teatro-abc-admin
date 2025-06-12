package com.teatroabc.admin.aplicacao.dto;


import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO (Data Transfer Object) para encapsular o resultado de uma operação de reembolso.
 * Transfere informações sobre o reembolso entre as camadas da aplicação.
 */
public class ReembolsoDTO {
    private final boolean sucesso;
    private final String mensagem;
    private final LocalDateTime dataReembolso;
    private final BigDecimal valorReembolsado;
    
    /**
     * Construtor para criar um objeto ReembolsoDTO.
     * @param sucesso Indica se o reembolso foi bem-sucedido
     * @param mensagem Mensagem de resultado (sucesso ou falha)
     * @param dataReembolso Data/hora do reembolso (null em caso de falha)
     * @param valorReembolsado Valor reembolsado (zero em caso de falha)
     */
    public ReembolsoDTO(boolean sucesso, String mensagem, LocalDateTime dataReembolso, BigDecimal valorReembolsado) {
        this.sucesso = sucesso;
        this.mensagem = mensagem;
        this.dataReembolso = dataReembolso;
        this.valorReembolsado = valorReembolsado != null ? valorReembolsado : BigDecimal.ZERO;
    }
    
    /**
     * Verifica se o reembolso foi bem-sucedido.
     * @return true se o reembolso foi bem-sucedido, false caso contrário
     */
    public boolean isSucesso() {
        return sucesso;
    }
    
    /**
     * Retorna a mensagem de resultado.
     * @return Mensagem de resultado (sucesso ou falha)
     */
    public String getMensagem() {
        return mensagem;
    }
    
    /**
     * Retorna a data/hora do reembolso.
     * @return Data/hora do reembolso, ou null se o reembolso falhou
     */
    public LocalDateTime getDataReembolso() {
        return dataReembolso;
    }
    
    /**
     * Retorna o valor reembolsado.
     * @return Valor reembolsado, ou zero se o reembolso falhou
     */
    public BigDecimal getValorReembolsado() {
        return valorReembolsado;
    }
}