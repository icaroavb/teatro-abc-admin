package com.teatroabc.admin.infraestrutura.ui_swing.util;




import java.awt.Color;

/**
 * Classe utilitária que define constantes visuais para padronização da interface.
 * Inclui cores, dimensões e outras configurações visuais compartilhadas.
 */
public class ConstantesUI {
    // Cores principais
    public static final Color COR_FUNDO_ESCURO = new Color(23, 42, 58);  // Azul escuro (fundo principal)
    public static final Color COR_FUNDO_MEDIO = new Color(34, 49, 63);   // Azul médio (painéis)
    public static final Color COR_FUNDO_CLARO = new Color(245, 245, 245); // Quase branco (conteúdo)
    
    // Cores de destaque
    public static final Color COR_DESTAQUE = new Color(239, 125, 0);     // Laranja (Teatro ABC)
    public static final Color COR_BOTAO_PRIMARIO = new Color(46, 204, 113); // Verde (ações principais)
    public static final Color COR_BOTAO_SECUNDARIO = new Color(52, 152, 219); // Azul (ações secundárias)
    
    // Cores de interface
    public static final Color COR_TEXTO_CLARO = Color.WHITE;             // Texto sobre fundos escuros
    public static final Color COR_TEXTO_ESCURO = new Color(44, 62, 80);  // Texto sobre fundos claros
    public static final Color COR_BORDA = new Color(189, 195, 199);      // Cinza claro (bordas)
    public static final Color COR_TITULO = new Color(52, 73, 94);        // Azul-cinza (cabeçalhos)
    public static final Color COR_MENU = new Color(44, 62, 80);          // Azul-cinza escuro (menu)
    public static final Color COR_STATUS = new Color(34, 49, 63);        // Azul médio (barra de status)
    public static final Color COR_HOVER = new Color(52, 73, 94);         // Efeito hover
    
    // Dimensões padrão
    public static final int LARGURA_JANELA_PADRAO = 1200;
    public static final int ALTURA_JANELA_PADRAO = 700;
    public static final int LARGURA_MENU_LATERAL = 220;
    public static final int ALTURA_BARRA_SUPERIOR = 60;
    public static final int ALTURA_BARRA_STATUS = 25;
    
    // Propriedades de componentes
    public static final int PADDING_PADRAO = 15;
    public static final int ESPACAMENTO_COMPONENTES = 10;
    public static final int ALTURA_BOTAO_PADRAO = 40;
    public static final int RAIO_ARREDONDAMENTO = 5;
    
    // Evita instanciação desta classe utilitária
    private ConstantesUI() {
        throw new AssertionError("A classe de constantes não deve ser instanciada");
    }
}