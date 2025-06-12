package com.teatroabc.admin.infraestrutura.ui_swing.telas;



import com.teatroabc.admin.aplicacao.dto.ReembolsoDTO;
import com.teatroabc.admin.aplicacao.interfaces.IBilheteServico;
import com.teatroabc.admin.dominio.entidades.BilheteVendido;
import com.teatroabc.admin.infraestrutura.ui_swing.componentes.PainelFiltros;
import com.teatroabc.admin.infraestrutura.ui_swing.componentes.TabelaBilhetes;
import com.teatroabc.admin.infraestrutura.ui_swing.util.ConstantesUI;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * Tela para gerenciamento de bilhetes vendidos.
 * Permite buscar, filtrar e realizar reembolsos de bilhetes.
 */
public class TelaGerenciamentoBilhetes extends JPanel {
    private final IBilheteServico bilheteServico;
    
    private TabelaBilhetes tabelaBilhetes;
    private PainelFiltros painelFiltros;
    private JButton btnAtualizar;
    private JButton btnDetalhes;
    private JButton btnReembolsar;
    private JLabel lblStatus;
    private JLabel lblContagem;
    
    /**
     * Construtor da tela de gerenciamento de bilhetes.
     * @param bilheteServico Serviço de bilhetes injetado
     */
    public TelaGerenciamentoBilhetes(IBilheteServico bilheteServico) {
        if (bilheteServico == null) {
            throw new IllegalArgumentException("Serviço de bilhetes não pode ser nulo");
        }
        this.bilheteServico = bilheteServico;
        
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        setBackground(ConstantesUI.COR_FUNDO_MEDIO);
        
        inicializarComponentes();
        configurarAcoes();
        carregarBilhetes();
    }
    
    private void inicializarComponentes() {
        // Título da tela
        JPanel painelTitulo = new JPanel(new BorderLayout());
        painelTitulo.setOpaque(false);
        painelTitulo.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        
        JLabel lblTitulo = new JLabel("Gerenciamento de Bilhetes");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 22));
        lblTitulo.setForeground(ConstantesUI.COR_TEXTO_CLARO);
        
        painelTitulo.add(lblTitulo, BorderLayout.WEST);
        
        // Painel de filtros
        painelFiltros = new PainelFiltros(e -> aplicarFiltros());
        
        // Botão de atualizar
        btnAtualizar = new JButton("Atualizar Dados");
        btnAtualizar.setFont(new Font("Arial", Font.PLAIN, 14));
        btnAtualizar.setBackground(ConstantesUI.COR_BOTAO_SECUNDARIO);
        btnAtualizar.setForeground(Color.WHITE);
        btnAtualizar.setFocusPainted(false);
        btnAtualizar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        painelTitulo.add(btnAtualizar, BorderLayout.EAST);
        
        // Painel central com tabela de bilhetes
        JPanel painelCentral = new JPanel(new BorderLayout(0, 10));
        painelCentral.setOpaque(false);
        
        tabelaBilhetes = new TabelaBilhetes();
        JScrollPane scrollTabela = new JScrollPane(tabelaBilhetes);
        scrollTabela.setBorder(BorderFactory.createLineBorder(ConstantesUI.COR_BORDA));
        
        // Painel de contagem e status
        JPanel painelContagem = new JPanel(new BorderLayout());
        painelContagem.setOpaque(false);
        painelContagem.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        lblContagem = new JLabel("0 bilhetes encontrados");
        lblContagem.setFont(new Font("Arial", Font.PLAIN, 12));
        lblContagem.setForeground(ConstantesUI.COR_TEXTO_CLARO);
        
        lblStatus = new JLabel("Pronto");
        lblStatus.setFont(new Font("Arial", Font.PLAIN, 12));
        lblStatus.setForeground(ConstantesUI.COR_TEXTO_CLARO);
        
        painelContagem.add(lblContagem, BorderLayout.WEST);
        painelContagem.add(lblStatus, BorderLayout.EAST);
        
        // Painel de ações/botões
        JPanel painelAcoes = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        painelAcoes.setOpaque(false);
        
        btnDetalhes = new JButton("Ver Detalhes");
        btnDetalhes.setFont(new Font("Arial", Font.PLAIN, 14));
        btnDetalhes.setBackground(ConstantesUI.COR_BOTAO_SECUNDARIO);
        btnDetalhes.setForeground(Color.WHITE);
        btnDetalhes.setEnabled(false);
        btnDetalhes.setFocusPainted(false);
        btnDetalhes.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        btnReembolsar = new JButton("Reembolsar");
        btnReembolsar.setFont(new Font("Arial", Font.PLAIN, 14));
        btnReembolsar.setBackground(ConstantesUI.COR_BOTAO_PRIMARIO);
        btnReembolsar.setForeground(Color.WHITE);
        btnReembolsar.setEnabled(false);
        btnReembolsar.setFocusPainted(false);
        btnReembolsar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        painelAcoes.add(btnDetalhes);
        painelAcoes.add(btnReembolsar);
        
        painelCentral.add(scrollTabela, BorderLayout.CENTER);
        painelCentral.add(painelContagem, BorderLayout.SOUTH);
        
        // Montar a tela
        add(painelTitulo, BorderLayout.NORTH);
        add(painelFiltros, BorderLayout.WEST);
        add(painelCentral, BorderLayout.CENTER);
        add(painelAcoes, BorderLayout.SOUTH);
    }
    
    private void configurarAcoes() {
        // Configurar seleção na tabela
        tabelaBilhetes.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                boolean temSelecao = tabelaBilhetes.getSelectedRow() != -1;
                btnDetalhes.setEnabled(temSelecao);
                
                if (temSelecao) {
                    BilheteVendido bilhete = tabelaBilhetes.getBilheteSelecionado();
                    btnReembolsar.setEnabled(!bilhete.isReembolsado());
                } else {
                    btnReembolsar.setEnabled(false);
                }
            }
        });
        
        // Botão Atualizar
        btnAtualizar.addActionListener(e -> {
            atualizarStatus("Sincronizando com banco de dados...");
            SwingWorker<Boolean, Void> worker = new SwingWorker<>() {
                @Override
                protected Boolean doInBackground() {
                    return bilheteServico.sincronizarComBancoDados();
                }
                
                @Override
                protected void done() {
                    try {
                        boolean sucesso = get();
                        if (sucesso) {
                            atualizarStatus("Sincronização concluída com sucesso");
                            carregarBilhetes();
                        } else {
                            atualizarStatus("Falha na sincronização");
                            JOptionPane.showMessageDialog(
                                TelaGerenciamentoBilhetes.this,
                                "Não foi possível sincronizar com o banco de dados",
                                "Erro de Sincronização",
                                JOptionPane.ERROR_MESSAGE
                            );
                        }
                    } catch (Exception ex) {
                        atualizarStatus("Erro: " + ex.getMessage());
                        ex.printStackTrace();
                    }
                }
            };
            worker.execute();
        });
        
        // Botão Ver Detalhes
        btnDetalhes.addActionListener(e -> {
            BilheteVendido bilhete = tabelaBilhetes.getBilheteSelecionado();
            if (bilhete != null) {
                JOptionPane.showMessageDialog(
                    this,
                    criarPainelDetalhesBilhete(bilhete),
                    "Detalhes do Bilhete",
                    JOptionPane.INFORMATION_MESSAGE
                );
            }
        });
        
        // Botão Reembolsar
        btnReembolsar.addActionListener(e -> {
            BilheteVendido bilhete = tabelaBilhetes.getBilheteSelecionado();
            if (bilhete != null && !bilhete.isReembolsado()) {
                processarReembolso(bilhete);
            }
        });
    }
    
    private void carregarBilhetes() {
        atualizarStatus("Carregando bilhetes...");
        
        SwingWorker<List<BilheteVendido>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<BilheteVendido> doInBackground() {
                return bilheteServico.buscarTodos();
            }
            
            @Override
            protected void done() {
                try {
                    List<BilheteVendido> bilhetes = get();
                    tabelaBilhetes.atualizarDados(bilhetes);
                    atualizarContagem(bilhetes.size());
                    atualizarStatus("Bilhetes carregados");
                } catch (Exception ex) {
                    atualizarStatus("Erro ao carregar: " + ex.getMessage());
                    ex.printStackTrace();
                }
            }
        };
        worker.execute();
    }
    
    private void aplicarFiltros() {
        String termo = painelFiltros.getTermoBusca();
        String tipoBusca = painelFiltros.getTipoBusca();
        boolean mostrarReembolsados = painelFiltros.getMostrarReembolsados();
        
        atualizarStatus("Aplicando filtros...");
        
        SwingWorker<List<BilheteVendido>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<BilheteVendido> doInBackground() {
                List<BilheteVendido> resultado;
                
                if (termo != null && !termo.isEmpty()) {
                    if ("cpf".equals(tipoBusca)) {
                        resultado = bilheteServico.buscarPorCpf(termo);
                    } else if ("peca".equals(tipoBusca)) {
                        resultado = bilheteServico.buscarPorPeca(termo);
                    } else {
                        // Considera como ID
                        Optional<BilheteVendido> bilheteOpt = bilheteServico.buscarPorId(termo);
                        resultado = bilheteOpt.map(List::of).orElse(List.of());
                    }
                } else {
                    resultado = bilheteServico.buscarTodos();
                }
                
                // Filtra por status de reembolso se necessário
                if (!mostrarReembolsados) {
                    resultado = resultado.stream()
                            .filter(b -> !b.isReembolsado())
                            .toList();
                }
                
                return resultado;
            }
            
            @Override
            protected void done() {
                try {
                    List<BilheteVendido> bilhetes = get();
                    tabelaBilhetes.atualizarDados(bilhetes);
                    atualizarContagem(bilhetes.size());
                    atualizarStatus("Filtros aplicados");
                } catch (Exception ex) {
                    atualizarStatus("Erro ao filtrar: " + ex.getMessage());
                    ex.printStackTrace();
                }
            }
        };
        worker.execute();
    }
    
    private void atualizarStatus(String mensagem) {
        lblStatus.setText(mensagem);
    }
    
    private void atualizarContagem(int quantidade) {
        lblContagem.setText(quantidade + " bilhetes encontrados");
    }
    
    private JPanel criarPainelDetalhesBilhete(BilheteVendido bilhete) {
        JPanel painel = new JPanel();
        painel.setLayout(new BoxLayout(painel, BoxLayout.Y_AXIS));
        painel.setPreferredSize(new Dimension(400, 300));
        
        // Título
        JLabel lblTitulo = new JLabel("Bilhete #" + bilhete.getIdIngresso());
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 16));
        lblTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Informações do bilhete
        JPanel painelInfo = new JPanel(new GridLayout(0, 2, 10, 5));
        painelInfo.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        adicionarCampoInfo(painelInfo, "ID:", bilhete.getIdIngresso());
        adicionarCampoInfo(painelInfo, "CPF:", bilhete.getCpf());
        adicionarCampoInfo(painelInfo, "Peça:", bilhete.getNomePeca());
        adicionarCampoInfo(painelInfo, "Turno:", bilhete.getTurno());
        adicionarCampoInfo(painelInfo, "Poltrona(s):", bilhete.getNumeroPoltronas());
        adicionarCampoInfo(painelInfo, "Preço:", "R$ " + bilhete.getPreco().toString());
        adicionarCampoInfo(painelInfo, "Status:", bilhete.isReembolsado() ? "REEMBOLSADO" : "ATIVO");
        
        if (bilhete.isReembolsado() && bilhete.getDataReembolso() != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            adicionarCampoInfo(painelInfo, "Data Reembolso:", sdf.format(bilhete.getDataReembolso()));
        }
        
        painel.add(Box.createVerticalStrut(10));
        painel.add(lblTitulo);
        painel.add(Box.createVerticalStrut(20));
        painel.add(painelInfo);
        
        return painel;
    }
    
    private void adicionarCampoInfo(JPanel painel, String rotulo, String valor) {
        JLabel lblRotulo = new JLabel(rotulo);
        lblRotulo.setFont(new Font("Arial", Font.BOLD, 12));
        
        JLabel lblValor = new JLabel(valor);
        lblValor.setFont(new Font("Arial", Font.PLAIN, 12));
        
        painel.add(lblRotulo);
        painel.add(lblValor);
    }
    
    private void processarReembolso(BilheteVendido bilhete) {
        int confirmacao = JOptionPane.showConfirmDialog(
            this,
            "Deseja realmente reembolsar o bilhete #" + bilhete.getIdIngresso() + "?",
            "Confirmar Reembolso",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );
        
        if (confirmacao != JOptionPane.YES_OPTION) {
            return;
        }
        
        // Solicitar motivo do reembolso
        String motivo = JOptionPane.showInputDialog(
            this,
            "Informe o motivo do reembolso:",
            "Motivo do Reembolso",
            JOptionPane.QUESTION_MESSAGE
        );
        
        if (motivo == null || motivo.trim().isEmpty()) {
            JOptionPane.showMessageDialog(
                this,
                "É necessário informar um motivo para o reembolso.",
                "Motivo Obrigatório",
                JOptionPane.WARNING_MESSAGE
            );
            return;
        }
        
        atualizarStatus("Processando reembolso...");
        
        SwingWorker<ReembolsoDTO, Void> worker = new SwingWorker<>() {
            @Override
            protected ReembolsoDTO doInBackground() {
                return bilheteServico.processarReembolso(bilhete.getIdIngresso(), motivo);
            }
            
            @Override
            protected void done() {
                try {
                    ReembolsoDTO resultado = get();
                    if (resultado.isSucesso()) {
                        atualizarStatus("Reembolso realizado com sucesso");
                        JOptionPane.showMessageDialog(
                            TelaGerenciamentoBilhetes.this,
                            "Reembolso realizado com sucesso!\nValor reembolsado: R$ " + resultado.getValorReembolsado(),
                            "Reembolso Concluído",
                            JOptionPane.INFORMATION_MESSAGE
                        );
                        carregarBilhetes(); // Recarrega a tabela
                    } else {
                        atualizarStatus("Falha no reembolso: " + resultado.getMensagem());
                        JOptionPane.showMessageDialog(
                            TelaGerenciamentoBilhetes.this,
                            "Falha ao processar reembolso: " + resultado.getMensagem(),
                            "Erro no Reembolso",
                            JOptionPane.ERROR_MESSAGE
                        );
                    }
                } catch (Exception ex) {
                    atualizarStatus("Erro: " + ex.getMessage());
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(
                        TelaGerenciamentoBilhetes.this,
                        "Erro ao processar reembolso: " + ex.getMessage(),
                        "Erro no Reembolso",
                        JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        };
        worker.execute();
    }
}