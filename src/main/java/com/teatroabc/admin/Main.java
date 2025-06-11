
import com.teatroabc.admin.aplicacao.interfaces.IAutenticacaoServico;
import com.teatroabc.admin.aplicacao.interfaces.IBilheteServico;
import com.teatroabc.admin.aplicacao.interfaces.IEstatisticaServico;
import com.teatroabc.admin.aplicacao.servicos.AutenticacaoServico;
import com.teatroabc.admin.aplicacao.servicos.BilheteServico;
import com.teatroabc.admin.aplicacao.servicos.EstatisticaServico;
import com.teatroabc.admin.dominio.interfaces.IRepositorioBilhete;
import com.teatroabc.admin.infraestrutura.persistencia.implementacao.BilheteRepositorio;
import com.teatroabc.admin.infraestrutura.persistencia.implementacao.UsuarioRepositorio;
import com.teatroabc.admin.infraestrutura.ui_swing.telas.TelaLogin;
import com.teatroabc.admin.infraestrutura.ui_swing.util.ConstantesUI;
import com.teatroabc.admin.aplicacao.servicos.IBilheteServico;
import com.teatroabc.admin.aplicacao.servicos.BilheteServico;





import javax.swing.*;
import java.awt.*;

/**
 * Classe principal da aplicação administrativa do Teatro ABC.
 * Responsável por inicializar os componentes e configurar a injeção de dependências.
 */
public class Main {
    
    /**
     * Ponto de entrada da aplicação.
     * @param args Argumentos de linha de comando
     */
    public static void main(String[] args) {
        // Configura o tema da aplicação
        configurarTema();
        
        // Inicializa os componentes da aplicação
        inicializarAplicacao();
    }
    
    /**
     * Configura o tema visual da aplicação.
     */
    private static void configurarTema() {
        try {
            // Configura o look and feel para o tema do sistema
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            
            // Sobrescreve algumas propriedades para personalização
            UIManager.put("Panel.background", ConstantesUI.COR_FUNDO_MEDIO);
            UIManager.put("OptionPane.background", ConstantesUI.COR_FUNDO_MEDIO);
            UIManager.put("OptionPane.messageForeground", ConstantesUI.COR_TEXTO_CLARO);
            UIManager.put("Button.background", ConstantesUI.COR_BOTAO_PRIMARIO);
            UIManager.put("Button.foreground", Color.WHITE);
            
        } catch (Exception e) {
            System.err.println("Erro ao configurar tema: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Inicializa os componentes da aplicação e configura a injeção de dependências.
     */
    private static void inicializarAplicacao() {
        // Instancia os repositórios
        IRepositorioBilhete bilheteRepositorio = new BilheteRepositorio();
        UsuarioRepositorio usuarioRepositorio = new UsuarioRepositorio();
        
        // Instancia os serviços e injeta as dependências
        IBilheteServico bilheteServico = new BilheteServico(bilheteRepositorio);
        IEstatisticaServico estatisticaServico = new EstatisticaServico(bilheteServico);
        IAutenticacaoServico autenticacaoServico = new AutenticacaoServico(usuarioRepositorio);
        
        // Inicializa o cache de bilhetes
        inicializarCache(bilheteServico);
        
        // Inicia a interface gráfica
        SwingUtilities.invokeLater(() -> {
            TelaLogin telaLogin = new TelaLogin(autenticacaoServico);
            telaLogin.setVisible(true);
        });
    }
    
    /**
     * Inicializa o cache de bilhetes em segundo plano.
     * @param bilheteServico Serviço de bilhetes
     */
    private static void inicializarCache(IBilheteServico bilheteServico) {
        new Thread(() -> {
            try {
                System.out.println("Inicializando cache de bilhetes...");
                long inicio = System.currentTimeMillis();
                bilheteServico.inicializarCache();
                long fim = System.currentTimeMillis();
                System.out.println("Cache inicializado em " + (fim - inicio) + "ms");
            } catch (Exception e) {
                System.err.println("Erro ao inicializar cache: " + e.getMessage());
                e.printStackTrace();
            }
        }).start();
    }
}