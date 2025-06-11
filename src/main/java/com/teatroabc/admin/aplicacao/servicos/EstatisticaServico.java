

import com.teatroabc.admin.aplicacao.dto.EstatisticaDTO;
import com.teatroabc.admin.aplicacao.interfaces.IBilheteServico;
import com.teatroabc.admin.aplicacao.interfaces.IEstatisticaServico;
import com.teatroabc.admin.dominio.entidades.BilheteVendido;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Implementação do serviço de estatísticas.
 * Coordena a geração de estatísticas a partir dos dados de bilhetes.
 */
public class EstatisticaServico implements IEstatisticaServico {
    
    private final IBilheteServico bilheteServico;
    
    /**
     * Construtor para injeção de dependência do serviço de bilhetes.
     * @param bilheteServico Serviço de bilhetes
     */
    public EstatisticaServico(IBilheteServico bilheteServico) {
        if (bilheteServico == null) {
            throw new IllegalArgumentException("Serviço de bilhetes não pode ser nulo");
        }
        this.bilheteServico = bilheteServico;
    }
    
    @Override
    public EstatisticaDTO gerarEstatisticasGerais() {
        BigDecimal totalVendas = calcularTotalVendas();
        BigDecimal totalReembolsos = calcularTotalReembolsos();
        
        List<BilheteVendido> todosBilhetes = bilheteServico.buscarTodos();
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
        List<BilheteVendido> bilhetes = bilheteServico.buscarTodos();
        Map<String, BigDecimal> vendasPorPeca = new HashMap<>();
        
        // Agrupa bilhetes por peça e soma os valores
        for (BilheteVendido bilhete : bilhetes) {
            if (!bilhete.isReembolsado()) {
                String nomePeca = bilhete.getNomePeca();
                BigDecimal valorAtual = vendasPorPeca.getOrDefault(nomePeca, BigDecimal.ZERO);
                vendasPorPeca.put(nomePeca, valorAtual.add(bilhete.getPreco()));
            }
        }
        
        return vendasPorPeca;
    }
    
    @Override
    public Map<String, BigDecimal> calcularVendasPorTurno() {
        List<BilheteVendido> bilhetes = bilheteServico.buscarTodos();
        Map<String, BigDecimal> vendasPorTurno = new HashMap<>();
        
        // Agrupa bilhetes por turno e soma os valores
        for (BilheteVendido bilhete : bilhetes) {
            if (!bilhete.isReembolsado()) {
                String turno = bilhete.getTurno();
                BigDecimal valorAtual = vendasPorTurno.getOrDefault(turno, BigDecimal.ZERO);
                vendasPorTurno.put(turno, valorAtual.add(bilhete.getPreco()));
            }
        }
        
        return vendasPorTurno;
    }
    
    @Override
    public Map<Integer, BigDecimal> calcularVendasPorMes(int ano) {
        List<BilheteVendido> bilhetes = bilheteServico.buscarTodos();
        Map<Integer, BigDecimal> vendasPorMes = new HashMap<>();
        
        // Inicializa todos os meses com zero para garantir que todos apareçam nos relatórios
        for (int i = 1; i <= 12; i++) {
            vendasPorMes.put(i, BigDecimal.ZERO);
        }
        
        // Usa o ano atual se o parâmetro for zero
        int anoAlvo = ano == 0 ? LocalDateTime.now().getYear() : ano;
        
        // Agrupa bilhetes por mês e soma os valores
        for (BilheteVendido bilhete : bilhetes) {
            if (!bilhete.isReembolsado() && bilhete.getDataCompra() != null) {
                LocalDateTime dataCompra = bilhete.getDataCompra();
                
                if (dataCompra.getYear() == anoAlvo) {
                    int mes = dataCompra.getMonthValue();
                    BigDecimal valorAtual = vendasPorMes.get(mes);
                    vendasPorMes.put(mes, valorAtual.add(bilhete.getPreco()));
                }
            }
        }
        
        return vendasPorMes;
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
        
        if (vendasPorPeca.isEmpty()) {
            return new Object[]{"Nenhuma", BigDecimal.ZERO};
        }
        
        // Encontra a peça com maior valor de vendas
        Map.Entry<String, BigDecimal> maiorVenda = vendasPorPeca.entrySet()
            .stream()
            .max(Map.Entry.comparingByValue())
            .get();
        
        return new Object[]{maiorVenda.getKey(), maiorVenda.getValue()};
    }
    
    @Override
    public Object[] calcularTurnoMaisLucrativo() {
        Map<String, BigDecimal> vendasPorTurno = calcularVendasPorTurno();
        
        if (vendasPorTurno.isEmpty()) {
            return new Object[]{"Nenhum", BigDecimal.ZERO};
        }
        
        // Encontra o turno com maior valor de vendas
        Map.Entry<String, BigDecimal> maiorVenda = vendasPorTurno.entrySet()
            .stream()
            .max(Map.Entry.comparingByValue())
            .get();
        
        return new Object[]{maiorVenda.getKey(), maiorVenda.getValue()};
    }
}