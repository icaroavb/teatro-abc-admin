package com.teatroabc.admin.infraestrutura.ui_swing.telas;

import com.teatroabc.admin.aplicacao.interfaces.IBilheteServico;
import com.teatroabc.admin.dominio.entidades.BilheteVendido;
import com.teatroabc.admin.infraestrutura.ui_swing.componentes.PainelFiltros;
import com.teatroabc.admin.infraestrutura.ui_swing.componentes.TabelaBilhetes;
import com.teatroabc.admin.infraestrutura.ui_swing.controllers.ControladorGerenciamentoBilhetes;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Tela para gerenciamento de bilhetes vendidos. Atua como a "View",
 * delegando a lógica de negócio para o ControladorGerenciamentoBilhetes.
 */
public class TelaGerenciamentoBilhetes extends JPanel {

    private final ControladorGerenciamentoBilhetes controlador;

    private final TabelaBilhetes tabelaBilhetes;
    private final PainelFiltros painelFiltros;
    private final JButton btnAtualizar, btnDetalhes, btnReembolsar;
    private final JLabel lblStatus, lblContagem;
    private final JProgressBar progressBar;

    private final DateTimeFormatter formatoData = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    /**
     * Construtor da tela.
     * @param bilheteServico Serviço de bilhetes injetado.
     */
    public TelaGerenciamentoBilhetes(IBilheteServico bilheteServico) {
        if (bilheteServico == null) {
            throw new IllegalArgumentException("Serviço de bilhetes não pode ser nulo");
        }
        this.controlador = new ControladorGerenciamentoBilhetes(bilheteServico, this);

        // Inicialização dos componentes
        this.tabelaBilhetes = new TabelaBilhetes();
        this.painelFiltros = new PainelFiltros(e -> controlador.aplicarFiltros());
        this.btnAtualizar = new JButton("Atualizar Dados");
        this.btnDetalhes = new JButton("Ver Detalhes");
        this.btnReembolsar = new JButton("Reembolsar");
        this.lblStatus = new JLabel("Pronto");
        this.lblContagem = new JLabel("0 bilhetes encontrados");
        this.progressBar = new JProgressBar();

        // Montagem do layout e configuração das ações
        montarLayout();
        configurarAcoes();

        // Carga inicial
        controlador.carregarBilhetes();
    }

    private void montarLayout() {
        setLayout(new BorderLayout(15, 15));
        setBorder(new EmptyBorder(20, 20, 20, 20));
        setBackground(new Color(25, 40, 55));

        add(criarPainelTitulo(), BorderLayout.NORTH);
        add(painelFiltros, BorderLayout.WEST);
        add(criarPainelCentral(), BorderLayout.CENTER);
        add(criarPainelAcoes(), BorderLayout.SOUTH);
    }
    
    private void configurarAcoes() {
        btnAtualizar.addActionListener(e -> controlador.sincronizarComBanco());
        btnDetalhes.addActionListener(e -> mostrarDetalhesBilhete());
        btnReembolsar.addActionListener(e -> iniciarProcessoReembolso());

        tabelaBilhetes.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                atualizarEstadoBotoes();
            }
        });
    }

    // --- Métodos Públicos para o Controlador ---

    public void setCarregando(boolean carregando) {
        progressBar.setVisible(carregando);
        btnAtualizar.setEnabled(!carregando);
        atualizarEstadoBotoes(); // Atualiza o estado dos botões de ação
    }
    
    public void atualizarTabela(List<BilheteVendido> bilhetes) {
        tabelaBilhetes.atualizarDados(bilhetes);
        lblContagem.setText(bilhetes.size() + " bilhetes encontrados");
    }

    public void atualizarStatus(String mensagem) {
        lblStatus.setText(mensagem);
    }

    public void mostrarErro(String mensagem) {
        JOptionPane.showMessageDialog(this, mensagem, "Erro", JOptionPane.ERROR_MESSAGE);
    }
    
    public void mostrarSucesso(String titulo, String mensagem) {
        JOptionPane.showMessageDialog(this, mensagem, titulo, JOptionPane.INFORMATION_MESSAGE);
    }

    // --- Getters para o Controlador ---
    
    public String getFiltroTermo() { return painelFiltros.getTermoBusca(); }
    public String getFiltroTipo() { return painelFiltros.getTipoBusca(); }
    public boolean getFiltroMostrarReembolsados() { return painelFiltros.getMostrarReembolsados(); }

    // --- Lógica Interna da View ---

    private void atualizarEstadoBotoes() {
        BilheteVendido bilheteSelecionado = tabelaBilhetes.getBilheteSelecionado();
        boolean temSelecao = bilheteSelecionado != null;
        btnDetalhes.setEnabled(temSelecao);
        btnReembolsar.setEnabled(temSelecao && !bilheteSelecionado.isReembolsado());
    }

    private void iniciarProcessoReembolso() {
        BilheteVendido bilhete = tabelaBilhetes.getBilheteSelecionado();
        if (bilhete == null || bilhete.isReembolsado()) return;

        JPanel painelMotivo = new JPanel(new BorderLayout(10, 10));
        painelMotivo.setBorder(new EmptyBorder(10, 10, 10, 10));
        JLabel lblInfo = new JLabel("<html>Deseja realmente reembolsar o bilhete #" + bilhete.getIdIngresso() + "?<br>" +
                                   "Peça: " + bilhete.getNomePeca() + " - Valor: R$" + bilhete.getPreco() + "</html>");
        lblInfo.setFont(new Font("Arial", Font.PLAIN, 14));
        
        JTextArea txtMotivo = new JTextArea(3, 30);
        txtMotivo.setLineWrap(true);
        txtMotivo.setWrapStyleWord(true);
        JScrollPane scrollMotivo = new JScrollPane(txtMotivo);
        
        painelMotivo.add(lblInfo, BorderLayout.NORTH);
        painelMotivo.add(scrollMotivo, BorderLayout.CENTER);
        
        int resultado = JOptionPane.showConfirmDialog(this, painelMotivo, "Confirmar Reembolso", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);

        if (resultado == JOptionPane.OK_OPTION) {
            String motivo = txtMotivo.getText().trim();
            if (motivo.isEmpty()) {
                mostrarErro("É necessário informar um motivo para o reembolso.");
                return;
            }
            controlador.processarReembolso(bilhete.getIdIngresso(), motivo);
        }
    }
    
    private void mostrarDetalhesBilhete() {
        BilheteVendido bilhete = tabelaBilhetes.getBilheteSelecionado();
        if (bilhete == null) return;
        
        JPanel painelDetalhes = new JPanel();
        painelDetalhes.setLayout(new BoxLayout(painelDetalhes, BoxLayout.Y_AXIS));
        painelDetalhes.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        // Adiciona os campos de detalhe
        adicionarCampoDetalhe(painelDetalhes, "ID do Ingresso:", bilhete.getIdIngresso());
        adicionarCampoDetalhe(painelDetalhes, "CPF do Cliente:", bilhete.getCpf());
        adicionarCampoDetalhe(painelDetalhes, "Peça:", bilhete.getNomePeca());
        adicionarCampoDetalhe(painelDetalhes, "Turno:", bilhete.getTurno());
        adicionarCampoDetalhe(painelDetalhes, "Poltrona(s):", bilhete.getNumeroPoltronas());
        adicionarCampoDetalhe(painelDetalhes, "Preço:", new DecimalFormat("R$ #,##0.00").format(bilhete.getPreco()));
        adicionarCampoDetalhe(painelDetalhes, "Status:", bilhete.isReembolsado() ? "REEMBOLSADO" : "Ativo");
        
        if (bilhete.isReembolsado() && bilhete.getDataReembolso() != null) {
            String dataFormatada = bilhete.getDataReembolso().format(formatoData);
            adicionarCampoDetalhe(painelDetalhes, "Data do Reembolso:", dataFormatada);
        }
        JOptionPane.showMessageDialog(this, painelDetalhes, "Detalhes do Bilhete", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void adicionarCampoDetalhe(JPanel painel, String rotulo, String valor) {
        JPanel campoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        campoPanel.add(new JLabel("<html><b>" + rotulo + "</b></html>"));
        campoPanel.add(new JLabel(valor));
        painel.add(campoPanel);
    }
    
    // --- Métodos de Construção da UI (privados) ---

    private JPanel criarPainelTitulo() {
        JPanel painel = new JPanel(new BorderLayout(10, 0));
        painel.setOpaque(false);
        painel.setBorder(new EmptyBorder(0, 0, 15, 0));
        
        JLabel lblTitulo = new JLabel("Gerenciamento de Bilhetes");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 24));
        lblTitulo.setForeground(Color.WHITE);
        
        btnAtualizar.setFont(new Font("Arial", Font.BOLD, 14));
        btnAtualizar.setBackground(new Color(52, 152, 219));
        btnAtualizar.setForeground(Color.WHITE);
        btnAtualizar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        painel.add(lblTitulo, BorderLayout.WEST);
        painel.add(btnAtualizar, BorderLayout.EAST);
        return painel;
    }
    
    private JPanel criarPainelCentral() {
        JPanel painelCentral = new JPanel(new BorderLayout(0, 10));
        painelCentral.setOpaque(false);
        JScrollPane scrollTabela = new JScrollPane(tabelaBilhetes);
        scrollTabela.setBorder(new LineBorder(new Color(60, 80, 120), 1));
        scrollTabela.getViewport().setBackground(new Color(25, 40, 55));
        painelCentral.add(scrollTabela, BorderLayout.CENTER);
        
        JPanel painelInfo = new JPanel(new BorderLayout(10, 0));
        painelInfo.setOpaque(false);
        painelInfo.setBorder(new EmptyBorder(10, 5, 5, 5));
        
        lblContagem.setFont(new Font("Arial", Font.PLAIN, 12));
        lblContagem.setForeground(new Color(180, 190, 200));
        
        JPanel painelStatus = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        painelStatus.setOpaque(false);
        
        lblStatus.setFont(new Font("Arial", Font.PLAIN, 12));
        lblStatus.setForeground(new Color(180, 190, 200));
        
        progressBar.setIndeterminate(true);
        progressBar.setVisible(false);
        progressBar.setPreferredSize(new Dimension(150, 14));
        
        painelStatus.add(lblStatus);
        painelStatus.add(progressBar);
        
        painelInfo.add(lblContagem, BorderLayout.WEST);
        painelInfo.add(painelStatus, BorderLayout.EAST);

        painelCentral.add(painelInfo, BorderLayout.SOUTH);

        return painelCentral;
    }
    
    private JPanel criarPainelAcoes() {
        JPanel painel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        painel.setOpaque(false);
        painel.setBorder(new EmptyBorder(10, 0, 0, 0));

        // Estilização dos botões
        btnDetalhes.setFont(new Font("Arial", Font.PLAIN, 14));
        btnDetalhes.setEnabled(false);
        
        btnReembolsar.setFont(new Font("Arial", Font.BOLD, 14));
        btnReembolsar.setBackground(new Color(46, 204, 113));
        btnReembolsar.setForeground(Color.WHITE);
        btnReembolsar.setEnabled(false);
        
        painel.add(btnDetalhes);
        painel.add(btnReembolsar);
        return painel;
    }
}
