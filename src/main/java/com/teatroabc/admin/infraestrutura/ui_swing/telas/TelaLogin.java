package com.teatroabc.admin.infraestrutura.ui_swing.telas;

import com.teatroabc.admin.aplicacao.dto.LoginDTO;
import com.teatroabc.admin.aplicacao.interfaces.IAutenticacaoServico;
import com.teatroabc.admin.dominio.entidades.Usuario;
import com.teatroabc.admin.infraestrutura.ui_swing.util.ConstantesUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * Tela de login para o sistema administrativo do Teatro ABC.
 * Permite que usuários autorizados acessem o sistema.
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
        setSize(450, 350);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        
        // Layout principal
        setLayout(new BorderLayout());
        getContentPane().setBackground(ConstantesUI.COR_FUNDO_ESCURO);
    }
    
    private void inicializarComponentes() {
        // Painel superior com logo
        JPanel painelLogo = new JPanel(new FlowLayout(FlowLayout.CENTER));
        painelLogo.setBackground(ConstantesUI.COR_FUNDO_ESCURO);
        painelLogo.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));
        
        // Logo do teatro
        JLabel lblLogo = new JLabel("TEATRO ABC");
        lblLogo.setFont(new Font("Arial", Font.BOLD, 28));
        lblLogo.setForeground(ConstantesUI.COR_DESTAQUE);
        painelLogo.add(lblLogo);
        
        // Subtítulo
        JLabel lblAdmin = new JLabel("Sistema Administrativo");
        lblAdmin.setFont(new Font("Arial", Font.ITALIC, 16));
        lblAdmin.setForeground(Color.WHITE);
        painelLogo.add(lblAdmin);
        
        // Painel de formulário
        JPanel painelFormulario = new JPanel();
        painelFormulario.setLayout(new BoxLayout(painelFormulario, BoxLayout.Y_AXIS));
        painelFormulario.setBackground(ConstantesUI.COR_FUNDO_ESCURO);
        painelFormulario.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));
        
        // Campo de usuário
        JLabel lblUsuario = new JLabel("Usuário:");
        lblUsuario.setForeground(Color.WHITE);
        lblUsuario.setFont(new Font("Arial", Font.BOLD, 14));
        lblUsuario.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        txtUsuario = new JTextField(15);
        txtUsuario.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        txtUsuario.setFont(new Font("Arial", Font.PLAIN, 14));
        txtUsuario.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ConstantesUI.COR_BORDA, 1),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        
        // Campo de senha
        JLabel lblSenha = new JLabel("Senha:");
        lblSenha.setForeground(Color.WHITE);
        lblSenha.setFont(new Font("Arial", Font.BOLD, 14));
        lblSenha.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        txtSenha = new JPasswordField(15);
        txtSenha.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        txtSenha.setFont(new Font("Arial", Font.PLAIN, 14));
        txtSenha.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ConstantesUI.COR_BORDA, 1),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        
        // Botão de login
        btnEntrar = new JButton("Entrar");
        btnEntrar.setFont(new Font("Arial", Font.BOLD, 14));
        btnEntrar.setBackground(ConstantesUI.COR_BOTAO_PRIMARIO);
        btnEntrar.setForeground(Color.WHITE);
        btnEntrar.setFocusPainted(false);
        btnEntrar.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        btnEntrar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnEntrar.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Mensagem de erro
        lblMensagem = new JLabel(" ");
        lblMensagem.setForeground(Color.RED);
        lblMensagem.setFont(new Font("Arial", Font.ITALIC, 12));
        lblMensagem.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Montagem do formulário
        painelFormulario.add(lblUsuario);
        painelFormulario.add(Box.createVerticalStrut(5));
        painelFormulario.add(txtUsuario);
        painelFormulario.add(Box.createVerticalStrut(15));
        painelFormulario.add(lblSenha);
        painelFormulario.add(Box.createVerticalStrut(5));
        painelFormulario.add(txtSenha);
        painelFormulario.add(Box.createVerticalStrut(25));
        painelFormulario.add(btnEntrar);
        painelFormulario.add(Box.createVerticalStrut(15));
        painelFormulario.add(lblMensagem);
        
        // Rodapé
        JPanel painelRodape = new JPanel(new FlowLayout(FlowLayout.CENTER));
        painelRodape.setBackground(ConstantesUI.COR_FUNDO_ESCURO);
        painelRodape.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        
        JLabel lblDireitos = new JLabel("© 2025 Teatro ABC - Todos os direitos reservados");
        lblDireitos.setForeground(Color.LIGHT_GRAY);
        lblDireitos.setFont(new Font("Arial", Font.PLAIN, 10));
        painelRodape.add(lblDireitos);
        
        // Adiciona os painéis ao frame
        add(painelLogo, BorderLayout.NORTH);
        add(painelFormulario, BorderLayout.CENTER);
        add(painelRodape, BorderLayout.SOUTH);
    }
    
    private void configurarAcoes() {
        // Ação do botão Entrar
        btnEntrar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                realizarLogin();
            }
        });
        
        // Ação de pressionar Enter no campo de senha
        txtSenha.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    realizarLogin();
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
            LoginDTO loginDTO = new LoginDTO(username, senha);
            Usuario usuario = autenticacaoServico.autenticar(loginDTO);
            
            if (usuario != null) {
                abrirTelaPrincipal(usuario);
            } else {
                lblMensagem.setText("Usuário ou senha incorretos");
                txtSenha.setText("");
            }
            
        } catch (Exception ex) {
            lblMensagem.setText("Erro ao realizar login: " + ex.getMessage());
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
                // TODO Auto-generated method stub
                throw new UnsupportedOperationException("Unimplemented method 'encerrarSessao'");
            }
        };
        
        SwingUtilities.invokeLater(() -> {
            TelaLogin telaLogin = new TelaLogin(servicoMock);
            telaLogin.setVisible(true);
        });
    }
}