package com.teatroabc.admin;

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
        // Configura o tema visual da aplicação
        configurarTema();
        
        // Inicializa a aplicação
        inicializarAplicacao();
    }
    
    /**
     * Configura o tema visual (Look and Feel) da aplicação Swing.
     */
    private static void configurarTema() {
        try {
            // Tenta usar o tema Nimbus para um visual mais moderno
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            // Caso Nimbus não esteja disponível, o look and feel padrão será usado.
            System.err.println("Erro ao configurar tema Nimbus: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Inicializa os repositórios, serviços e a interface gráfica principal.
     * Segue o padrão de Injeção de Dependência para desacoplar as camadas.
     */
    private static void inicializarAplicacao() {
        // --- Composição da Raiz (Composition Root) ---
        // 1. Instancia os repositórios (camada de persistência)
        IRepositorioBilhete bilheteRepositorio = new BilheteRepositorio();
        UsuarioRepositorio usuarioRepositorio = new UsuarioRepositorio();
        
        // 2. Instancia os serviços e injeta suas dependências (repositórios)
        IBilheteServico bilheteServico = new BilheteServico(bilheteRepositorio);
        IEstatisticaServico estatisticaServico = new EstatisticaServico(bilheteServico);
        IAutenticacaoServico autenticacaoServico = new AutenticacaoServico(usuarioRepositorio);
        
        // O ServiceLocator foi removido. As dependências são passadas explicitamente.
        
        // 3. Inicializa o cache de bilhetes em uma thread separada para não bloquear a UI
        inicializarCacheEmBackground(bilheteServico);
        
        // 4. Inicia a interface gráfica na Event Dispatch Thread (EDT) do Swing
        SwingUtilities.invokeLater(() -> {
            // A tela de login agora recebe os serviços de que precisa.
            // Neste caso, apenas o serviço de autenticação.
            TelaLogin telaLogin = new TelaLogin(autenticacaoServico);
            telaLogin.setVisible(true);
        });
    }
    
    /**
     * Inicializa o cache de bilhetes em uma thread de fundo para não travar a UI.
     * @param bilheteServico O serviço de bilhetes a ser usado.
     */
    private static void inicializarCacheEmBackground(IBilheteServico bilheteServico) {
        Thread threadCache = new Thread(() -> {
            try {
                System.out.println("Inicializando cache de bilhetes em background...");
                long inicio = System.currentTimeMillis();
                bilheteServico.inicializarCache();
                long fim = System.currentTimeMillis();
                System.out.println("Cache inicializado em " + (fim - inicio) + "ms.");
            } catch (Exception e) {
                System.err.println("Erro crítico ao inicializar o cache: " + e.getMessage());
                e.printStackTrace();
                // Opcional: Mostrar uma mensagem de erro para o usuário se o cache falhar.
            }
        });
        threadCache.setName("Cache-Initializer-Thread");
        threadCache.setDaemon(true); // Permite que a JVM encerre mesmo se esta thread estiver rodando
        threadCache.start();
    }
}
