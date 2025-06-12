package com.teatroabc.admin.aplicacao.servicos;

import com.teatroabc.admin.aplicacao.interfaces.IBilheteServico; // Corrigido para o pacote interfaces
import com.teatroabc.admin.aplicacao.dto.ReembolsoDTO;
import com.teatroabc.admin.dominio.entidades.BilheteVendido;
import com.teatroabc.admin.dominio.interfaces.IRepositorioBilhete;
import com.teatroabc.admin.infraestrutura.persistencia.cache.CacheBilhetes;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
/**
 * Serviço de aplicação responsável pela lógica de negócio de bilhetes.
 * Coordena operações entre cache e repositório, garantindo consistência.
 */
public class BilheteServico implements IBilheteServico {
    
    private final IRepositorioBilhete repositorio;
    private final CacheBilhetes cache;
    
    public BilheteServico(IRepositorioBilhete repositorio) {
        if (repositorio == null) {
            throw new IllegalArgumentException("Repositório não pode ser nulo");
        }
        this.repositorio = repositorio;
        this.cache = CacheBilhetes.getInstance();
    }
    
    @Override
    public void inicializarCache() {
        try {
            List<BilheteVendido> todosBilhetes = repositorio.buscarTodos();
            cache.carregarBilhetes(todosBilhetes);
            System.out.println("Cache inicializado com sucesso: " + todosBilhetes.size() + " bilhetes");
        } catch (Exception e) {
            throw new RuntimeException("Erro ao inicializar cache: " + e.getMessage(), e);
        }
    }
    
    @Override
    public List<BilheteVendido> buscarTodos() {
        if (!cache.isSincronizado()) {
            inicializarCache();
        }
        return cache.buscarTodos();
    }
    
    @Override
    public Optional<BilheteVendido> buscarPorId(String idIngresso) {
        if (idIngresso == null || idIngresso.trim().isEmpty()) {
            return Optional.empty();
        }
        
        // Busca primeiro no cache
        Optional<BilheteVendido> bilheteCache = cache.buscarPorId(idIngresso);
        if (bilheteCache.isPresent()) {
            return bilheteCache;
        }
        
        // Se não encontrar no cache, busca no BD e adiciona ao cache
        Optional<BilheteVendido> bilheteBD = repositorio.buscarPorId(idIngresso);
        bilheteBD.ifPresent(cache::adicionar);
        
        return bilheteBD;
    }
    
    @Override
    public List<BilheteVendido> buscarPorCpf(String cpf) {
        if (cpf == null || cpf.trim().isEmpty()) {
            return List.of();
        }
        
        String cpfNormalizado = cpf.replaceAll("[^0-9]", "");
        return cache.buscarPorCpf(cpfNormalizado);
    }
    
    @Override
    public List<BilheteVendido> buscarPorPeca(String nomePeca) {
        if (nomePeca == null || nomePeca.trim().isEmpty()) {
            return List.of();
        }
        return cache.buscarPorPeca(nomePeca);
    }
    
    @Override
    public ReembolsoDTO processarReembolso(String idIngresso, String motivo) {
        // Validações
        if (idIngresso == null || idIngresso.trim().isEmpty()) {
            return new ReembolsoDTO(false, "ID do ingresso inválido", null, BigDecimal.ZERO);
        }
        
        Optional<BilheteVendido> bilheteOpt = buscarPorId(idIngresso);
        if (bilheteOpt.isEmpty()) {
            return new ReembolsoDTO(false, "Bilhete não encontrado", null, BigDecimal.ZERO);
        }
        
        BilheteVendido bilhete = bilheteOpt.get();
        
        // Verifica se já foi reembolsado
        if (bilhete.isReembolsado()) {
            return new ReembolsoDTO(false, "Bilhete já foi reembolsado anteriormente", 
                                   bilhete.getDataReembolso(), BigDecimal.ZERO);
        }
        
        try {
            // Realiza o reembolso
            bilhete.realizarReembolso();
            BigDecimal valorReembolso = bilhete.calcularValorReembolso();
            
            // Atualiza no banco de dados
            boolean atualizadoNoBD = repositorio.atualizarStatusReembolso(
                idIngresso, true, LocalDateTime.now(), motivo
            );
            
            if (!atualizadoNoBD) {
                // Reverter o reembolso no objeto se falhar no BD
                return new ReembolsoDTO(false, "Erro ao atualizar banco de dados", 
                                       null, BigDecimal.ZERO);
            }
            
            // Atualiza no cache
            cache.atualizar(bilhete);
            
            return new ReembolsoDTO(true, "Reembolso processado com sucesso", 
                                   bilhete.getDataReembolso(), valorReembolso);
            
        } catch (Exception e) {
            return new ReembolsoDTO(false, "Erro ao processar reembolso: " + e.getMessage(), 
                                   null, BigDecimal.ZERO);
        }
    }
    
    @Override
    public boolean sincronizarComBancoDados() {
        try {
            // Recarrega todos os dados do banco
            List<BilheteVendido> bilhetesAtualizados = repositorio.buscarTodos();
            cache.carregarBilhetes(bilhetesAtualizados);
            return true;
        } catch (Exception e) {
            System.err.println("Erro ao sincronizar com banco de dados: " + e.getMessage());
            return false;
        }
    }
    
    @Override
    public int contarBilhetesReembolsados() {
        return (int) cache.buscarTodos().stream()
                         .filter(BilheteVendido::isReembolsado)
                         .count();
    }
    
    @Override
    public BigDecimal calcularTotalVendas() {
        return cache.buscarTodos().stream()
                   .filter(b -> !b.isReembolsado())
                   .map(BilheteVendido::getPreco)
                   .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    @Override
    public BigDecimal calcularTotalReembolsos() {
        return cache.buscarTodos().stream()
                   .filter(BilheteVendido::isReembolsado)
                   .map(BilheteVendido::getPreco)
                   .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}