

import com.teatroabc.admin.aplicacao.dto.EstatisticaDTO;
import com.teatroabc.admin.aplicacao.interfaces.IBilheteServico;
import com.teatroabc.admin.aplicacao.interfaces.IEstatisticaServico;
import com.teatroabc.admin.dominio.entidades.BilheteVendido;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Serviço de aplicação responsável por gerar estatísticas de vendas.
 * Implementa a lógica de negócio para análise de dados de bilhetes.
 */
public class EstatisticaServico implements IEstatisticaServico {
    
    private final IBilheteServico bilheteServico;
    
    /**
     * Construtor do serviço de estatísticas.
     * @param bilheteServico Serviço de bilhetes injetado
     */
    public EstatisticaServico(IBilheteServico bilheteServico) {
        if (bilheteServico == null) {
            throw new IllegalArgumentException("Serviço de bilhetes não pode ser nulo");
        }
        this.bilheteServico = bilheteServico;
    }
    
    @Override
    public EstatisticaDTO gerarEstatisticasGerais() {
        List<BilheteVendido> todosBilhetes = bilheteServico.buscarTodos();
        
        BigDecimal totalVendas = calcularTotalVendas();
        BigDecimal totalReembolsos = calcularTotalReembolsos();
        int quantidadeBilhetes = todosBilhetes.size();
        int quantidadeReembolsados = bilheteServico.contarBilhetesReembolsados();
        
        Map<String, BigDecimal> vendasPorPeca = calcularVendasPorPeca();
        Map<String, BigDecimal> vendasPorTurno = calcularVendasPorTurno();
        
        return new EstatisticaDTO(
            totalVendas,
            totalReembolsos,
            quantidadeBilhetes,
            quantidadeReembolsados,
            vendasPorPeca,
            vendasPorTurno
        );
    }
    
    @Override
    public Map<String, BigDecimal> calcularVendasPorPeca() {
        Map<String, BigDecimal> resultado = new HashMap<>();
        List<BilheteVendido> bilhetes = bilheteServico.buscarTodos();
        
        for (BilheteVendido bilhete : bilhetes) {
            if (!bilhete.isReembolsado()) {
                String nomePeca = bilhete.getNomePeca();
                BigDecimal valorAtual = resultado.getOrDefault(nomePeca, BigDecimal.ZERO);
                resultado.put(nomePeca, valorAtual.add(bilhete.getPreco()));
            }
        }
        
        return resultado;
    }
    
    @Override
    public Map<String, BigDecimal> calcularVendasPorTurno() {
        Map<String, BigDecimal> resultado = new HashMap<>();
        List<BilheteVendido> bilhetes = bilheteServico.buscarTodos();
        
        for (BilheteVendido bilhete : bilhetes) {
            if (!bilhete.isReembolsado()) {
                String turno = bilhete.getTurno();
                BigDecimal valorAtual = resultado.getOrDefault(turno, BigDecimal.ZERO);
                resultado.put(turno, valorAtual.add(bilhete.getPreco()));
            }
        }
        
        return resultado;
    }
    
    @Override
    public Map<Integer, BigDecimal> calcularVendasPorMes(int ano) {
        Map<Integer, BigDecimal> resultado = new HashMap<>();
        
        // Inicializa todos os meses com zero
        for (int mes = 1; mes <= 12; mes++) {
            resultado.put(mes, BigDecimal.ZERO);
        }
        
        // Como não temos a data de compra nos bilhetes, vamos simular
        // Em uma aplicação real, você precisaria adicionar esse campo
        List<BilheteVendido> bilhetes = bilheteServico.buscarTodos();
        
        // Por enquanto, distribuir uniformemente pelos meses
        if (!bilhetes.isEmpty()) {
            BigDecimal totalPorMes = calcularTotalVendas().divide(
                BigDecimal.valueOf(12), 2, BigDecimal.ROUND_HALF_UP
            );
            
            for (int mes = 1; mes <= 12; mes++) {
                resultado.put(mes, totalPorMes);
            }
        }
        
        return resultado;
    }
    
    @Override
    public BigDecimal calcularTotalVendas() {
        return bilheteServico.calcularTotalVendas();
    }
    
    @Override
    public BigDecimal calcularTotalReembolsos() {
        return bilheteServico.calcularTotalReembolsos();
    }
    
    @Override
    public Object[] calcularPecaMaisLucrativa() {
        Map<String, BigDecimal> vendasPorPeca = calcularVendasPorPeca();
        
        String pecaMaisLucrativa = null;
        BigDecimal maiorValor = BigDecimal.ZERO;
        
        for (Map.Entry<String, BigDecimal> entry : vendasPorPeca.entrySet()) {
            if (entry.getValue().compareTo(maiorValor) > 0) {
                maiorValor = entry.getValue();
                pecaMaisLucrativa = entry.getKey();
            }
        }
        
        if (pecaMaisLucrativa != null) {
            return new Object[]{pecaMaisLucrativa, maiorValor};
        }
        
        return new Object[]{"Nenhuma", BigDecimal.ZERO};
    }
    
    @Override
    public Object[] calcularTurnoMaisLucrativo() {
        Map<String, BigDecimal> vendasPorTurno = calcularVendasPorTurno();
        
        String turnoMaisLucrativo = null;
        BigDecimal maiorValor = BigDecimal.ZERO;
        
        for (Map.Entry<String, BigDecimal> entry : vendasPorTurno.entrySet()) {
            if (entry.getValue().compareTo(maiorValor) > 0) {
                maiorValor = entry.getValue();
                turnoMaisLucrativo = entry.getKey();
            }
        }
        
        if (turnoMaisLucrativo != null) {
            return new Object[]{turnoMaisLucrativo, maiorValor};
        }
        
        return new Object[]{"Nenhum", BigDecimal.ZERO};
    }
}