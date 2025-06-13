package com.teatroabc.admin.infraestrutura.ui_swing.telas;

import com.teatroabc.admin.aplicacao.interfaces.IBilheteServico;
import com.teatroabc.admin.aplicacao.interfaces.IEstatisticaServico;
import com.teatroabc.admin.infraestrutura.ui_swing.componentes.GraficoBarras;
import com.teatroabc.admin.infraestrutura.ui_swing.controllers.ControladorEstatisticas;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Tela para visualização de estatísticas de vendas de bilhetes.
 * Atua como a "View" no padrão MVC, delegando a lógica de negócio para o ControladorEstatisticas.
 */
public class TelaEstatisticas extends JPanel {

    private final ControladorEstatisticas controlador;
    private final JLabel lblTotalVendas, lblTotalReembolsos, lblTaxaReembolso, lblQtdBilhetes, lblQtdReembolsados, lblUltimaAtualizacao;
    private final JButton btnAtualizar;
    private final GraficoBarras graficoVendasPorPeca, graficoVendasPorTurno;
    private final JProgressBar progressBar;

    private final DecimalFormat formatoDinheiro = new DecimalFormat("R$ #,##0.00");
    private final DecimalFormat formatoPorcentagem = new DecimalFormat("#.##%");
    private final DecimalFormat formatoNumero = new DecimalFormat("#,##0");

    /**
     * Construtor da tela de estatísticas.
     *
     * @param estatisticaServico Serviço de estatísticas injetado.
     * @param bilheteServico     Serviço de bilhetes injetado.
     */
    public TelaEstatisticas(IEstatisticaServico estatisticaServico, IBilheteServico bilheteServico) {
        if (estatisticaServico == null || bilheteServico == null) {
            throw new IllegalArgumentException("Serviços não podem ser nulos");
        }

        // Instancia o controlador, passando os serviços e a própria view (this)
        this.controlador = new ControladorEstatisticas(estatisticaServico, bilheteServico, this);

        // Inicializa a construção da UI
        setLayout(new BorderLayout(15, 15));
        setBorder(new EmptyBorder(20, 20, 20, 20));
        setBackground(new Color(25, 40, 55));

        // Componentes da UI
        this.btnAtualizar = criarBotao("Atualizar Estatísticas", new Color(52, 152, 219), new Color(41, 128, 185));
        this.lblTotalVendas = new JLabel("R$ 0,00");
        this.lblTotalReembolsos = new JLabel("R$ 0,00");
        this.lblTaxaReembolso = new JLabel("0%");
        this.lblQtdBilhetes = new JLabel("0");
        this.lblQtdReembolsados = new JLabel("0");
        this.lblUltimaAtualizacao = new JLabel("Última atualização: Nunca");
        this.progressBar = new JProgressBar();
        this.graficoVendasPorPeca = new GraficoBarras();
        this.graficoVendasPorTurno = new GraficoBarras();
        
        // Monta o layout
        montarLayout();
        configurarAcoes();

        // Carga inicial dos dados
        controlador.carregarEstatisticas();
    }

    /**
     * Monta o layout principal da tela.
     */
    private void montarLayout() {
        add(criarPainelTitulo(), BorderLayout.NORTH);
        
        JPanel painelCentral = new JPanel(new BorderLayout(0, 15));
        painelCentral.setOpaque(false);
        painelCentral.add(criarPainelIndicadores(), BorderLayout.NORTH);
        painelCentral.add(criarPainelGraficos(), BorderLayout.CENTER);
        add(painelCentral, BorderLayout.CENTER);
        
        add(criarPainelStatus(), BorderLayout.SOUTH);
    }
    
    private void configurarAcoes() {
        btnAtualizar.addActionListener(e -> controlador.carregarEstatisticas());
    }

    // --- Métodos Públicos para o Controlador ---

    /**
     * Atualiza os cartões de indicadores (KPIs) com os novos dados.
     * @param dados Um mapa contendo os dados a serem exibidos.
     */
    @SuppressWarnings("unchecked")
    public void atualizarNumeros(Map<String, Object> dados) {
        BigDecimal totalVendas = (BigDecimal) dados.get("totalVendas");
        BigDecimal totalReembolsos = (BigDecimal) dados.get("totalReembolsos");
        int qtdBilhetes = (Integer) dados.get("qtdBilhetes");
        int qtdReembolsados = (Integer) dados.get("qtdReembolsados");
        double taxaReembolso = qtdBilhetes > 0 ? (double) qtdReembolsados / qtdBilhetes : 0;

        lblTotalVendas.setText(formatoDinheiro.format(totalVendas));
        lblTotalReembolsos.setText(formatoDinheiro.format(totalReembolsos));
        lblTaxaReembolso.setText(formatoPorcentagem.format(taxaReembolso));
        lblQtdBilhetes.setText(formatoNumero.format(qtdBilhetes));
        lblQtdReembolsados.setText(formatoNumero.format(qtdReembolsados));
    }

    /**
     * Atualiza os gráficos de barras com os novos dados.
     * @param dados Um mapa contendo os dados para os gráficos.
     */
    @SuppressWarnings("unchecked")
    public void atualizarGraficos(Map<String, Object> dados) {
        Map<String, BigDecimal> vendasPorPeca = (Map<String, BigDecimal>) dados.get("vendasPorPeca");
        Map<String, BigDecimal> vendasPorTurno = (Map<String, BigDecimal>) dados.get("vendasPorTurno");

        Map<String, BigDecimal> vendasPecaOrdenadas = vendasPorPeca.entrySet().stream()
                .sorted(Map.Entry.<String, BigDecimal>comparingByValue().reversed())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));

        graficoVendasPorPeca.atualizarDados(vendasPecaOrdenadas);
        graficoVendasPorTurno.atualizarDados(vendasPorTurno);
    }

    /**
     * Atualiza a data da última atualização exibida.
     */
    public void atualizarDataAtualizacao() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        lblUltimaAtualizacao.setText("Última atualização: " + sdf.format(new Date()));
    }
    
    /**
     * Controla a visibilidade da barra de progresso.
     * @param carregando true para mostrar a barra, false para esconder.
     */
    public void setCarregando(boolean carregando) {
        progressBar.setVisible(carregando);
        btnAtualizar.setEnabled(!carregando);
    }

    /**
     * Exibe uma mensagem de erro para o usuário.
     * @param mensagem A mensagem de erro a ser exibida.
     */
    public void mostrarErro(String mensagem) {
        JOptionPane.showMessageDialog(this, mensagem, "Erro", JOptionPane.ERROR_MESSAGE);
    }

    // --- Métodos privados para construção da UI ---

    private JPanel criarPainelTitulo() {
        JPanel painel = new JPanel(new BorderLayout(10, 0));
        painel.setOpaque(false);
        painel.setBorder(new EmptyBorder(0, 0, 15, 0));
        JLabel lblTitulo = new JLabel("Estatísticas de Vendas");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 24));
        lblTitulo.setForeground(Color.WHITE);
        painel.add(lblTitulo, BorderLayout.WEST);
        painel.add(btnAtualizar, BorderLayout.EAST);
        return painel;
    }

    private JPanel criarPainelIndicadores() {
        JPanel painel = new JPanel(new GridLayout(1, 5, 15, 0));
        painel.setOpaque(false);
        painel.setBorder(criarBordaTitulada("Indicadores-Chave de Desempenho (KPIs)"));
        painel.add(criarCartaoIndicador("Total de Vendas", lblTotalVendas, new Color(46, 204, 113)));
        painel.add(criarCartaoIndicador("Total de Reembolsos", lblTotalReembolsos, new Color(231, 76, 60)));
        painel.add(criarCartaoIndicador("Taxa de Reembolso", lblTaxaReembolso, new Color(241, 196, 15)));
        painel.add(criarCartaoIndicador("Bilhetes Vendidos", lblQtdBilhetes, new Color(52, 152, 219)));
        painel.add(criarCartaoIndicador("Bilhetes Reembolsados", lblQtdReembolsados, new Color(231, 76, 60)));
        return painel;
    }

    private JPanel criarCartaoIndicador(String titulo, JLabel labelValor, Color cor) {
        JPanel cartao = new JPanel(new BorderLayout(0, 10));
        cartao.setBackground(new Color(34, 49, 63));
        cartao.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(cor, 2, true),
            new EmptyBorder(15, 15, 15, 15)
        ));
        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 14));
        lblTitulo.setForeground(new Color(180, 200, 220));
        lblTitulo.setHorizontalAlignment(SwingConstants.CENTER);
        labelValor.setFont(new Font("Arial", Font.BOLD, 24));
        labelValor.setForeground(cor);
        labelValor.setHorizontalAlignment(SwingConstants.CENTER);
        cartao.add(lblTitulo, BorderLayout.NORTH);
        cartao.add(labelValor, BorderLayout.CENTER);
        return cartao;
    }

    private JPanel criarPainelGraficos() {
        JPanel painel = new JPanel(new GridLayout(1, 2, 15, 0));
        painel.setOpaque(false);
        JPanel painelGraficoPeca = criarPainelGrafico("Vendas por Peça Teatral");
        painelGraficoPeca.add(graficoVendasPorPeca, BorderLayout.CENTER);
        JPanel painelGraficoTurno = criarPainelGrafico("Vendas por Turno");
        painelGraficoTurno.add(graficoVendasPorTurno, BorderLayout.CENTER);
        painel.add(painelGraficoPeca);
        painel.add(painelGraficoTurno);
        return painel;
    }

    private JPanel criarPainelGrafico(String titulo) {
        JPanel painel = new JPanel(new BorderLayout());
        painel.setBackground(new Color(34, 49, 63));
        painel.setBorder(criarBordaTitulada(titulo));
        return painel;
    }

    private JPanel criarPainelStatus() {
        JPanel painel = new JPanel(new BorderLayout(10, 0));
        painel.setOpaque(false);
        painel.setBorder(new EmptyBorder(5, 0, 0, 0));
        lblUltimaAtualizacao.setFont(new Font("Arial", Font.ITALIC, 12));
        lblUltimaAtualizacao.setForeground(new Color(150, 160, 170));
        progressBar.setIndeterminate(true);
        progressBar.setVisible(false);
        progressBar.setPreferredSize(new Dimension(150, 5));
        progressBar.setBorderPainted(false);
        progressBar.setBackground(new Color(40, 50, 60));
        progressBar.setForeground(new Color(46, 204, 113));
        JPanel painelProgress = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        painelProgress.setOpaque(false);
        painelProgress.add(progressBar);
        painel.add(lblUltimaAtualizacao, BorderLayout.WEST);
        painel.add(painelProgress, BorderLayout.EAST);
        return painel;
    }

    private TitledBorder criarBordaTitulada(String titulo) {
        TitledBorder borda = BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(60, 80, 100), 1), titulo
        );
        borda.setTitleFont(new Font("Arial", Font.BOLD, 14));
        borda.setTitleColor(new Color(180, 200, 220));
        return borda;
    }

    private JButton criarBotao(String texto, Color corFundo, Color corBorda) {
        JButton botao = new JButton(texto);
        botao.setFont(new Font("Arial", Font.BOLD, 14));
        botao.setBackground(corFundo);
        botao.setForeground(Color.WHITE);
        botao.setFocusPainted(false);
        botao.setBorder(new LineBorder(corBorda, 1));
        botao.setCursor(new Cursor(Cursor.HAND_CURSOR));
        botao.setPreferredSize(new Dimension(180, 40));
        return botao;
    }
}
