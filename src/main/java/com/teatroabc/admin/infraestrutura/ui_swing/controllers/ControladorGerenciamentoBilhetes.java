package com.teatroabc.admin.infraestrutura.ui_swing.controllers;

import com.teatroabc.admin.aplicacao.dto.ReembolsoDTO;
import com.teatroabc.admin.aplicacao.interfaces.IBilheteServico;
import com.teatroabc.admin.dominio.entidades.BilheteVendido;
import com.teatroabc.admin.infraestrutura.ui_swing.telas.TelaGerenciamentoBilhetes;

import javax.swing.*;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Classe controladora para a TelaGerenciamentoBilhetes.
 * Lida com a lógica de negócio, busca de dados e interações com o serviço de bilhetes.
 */
public class ControladorGerenciamentoBilhetes {

    private final IBilheteServico bilheteServico;
    private final TelaGerenciamentoBilhetes view; // Referência à View

    /**
     * Construtor do controlador.
     *
     * @param bilheteServico O serviço de bilhetes injetado.
     * @param view           A instância da tela a ser controlada.
     */
    public ControladorGerenciamentoBilhetes(IBilheteServico bilheteServico, TelaGerenciamentoBilhetes view) {
        this.bilheteServico = bilheteServico;
        this.view = view;
    }

    /**
     * Carrega todos os bilhetes do serviço e atualiza a view.
     */
    public void carregarBilhetes() {
        view.setCarregando(true);
        view.atualizarStatus("Carregando bilhetes...");

        SwingWorker<List<BilheteVendido>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<BilheteVendido> doInBackground() {
                return bilheteServico.buscarTodos();
            }

            @Override
            protected void done() {
                try {
                    view.atualizarTabela(get());
                    view.atualizarStatus("Bilhetes carregados");
                } catch (Exception ex) {
                    view.mostrarErro("Erro ao carregar bilhetes: " + ex.getMessage());
                    ex.printStackTrace();
                } finally {
                    view.setCarregando(false);
                }
            }
        };
        worker.execute();
    }

    /**
     * Aplica os filtros com base nos dados fornecidos pela view.
     */
    public void aplicarFiltros() {
        String termo = view.getFiltroTermo();
        String tipoBusca = view.getFiltroTipo();
        boolean mostrarReembolsados = view.getFiltroMostrarReembolsados();

        view.setCarregando(true);
        view.atualizarStatus("Aplicando filtros...");

        SwingWorker<List<BilheteVendido>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<BilheteVendido> doInBackground() {
                List<BilheteVendido> resultado;
                if (termo != null && !termo.isEmpty()) {
                    resultado = switch (tipoBusca) {
                        case "cpf" -> bilheteServico.buscarPorCpf(termo);
                        case "peca" -> bilheteServico.buscarPorPeca(termo);
                        default -> bilheteServico.buscarPorId(termo).map(List::of).orElse(List.of());
                    };
                } else {
                    resultado = bilheteServico.buscarTodos();
                }

                if (!mostrarReembolsados) {
                    return resultado.stream().filter(b -> !b.isReembolsado()).toList();
                }
                return resultado;
            }

            @Override
            protected void done() {
                try {
                    view.atualizarTabela(get());
                    view.atualizarStatus("Filtros aplicados");
                } catch (Exception ex) {
                    view.mostrarErro("Erro ao aplicar filtros: " + ex.getMessage());
                    ex.printStackTrace();
                } finally {
                    view.setCarregando(false);
                }
            }
        };
        worker.execute();
    }

    /**
     * Sincroniza os dados com o banco de dados e recarrega os bilhetes.
     */
    public void sincronizarComBanco() {
        view.setCarregando(true);
        view.atualizarStatus("Sincronizando com banco de dados...");

        SwingWorker<Boolean, Void> worker = new SwingWorker<>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                Thread.sleep(500); // Delay para feedback visual
                return bilheteServico.sincronizarComBancoDados();
            }

            @Override
            protected void done() {
                try {
                    if (get()) {
                        view.atualizarStatus("Sincronização concluída com sucesso");
                        carregarBilhetes(); // Recarrega os dados após a sincronização
                    } else {
                        view.mostrarErro("Não foi possível sincronizar com o banco de dados");
                        view.atualizarStatus("Falha na sincronização");
                    }
                } catch (Exception ex) {
                    view.mostrarErro("Erro de sincronização: " + ex.getMessage());
                    view.atualizarStatus("Falha na sincronização");
                    ex.printStackTrace();
                } finally {
                    view.setCarregando(false);
                }
            }
        };
        worker.execute();
    }

    /**
     * Processa o reembolso de um bilhete.
     *
     * @param idBilhete O ID do bilhete a ser reembolsado.
     * @param motivo    O motivo do reembolso.
     */
    public void processarReembolso(String idBilhete, String motivo) {
        view.setCarregando(true);
        view.atualizarStatus("Processando reembolso...");

        SwingWorker<ReembolsoDTO, Void> worker = new SwingWorker<>() {
            @Override
            protected ReembolsoDTO doInBackground() throws Exception {
                Thread.sleep(500); // Delay para feedback visual
                return bilheteServico.processarReembolso(idBilhete, motivo);
            }

            @Override
            protected void done() {
                try {
                    ReembolsoDTO resultado = get();
                    if (resultado != null && resultado.isSucesso()) {
                        view.mostrarSucesso("Reembolso Concluído", "O bilhete foi reembolsado com sucesso.\nValor: " + resultado.getValorReembolsado());
                        carregarBilhetes(); // Atualiza a lista
                    } else {
                        String msg = resultado != null ? resultado.getMensagem() : "Erro desconhecido.";
                        view.mostrarErro("Falha ao processar reembolso: " + msg);
                    }
                } catch (Exception ex) {
                    view.mostrarErro("Erro ao processar reembolso: " + ex.getMessage());
                    ex.printStackTrace();
                } finally {
                    view.setCarregando(false);
                }
            }
        };
        worker.execute();
    }
}
