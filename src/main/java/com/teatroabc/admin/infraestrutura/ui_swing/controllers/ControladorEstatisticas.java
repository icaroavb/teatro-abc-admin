package com.teatroabc.admin.infraestrutura.ui_swing.controllers;

import com.teatroabc.admin.aplicacao.interfaces.IBilheteServico;
import com.teatroabc.admin.aplicacao.interfaces.IEstatisticaServico;
import com.teatroabc.admin.infraestrutura.ui_swing.telas.TelaEstatisticas;

import javax.swing.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Classe controladora para a TelaEstatisticas.
 * Responsável por buscar os dados dos serviços e orquestrar a atualização da view.
 */
public class ControladorEstatisticas {

    private final IEstatisticaServico estatisticaServico;
    private final IBilheteServico bilheteServico;
    private final TelaEstatisticas view; // Referência à View que este controlador gerencia

    /**
     * Construtor do controlador.
     *
     * @param estatisticaServico Serviço para obter dados estatísticos agregados.
     * @param bilheteServico     Serviço para obter dados brutos de bilhetes.
     * @param view               A instância da TelaEstatisticas a ser controlada.
     */
    public ControladorEstatisticas(IEstatisticaServico estatisticaServico, IBilheteServico bilheteServico, TelaEstatisticas view) {
        this.estatisticaServico = estatisticaServico;
        this.bilheteServico = bilheteServico;
        this.view = view;
    }

    /**
     * Inicia o processo de carregamento dos dados de estatísticas em uma thread de fundo.
     */
    public void carregarEstatisticas() {
        // Informa à view que o carregamento começou
        view.setCarregando(true);

        // SwingWorker para realizar a busca de dados sem travar a UI
        SwingWorker<Map<String, Object>, Void> worker = new SwingWorker<>() {
            @Override
            protected Map<String, Object> doInBackground() throws Exception {
                Map<String, Object> resultado = new HashMap<>();
                
                // Simula um pequeno atraso para que o feedback visual seja perceptível
                Thread.sleep(500);

                // Busca os dados dos serviços
                resultado.put("totalVendas", bilheteServico.calcularTotalVendas());
                resultado.put("totalReembolsos", bilheteServico.calcularTotalReembolsos());
                resultado.put("qtdBilhetes", bilheteServico.buscarTodos().size());
                resultado.put("qtdReembolsados", bilheteServico.contarBilhetesReembolsados());
                resultado.put("vendasPorPeca", estatisticaServico.calcularVendasPorPeca());
                resultado.put("vendasPorTurno", estatisticaServico.calcularVendasPorTurno());
                
                return resultado;
            }

            @Override
            protected void done() {
                try {
                    // Obtém o resultado da thread de fundo
                    Map<String, Object> resultado = get();
                    
                    // Envia os dados para a view atualizar seus componentes
                    view.atualizarNumeros(resultado);
                    view.atualizarGraficos(resultado);
                    view.atualizarDataAtualizacao();
                    
                } catch (Exception ex) {
                    ex.printStackTrace();
                    view.mostrarErro("Erro ao carregar estatísticas: " + ex.getMessage());
                } finally {
                    // Informa à view que o carregamento terminou
                    view.setCarregando(false);
                }
            }
        };

        worker.execute();
    }
}
