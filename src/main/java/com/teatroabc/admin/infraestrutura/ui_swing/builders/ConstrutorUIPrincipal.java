package com.teatroabc.admin.infraestrutura.ui_swing.builders;

import com.teatroabc.admin.dominio.entidades.Usuario;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;

/**
 * Classe responsável por construir a interface gráfica principal da aplicação.
 * Segue o princípio da responsabilidade única, isolando a lógica de construção
 * da UI da lógica de controle da TelaPrincipalAdmin.
 * Padrão de projeto: Builder.
 */
public class ConstrutorUIPrincipal {

    private final JFrame frame;
    private final Usuario usuarioLogado;
    private final Map<String, ActionListener> acoesMenu;

    // Componentes que podem precisar de acesso externo
    private JPanel painelConteudo;
    private final Map<String, JButton> botoesMenu = new HashMap<>();

    /**
     * Construtor da classe.
     *
     * @param frame          O JFrame principal da aplicação.
     * @param usuarioLogado  O usuário que está logado no sistema.
     * @param acoesMenu      Um mapa contendo o nome do menu e a ação (ActionListener) correspondente.
     */
    public ConstrutorUIPrincipal(JFrame frame, Usuario usuarioLogado, Map<String, ActionListener> acoesMenu) {
        this.frame = frame;
        this.usuarioLogado = usuarioLogado;
        this.acoesMenu = acoesMenu;
    }

    /**
     * Monta o layout completo da janela principal.
     */
    public void construirLayout() {
        // Configuração básica do frame
        frame.setLayout(new BorderLayout());
        frame.getContentPane().setBackground(new Color(23, 42, 58));

        // Criação e adição dos painéis principais
        JPanel painelTitulo = criarPainelTitulo();
        frame.add(painelTitulo, BorderLayout.NORTH);

        JPanel painelMenu = criarPainelMenu();
        frame.add(painelMenu, BorderLayout.WEST);

        // O painel de conteúdo é um campo da classe para ser acessado externamente
        this.painelConteudo = new JPanel(new BorderLayout());
        this.painelConteudo.setBackground(new Color(25, 40, 55));
        frame.add(painelConteudo, BorderLayout.CENTER);

        // A Barra de Status será outra classe, mas por enquanto a criamos aqui.
        // Em uma próxima etapa, isso seria substituído por: frame.add(new BarraStatus(), BorderLayout.SOUTH);
    }

    /**
     * Cria o painel superior (cabeçalho) com o título e informações do usuário.
     *
     * @return O JPanel do cabeçalho configurado.
     */
    private JPanel criarPainelTitulo() {
        JPanel painelTitulo = new JPanel(new BorderLayout());
        painelTitulo.setBackground(new Color(34, 49, 63));
        painelTitulo.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        // Logo e título
        JPanel painelLogoTitulo = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        painelLogoTitulo.setOpaque(false);

        JLabel lblLogo = new JLabel("TEATRO ABC");
        lblLogo.setFont(new Font("Arial", Font.BOLD, 24));
        lblLogo.setForeground(new Color(239, 125, 0));

        JLabel lblSubtitulo = new JLabel("Sistema Administrativo");
        lblSubtitulo.setFont(new Font("Arial", Font.ITALIC, 16));
        lblSubtitulo.setForeground(Color.WHITE);

        painelLogoTitulo.add(lblLogo);
        painelLogoTitulo.add(lblSubtitulo);

        // Informações do usuário e botão de sair
        JPanel painelUsuario = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        painelUsuario.setOpaque(false);

        JLabel lblUsuario = new JLabel("Usuário: " + usuarioLogado.getNome());
        lblUsuario.setFont(new Font("Arial", Font.BOLD, 14));
        lblUsuario.setForeground(Color.WHITE);

        JButton btnSair = new JButton("Sair");
        btnSair.setFont(new Font("Arial", Font.PLAIN, 14));
        btnSair.setBackground(new Color(231, 76, 60));
        btnSair.setForeground(Color.WHITE);
        btnSair.setFocusPainted(false);
        btnSair.setBorder(new LineBorder(new Color(192, 57, 43), 1));
        btnSair.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSair.setPreferredSize(new Dimension(80, 35));
        // A ação de sair será fornecida pelo controlador
        if (acoesMenu.containsKey("Sair")) {
            btnSair.addActionListener(acoesMenu.get("Sair"));
        }

        painelUsuario.add(lblUsuario);
        painelUsuario.add(btnSair);

        painelTitulo.add(painelLogoTitulo, BorderLayout.WEST);
        painelTitulo.add(painelUsuario, BorderLayout.EAST);

        return painelTitulo;
    }

    /**
     * Cria o painel de menu lateral.
     *
     * @return O JPanel do menu configurado.
     */
    private JPanel criarPainelMenu() {
        JPanel painelMenu = new JPanel();
        painelMenu.setLayout(new BoxLayout(painelMenu, BoxLayout.Y_AXIS));
        painelMenu.setBackground(new Color(23, 42, 58));
        painelMenu.setPreferredSize(new Dimension(250, 0));
        painelMenu.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, new Color(60, 80, 100)));

        JLabel lblMenu = new JLabel("MENU");
        lblMenu.setFont(new Font("Arial", Font.BOLD, 18));
        lblMenu.setForeground(Color.WHITE);
        lblMenu.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblMenu.setBorder(BorderFactory.createEmptyBorder(25, 0, 25, 0));
        painelMenu.add(lblMenu);

        // Cria os botões de menu com base nas ações fornecidas
        acoesMenu.forEach((nome, acao) -> {
            if (!"Sair".equals(nome)) { // Ação "Sair" é tratada no cabeçalho
                JButton botao = criarBotaoMenu(nome, acao);
                painelMenu.add(botao);
                botoesMenu.put(nome, botao);
            }
        });

        painelMenu.add(Box.createVerticalGlue());
        return painelMenu;
    }

    /**
     * Método auxiliar para criar um botão de menu estilizado.
     *
     * @param texto  O texto do botão.
     * @param action A ação a ser executada quando o botão for clicado.
     * @return Um JButton estilizado.
     */
    private JButton criarBotaoMenu(String texto, ActionListener action) {
        JButton botao = new JButton(texto);
        botao.setFont(new Font("Arial", Font.BOLD, 16));
        botao.setForeground(Color.WHITE);
        botao.setBackground(new Color(23, 42, 58));
        botao.setBorderPainted(false);
        botao.setFocusPainted(false);
        botao.setHorizontalAlignment(SwingConstants.LEFT);
        botao.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        botao.setCursor(new Cursor(Cursor.HAND_CURSOR));
        botao.setBorder(BorderFactory.createEmptyBorder(15, 25, 15, 25));
        botao.setContentAreaFilled(false);

        botao.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent evt) {
                if (!botao.getBackground().equals(new Color(60, 90, 150))) { // Não altera se estiver ativo
                    botao.setBackground(new Color(34, 49, 63));
                    botao.setContentAreaFilled(true);
                }
            }

            @Override
            public void mouseExited(MouseEvent evt) {
                 if (!botao.getBackground().equals(new Color(60, 90, 150))) {
                    botao.setContentAreaFilled(false);
                }
            }
        });

        botao.addActionListener(action);
        return botao;
    }

    // --- Getters para componentes que precisam ser acessados externamente ---

    /**
     * Retorna o painel onde o conteúdo principal (telas) será exibido.
     * @return O JPanel de conteúdo.
     */
    public JPanel getPainelConteudo() {
        return painelConteudo;
    }

    /**
     * Retorna o mapa de botões do menu para que possam ser manipulados
     * externamente (ex: para destacar o botão ativo).
     * @return Um mapa com os botões do menu.
     */
    public Map<String, JButton> getBotoesMenu() {
        return botoesMenu;
    }
}
