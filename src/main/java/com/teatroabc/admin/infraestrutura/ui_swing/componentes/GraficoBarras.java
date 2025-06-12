package com.teatroabc.admin.infraestrutura.ui_swing.componentes;

import com.teatroabc.admin.infraestrutura.ui_swing.util.ConstantesUI;

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
 * Suporta renderização de valores numéricos com rótulos.
 */
public class GraficoBarras extends JPanel {
    private Map<String, BigDecimal> dados;
    private String titulo;
    private final DecimalFormat formatoDinheiro = new DecimalFormat("R$ #,##0.00");
    
    private static final int LARGURA_BARRA = 40;
    private static final int ESPACAMENTO = 20;
    private static final int MARGEM_SUPERIOR = 30;
    private static final int MARGEM_INFERIOR = 70;
    private static final int MARGEM_ESQUERDA = 50;
    private static final int MARGEM_DIREITA = 20;
    
    private Color[] cores = {
        new Color(70, 130, 180),   // Azul Aço
        new Color(60, 179, 113),   // Verde Médio do Mar
        new Color(255, 165, 0),    // Laranja
        new Color(186, 85, 211),   // Roxo Médio
        new Color(220, 20, 60),    // Vermelho Carmesim
        new Color(30, 144, 255),   // Azul Dodger
        new Color(255, 215, 0),    // Ouro
        new Color(128, 128, 0),    // Oliva
        new Color(0, 128, 128),    // Verde-azulado
        new Color(255, 20, 147)    // Rosa Profundo
    };
    
    /**
     * Construtor do gráfico de barras.
     */
    public GraficoBarras() {
        this.dados = new HashMap<>();
        this.titulo = "";
        
        setBackground(Color.WHITE);
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
            g2d.setColor(ConstantesUI.COR_TEXTO_ESCURO);
            FontMetrics fm = g2d.getFontMetrics();
            int larguraTitulo = fm.stringWidth(titulo);
            g2d.drawString(titulo, (largura - larguraTitulo) / 2, 20);
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
        
        // Verifica se há dados suficientes para desenhar
        if (alturaGrafico <= 0) {
            g2d.dispose();
            return;
        }
        
        // Calcula a largura total necessária para todas as barras
        int larguraNecessaria = dados.size() * (LARGURA_BARRA + ESPACAMENTO) - ESPACAMENTO;
        int xInicial = Math.max(MARGEM_ESQUERDA, (largura - larguraNecessaria) / 2);
        
        // Desenha os eixos
        desenharEixos(g2d, xInicial, MARGEM_SUPERIOR, larguraNecessaria, alturaGrafico);
        
        // Desenha as barras
        int indice = 0;
        for (Map.Entry<String, BigDecimal> entrada : dados.entrySet()) {
            String chave = entrada.getKey();
            BigDecimal valor = entrada.getValue();
            
            // Seleciona a cor da barra
            Color corBarra = cores[indice % cores.length];
            
            // Calcula a posição e tamanho da barra
            int x = xInicial + indice * (LARGURA_BARRA + ESPACAMENTO);
            int alturaValor = valor.multiply(BigDecimal.valueOf(alturaGrafico))
                    .divide(valorMaximo, 0, BigDecimal.ROUND_DOWN)
                    .intValue();
            
            // Garante altura mínima visível se valor for muito pequeno mas não zero
            if (valor.compareTo(BigDecimal.ZERO) > 0 && alturaValor < 5) {
                alturaValor = 5;
            }
            
            int y = MARGEM_SUPERIOR + alturaGrafico - alturaValor;
            
            // Desenha a barra
            g2d.setColor(corBarra);
            g2d.fillRect(x, y, LARGURA_BARRA, alturaValor);
            
            // Contorno da barra
            g2d.setColor(corBarra.darker());
            g2d.drawRect(x, y, LARGURA_BARRA, alturaValor);
            
            // Desenha o valor acima da barra
            desenharValorDaBarra(g2d, valor, x, y, LARGURA_BARRA);
            
            // Desenha o rótulo abaixo da barra
            desenharRotuloBarra(g2d, chave, x, MARGEM_SUPERIOR + alturaGrafico, LARGURA_BARRA);
            
            indice++;
        }
        
        g2d.dispose();
    }
    
    private void desenharEixos(Graphics2D g2d, int x, int y, int largura, int altura) {
        g2d.setColor(ConstantesUI.COR_TEXTO_ESCURO);
        g2d.setStroke(new BasicStroke(1.5f));
        
        // Eixo Y (vertical)
        g2d.drawLine(x - 5, y, x - 5, y + altura);
        
        // Eixo X (horizontal)
        g2d.drawLine(x - 5, y + altura, x + largura, y + altura);
    }
    
    private void desenharValorDaBarra(Graphics2D g2d, BigDecimal valor, int x, int y, int larguraBarra) {
        g2d.setFont(new Font("Arial", Font.PLAIN, 10));
        String valorFormatado = formatoDinheiro.format(valor);
        
        FontMetrics fm = g2d.getFontMetrics();
        int larguraTexto = fm.stringWidth(valorFormatado);
        
        // Rotaciona o texto se for muito largo para a barra
        if (larguraTexto > larguraBarra * 1.5) {
            g2d.setColor(Color.BLACK);
            
            // Salva a transformação atual
            AffineTransform transformacaoOriginal = g2d.getTransform();
            
            // Rotaciona e posiciona o texto
            g2d.rotate(-Math.PI / 2); // Rotaciona 90 graus no sentido anti-horário
            g2d.drawString(valorFormatado, -y + 5, x + larguraBarra / 2 + fm.getAscent() / 2);
            
            // Restaura a transformação
            g2d.setTransform(transformacaoOriginal);
        } else {
            // Texto horizontal normal
            g2d.setColor(Color.BLACK);
            int xTexto = x + (larguraBarra - larguraTexto) / 2;
            g2d.drawString(valorFormatado, xTexto, y - 5);
        }
    }
    
    private void desenharRotuloBarra(Graphics2D g2d, String rotulo, int x, int y, int larguraBarra) {
        g2d.setFont(new Font("Arial", Font.BOLD, 10));
        g2d.setColor(ConstantesUI.COR_TEXTO_ESCURO);
        
        FontMetrics fm = g2d.getFontMetrics();
        
        // Limita o tamanho do rótulo
        String rotuloAjustado = limitarTexto(rotulo, fm, larguraBarra * 2);
        int larguraTexto = fm.stringWidth(rotuloAjustado);
        
        // Rotaciona o texto se ainda for muito largo
        if (larguraTexto > larguraBarra * 1.5) {
            // Salva a transformação atual
            AffineTransform transformacaoOriginal = g2d.getTransform();
            
            // Rotaciona e posiciona o texto
            g2d.rotate(Math.PI / 4); // Rotaciona 45 graus
            double novoX = (x + larguraBarra / 2) * Math.cos(Math.PI / 4) - (y + 15) * Math.sin(Math.PI / 4);
            double novoY = (x + larguraBarra / 2) * Math.sin(Math.PI / 4) + (y + 15) * Math.cos(Math.PI / 4);
            g2d.drawString(rotuloAjustado, (float)novoX, (float)novoY);
            
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
        g2d.setColor(Color.GRAY);
        
        String mensagem = "Sem dados para exibir";
        FontMetrics fm = g2d.getFontMetrics();
        int larguraMensagem = fm.stringWidth(mensagem);
        
        g2d.drawString(mensagem, (largura - larguraMensagem) / 2, altura / 2);
    }
}