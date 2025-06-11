
import com.teatroabc.admin.aplicacao.interfaces.IBilheteServico;
import com.teatroabc.admin.aplicacao.interfaces.IEstatisticaServico;
import com.teatroabc.admin.dominio.entidades.Usuario;
import com.teatroabc.admin.infraestrutura.ui_swing.util.ConstantesUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Tela principal do sistema administrativo do Teatro ABC.
 * Contém um menu de navegação e gerencia a exibição de painéis específicos.
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
    
    /**
     * Construtor da tela principal.
     * @param usuario Usuário autenticado
     */
    public TelaPrincipalAdmin(Usuario usuario) {
        this.usuarioLogado = usuario;
        
        // Obter serviços do contexto de aplicação
        // Em uma aplicação real, estes serviços seriam injetados pelo inicializador da aplicação
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
        setSize(1200, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(900, 600));
        
        setLayout(new BorderLayout());
        getContentPane().setBackground(ConstantesUI.COR_FUNDO_ESCURO);
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
        painelConteudo.setBackground(ConstantesUI.COR_FUNDO_MEDIO);
        add(painelConteudo, BorderLayout.CENTER);
        
        // Painel de status (inferior)
        JPanel painelStatus = criarPainelStatus();
        add(painelStatus, BorderLayout.SOUTH);
    }
    
    private JPanel criarPainelTitulo() {
        JPanel painelTitulo = new JPanel(new BorderLayout());
        painelTitulo.setBackground(ConstantesUI.COR_TITULO);
        painelTitulo.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        
        // Logo e título
        JPanel painelLogoTitulo = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        painelLogoTitulo.setOpaque(false);
        
        JLabel lblLogo = new JLabel("TEATRO ABC");
        lblLogo.setFont(new Font("Arial", Font.BOLD, 24));
        lblLogo.setForeground(Color.WHITE);
        
        JLabel lblSubtitulo = new JLabel("Sistema Administrativo");
        lblSubtitulo.setFont(new Font("Arial", Font.ITALIC, 14));
        lblSubtitulo.setForeground(Color.WHITE);
        
        painelLogoTitulo.add(lblLogo);
        painelLogoTitulo.add(lblSubtitulo);
        
        // Informações do usuário
        JPanel painelUsuario = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        painelUsuario.setOpaque(false);
        
        JLabel lblUsuario = new JLabel("Usuário: " + usuarioLogado.getNome());
        lblUsuario.setFont(new Font("Arial", Font.BOLD, 14));
        lblUsuario.setForeground(Color.WHITE);
        
        JButton btnSair = new JButton("Sair");
        btnSair.setFont(new Font("Arial", Font.PLAIN, 12));
        btnSair.setBackground(new Color(200, 50, 50));
        btnSair.setForeground(Color.WHITE);
        btnSair.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSair.setFocusPainted(false);
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
        painelMenu.setBackground(ConstantesUI.COR_MENU);
        painelMenu.setPreferredSize(new Dimension(220, 0));
        painelMenu.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, ConstantesUI.COR_BORDA));
        
        // Título do menu
        JLabel lblMenu = new JLabel("MENU");
        lblMenu.setFont(new Font("Arial", Font.BOLD, 16));
        lblMenu.setForeground(Color.WHITE);
        lblMenu.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblMenu.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        
        // Botões do menu
        JButton btnEstatisticas = criarBotaoMenu("Estatísticas", e -> mostrarTelaEstatisticas());
        JButton btnBilhetes = criarBotaoMenu("Gerenciar Bilhetes", e -> mostrarTelaGerenciamentoBilhetes());
        
        // Adiciona componentes ao menu
        painelMenu.add(lblMenu);
        painelMenu.add(btnEstatisticas);
        painelMenu.add(btnBilhetes);
        painelMenu.add(Box.createVerticalGlue());
        
        return painelMenu;
    }
    
    private JButton criarBotaoMenu(String texto, ActionListener action) {
        JButton botao = new JButton(texto);
        botao.setFont(new Font("Arial", Font.PLAIN, 14));
        botao.setForeground(Color.WHITE);
        botao.setBackground(ConstantesUI.COR_MENU);
        botao.setBorderPainted(false);
        botao.setFocusPainted(false);
        botao.setHorizontalAlignment(SwingConstants.LEFT);
        botao.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        botao.setCursor(new Cursor(Cursor.HAND_CURSOR));
        botao.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        
        // Efeito hover
        botao.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                botao.setBackground(ConstantesUI.COR_HOVER);
            }
            
            public void mouseExited(java.awt.event.MouseEvent evt) {
                botao.setBackground(ConstantesUI.COR_MENU);
            }
        });
        
        botao.addActionListener(action);
        return botao;
    }
    
    private JPanel criarPainelStatus() {
        JPanel painelStatus = new JPanel(new BorderLayout());
        painelStatus.setBackground(ConstantesUI.COR_STATUS);
        painelStatus.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        
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
        trocarPainel(new TelaEstatisticas(estatisticaServico, bilheteServico));
        atualizarStatus("Visualizando estatísticas");
    }
    
    private void mostrarTelaGerenciamentoBilhetes() {
        trocarPainel(new TelaGerenciamentoBilhetes(bilheteServico));
        atualizarStatus("Gerenciando bilhetes");
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
            JOptionPane.YES_NO_OPTION
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
    
    // Classe auxiliar para obter serviços (seria substituída por injeção de dependência)
    private static class ServiceLocator {
        public static IBilheteServico getBilheteServico() {
            // Em uma aplicação real, este método retornaria o serviço do contêiner de DI
            return null; // Substituir por implementação real
        }
        
        public static IEstatisticaServico getEstatisticaServico() {
            return null; // Substituir por implementação real
        }
        
        public static IAutenticacaoServico getAutenticacaoServico() {
            return null; // Substituir por implementação real
        }
    }
    
    // Para testes e desenvolvimento
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Usuario usuarioTeste = new Usuario("1", "Administrador", "admin", true);
            TelaPrincipalAdmin telaPrincipal = new TelaPrincipalAdmin(usuarioTeste);
            telaPrincipal.setVisible(true);
        });
    }
}