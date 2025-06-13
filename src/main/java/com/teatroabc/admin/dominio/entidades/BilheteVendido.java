package com.teatroabc.admin.dominio.entidades;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

//Atenção - deve haver coincidência com a classe bilhete presente na outra classe
/**
 * Entidade de domínio que representa um bilhete vendido no sistema.
 * Mapeia os dados da tabela de vendas do banco de dados.
 * Imutável após criação para garantir consistência.
 */
public class BilheteVendido {
    private final String idIngresso;
    private final String cpf;
    private final String nomePeca;
    private final String turno;
    private final String numeroPoltronas;
    private final String nomeSessao;
    private final int capacidade;
    private final BigDecimal preco;
    private boolean reembolsado;
    private LocalDateTime dataReembolso;
    
    /**
     * Construtor principal para criar instância a partir do banco de dados
     */
    public BilheteVendido(
                        String idIngresso, 
                        String cpf, 
                        String nomePeca, 
                        String turno, 
                        String numeroPoltronas, 
                        String nomeSessao,
                        int capacidade, 
                        BigDecimal preco) 
                        {
        validarParametrosObrigatorios(idIngresso, cpf, nomePeca, turno, preco);
        
        this.idIngresso = idIngresso;
        this.cpf = normalizarCpf(cpf);
        this.nomePeca = nomePeca;
        this.turno = turno;
        this.numeroPoltronas = numeroPoltronas;
        this.nomeSessao = nomeSessao;
        this.capacidade = capacidade;
        this.preco = preco;
        this.reembolsado = false;
        this.dataReembolso = null;
    }
    
    /**
     * Realiza o reembolso do bilhete
     * @throws IllegalStateException se o bilhete já foi reembolsado
     */
    public void realizarReembolso() {
        if (this.reembolsado) {
            throw new IllegalStateException("Bilhete já foi reembolsado anteriormente");
        }
        this.reembolsado = true;
        this.dataReembolso = LocalDateTime.now();
    }
    
    /**
     * Calcula o valor do reembolso (pode aplicar regras de negócio como taxas)
     */
    public BigDecimal calcularValorReembolso() {
        if (!reembolsado) {
            return BigDecimal.ZERO;
        }
        // Por enquanto, reembolso total. Pode-se adicionar lógica de taxas aqui
        return this.preco;
    }
    
    private void validarParametrosObrigatorios(String idIngresso, String cpf, 
                                                String nomePeca, String turno, 
                                                BigDecimal preco) {
        if (idIngresso == null || idIngresso.trim().isEmpty()) {
            throw new IllegalArgumentException("ID do ingresso não pode ser nulo ou vazio");
        }
        if (cpf == null || cpf.trim().isEmpty()) {
            throw new IllegalArgumentException("CPF não pode ser nulo ou vazio");
        }
        if (nomePeca == null || nomePeca.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome da peça não pode ser nulo ou vazio");
        }
        if (turno == null || turno.trim().isEmpty()) {
            throw new IllegalArgumentException("Turno não pode ser nulo ou vazio");
        }
        if (preco == null || preco.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Preço não pode ser nulo ou negativo");
        }
    }
    
    private String normalizarCpf(String cpf) {
        return cpf.replaceAll("[^0-9]", "");
    }
    
    // Getters
    public String getIdIngresso() { return idIngresso; }
    public String getCpf() { return cpf; }
    public String getNomePeca() { return nomePeca; }
    public String getTurno() { return turno; }
    public String getNumeroPoltronas() { return numeroPoltronas; }
    public String getNomeSessao() { return nomeSessao; }
    public int getCapacidade() { return capacidade; }
    public BigDecimal getPreco() { return preco; }
    public boolean isReembolsado() { return reembolsado; }
    public LocalDateTime getDataReembolso() { return dataReembolso; }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BilheteVendido that = (BilheteVendido) o;
        return Objects.equals(idIngresso, that.idIngresso);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(idIngresso);
    }
    
    @Override
    public String toString() {
        return "BilheteVendido{" +
                "idIngresso='" + idIngresso + '\'' +
                ", nomePeca='" + nomePeca + '\'' +
                ", turno='" + turno + '\'' +
                ", reembolsado=" + reembolsado +
                '}';
    }

private LocalDateTime dataCompra;


public LocalDateTime getDataCompra() {
    return dataCompra;
}
}