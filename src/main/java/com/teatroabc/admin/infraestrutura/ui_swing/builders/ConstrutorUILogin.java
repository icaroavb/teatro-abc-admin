package com.teatroabc.admin.infraestrutura.ui_swing.builders;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;

/**
 * Classe responsável por construir a interface gráfica da tela de login.
 * Segue o SRP ao isolar a lógica de construção da UI da lógica de controle.
 * Padrão de projeto: Builder.
 */
public class ConstrutorUILogin {

    private final JFrame frame;
    private JTextField txtUsuario;
    private JPasswordField txtSenha;
    private JButton btnEntrar;
    private JLabel lblMensagem;

    /**
     * Construtor da classe.
     *
     * @param frame O JFrame que servirá como a janela de login.
     */
    public ConstrutorUILogin(JFrame frame) {
        this.frame = frame;
    }

    /**
     * Monta todos os componentes visuais na janela de login.
     */
    public void construirLayout() {
        // Configuração do Frame
        frame.setTitle("Teatro ABC - Administração");
        frame.setSize(450, 500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setLayout(new BorderLayout());
        frame.getContentPane().setBackground(new Color(23, 42, 58));

        // Painel principal
        JPanel painelPrincipal = new JPanel();
        painelPrincipal.setLayout(new BoxLayout(painelPrincipal, BoxLayout.Y_AXIS));
        painelPrincipal.setBackground(new Color(23, 42, 58));
        painelPrincipal.setBorder(new EmptyBorder(30, 40, 30, 40));

        // Adiciona os sub-painéis
        painelPrincipal.add(criarPainelTitulo());
        painelPrincipal.add(criarPainelUsuario());
        painelPrincipal.add(criarPainelSenha());
        painelPrincipal.add(criarPainelBotao());

        // Label para mensagens de erro/status
        lblMensagem = new JLabel(" ", SwingConstants.CENTER); // Inicializa com espaço para manter a altura
        lblMensagem.setFont(new Font("Arial", Font.ITALIC, 14));
        lblMensagem.setForeground(new Color(231, 76, 60));
        lblMensagem.setAlignmentX(Component.CENTER_ALIGNMENT);
        painelPrincipal.add(lblMensagem);
        
        painelPrincipal.add(Box.createVerticalGlue()); // Empurra o rodapé para baixo
        painelPrincipal.add(criarPainelRodape());

        frame.add(painelPrincipal, BorderLayout.CENTER);
    }

    private JPanel criarPainelTitulo() {
        JPanel painel = new JPanel();
        painel.setLayout(new BoxLayout(painel, BoxLayout.Y_AXIS));
        painel.setOpaque(false);
        painel.setBorder(new EmptyBorder(0, 0, 40, 0));

        JLabel lblLogo = new JLabel("TEATRO ABC");
        lblLogo.setFont(new Font("Arial", Font.BOLD, 34));
        lblLogo.setForeground(new Color(239, 125, 0));
        lblLogo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblAdmin = new JLabel("Sistema Administrativo");
        lblAdmin.setFont(new Font("Arial", Font.ITALIC, 18));
        lblAdmin.setForeground(Color.WHITE);
        lblAdmin.setAlignmentX(Component.CENTER_ALIGNMENT);

        painel.add(lblLogo);
        painel.add(Box.createVerticalStrut(5));
        painel.add(lblAdmin);
        return painel;
    }

    private JPanel criarPainelUsuario() {
        JPanel painel = new JPanel();
        painel.setLayout(new BoxLayout(painel, BoxLayout.Y_AXIS));
        painel.setOpaque(false);

        JLabel lblUsuario = new JLabel("Usuário");
        lblUsuario.setFont(new Font("Arial", Font.BOLD, 14));
        lblUsuario.setForeground(new Color(180, 200, 220));
        lblUsuario.setAlignmentX(Component.LEFT_ALIGNMENT);

        txtUsuario = new JTextField();
        estilizarCampoTexto(txtUsuario);
        
        painel.add(lblUsuario);
        painel.add(Box.createVerticalStrut(8));
        painel.add(txtUsuario);
        return painel;
    }

    private JPanel criarPainelSenha() {
        JPanel painel = new JPanel();
        painel.setLayout(new BoxLayout(painel, BoxLayout.Y_AXIS));
        painel.setOpaque(false);
        painel.setBorder(new EmptyBorder(15, 0, 0, 0));

        JLabel lblSenha = new JLabel("Senha");
        lblSenha.setFont(new Font("Arial", Font.BOLD, 14));
        lblSenha.setForeground(new Color(180, 200, 220));
        lblSenha.setAlignmentX(Component.LEFT_ALIGNMENT);

        txtSenha = new JPasswordField();
        estilizarCampoTexto(txtSenha);

        painel.add(lblSenha);
        painel.add(Box.createVerticalStrut(8));
        painel.add(txtSenha);
        return painel;
    }

    private JPanel criarPainelBotao() {
        JPanel painel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        painel.setOpaque(false);
        painel.setBorder(new EmptyBorder(25, 0, 10, 0));
        
        btnEntrar = new JButton("Entrar");
        btnEntrar.setFont(new Font("Arial", Font.BOLD, 16));
        btnEntrar.setForeground(Color.WHITE);
        btnEntrar.setBackground(new Color(46, 204, 113));
        btnEntrar.setFocusPainted(false);
        btnEntrar.setBorder(BorderFactory.createEmptyBorder(10, 30, 10, 30));
        btnEntrar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnEntrar.setPreferredSize(new Dimension(200, 45));
        
        painel.add(btnEntrar);
        return painel;
    }

    private JPanel criarPainelRodape() {
        JPanel painel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        painel.setOpaque(false);
        painel.setBorder(new EmptyBorder(20, 0, 0, 0));

        JLabel lblDireitos = new JLabel("© 2025 Teatro ABC - Todos os direitos reservados");
        lblDireitos.setFont(new Font("Arial", Font.PLAIN, 12));
        lblDireitos.setForeground(new Color(130, 140, 150));
        
        painel.add(lblDireitos);
        return painel;
    }
    
    private void estilizarCampoTexto(JTextField campo) {
        campo.setFont(new Font("Arial", Font.PLAIN, 14));
        campo.setForeground(Color.WHITE);
        campo.setBackground(new Color(40, 60, 85));
        campo.setCaretColor(Color.WHITE);
        campo.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(60, 80, 120), 1, true),
            new EmptyBorder(10, 10, 10, 10)
        ));
        campo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
    }

    // --- Getters para componentes que o Controlador precisará acessar ---
    public JTextField getTxtUsuario() { return txtUsuario; }
    public JPasswordField getTxtSenha() { return txtSenha; }
    public JButton getBtnEntrar() { return btnEntrar; }
    public JLabel getLblMensagem() { return lblMensagem; }
}
