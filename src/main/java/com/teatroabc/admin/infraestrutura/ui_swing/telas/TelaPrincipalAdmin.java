package com.teatroabc.admin.infraestrutura.ui_swing.telas;

import com.teatroabc.admin.aplicacao.interfaces.IAutenticacaoServico;
import com.teatroabc.admin.aplicacao.interfaces.IBilheteServico;
import com.teatroabc.admin.aplicacao.interfaces.IEstatisticaServico;
import com.teatroabc.admin.dominio.entidades.Usuario;
import com.teatroabc.admin.infraestrutura.ui_swing.util.ConstantesUI;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Tela principal do sistema administrativo do Teatro ABC.
 * Versão corrigida com design modernizado.
 */
public class TelaPrincipalAdmin extends JFrame {
    private final Usuario usuarioLogado;
    
    private JPanel painelConteudo;
    private JPanel painelAtual;
    private JLabel lblStatus;
    private JLabel lblHora;
    private Timer timerStatus;
    
    private IBilheteServico bilheteServico;
    private IEstatisticaServico estatisticaServico;
    
    // Botões do menu
    private JButton btnEstatisticas;
    private JButton btnBilhetes;
    
    /**
     * Construtor da tela principal.
     * @param usuario Usuário autenticado
     */
    public TelaPrincipalAdmin(Usuario usuario) {
        this.usuarioLogado = usuario;
        
        // Obter serviços do contexto de aplicação
        this.bilheteServico = ServiceLocator.getBilheteServico();
        this.estatisticaServico = ServiceLocator.getEstatisticaServico();
        
        configurarJanela();
        inicializarComponentes();
        configurarTimerStatus();
        
        // Inicialmente mostra a tela de estatísticas
        mostrarTelaEstatisticas();
    }
    
    private void configurarJanela() {
        setTitle("Teatro ABC - Sistema Administrativo");
        setSize(1280, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        

        
        //setLocationRelativeTo(null);
        //setMinimumSize(new Dimension(1024, 768));
        //setLayout(new BorderLayout());
        //getContentPane().setBackground(new Color(23, 42, 58));
    }
    
    private void inicializarComponentes() {
        // Painel superior com barra de título
        JPanel painelTitulo = criarPainelTitulo();
        add(painelTitulo, BorderLayout.NORTH);
        
        // Painel de menu à esquerda
        JPanel painelMenu = criarPainelMenu();
        add(painelMenu, BorderLayout.WEST);
        
        // Painel de conteúdo (centro)
        painelConteudo = new JPanel(new BorderLayout());
        painelConteudo.setBackground(new Color(25, 40, 55));
        add(painelConteudo, BorderLayout.CENTER);
        
        // Painel de status (inferior)
        JPanel painelStatus = criarPainelStatus();
        add(painelStatus, BorderLayout.SOUTH);
    }
    
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
        
        // Informações do usuário
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
        btnSair.setBorderPainted(true);
        btnSair.setBorder(new LineBorder(new Color(192, 57, 43), 1));
        btnSair.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSair.setPreferredSize(new Dimension(80, 35));
        btnSair.addActionListener(e -> sair());
        
        painelUsuario.add(lblUsuario);
        painelUsuario.add(btnSair);
        
        painelTitulo.add(painelLogoTitulo, BorderLayout.WEST);
        painelTitulo.add(painelUsuario, BorderLayout.EAST);
        
        return painelTitulo;
    }
    
    private JPanel criarPainelMenu() {
        JPanel painelMenu = new JPanel();
        painelMenu.setLayout(new BoxLayout(painelMenu, BoxLayout.Y_AXIS));
        painelMenu.setBackground(new Color(23, 42, 58));
        painelMenu.setPreferredSize(new Dimension(250, 0));
        painelMenu.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, new Color(60, 80, 100)));
        
        // Título do menu
        JLabel lblMenu = new JLabel("MENU");
        lblMenu.setFont(new Font("Arial", Font.BOLD, 18));
        lblMenu.setForeground(Color.WHITE);
        lblMenu.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblMenu.setBorder(BorderFactory.createEmptyBorder(25, 0, 25, 0));
        
        // Botões do menu
        btnEstatisticas = criarBotaoMenu("Estatísticas", e -> mostrarTelaEstatisticas());
        btnBilhetes = criarBotaoMenu("Gerenciar Bilhetes", e -> mostrarTelaGerenciamentoBilhetes());
        
        // Adiciona componentes ao menu
        painelMenu.add(lblMenu);
        painelMenu.add(btnEstatisticas);
        painelMenu.add(btnBilhetes);
        painelMenu.add(Box.createVerticalGlue());
        
        return painelMenu;
    }
    
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
        
        // Efeito hover
        botao.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent evt) {
                botao.setBackground(new Color(34, 49, 63));
                botao.setContentAreaFilled(true);
            }
            
            @Override
            public void mouseExited(MouseEvent evt) {
                botao.setContentAreaFilled(false);
            }
        });
        
        botao.addActionListener(action);
        return botao;
    }
    
    private JPanel criarPainelStatus() {
        JPanel painelStatus = new JPanel(new BorderLayout());
        painelStatus.setBackground(new Color(34, 49, 63));
        painelStatus.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        
        // Status à esquerda
        lblStatus = new JLabel("Pronto");
        lblStatus.setFont(new Font("Arial", Font.PLAIN, 12));
        lblStatus.setForeground(Color.WHITE);
        
        // Hora à direita
        lblHora = new JLabel();
        lblHora.setFont(new Font("Arial", Font.PLAIN, 12));
        lblHora.setForeground(Color.WHITE);
        atualizarHora();
        
        painelStatus.add(lblStatus, BorderLayout.WEST);
        painelStatus.add(lblHora, BorderLayout.EAST);
        
        return painelStatus;
    }
    
    private void configurarTimerStatus() {
        timerStatus = new Timer(1000, e -> atualizarHora());
        timerStatus.start();
    }
    
    private void atualizarHora() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        lblHora.setText(sdf.format(new Date()));
    }
    
    private void atualizarStatus(String mensagem) {
        lblStatus.setText(mensagem);
    }
    
    private void mostrarTelaEstatisticas() {
        // Destacar botão ativo
        destacarBotaoAtivo(btnEstatisticas);
        
        // Trocar o painel
        trocarPainel(new TelaEstatisticas(estatisticaServico, bilheteServico));
        atualizarStatus("Visualizando estatísticas");
    }
    
    private void mostrarTelaGerenciamentoBilhetes() {
        // Destacar botão ativo
        destacarBotaoAtivo(btnBilhetes);
        
        // Trocar o painel
        trocarPainel(new TelaGerenciamentoBilhetes(bilheteServico));
        atualizarStatus("Gerenciando bilhetes");
    }
    
    private void destacarBotaoAtivo(JButton botaoAtivo) {
        // Reset todos os botões
        btnEstatisticas.setContentAreaFilled(false);
        btnEstatisticas.setForeground(Color.WHITE);
        btnBilhetes.setContentAreaFilled(false);
        btnBilhetes.setForeground(Color.WHITE);
        
        // Destacar botão ativo
        botaoAtivo.setBackground(new Color(60, 90, 150));
        botaoAtivo.setContentAreaFilled(true);
        botaoAtivo.setForeground(new Color(239, 125, 0));
    }
    
    private void trocarPainel(JPanel novoPainel) {
        if (painelAtual != null) {
            painelConteudo.remove(painelAtual);
        }
        
        painelAtual = novoPainel;
        painelConteudo.add(painelAtual, BorderLayout.CENTER);
        painelConteudo.revalidate();
        painelConteudo.repaint();
    }
    
    private void sair() {
        int confirmacao = JOptionPane.showConfirmDialog(
            this,
            "Deseja realmente sair do sistema?",
            "Confirmar Saída",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );
        
        if (confirmacao == JOptionPane.YES_OPTION) {
            if (timerStatus != null) {
                timerStatus.stop();
            }
            dispose();
            SwingUtilities.invokeLater(() -> {
                TelaLogin login = new TelaLogin(ServiceLocator.getAutenticacaoServico());
                login.setVisible(true);
            });
        }
    }
    
    // Classe auxiliar para obter serviços
    public static class ServiceLocator {
        private static IBilheteServico bilheteServico;
        private static IEstatisticaServico estatisticaServico;
        private static IAutenticacaoServico autenticacaoServico;
        
        public static void inicializar(IBilheteServico bilheteServ, 
                                      IEstatisticaServico estatisticaServ, 
                                      IAutenticacaoServico autenticacaoServ) {
            bilheteServico = bilheteServ;
            estatisticaServico = estatisticaServ;
            autenticacaoServico = autenticacaoServ;
        }
        
        public static IBilheteServico getBilheteServico() {
            return bilheteServico;
        }
        
        public static IEstatisticaServico getEstatisticaServico() {
            return estatisticaServico;
        }
        
        public static IAutenticacaoServico getAutenticacaoServico() {
            return autenticacaoServico;
        }
    }
    
    // Para testes e desenvolvimento
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            Usuario usuarioTeste = new Usuario("1", "Administrador", "admin", true);
            TelaPrincipalAdmin telaPrincipal = new TelaPrincipalAdmin(usuarioTeste);
            telaPrincipal.setVisible(true);
        });
    }
}