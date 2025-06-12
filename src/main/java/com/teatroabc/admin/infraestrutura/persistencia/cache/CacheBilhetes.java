package com.teatroabc.admin.infraestrutura.persistencia.cache;



import com.teatroabc.admin.dominio.entidades.BilheteVendido;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Sistema de cache em memória para bilhetes vendidos.
 * Utiliza ConcurrentHashMap para garantir thread-safety.
 * Padrão Singleton para garantir instância única.
 */
public class CacheBilhetes {
    private static volatile CacheBilhetes instancia;
    private final Map<String, BilheteVendido> cachePrincipal;
    private final Map<String, Set<String>> indicePorCpf;
    private final Map<String, Set<String>> indicePorPeca;
    private boolean sincronizado;
    
    private CacheBilhetes() {
        this.cachePrincipal = new ConcurrentHashMap<>();
        this.indicePorCpf = new ConcurrentHashMap<>();
        this.indicePorPeca = new ConcurrentHashMap<>();
        this.sincronizado = false;
    }
    
    /**
     * Obtém a instância única do cache (Singleton thread-safe)
     */
    public static CacheBilhetes getInstance() {
        if (instancia == null) {
            synchronized (CacheBilhetes.class) {
                if (instancia == null) {
                    instancia = new CacheBilhetes();
                }
            }
        }
        return instancia;
    }
    
    /**
     * Carrega todos os bilhetes para o cache e cria índices
     */
    public void carregarBilhetes(List<BilheteVendido> bilhetes) {
        limparCache();
        
        for (BilheteVendido bilhete : bilhetes) {
            adicionar(bilhete);
        }
        
        this.sincronizado = true;
        System.out.println("Cache carregado com " + bilhetes.size() + " bilhetes");
    }
    
    /**
     * Adiciona um bilhete ao cache e atualiza índices
     */
    public void adicionar(BilheteVendido bilhete) {
        if (bilhete == null) return;
        
        cachePrincipal.put(bilhete.getIdIngresso(), bilhete);
        
        // Atualizar índice por CPF
        indicePorCpf.computeIfAbsent(bilhete.getCpf(), k -> new HashSet<>())
                    .add(bilhete.getIdIngresso());
        
        // Atualizar índice por peça
        indicePorPeca.computeIfAbsent(bilhete.getNomePeca(), k -> new HashSet<>())
                     .add(bilhete.getIdIngresso());
    }
    
    /**
     * Remove um bilhete do cache e atualiza índices
     */
    public void remover(String idIngresso) {
        BilheteVendido bilhete = cachePrincipal.remove(idIngresso);
        if (bilhete != null) {
            // Remover dos índices
            Set<String> idsPorCpf = indicePorCpf.get(bilhete.getCpf());
            if (idsPorCpf != null) {
                idsPorCpf.remove(idIngresso);
                if (idsPorCpf.isEmpty()) {
                    indicePorCpf.remove(bilhete.getCpf());
                }
            }
            
            Set<String> idsPorPeca = indicePorPeca.get(bilhete.getNomePeca());
            if (idsPorPeca != null) {
                idsPorPeca.remove(idIngresso);
                if (idsPorPeca.isEmpty()) {
                    indicePorPeca.remove(bilhete.getNomePeca());
                }
            }
        }
    }
    
    /**
     * Busca um bilhete por ID
     */
    public Optional<BilheteVendido> buscarPorId(String idIngresso) {
        return Optional.ofNullable(cachePrincipal.get(idIngresso));
    }
    
    /**
     * Busca bilhetes por CPF usando índice
     */
    public List<BilheteVendido> buscarPorCpf(String cpf) {
        Set<String> ids = indicePorCpf.get(cpf);
        if (ids == null || ids.isEmpty()) {
            return Collections.emptyList();
        }
        
        return ids.stream()
                  .map(cachePrincipal::get)
                  .filter(Objects::nonNull)
                  .collect(Collectors.toList());
    }
    
    /**
     * Busca bilhetes por peça usando índice
     */
    public List<BilheteVendido> buscarPorPeca(String nomePeca) {
        Set<String> ids = indicePorPeca.get(nomePeca);
        if (ids == null || ids.isEmpty()) {
            return Collections.emptyList();
        }
        
        return ids.stream()
                  .map(cachePrincipal::get)
                  .filter(Objects::nonNull)
                  .collect(Collectors.toList());
    }
    
    /**
     * Retorna todos os bilhetes do cache
     */
    public List<BilheteVendido> buscarTodos() {
        return new ArrayList<>(cachePrincipal.values());
    }
    
    /**
     * Retorna bilhetes filtrados por status de reembolso
     */
    public List<BilheteVendido> buscarPorStatusReembolso(boolean reembolsado) {
        return cachePrincipal.values().stream()
                .filter(b -> b.isReembolsado() == reembolsado)
                .collect(Collectors.toList());
    }
    
    /**
     * Atualiza um bilhete no cache
     */
    public void atualizar(BilheteVendido bilhete) {
        if (bilhete != null && cachePrincipal.containsKey(bilhete.getIdIngresso())) {
            cachePrincipal.put(bilhete.getIdIngresso(), bilhete);
        }
    }
    
    /**
     * Estatísticas rápidas do cache
     */
    public Map<String, Object> obterEstatisticas() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalBilhetes", cachePrincipal.size());
        stats.put("totalClientes", indicePorCpf.size());
        stats.put("totalPecas", indicePorPeca.size());
        stats.put("bilhetesReembolsados", 
                 cachePrincipal.values().stream()
                               .filter(BilheteVendido::isReembolsado)
                               .count());
        return stats;
    }
    
    /**
     * Limpa todo o cache
     */
    public void limparCache() {
        cachePrincipal.clear();
        indicePorCpf.clear();
        indicePorPeca.clear();
        this.sincronizado = false;
    }
    
    public boolean isSincronizado() {
        return sincronizado;
    }
    
    public void setSincronizado(boolean sincronizado) {
        this.sincronizado = sincronizado;
    }
    
    /**
     * Tamanho do cache
     */
    public int tamanho() {
        return cachePrincipal.size();
    }
}