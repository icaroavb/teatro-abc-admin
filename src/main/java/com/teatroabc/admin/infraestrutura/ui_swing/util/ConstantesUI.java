package com.teatroabc.admin.infraestrutura.ui_swing.util;

import java.awt.Color;

/**
 * Classe utilitária que define constantes visuais para padronização da interface.
 * Versão modernizada com paleta de cores mais atraente e consistente.
 */
public class ConstantesUI {
    // Cores principais (tema escuro moderno)
    public static final Color COR_FUNDO_ESCURO = new Color(18, 30, 43);  // Azul muito escuro (fundo principal)
    public static final Color COR_FUNDO_MEDIO = new Color(25, 40, 55);   // Azul escuro (painéis e cards)
    public static final Color COR_FUNDO_CLARO = new Color(34, 49, 63);   // Azul médio (elementos interativos)
    public static final Color COR_FUNDO_COMPONENTE = new Color(40, 60, 85); // Azul médio-claro (campos, botões)
    
    // Cores de destaque e accent
    public static final Color COR_DESTAQUE = new Color(239, 132, 25);     // Laranja (Teatro ABC)
    public static final Color COR_BOTAO_PRIMARIO = new Color(46, 204, 113); // Verde (ações principais)
    public static final Color COR_BOTAO_SECUNDARIO = new Color(52, 152, 219); // Azul (ações secundárias)
    public static final Color COR_ERRO = new Color(231, 76, 60);         // Vermelho (alertas e erros)
    public static final Color COR_AVISO = new Color(241, 196, 15);       // Amarelo (avisos)
    
    // Cores de texto
    public static final Color COR_TEXTO_CLARO = Color.WHITE;             // Texto principal
    public static final Color COR_TEXTO_SECUNDARIO = new Color(180, 200, 220); // Texto secundário
    public static final Color COR_TEXTO_TERCIARIO = new Color(130, 150, 170); // Texto desabilitado
    public static final Color COR_TEXTO_ESCURO = new Color(50, 60, 70);  // Texto sobre fundos claros
    
    // Cores de interface
    public static final Color COR_BORDA = new Color(60, 80, 120);        // Bordas de elementos
    public static final Color COR_BORDA_CLARA = new Color(90, 110, 140); // Bordas de elementos com foco
    public static final Color COR_SOMBRA = new Color(0, 0, 0, 40);       // Sombras
    public static final Color COR_OVERLAY = new Color(10, 20, 30, 150);  // Overlay para modais
    
    // Gradientes (definidos como arrays para facilitar o uso)
    public static final Color[] GRADIENTE_PRIMARIO = {
        new Color(34, 49, 63),    // Início
        new Color(18, 30, 43)     // Fim
    };
    
    public static final Color[] GRADIENTE_SECUNDARIO = {
        new Color(41, 128, 185),  // Início
        new Color(52, 152, 219)   // Fim
    };
    
    // Estados de hover
    public static final Color COR_HOVER = new Color(60, 90, 150);         // Efeito hover
    public static final Color COR_SELECIONADO = new Color(60, 90, 150);   // Item selecionado
    
    // Dimensões padrão
    public static final int LARGURA_JANELA_PADRAO = 1280;
    public static final int ALTURA_JANELA_PADRAO = 800;
    public static final int LARGURA_MENU_LATERAL = 250;
    public static final int ALTURA_BARRA_SUPERIOR = 70;
    public static final int ALTURA_BARRA_STATUS = 30;
    
    // Propriedades de componentes
    public static final int PADDING_PADRAO = 20;
    public static final int ESPACAMENTO_COMPONENTES = 15;
    public static final int ALTURA_BOTAO_PADRAO = 45;
    public static final int RAIO_ARREDONDAMENTO = 10;
    public static final int ALTURA_LINHA_TABELA = 40;
    
    // Evita instanciação desta classe utilitária
    private ConstantesUI() {
        throw new AssertionError("A classe de constantes não deve ser instanciada");
    }
    
    /**
     * Retorna uma versão mais clara da cor fornecida.
     * @param cor Cor base
     * @param fator Fator de clareamento (0.0 a 1.0)
     * @return Cor mais clara
     */
    public static Color clarear(Color cor, float fator) {
        int r = cor.getRed();
        int g = cor.getGreen();
        int b = cor.getBlue();
        
        r = Math.min(255, r + (int)((255 - r) * fator));
        g = Math.min(255, g + (int)((255 - g) * fator));
        b = Math.min(255, b + (int)((255 - b) * fator));
        
        return new Color(r, g, b);
    }
    
    /**
     * Retorna uma versão mais escura da cor fornecida.
     * @param cor Cor base
     * @param fator Fator de escurecimento (0.0 a 1.0)
     * @return Cor mais escura
     */
    public static Color escurecer(Color cor, float fator) {
        int r = cor.getRed();
        int g = cor.getGreen();
        int b = cor.getBlue();
        
        r = Math.max(0, r - (int)(r * fator));
        g = Math.max(0, g - (int)(g * fator));
        b = Math.max(0, b - (int)(b * fator));
        
        return new Color(r, g, b);
    }
}