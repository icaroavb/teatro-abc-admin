package com.teatroabc.admin.infraestrutura.ui_swing.telas;

import com.teatroabc.admin.aplicacao.dto.ReembolsoDTO;
import com.teatroabc.admin.aplicacao.interfaces.IBilheteServico;
import com.teatroabc.admin.dominio.entidades.BilheteVendido;
import com.teatroabc.admin.infraestrutura.ui_swing.componentes.PainelFiltros;
import com.teatroabc.admin.infraestrutura.ui_swing.componentes.TabelaBilhetes;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

/**
 * Tela para gerenciamento de bilhetes vendidos.
 * Versão corrigida com tratamento adequado da data de reembolso.
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
    private JProgressBar progressBar;
    
    // Formatador para datas (LocalDateTime)
    private final DateTimeFormatter formatoData = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
    
    /**
     * Construtor da tela de gerenciamento de bilhetes.
     * @param bilheteServico Serviço de bilhetes injetado
     */
    public TelaGerenciamentoBilhetes(IBilheteServico bilheteServico) {
        if (bilheteServico == null) {
            throw new IllegalArgumentException("Serviço de bilhetes não pode ser nulo");
        }
        this.bilheteServico = bilheteServico;
        
        setLayout(new BorderLayout(15, 15));
        setBorder(new EmptyBorder(20, 20, 20, 20));
        setBackground(new Color(25, 40, 55));
        
        inicializarComponentes();
        configurarAcoes();
        carregarBilhetes();
    }
    
    private void inicializarComponentes() {
        // Painel de título e botões
        JPanel painelTitulo = criarPainelTitulo();
        add(painelTitulo, BorderLayout.NORTH);
        
        // Painel de filtros (lateral)
        painelFiltros = new PainelFiltros(e -> aplicarFiltros());
        add(painelFiltros, BorderLayout.WEST);
        
        // Painel central com tabela
        JPanel painelCentral = new JPanel(new BorderLayout(0, 10));
        painelCentral.setOpaque(false);
        
        // Tabela de bilhetes
        tabelaBilhetes = new TabelaBilhetes();
        JScrollPane scrollTabela = new JScrollPane(tabelaBilhetes);
        scrollTabela.setBorder(new LineBorder(new Color(60, 80, 120), 1));
        scrollTabela.getViewport().setBackground(new Color(25, 40, 55));
        
        // Painel de informações (contagem e status)
        JPanel painelInfo = new JPanel(new BorderLayout(10, 0));
        painelInfo.setOpaque(false);
        painelInfo.setBorder(new EmptyBorder(10, 5, 5, 5));
        
        lblContagem = new JLabel("0 bilhetes encontrados");
        lblContagem.setFont(new Font("Arial", Font.PLAIN, 12));
        lblContagem.setForeground(new Color(180, 190, 200));
        
        JPanel painelStatus = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        painelStatus.setOpaque(false);
        
        lblStatus = new JLabel("Pronto");
        lblStatus.setFont(new Font("Arial", Font.PLAIN, 12));
        lblStatus.setForeground(new Color(180, 190, 200));
        
        // Barra de progresso
        progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);
        progressBar.setVisible(false);
        progressBar.setPreferredSize(new Dimension(150, 5));
        progressBar.setBorderPainted(false);
        progressBar.setBackground(new Color(40, 50, 60));
        progressBar.setForeground(new Color(46, 204, 113));
        
        painelStatus.add(lblStatus);
        painelStatus.add(progressBar);
        
        painelInfo.add(lblContagem, BorderLayout.WEST);
        painelInfo.add(painelStatus, BorderLayout.EAST);
        
        painelCentral.add(scrollTabela, BorderLayout.CENTER);
        painelCentral.add(painelInfo, BorderLayout.SOUTH);
        
        add(painelCentral, BorderLayout.CENTER);
        
        // Painel de ações (inferior)
        JPanel painelAcoes = criarPainelAcoes();
        add(painelAcoes, BorderLayout.SOUTH);
    }
    
    private JPanel criarPainelTitulo() {
        JPanel painel = new JPanel(new BorderLayout(10, 0));
        painel.setOpaque(false);
        painel.setBorder(new EmptyBorder(0, 0, 15, 0));
        
        // Título
        JLabel lblTitulo = new JLabel("Gerenciamento de Bilhetes");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 24));
        lblTitulo.setForeground(Color.WHITE);
        
        // Subtítulo
        JLabel lblSubtitulo = new JLabel("Consulta e processamento de reembolsos");
        lblSubtitulo.setFont(new Font("Arial", Font.ITALIC, 14));
        lblSubtitulo.setForeground(new Color(180, 180, 180));
        
        JPanel painelTexto = new JPanel(new BorderLayout());
        painelTexto.setOpaque(false);
        painelTexto.add(lblTitulo, BorderLayout.NORTH);
        painelTexto.add(lblSubtitulo, BorderLayout.SOUTH);
        
        // Botão de atualizar
        btnAtualizar = new JButton("Atualizar Dados");
        btnAtualizar.setFont(new Font("Arial", Font.BOLD, 14));
        btnAtualizar.setBackground(new Color(52, 152, 219));
        btnAtualizar.setForeground(Color.WHITE);
        btnAtualizar.setFocusPainted(false);
        btnAtualizar.setBorderPainted(true);
        btnAtualizar.setBorder(new LineBorder(new Color(41, 128, 185), 1));
        btnAtualizar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnAtualizar.setPreferredSize(new Dimension(150, 40));
        
        painel.add(painelTexto, BorderLayout.WEST);
        painel.add(btnAtualizar, BorderLayout.EAST);
        
        return painel;
    }
    
    private JPanel criarPainelAcoes() {
        JPanel painel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        painel.setOpaque(false);
        painel.setBorder(new EmptyBorder(10, 0, 0, 0));
        
        btnDetalhes = new JButton("Ver Detalhes");
        btnDetalhes.setFont(new Font("Arial", Font.PLAIN, 14));
        btnDetalhes.setBackground(new Color(52, 152, 219));
        btnDetalhes.setForeground(Color.WHITE);
        btnDetalhes.setEnabled(false);
        btnDetalhes.setFocusPainted(false);
        btnDetalhes.setBorderPainted(true);
        btnDetalhes.setBorder(new LineBorder(new Color(41, 128, 185), 1));
        btnDetalhes.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnDetalhes.setPreferredSize(new Dimension(150, 40));
        
        btnReembolsar = new JButton("Reembolsar");
        btnReembolsar.setFont(new Font("Arial", Font.BOLD, 14));
        btnReembolsar.setBackground(new Color(46, 204, 113));
        btnReembolsar.setForeground(Color.WHITE);
        btnReembolsar.setEnabled(false);
        btnReembolsar.setFocusPainted(false);
        btnReembolsar.setBorderPainted(true);
        btnReembolsar.setBorder(new LineBorder(new Color(39, 174, 96), 1));
        btnReembolsar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnReembolsar.setPreferredSize(new Dimension(150, 40));
        
        painel.add(btnDetalhes);
        painel.add(btnReembolsar);
        
        return painel;
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
            progressBar.setVisible(true);
            btnAtualizar.setEnabled(false);
            
            SwingWorker<Boolean, Void> worker = new SwingWorker<>() {
                @Override
                protected Boolean doInBackground() {
                    try {
                        // Simulando um pequeno delay para feedback visual
                        Thread.sleep(500);
                        return bilheteServico.sincronizarComBancoDados();
                    } catch (InterruptedException ex) {
                        Thread.currentThread().interrupt();
                        return false;
                    }
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
                    } finally {
                        progressBar.setVisible(false);
                        btnAtualizar.setEnabled(true);
                    }
                }
            };
            
            worker.execute();
        });
        
        // Botão Ver Detalhes
        btnDetalhes.addActionListener(e -> {
            BilheteVendido bilhete = tabelaBilhetes.getBilheteSelecionado();
            if (bilhete != null) {
                mostrarDetalhesBilhete(bilhete);
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
        progressBar.setVisible(true);
        
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
                } finally {
                    progressBar.setVisible(false);
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
        progressBar.setVisible(true);
        
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
                } finally {
                    progressBar.setVisible(false);
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
    
     private void mostrarDetalhesBilhete(BilheteVendido bilhete) {
        // Criar o painel de detalhes do bilhete
        JPanel painelDetalhes = new JPanel();
        painelDetalhes.setLayout(new BoxLayout(painelDetalhes, BoxLayout.Y_AXIS));
        painelDetalhes.setBorder(new EmptyBorder(20, 20, 20, 20));
        painelDetalhes.setBackground(new Color(30, 45, 60));
        
        // Título
        JLabel lblTitulo = new JLabel("Bilhete #" + bilhete.getIdIngresso());
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 20));
        lblTitulo.setForeground(Color.WHITE);
        lblTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Status
        JLabel lblStatus = new JLabel(bilhete.isReembolsado() ? "REEMBOLSADO" : "ATIVO");
        lblStatus.setFont(new Font("Arial", Font.BOLD, 14));
        lblStatus.setForeground(bilhete.isReembolsado() ? new Color(231, 76, 60) : new Color(46, 204, 113));
        lblStatus.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblStatus.setBorder(new EmptyBorder(5, 0, 20, 0));
        
        // Painel de informações
        JPanel painelInfo = new JPanel(new GridLayout(0, 2, 15, 10));
        painelInfo.setOpaque(false);
        painelInfo.setBorder(new LineBorder(new Color(60, 80, 100), 1));
        
        // Adiciona as informações
        adicionarCampoInfo(painelInfo, "ID:", bilhete.getIdIngresso());
        adicionarCampoInfo(painelInfo, "CPF:", formatarCPF(bilhete.getCpf()));
        adicionarCampoInfo(painelInfo, "Peça:", bilhete.getNomePeca());
        adicionarCampoInfo(painelInfo, "Turno:", bilhete.getTurno());
        adicionarCampoInfo(painelInfo, "Poltrona(s):", bilhete.getNumeroPoltronas());
        
        // Formatar preço
        DecimalFormat df = new DecimalFormat("R$ #,##0.00");
        adicionarCampoInfo(painelInfo, "Preço:", df.format(bilhete.getPreco()));
        
        // Data de reembolso se aplicável - ESTA É A PARTE CORRIGIDA
        if (bilhete.isReembolsado() && bilhete.getDataReembolso() != null) {
            LocalDateTime dataReembolso = bilhete.getDataReembolso();
            String dataFormatada = dataReembolso.format(formatoData);
            adicionarCampoInfo(painelInfo, "Data do Reembolso:", dataFormatada);
        }
        
        // Montar o painel
        painelDetalhes.add(lblTitulo);
        painelDetalhes.add(lblStatus);
        painelDetalhes.add(Box.createVerticalStrut(10));
        painelDetalhes.add(painelInfo);
        
        // Mostrar diálogo
        JOptionPane.showMessageDialog(
            this,
            painelDetalhes,
            "Detalhes do Bilhete",
            JOptionPane.PLAIN_MESSAGE
        );
    }
    
    private void adicionarCampoInfo(JPanel painel, String rotulo, String valor) {
        JLabel lblRotulo = new JLabel(rotulo);
        lblRotulo.setFont(new Font("Arial", Font.BOLD, 14));
        lblRotulo.setForeground(new Color(180, 200, 220));
        
        JLabel lblValor = new JLabel(valor);
        lblValor.setFont(new Font("Arial", Font.PLAIN, 14));
        lblValor.setForeground(Color.WHITE);
        
        painel.add(lblRotulo);
        painel.add(lblValor);
    }
    
    private String formatarCPF(String cpf) {
        if (cpf == null || cpf.length() != 11) {
            return cpf;
        }
        
        return cpf.substring(0, 3) + "." + 
               cpf.substring(3, 6) + "." + 
               cpf.substring(6, 9) + "-" + 
               cpf.substring(9, 11);
    }
    
    private void processarReembolso(BilheteVendido bilhete) {
        // Painel para input do motivo
        JPanel painelMotivo = new JPanel();
        painelMotivo.setLayout(new BoxLayout(painelMotivo, BoxLayout.Y_AXIS));
        painelMotivo.setBorder(new EmptyBorder(20, 20, 20, 20));
        painelMotivo.setBackground(new Color(30, 45, 60));
        
        JLabel lblMensagem = new JLabel("Deseja realmente reembolsar o bilhete #" + bilhete.getIdIngresso() + "?");
        lblMensagem.setFont(new Font("Arial", Font.BOLD, 14));
        lblMensagem.setForeground(Color.WHITE);
        lblMensagem.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel lblInfoBilhete = new JLabel("Peça: " + bilhete.getNomePeca() + " - Valor: R$" + bilhete.getPreco());
        lblInfoBilhete.setFont(new Font("Arial", Font.PLAIN, 14));
        lblInfoBilhete.setForeground(new Color(180, 200, 220));
        lblInfoBilhete.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblInfoBilhete.setBorder(new EmptyBorder(0, 0, 15, 0));
        
        JLabel lblMotivo = new JLabel("Motivo do reembolso:");
        lblMotivo.setFont(new Font("Arial", Font.BOLD, 14));
        lblMotivo.setForeground(Color.WHITE);
        lblMotivo.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JTextArea txtMotivo = new JTextArea();
        txtMotivo.setFont(new Font("Arial", Font.PLAIN, 14));
        txtMotivo.setForeground(Color.WHITE);
        txtMotivo.setBackground(new Color(40, 60, 85));
        txtMotivo.setCaretColor(Color.WHITE);
        txtMotivo.setLineWrap(true);
        txtMotivo.setWrapStyleWord(true);
        txtMotivo.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(60, 80, 120), 1),
            new EmptyBorder(8, 8, 8, 8)
        ));
        
        JScrollPane scrollMotivo = new JScrollPane(txtMotivo);
        scrollMotivo.setPreferredSize(new Dimension(400, 120));
        scrollMotivo.setBorder(BorderFactory.createEmptyBorder());
        
        painelMotivo.add(lblMensagem);
        painelMotivo.add(lblInfoBilhete);
        painelMotivo.add(lblMotivo);
        painelMotivo.add(Box.createVerticalStrut(5));
        painelMotivo.add(scrollMotivo);
        
        // Mostrar diálogo
        int resultado = JOptionPane.showConfirmDialog(
            this,
            painelMotivo,
            "Confirmar Reembolso",
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );
        
        // Processar resultado
        if (resultado == JOptionPane.OK_OPTION) {
            String motivo = txtMotivo.getText().trim();
            
            if (motivo.isEmpty()) {
                JOptionPane.showMessageDialog(
                    this,
                    "É necessário informar um motivo para o reembolso.",
                    "Motivo Obrigatório",
                    JOptionPane.WARNING_MESSAGE
                );
                return;
            }
            
            // Realizar o reembolso
            realizarReembolso(bilhete.getIdIngresso(), motivo);
        }
    }
    
    private void realizarReembolso(String idBilhete, String motivo) {
        atualizarStatus("Processando reembolso...");
        progressBar.setVisible(true);
        btnReembolsar.setEnabled(false);
        
        SwingWorker<ReembolsoDTO, Void> worker = new SwingWorker<>() {
            @Override
            protected ReembolsoDTO doInBackground() {
                try {
                    // Simulando um pequeno delay para feedback visual
                    Thread.sleep(500);
                    return bilheteServico.processarReembolso(idBilhete, motivo);
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                    return null;
                }
            }
            
            @Override
            protected void done() {
                try {
                    ReembolsoDTO resultado = get();
                    if (resultado != null && resultado.isSucesso()) {
                        atualizarStatus("Reembolso realizado com sucesso");
                        mostrarMensagemSucesso(
                            "Reembolso concluído",
                            "O bilhete foi reembolsado com sucesso.\nValor: " + 
                            new DecimalFormat("R$ #,##0.00").format(resultado.getValorReembolsado())
                        );
                        carregarBilhetes(); // Recarrega para atualizar
                    } else {
                        String mensagemErro = (resultado != null) ? 
                            resultado.getMensagem() : "Erro desconhecido";
                        atualizarStatus("Falha no reembolso: " + mensagemErro);
                        mostrarMensagemErro("Falha ao processar reembolso: " + mensagemErro);
                    }
                } catch (Exception ex) {
                    atualizarStatus("Erro: " + ex.getMessage());
                    ex.printStackTrace();
                    mostrarMensagemErro("Erro ao processar reembolso: " + ex.getMessage());
                } finally {
                    progressBar.setVisible(false);
                    btnReembolsar.setEnabled(true);
                }
            }
        };
        
        worker.execute();
    }
    
    private void mostrarMensagemErro(String mensagem) {
        JLabel label = new JLabel(mensagem);
        label.setFont(new Font("Arial", Font.BOLD, 14));
        label.setForeground(new Color(231, 76, 60));
        
        JOptionPane.showMessageDialog(
            this,
            label,
            "Erro",
            JOptionPane.ERROR_MESSAGE
        );
    }
    
    private void mostrarMensagemSucesso(String titulo, String mensagem) {
        // Painel para a mensagem de sucesso
        JPanel painelSucesso = new JPanel();
        painelSucesso.setLayout(new BoxLayout(painelSucesso, BoxLayout.Y_AXIS));
        painelSucesso.setBorder(new EmptyBorder(20, 20, 20, 20));
        painelSucesso.setBackground(new Color(30, 45, 60));
        
        // Título
        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 18));
        lblTitulo.setForeground(new Color(46, 204, 113));
        lblTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Mensagem
        JLabel lblMensagem = new JLabel("<html><body style='width: 250px; text-align: center'>" + 
                                       mensagem.replace("\n", "<br>") + "</body></html>");
        lblMensagem.setFont(new Font("Arial", Font.PLAIN, 14));
        lblMensagem.setForeground(Color.WHITE);
        lblMensagem.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        painelSucesso.add(lblTitulo);
        painelSucesso.add(Box.createVerticalStrut(15));
        painelSucesso.add(lblMensagem);
        
        JOptionPane.showMessageDialog(
            this,
            painelSucesso,
            "Sucesso",
            JOptionPane.PLAIN_MESSAGE
        );
    }
}