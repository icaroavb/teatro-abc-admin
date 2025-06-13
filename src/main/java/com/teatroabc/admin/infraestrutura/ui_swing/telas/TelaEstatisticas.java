package com.teatroabc.admin.infraestrutura.ui_swing.telas;

import com.teatroabc.admin.aplicacao.dto.EstatisticaDTO;
import com.teatroabc.admin.aplicacao.interfaces.IBilheteServico;
import com.teatroabc.admin.aplicacao.interfaces.IEstatisticaServico;
import com.teatroabc.admin.dominio.entidades.BilheteVendido;
import com.teatroabc.admin.infraestrutura.ui_swing.componentes.GraficoBarras;
import com.teatroabc.admin.infraestrutura.ui_swing.util.ConstantesUI;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Tela para visualização de estatísticas de vendas de bilhetes.
 * Versão corrigida com design modernizado.
 */
public class TelaEstatisticas extends JPanel {
    private final IEstatisticaServico estatisticaServico;
    private final IBilheteServico bilheteServico;
    
    private JPanel painelNumeros;
    private JPanel painelGraficos;
    private JButton btnAtualizar;
    private GraficoBarras graficoVendasPorPeca;
    private GraficoBarras graficoVendasPorTurno;
    
    private JLabel lblTotalVendas;
    private JLabel lblTotalReembolsos;
    private JLabel lblTaxaReembolso;
    private JLabel lblQtdBilhetes;
    private JLabel lblQtdReembolsados;
    private JLabel lblUltimaAtualizacao;
    private JProgressBar progressBar;
    
    // Formatos para exibição de valores
    private final DecimalFormat formatoDinheiro = new DecimalFormat("R$ #,##0.00");
    private final DecimalFormat formatoPorcentagem = new DecimalFormat("#.##%");
    private final DecimalFormat formatoNumero = new DecimalFormat("#,##0");
    
    /**
     * Construtor da tela de estatísticas.
     * @param estatisticaServico Serviço de estatísticas injetado
     * @param bilheteServico Serviço de bilhetes injetado
     */
    public TelaEstatisticas(IEstatisticaServico estatisticaServico, IBilheteServico bilheteServico) {
        if (estatisticaServico == null || bilheteServico == null) {
            throw new IllegalArgumentException("Serviços não podem ser nulos");
        }
        
        this.estatisticaServico = estatisticaServico;
        this.bilheteServico = bilheteServico;
        
        setLayout(new BorderLayout(15, 15));
        setBorder(new EmptyBorder(20, 20, 20, 20));
        setBackground(new Color(25, 40, 55));
        
        inicializarComponentes();
        configurarAcoes();
        carregarEstatisticas();
    }
    
    private void inicializarComponentes() {
        // Painel de título e botões
        JPanel painelTitulo = criarPainelTitulo();
        add(painelTitulo, BorderLayout.NORTH);
        
        // Painel central
        JPanel painelCentral = new JPanel(new BorderLayout(0, 15));
        painelCentral.setOpaque(false);
        
        // Painel de indicadores
        painelNumeros = criarPainelIndicadores();
        painelCentral.add(painelNumeros, BorderLayout.NORTH);
        
        // Painel de gráficos
        painelGraficos = criarPainelGraficos();
        painelCentral.add(painelGraficos, BorderLayout.CENTER);
        
        add(painelCentral, BorderLayout.CENTER);
        
        // Painel de status
        JPanel painelStatus = criarPainelStatus();
        add(painelStatus, BorderLayout.SOUTH);
    }
    
    private JPanel criarPainelTitulo() {
        JPanel painel = new JPanel(new BorderLayout(10, 0));
        painel.setOpaque(false);
        painel.setBorder(new EmptyBorder(0, 0, 15, 0));
        
        // Título
        JLabel lblTitulo = new JLabel("Estatísticas de Vendas");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 24));
        lblTitulo.setForeground(Color.WHITE);
        
        // Subtítulo
        JLabel lblSubtitulo = new JLabel("Dashboard de desempenho");
        lblSubtitulo.setFont(new Font("Arial", Font.ITALIC, 14));
        lblSubtitulo.setForeground(new Color(180, 180, 180));
        
        JPanel painelTexto = new JPanel(new BorderLayout());
        painelTexto.setOpaque(false);
        painelTexto.add(lblTitulo, BorderLayout.NORTH);
        painelTexto.add(lblSubtitulo, BorderLayout.SOUTH);
        
        // Botão de atualizar
        btnAtualizar = new JButton("Atualizar Estatísticas");
        btnAtualizar.setFont(new Font("Arial", Font.BOLD, 14));
        btnAtualizar.setBackground(new Color(52, 152, 219));
        btnAtualizar.setForeground(Color.WHITE);
        btnAtualizar.setFocusPainted(false);
        btnAtualizar.setBorderPainted(true);
        btnAtualizar.setBorder(new LineBorder(new Color(41, 128, 185), 1));
        btnAtualizar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnAtualizar.setPreferredSize(new Dimension(180, 40));
        
        painel.add(painelTexto, BorderLayout.WEST);
        painel.add(btnAtualizar, BorderLayout.EAST);
        
        return painel;
    }
    
    private JPanel criarPainelIndicadores() {
        // Painel principal com layout de grid
        JPanel painel = new JPanel(new GridLayout(1, 5, 15, 0));
        painel.setOpaque(false);
        painel.setBorder(criarBordaTitulada("Indicadores-Chave de Desempenho (KPIs)"));
        
        // Cartões de indicadores
        painel.add(criarCartaoIndicador("Total de Vendas", "R$ 0,00", new Color(46, 204, 113), lblTotalVendas = new JLabel()));
        painel.add(criarCartaoIndicador("Total de Reembolsos", "R$ 0,00", new Color(231, 76, 60), lblTotalReembolsos = new JLabel()));
        painel.add(criarCartaoIndicador("Taxa de Reembolso", "0%", new Color(241, 196, 15), lblTaxaReembolso = new JLabel()));
        painel.add(criarCartaoIndicador("Bilhetes Vendidos", "0", new Color(52, 152, 219), lblQtdBilhetes = new JLabel()));
        painel.add(criarCartaoIndicador("Bilhetes Reembolsados", "0", new Color(231, 76, 60), lblQtdReembolsados = new JLabel()));
        
        return painel;
    }
    
    private JPanel criarCartaoIndicador(String titulo, String valorInicial, Color cor, JLabel labelValor) {
        JPanel cartao = new JPanel(new BorderLayout(0, 10));
        cartao.setBackground(new Color(34, 49, 63));
        cartao.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(cor, 2, true),
            new EmptyBorder(15, 15, 15, 15)
        ));
        
        // Título
        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 14));
        lblTitulo.setForeground(new Color(180, 200, 220));
        lblTitulo.setHorizontalAlignment(SwingConstants.CENTER);
        
        // Valor
        labelValor.setText(valorInicial);
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
        
        // Gráfico de Vendas por Peça
        JPanel painelGraficoPeca = criarPainelGrafico("Vendas por Peça Teatral");
        graficoVendasPorPeca = new GraficoBarras();
        painelGraficoPeca.add(graficoVendasPorPeca, BorderLayout.CENTER);
        
        // Gráfico de Vendas por Turno
        JPanel painelGraficoTurno = criarPainelGrafico("Vendas por Turno");
        graficoVendasPorTurno = new GraficoBarras();
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
        
        // Data de atualização
        lblUltimaAtualizacao = new JLabel("Última atualização: Nunca");
        lblUltimaAtualizacao.setFont(new Font("Arial", Font.ITALIC, 12));
        lblUltimaAtualizacao.setForeground(new Color(150, 160, 170));
        
        // Barra de progresso
        progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);
        progressBar.setVisible(false);
        progressBar.setPreferredSize(new Dimension(150, 5));
        progressBar.setBorderPainted(false);
        progressBar.setBackground(new Color(40, 50, 60));
        progressBar.setForeground(new Color(46, 204, 113));
        
        JPanel painelProgress = new JPanel(new FlowLayout(FlowLayout.CENTER));
        painelProgress.setOpaque(false);
        painelProgress.add(progressBar);
        
        painel.add(lblUltimaAtualizacao, BorderLayout.WEST);
        painel.add(painelProgress, BorderLayout.CENTER);
        
        return painel;
    }
    
    private TitledBorder criarBordaTitulada(String titulo) {
        TitledBorder borda = BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(60, 80, 100), 1),
            titulo
        );
        borda.setTitleFont(new Font("Arial", Font.BOLD, 14));
        borda.setTitleColor(new Color(180, 200, 220));
        return borda;
    }
    
    private void configurarAcoes() {
        btnAtualizar.addActionListener(e -> carregarEstatisticas());
    }
    
    private void carregarEstatisticas() {
        // Mostra a barra de progresso
        progressBar.setVisible(true);
        btnAtualizar.setEnabled(false);
        
        SwingWorker<Map<String, Object>, Void> worker = new SwingWorker<>() {
            @Override
            protected Map<String, Object> doInBackground() {
                Map<String, Object> resultado = new HashMap<>();
                
                try {
                    // Simular um pequeno delay para feedback visual
                    Thread.sleep(500);
                    
                    // Obter todos os bilhetes
                    List<BilheteVendido> todosBilhetes = bilheteServico.buscarTodos();
                    
                    // Totais básicos
                    BigDecimal totalVendas = bilheteServico.calcularTotalVendas();
                    BigDecimal totalReembolsos = bilheteServico.calcularTotalReembolsos();
                    int bilhetesReembolsados = bilheteServico.contarBilhetesReembolsados();
                    
                    resultado.put("totalVendas", totalVendas);
                    resultado.put("totalReembolsos", totalReembolsos);
                    resultado.put("qtdBilhetes", todosBilhetes.size());
                    resultado.put("qtdReembolsados", bilhetesReembolsados);
                    
                    // Dados para gráficos
                    Map<String, BigDecimal> vendasPorPeca = estatisticaServico.calcularVendasPorPeca();
                    Map<String, BigDecimal> vendasPorTurno = estatisticaServico.calcularVendasPorTurno();
                    
                    resultado.put("vendasPorPeca", vendasPorPeca);
                    resultado.put("vendasPorTurno", vendasPorTurno);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                
                return resultado;
            }
            
            @Override
            protected void done() {
                try {
                    Map<String, Object> resultado = get();
                    atualizarNumeros(resultado);
                    atualizarGraficos(resultado);
                    atualizarDataAtualizacao();
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(
                        TelaEstatisticas.this,
                        "Erro ao carregar estatísticas: " + ex.getMessage(),
                        "Erro",
                        JOptionPane.ERROR_MESSAGE
                    );
                } finally {
                    // Esconde a barra de progresso
                    progressBar.setVisible(false);
                    btnAtualizar.setEnabled(true);
                }
            }
        };
        
        worker.execute();
    }
    
    @SuppressWarnings("unchecked")
    private void atualizarNumeros(Map<String, Object> dados) {
        // Extrair dados
        BigDecimal totalVendas = (BigDecimal) dados.get("totalVendas");
        BigDecimal totalReembolsos = (BigDecimal) dados.get("totalReembolsos");
        int qtdBilhetes = (Integer) dados.get("qtdBilhetes");
        int qtdReembolsados = (Integer) dados.get("qtdReembolsados");
        
        // Calcular taxa de reembolso
        double taxaReembolso = qtdBilhetes > 0 ? (double) qtdReembolsados / qtdBilhetes : 0;
        
        // Atualizar labels com animação de fadeIn
        lblTotalVendas.setText(formatoDinheiro.format(totalVendas));
        lblTotalReembolsos.setText(formatoDinheiro.format(totalReembolsos));
        lblTaxaReembolso.setText(formatoPorcentagem.format(taxaReembolso));
        lblQtdBilhetes.setText(formatoNumero.format(qtdBilhetes));
        lblQtdReembolsados.setText(formatoNumero.format(qtdReembolsados));
    }
    
    @SuppressWarnings("unchecked")
    private void atualizarGraficos(Map<String, Object> dados) {
        // Extrair dados
        Map<String, BigDecimal> vendasPorPeca = (Map<String, BigDecimal>) dados.get("vendasPorPeca");
        Map<String, BigDecimal> vendasPorTurno = (Map<String, BigDecimal>) dados.get("vendasPorTurno");
        
        // Ordenar os dados para uma melhor visualização
        Map<String, BigDecimal> vendasPecaOrdenadas = vendasPorPeca.entrySet().stream()
                .sorted(Map.Entry.<String, BigDecimal>comparingByValue().reversed())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));
        
        // Atualizar gráficos
        graficoVendasPorPeca.atualizarDados(vendasPecaOrdenadas);
        graficoVendasPorTurno.atualizarDados(vendasPorTurno);
    }
    
    private void atualizarDataAtualizacao() {
        Date agora = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        lblUltimaAtualizacao.setText("Última atualização: " + sdf.format(agora));
    }
}