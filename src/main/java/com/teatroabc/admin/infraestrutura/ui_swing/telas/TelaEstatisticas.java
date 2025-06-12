package com.teatroabc.admin.infraestrutura.ui_swing.telas;



import com.teatroabc.admin.aplicacao.dto.EstatisticaDTO;
import com.teatroabc.admin.aplicacao.interfaces.IBilheteServico;
import com.teatroabc.admin.aplicacao.interfaces.IEstatisticaServico;
import com.teatroabc.admin.dominio.entidades.BilheteVendido;
import com.teatroabc.admin.infraestrutura.ui_swing.componentes.GraficoBarras;
import com.teatroabc.admin.infraestrutura.ui_swing.util.ConstantesUI;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Tela para visualização de estatísticas de vendas de bilhetes.
 * Exibe gráficos e indicadores-chave sobre vendas e reembolsos.
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
        
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        setBackground(ConstantesUI.COR_FUNDO_MEDIO);
        
        inicializarComponentes();
        configurarAcoes();
        carregarEstatisticas();
    }
    
    private void inicializarComponentes() {
        // Título da tela
        JPanel painelTitulo = new JPanel(new BorderLayout());
        painelTitulo.setOpaque(false);
        painelTitulo.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        
        JLabel lblTitulo = new JLabel("Estatísticas de Vendas");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 22));
        lblTitulo.setForeground(ConstantesUI.COR_TEXTO_CLARO);
        
        btnAtualizar = new JButton("Atualizar Estatísticas");
        btnAtualizar.setFont(new Font("Arial", Font.PLAIN, 14));
        btnAtualizar.setBackground(ConstantesUI.COR_BOTAO_SECUNDARIO);
        btnAtualizar.setForeground(Color.WHITE);
        btnAtualizar.setFocusPainted(false);
        btnAtualizar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        painelTitulo.add(lblTitulo, BorderLayout.WEST);
        painelTitulo.add(btnAtualizar, BorderLayout.EAST);
        
        // Painel de números (KPIs)
        painelNumeros = criarPainelNumeros();
        
        // Painel de gráficos
        painelGraficos = criarPainelGraficos();
        
        // Painel de data de atualização
        JPanel painelAtualizacao = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        painelAtualizacao.setOpaque(false);
        
        lblUltimaAtualizacao = new JLabel("Última atualização: Nunca");
        lblUltimaAtualizacao.setFont(new Font("Arial", Font.ITALIC, 12));
        lblUltimaAtualizacao.setForeground(ConstantesUI.COR_TEXTO_CLARO);
        
        painelAtualizacao.add(lblUltimaAtualizacao);
        
        // Layout principal
        JPanel painelCentral = new JPanel(new GridBagLayout());
        painelCentral.setOpaque(false);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 0.3;
        painelCentral.add(painelNumeros, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 0.7;
        painelCentral.add(painelGraficos, gbc);
        
        // Montagem final
        add(painelTitulo, BorderLayout.NORTH);
        add(painelCentral, BorderLayout.CENTER);
        add(painelAtualizacao, BorderLayout.SOUTH);
    }
    
    private JPanel criarPainelNumeros() {
        JPanel painel = new JPanel(new GridLayout(1, 5, 10, 0));
        painel.setOpaque(false);
        painel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(ConstantesUI.COR_BORDA),
            "Indicadores",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 14),
            ConstantesUI.COR_TEXTO_CLARO
        ));
        
        // Cartões de indicadores
        painel.add(criarCartaoIndicador("Total de Vendas", "R$ 0,00", ConstantesUI.COR_DESTAQUE, lblTotalVendas = new JLabel()));
        painel.add(criarCartaoIndicador("Total de Reembolsos", "R$ 0,00", new Color(217, 83, 79), lblTotalReembolsos = new JLabel()));
        painel.add(criarCartaoIndicador("Taxa de Reembolso", "0%", new Color(240, 173, 78), lblTaxaReembolso = new JLabel()));
        painel.add(criarCartaoIndicador("Bilhetes Vendidos", "0", new Color(91, 192, 222), lblQtdBilhetes = new JLabel()));
        painel.add(criarCartaoIndicador("Bilhetes Reembolsados", "0", new Color(217, 83, 79), lblQtdReembolsados = new JLabel()));
        
        return painel;
    }
    
    private JPanel criarCartaoIndicador(String titulo, String valorInicial, Color cor, JLabel labelValor) {
        JPanel cartao = new JPanel();
        cartao.setLayout(new BoxLayout(cartao, BoxLayout.Y_AXIS));
        cartao.setBackground(ConstantesUI.COR_FUNDO_CLARO);
        cartao.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(cor, 1),
            BorderFactory.createEmptyBorder(15, 10, 15, 10)
        ));
        
        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 14));
        lblTitulo.setForeground(ConstantesUI.COR_TEXTO_ESCURO);
        lblTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        labelValor.setText(valorInicial);
        labelValor.setFont(new Font("Arial", Font.BOLD, 24));
        labelValor.setForeground(cor);
        labelValor.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        cartao.add(lblTitulo);
        cartao.add(Box.createVerticalStrut(10));
        cartao.add(labelValor);
        
        return cartao;
    }
    
    private JPanel criarPainelGraficos() {
        JPanel painel = new JPanel(new GridLayout(1, 2, 10, 0));
        painel.setOpaque(false);
        
        // Gráfico de Vendas por Peça
        JPanel painelGraficoPeca = new JPanel(new BorderLayout());
        painelGraficoPeca.setBackground(ConstantesUI.COR_FUNDO_CLARO);
        painelGraficoPeca.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(ConstantesUI.COR_BORDA),
            "Vendas por Peça",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 14),
            ConstantesUI.COR_TEXTO_ESCURO
        ));
        
        graficoVendasPorPeca = new GraficoBarras();
        painelGraficoPeca.add(graficoVendasPorPeca, BorderLayout.CENTER);
        
        // Gráfico de Vendas por Turno
        JPanel painelGraficoTurno = new JPanel(new BorderLayout());
        painelGraficoTurno.setBackground(ConstantesUI.COR_FUNDO_CLARO);
        painelGraficoTurno.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(ConstantesUI.COR_BORDA),
            "Vendas por Turno",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 14),
            ConstantesUI.COR_TEXTO_ESCURO
        ));
        
        graficoVendasPorTurno = new GraficoBarras();
        painelGraficoTurno.add(graficoVendasPorTurno, BorderLayout.CENTER);
        
        painel.add(painelGraficoPeca);
        painel.add(painelGraficoTurno);
        
        return painel;
    }
    
    private void configurarAcoes() {
        btnAtualizar.addActionListener(e -> carregarEstatisticas());
    }
    
    private void carregarEstatisticas() {
        SwingWorker<Map<String, Object>, Void> worker = new SwingWorker<>() {
            @Override
            protected Map<String, Object> doInBackground() {
                Map<String, Object> resultado = new HashMap<>();
                
                // Obter todos os bilhetes (usaremos o bilheteServico diretamente para simplicidade)
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
                Map<String, BigDecimal> vendasPorPeca = calcularVendasPorPeca(todosBilhetes);
                Map<String, BigDecimal> vendasPorTurno = calcularVendasPorTurno(todosBilhetes);
                
                resultado.put("vendasPorPeca", vendasPorPeca);
                resultado.put("vendasPorTurno", vendasPorTurno);
                
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
                }
            }
        };
        
        worker.execute();
    }
    
    @SuppressWarnings("unchecked")
    private void atualizarNumeros(Map<String, Object> dados) {
        // Formatadores
        DecimalFormat formatoDinheiro = new DecimalFormat("R$ #,##0.00");
        DecimalFormat formatoPorcentagem = new DecimalFormat("#.##%");
        DecimalFormat formatoNumero = new DecimalFormat("#,##0");
        
        // Extrair dados
        BigDecimal totalVendas = (BigDecimal) dados.get("totalVendas");
        BigDecimal totalReembolsos = (BigDecimal) dados.get("totalReembolsos");
        int qtdBilhetes = (Integer) dados.get("qtdBilhetes");
        int qtdReembolsados = (Integer) dados.get("qtdReembolsados");
        
        // Calcular taxa de reembolso
        double taxaReembolso = qtdBilhetes > 0 ? (double) qtdReembolsados / qtdBilhetes : 0;
        
        // Atualizar labels
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
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        lblUltimaAtualizacao.setText("Última atualização: " + sdf.format(agora));
    }
    
    private Map<String, BigDecimal> calcularVendasPorPeca(List<BilheteVendido> bilhetes) {
        Map<String, BigDecimal> resultado = new HashMap<>();
        
        for (BilheteVendido bilhete : bilhetes) {
            if (bilhete.isReembolsado()) {
                continue; // Ignora bilhetes reembolsados para esta análise
            }
            
            String nomePeca = bilhete.getNomePeca();
            BigDecimal valorAtual = resultado.getOrDefault(nomePeca, BigDecimal.ZERO);
            resultado.put(nomePeca, valorAtual.add(bilhete.getPreco()));
        }
        
        return resultado;
    }
    
    private Map<String, BigDecimal> calcularVendasPorTurno(List<BilheteVendido> bilhetes) {
        Map<String, BigDecimal> resultado = new HashMap<>();
        
        for (BilheteVendido bilhete : bilhetes) {
            if (bilhete.isReembolsado()) {
                continue; // Ignora bilhetes reembolsados para esta análise
            }
            
            String turno = bilhete.getTurno();
            BigDecimal valorAtual = resultado.getOrDefault(turno, BigDecimal.ZERO);
            resultado.put(turno, valorAtual.add(bilhete.getPreco()));
        }
        
        return resultado;
    }
}