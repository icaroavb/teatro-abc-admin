package com.teatroabc.admin.infraestrutura.ui_swing.telas;

import com.teatroabc.admin.aplicacao.dto.LoginDTO;
import com.teatroabc.admin.aplicacao.interfaces.IAutenticacaoServico;
import com.teatroabc.admin.dominio.entidades.Usuario;
import com.teatroabc.admin.infraestrutura.ui_swing.util.ConstantesUI;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * Tela de login para o sistema administrativo do Teatro ABC.
 * Versão corrigida com design modernizado.
 */
public class TelaLogin extends JFrame {
    private JTextField txtUsuario;
    private JPasswordField txtSenha;
    private JButton btnEntrar;
    private JLabel lblMensagem;
    
    private final IAutenticacaoServico autenticacaoServico;
    
    /**
     * Construtor da tela de login.
     * @param autenticacaoServico Serviço de autenticação injetado
     */
    public TelaLogin(IAutenticacaoServico autenticacaoServico) {
        if (autenticacaoServico == null) {
            throw new IllegalArgumentException("Serviço de autenticação não pode ser nulo");
        }
        this.autenticacaoServico = autenticacaoServico;
        
        configurarJanela();
        inicializarComponentes();
        configurarAcoes();
    }

    private void configurarJanela() {
        setTitle("Teatro ABC - Administração");
        setSize(450, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        
        // Layout principal
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(23, 42, 58));
    }
    
    private void inicializarComponentes() {
        // Painel principal
        JPanel painelPrincipal = new JPanel();
        painelPrincipal.setLayout(new BoxLayout(painelPrincipal, BoxLayout.Y_AXIS));
        painelPrincipal.setBackground(new Color(23, 42, 58));
        painelPrincipal.setBorder(new EmptyBorder(30, 40, 30, 40));
        
        // Logo e título
        JPanel painelTitulo = new JPanel();
        painelTitulo.setLayout(new BoxLayout(painelTitulo, BoxLayout.Y_AXIS));
        painelTitulo.setOpaque(false);
        
        JLabel lblLogo = new JLabel("TEATRO ABC");
        lblLogo.setFont(new Font("Arial", Font.BOLD, 34));
        lblLogo.setForeground(new Color(239, 125, 0));
        lblLogo.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel lblAdmin = new JLabel("Sistema Administrativo");
        lblAdmin.setFont(new Font("Arial", Font.ITALIC, 18));
        lblAdmin.setForeground(Color.WHITE);
        lblAdmin.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        painelTitulo.add(lblLogo);
        painelTitulo.add(Box.createVerticalStrut(5));
        painelTitulo.add(lblAdmin);
        painelTitulo.add(Box.createVerticalStrut(40));
        
        // Campo de usuário
        JPanel painelUsuario = new JPanel();
        painelUsuario.setLayout(new BoxLayout(painelUsuario, BoxLayout.Y_AXIS));
        painelUsuario.setOpaque(false);
        
        JLabel lblUsuario = new JLabel("Usuário");
        lblUsuario.setFont(new Font("Arial", Font.BOLD, 14));
        lblUsuario.setForeground(new Color(180, 200, 220));
        lblUsuario.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        txtUsuario = new JTextField();
        txtUsuario.setFont(new Font("Arial", Font.PLAIN, 14));
        txtUsuario.setForeground(Color.WHITE);
        txtUsuario.setBackground(new Color(40, 60, 85));
        txtUsuario.setCaretColor(Color.WHITE);
        txtUsuario.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(60, 80, 120), 1, true),
            new EmptyBorder(10, 10, 10, 10)
        ));
        txtUsuario.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        
        painelUsuario.add(lblUsuario);
        painelUsuario.add(Box.createVerticalStrut(8));
        painelUsuario.add(txtUsuario);
        
        // Campo de senha
        JPanel painelSenha = new JPanel();
        painelSenha.setLayout(new BoxLayout(painelSenha, BoxLayout.Y_AXIS));
        painelSenha.setOpaque(false);
        painelSenha.setBorder(new EmptyBorder(15, 0, 0, 0));
        
        JLabel lblSenha = new JLabel("Senha");
        lblSenha.setFont(new Font("Arial", Font.BOLD, 14));
        lblSenha.setForeground(new Color(180, 200, 220));
        lblSenha.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        txtSenha = new JPasswordField();
        txtSenha.setFont(new Font("Arial", Font.PLAIN, 14));
        txtSenha.setForeground(Color.WHITE);
        txtSenha.setBackground(new Color(40, 60, 85));
        txtSenha.setCaretColor(Color.WHITE);
        txtSenha.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(60, 80, 120), 1, true),
            new EmptyBorder(10, 10, 10, 10)
        ));
        txtSenha.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        
        painelSenha.add(lblSenha);
        painelSenha.add(Box.createVerticalStrut(8));
        painelSenha.add(txtSenha);
        
        // Botão de login
        JPanel painelBotao = new JPanel(new FlowLayout(FlowLayout.CENTER));
        painelBotao.setOpaque(false);
        painelBotao.setBorder(new EmptyBorder(25, 0, 10, 0));
        
        btnEntrar = new JButton("Entrar");
        btnEntrar.setFont(new Font("Arial", Font.BOLD, 16));
        btnEntrar.setForeground(Color.WHITE);
        btnEntrar.setBackground(new Color(46, 204, 113));
        btnEntrar.setFocusPainted(false);
        btnEntrar.setBorder(BorderFactory.createEmptyBorder(10, 30, 10, 30));
        btnEntrar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnEntrar.setPreferredSize(new Dimension(200, 45));
        
        painelBotao.add(btnEntrar);
        
        // Mensagem de erro
        lblMensagem = new JLabel(" ");
        lblMensagem.setFont(new Font("Arial", Font.ITALIC, 14));
        lblMensagem.setForeground(new Color(231, 76, 60));
        lblMensagem.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Rodapé
        JPanel painelRodape = new JPanel(new FlowLayout(FlowLayout.CENTER));
        painelRodape.setOpaque(false);
        painelRodape.setBorder(new EmptyBorder(20, 0, 0, 0));
        
        JLabel lblDireitos = new JLabel("© 2025 Teatro ABC - Todos os direitos reservados");
        lblDireitos.setFont(new Font("Arial", Font.PLAIN, 12));
        lblDireitos.setForeground(new Color(130, 140, 150));
        
        painelRodape.add(lblDireitos);
        
        // Montagem do painel principal
        painelPrincipal.add(painelTitulo);
        painelPrincipal.add(painelUsuario);
        painelPrincipal.add(painelSenha);
        painelPrincipal.add(painelBotao);
        painelPrincipal.add(lblMensagem);
        painelPrincipal.add(Box.createVerticalStrut(20));
        painelPrincipal.add(painelRodape);
        
        // Adiciona o painel principal ao frame
        add(painelPrincipal, BorderLayout.CENTER);
        
        // Foco inicial no campo de usuário
        SwingUtilities.invokeLater(() -> txtUsuario.requestFocusInWindow());
    }
    
    private void configurarAcoes() {
        // Ação do botão Entrar
        btnEntrar.addActionListener(e -> realizarLogin());
        
        // Ação de pressionar Enter no campo de senha
        txtSenha.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    realizarLogin();
                }
            }
        });
        
        // Ação de pressionar Enter no campo de usuário
        txtUsuario.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    txtSenha.requestFocusInWindow();
                }
            }
        });
    }
    
    private void realizarLogin() {
        String username = txtUsuario.getText().trim();
        String senha = new String(txtSenha.getPassword());
        
        if (username.isEmpty() || senha.isEmpty()) {
            lblMensagem.setText("Preencha todos os campos");
            return;
        }
        
        try {
            // Mostrar feedback de "Autenticando..."
            lblMensagem.setText("Autenticando...");
            lblMensagem.setForeground(new Color(180, 180, 180));
            btnEntrar.setEnabled(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            
            // Usar SwingWorker para não bloquear a interface
            SwingWorker<Usuario, Void> worker = new SwingWorker<>() {
                @Override
                protected Usuario doInBackground() {
                    // Simular um pequeno delay para mostrar o feedback
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException ex) {
                        Thread.currentThread().interrupt();
                    }
                    
                    LoginDTO loginDTO = new LoginDTO(username, senha);
                    return autenticacaoServico.autenticar(loginDTO);
                }
                
                @Override
                protected void done() {
                    try {
                        Usuario usuario = get();
                        if (usuario != null) {
                            abrirTelaPrincipal(usuario);
                        } else {
                            lblMensagem.setText("Usuário ou senha incorretos");
                            lblMensagem.setForeground(new Color(231, 76, 60));
                            txtSenha.setText("");
                            txtSenha.requestFocusInWindow();
                        }
                    } catch (Exception ex) {
                        lblMensagem.setText("Erro ao realizar login: " + ex.getMessage());
                        lblMensagem.setForeground(new Color(231, 76, 60));
                    } finally {
                        btnEntrar.setEnabled(true);
                        setCursor(Cursor.getDefaultCursor());
                    }
                }
            };
            
            worker.execute();
            
        } catch (Exception ex) {
            lblMensagem.setText("Erro ao realizar login: " + ex.getMessage());
            lblMensagem.setForeground(new Color(231, 76, 60));
            System.err.println("Erro de autenticação: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
    
    private void abrirTelaPrincipal(Usuario usuario) {
        SwingUtilities.invokeLater(() -> {
            TelaPrincipalAdmin telaPrincipal = new TelaPrincipalAdmin(usuario);
            telaPrincipal.setVisible(true);
            this.dispose();
        });
    }
    
    // Para testes e desenvolvimento
    public static void main(String[] args) {
        // Mock do serviço de autenticação para testes
        IAutenticacaoServico servicoMock = new IAutenticacaoServico() {
            @Override
            public Usuario autenticar(LoginDTO loginDTO) {
                if ("admin".equals(loginDTO.getUsuario()) && "admin".equals(loginDTO.getSenha())) {
                    return new Usuario("1", "Administrador", "admin", true);
                }
                return null;
            }
            
            @Override
            public boolean validarSessao(String token) {
                return true;
            }
            
            @Override
            public void encerrarSessao(String token) {
                // Não implementado para o teste
            }
        };
        
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            TelaLogin telaLogin = new TelaLogin(servicoMock);
            telaLogin.setVisible(true);
        });
    }
}