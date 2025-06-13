package com.teatroabc.admin.infraestrutura.ui_swing.componentes;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.*;
import java.util.List;

/**
 * Componente para exibição de gráficos de barras.
 * Versão corrigida com melhor visualização.
 */
public class GraficoBarras extends JPanel {
    private Map<String, BigDecimal> dados;
    private String titulo;
    private final DecimalFormat formatoDinheiro = new DecimalFormat("R$ #,##0.00");
    
    private static final int LARGURA_BARRA = 40;
    private static final int ESPACAMENTO = 20;
    private static final int MARGEM_SUPERIOR = 40;
    private static final int MARGEM_INFERIOR = 70;
    private static final int MARGEM_ESQUERDA = 50;
    private static final int MARGEM_DIREITA = 30;
    
    private final Color[] cores = {
        new Color(52, 152, 219),  // Azul
        new Color(46, 204, 113),  // Verde
        new Color(230, 126, 34),  // Laranja
        new Color(155, 89, 182),  // Roxo
        new Color(231, 76, 60),   // Vermelho
        new Color(241, 196, 15),  // Amarelo
        new Color(26, 188, 156),  // Verde água
        new Color(41, 128, 185),  // Azul escuro
        new Color(39, 174, 96),   // Verde escuro
        new Color(211, 84, 0)     // Laranja escuro
    };
    
    /**
     * Construtor do gráfico de barras.
     */
    public GraficoBarras() {
        this.dados = new HashMap<>();
        this.titulo = "";
        
        setBackground(new Color(34, 49, 63));
        setPreferredSize(new Dimension(400, 300));
    }
    
    /**
     * Define o título do gráfico.
     * @param titulo Título a ser exibido
     */
    public void setTitulo(String titulo) {
        this.titulo = titulo;
        repaint();
    }
    
    /**
     * Atualiza os dados do gráfico.
     * @param dados Mapa com chaves (rótulos) e valores numéricos
     */
    public void atualizarDados(Map<String, BigDecimal> dados) {
        if (dados == null) {
            this.dados = new HashMap<>();
        } else {
            this.dados = new HashMap<>(dados);
        }
        repaint();
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();
        
        // Configurações de renderização
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        
        int largura = getWidth();
        int altura = getHeight();
        
        // Desenha o título se houver
        if (titulo != null && !titulo.isEmpty()) {
            g2d.setFont(new Font("Arial", Font.BOLD, 16));
            g2d.setColor(Color.WHITE);
            FontMetrics fm = g2d.getFontMetrics();
            int larguraTitulo = fm.stringWidth(titulo);
            g2d.drawString(titulo, (largura - larguraTitulo) / 2, 25);
        }
        
        // Se não houver dados, exibe mensagem
        if (dados.isEmpty()) {
            desenharMensagemSemDados(g2d, largura, altura);
            g2d.dispose();
            return;
        }
        
        // Calcula o valor máximo para escala
        BigDecimal valorMaximo = dados.values().stream()
                .max(BigDecimal::compareTo)
                .orElse(BigDecimal.ONE);
        
        // Se o valor máximo for zero, ajusta para 1 para evitar divisão por zero
        if (valorMaximo.compareTo(BigDecimal.ZERO) <= 0) {
            valorMaximo = BigDecimal.ONE;
        }
        
        // Calcula a altura disponível para o gráfico
        int alturaGrafico = altura - MARGEM_SUPERIOR - MARGEM_INFERIOR;
        
        // Verifica se há espaço suficiente para desenhar
        if (alturaGrafico <= 0) {
            g2d.dispose();
            return;
        }
        
        // Desenha os eixos
        desenharEixos(g2d, MARGEM_ESQUERDA, MARGEM_SUPERIOR, alturaGrafico);
        
        // Calcula a largura disponível para todas as barras
        int larguraDisponivel = largura - MARGEM_ESQUERDA - MARGEM_DIREITA;
        
        // Calcula a largura de cada barra, ajustando se necessário
        int numBarras = dados.size();
        int larguraBarra = Math.min(LARGURA_BARRA, (larguraDisponivel - (numBarras - 1) * ESPACAMENTO) / numBarras);
        if (larguraBarra < 10) larguraBarra = 10; // Largura mínima
        
        // Calcula a largura total necessária para todas as barras
        int larguraNecessaria = numBarras * (larguraBarra + ESPACAMENTO) - ESPACAMENTO;
        int xInicial = Math.max(MARGEM_ESQUERDA, (largura - larguraNecessaria) / 2);
        
        // Desenha as barras
        int indice = 0;
        for (Map.Entry<String, BigDecimal> entrada : dados.entrySet()) {
            String chave = entrada.getKey();
            BigDecimal valor = entrada.getValue();
            
            // Seleciona a cor da barra
            Color corBarra = cores[indice % cores.length];
            
            // Calcula a posição e tamanho da barra
            int x = xInicial + indice * (larguraBarra + ESPACAMENTO);
            int alturaValor = valor.multiply(BigDecimal.valueOf(alturaGrafico))
                    .divide(valorMaximo, 0, BigDecimal.ROUND_DOWN)
                    .intValue();
            
            // Garante altura mínima visível se valor for muito pequeno mas não zero
            if (valor.compareTo(BigDecimal.ZERO) > 0 && alturaValor < 5) {
                alturaValor = 5;
            }
            
            int y = MARGEM_SUPERIOR + alturaGrafico - alturaValor;
            
            // Desenha a barra com um gradiente
            GradientPaint gradiente = new GradientPaint(
                x, y, corBarra,
                x, y + alturaValor, corBarra.darker()
            );
            g2d.setPaint(gradiente);
            g2d.fillRect(x, y, larguraBarra, alturaValor);
            
            // Contorno da barra
            g2d.setColor(corBarra.darker());
            g2d.drawRect(x, y, larguraBarra, alturaValor);
            
            // Desenha o valor acima da barra
            desenharValorDaBarra(g2d, valor, x, y, larguraBarra);
            
            // Desenha o rótulo abaixo da barra
            desenharRotuloBarra(g2d, chave, x, MARGEM_SUPERIOR + alturaGrafico, larguraBarra);
            
            indice++;
        }
        
        g2d.dispose();
    }
    
    private void desenharEixos(Graphics2D g2d, int x, int y, int altura) {
        g2d.setColor(new Color(150, 160, 170));
        g2d.setStroke(new BasicStroke(1.0f));
        
        // Eixo Y (vertical)
        g2d.drawLine(x, y, x, y + altura);
        
        // Eixo X (horizontal)
        g2d.drawLine(x, y + altura, getWidth() - MARGEM_DIREITA, y + altura);
        
        // Desenha linhas horizontais de referência
        g2d.setColor(new Color(60, 70, 80));
        for (int i = 1; i <= 4; i++) {
            int yLinha = y + altura - (altura * i / 4);
            g2d.drawLine(x, yLinha, getWidth() - MARGEM_DIREITA, yLinha);
        }
    }
    
    private void desenharValorDaBarra(Graphics2D g2d, BigDecimal valor, int x, int y, int larguraBarra) {
        g2d.setFont(new Font("Arial", Font.PLAIN, 10));
        String valorFormatado = formatoDinheiro.format(valor);
        
        FontMetrics fm = g2d.getFontMetrics();
        int larguraTexto = fm.stringWidth(valorFormatado);
        
        // Rotaciona o texto se for muito largo para a barra
        if (larguraTexto > larguraBarra * 1.5) {
            g2d.setColor(Color.WHITE);
            
            // Salva a transformação atual
            AffineTransform transformacaoOriginal = g2d.getTransform();
            
            // Rotaciona e posiciona o texto
            g2d.rotate(-Math.PI / 2, x + larguraBarra / 2, y - 5); // Rotaciona 90 graus
            g2d.drawString(valorFormatado, x + larguraBarra / 2 - larguraTexto / 2, y - 5);
            
            // Restaura a transformação
            g2d.setTransform(transformacaoOriginal);
        } else {
            // Texto horizontal normal
            g2d.setColor(Color.WHITE);
            int xTexto = x + (larguraBarra - larguraTexto) / 2;
            g2d.drawString(valorFormatado, xTexto, y - 5);
        }
    }
    
    private void desenharRotuloBarra(Graphics2D g2d, String rotulo, int x, int y, int larguraBarra) {
        g2d.setFont(new Font("Arial", Font.BOLD, 11));
        g2d.setColor(new Color(180, 200, 220));
        
        FontMetrics fm = g2d.getFontMetrics();
        
        // Limita o tamanho do rótulo
        String rotuloAjustado = limitarTexto(rotulo, fm, larguraBarra * 2);
        int larguraTexto = fm.stringWidth(rotuloAjustado);
        
        // Rotaciona o texto se ainda for muito largo
        if (larguraTexto > larguraBarra * 1.5) {
            // Salva a transformação atual
            AffineTransform transformacaoOriginal = g2d.getTransform();
            
            // Rotaciona e posiciona o texto em 45 graus
            g2d.rotate(Math.PI / 4, x + larguraBarra / 2, y + 10);
            g2d.drawString(rotuloAjustado, x + larguraBarra / 2 - larguraTexto / 2, y + 10);
            
            // Restaura a transformação
            g2d.setTransform(transformacaoOriginal);
        } else {
            // Texto horizontal normal
            int xTexto = x + (larguraBarra - larguraTexto) / 2;
            g2d.drawString(rotuloAjustado, xTexto, y + 15);
        }
    }
    
    private String limitarTexto(String texto, FontMetrics fm, int larguraMaxima) {
        if (fm.stringWidth(texto) <= larguraMaxima) {
            return texto;
        }
        
        // Limita o texto com "..."
        String resultado = texto;
        while (fm.stringWidth(resultado + "...") > larguraMaxima && resultado.length() > 0) {
            resultado = resultado.substring(0, resultado.length() - 1);
        }
        
        return resultado + "...";
    }
    
    private void desenharMensagemSemDados(Graphics2D g2d, int largura, int altura) {
        g2d.setFont(new Font("Arial", Font.ITALIC, 14));
        g2d.setColor(new Color(150, 160, 170));
        
        String mensagem = "Sem dados para exibir";
        FontMetrics fm = g2d.getFontMetrics();
        int larguraMensagem = fm.stringWidth(mensagem);
        
        g2d.drawString(mensagem, (largura - larguraMensagem) / 2, altura / 2);
    }
}