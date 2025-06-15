package com.teatroabc.admin.infraestrutura.ui_swing.telas;

import com.teatroabc.admin.aplicacao.interfaces.IAutenticacaoServico;
import com.teatroabc.admin.aplicacao.interfaces.IBilheteServico;
import com.teatroabc.admin.aplicacao.interfaces.IEstatisticaServico;
import com.teatroabc.admin.dominio.entidades.Usuario;
import com.teatroabc.admin.infraestrutura.ui_swing.builders.ConstrutorUILogin;
import com.teatroabc.admin.infraestrutura.ui_swing.controllers.ControladorLogin;

import javax.swing.*;

/**
 * Tela de login para o sistema administrativo.
 * Após a refatoração, esta classe atua como um container para a UI de login,
 * delegando a construção da interface e a lógica de controle para classes especializadas.
 */
public class TelaLogin extends JFrame {

    private final IAutenticacaoServico autenticacaoServico;

    // A tela de login precisa saber dos outros serviços para poder passá-los para a TelaPrincipal
    private IBilheteServico bilheteServico;
    private IEstatisticaServico estatisticaServico;
    
    /**
     * Construtor principal para quando a aplicação inicia.
     * @param autenticacaoServico Serviço de autenticação.
     */
    public TelaLogin(IAutenticacaoServico autenticacaoServico) {
        this.autenticacaoServico = autenticacaoServico;
        inicializarUI();
    }
    
    /**
     * Construtor usado para injetar todos os serviços necessários para a próxima tela.
     * @param autenticacaoServico Serviço de autenticação.
     * @param bilheteServico Serviço de bilhetes.
     * @param estatisticaServico Serviço de estatísticas.
     */
    public TelaLogin(IAutenticacaoServico autenticacaoServico, IBilheteServico bilheteServico, IEstatisticaServico estatisticaServico) {
        this.autenticacaoServico = autenticacaoServico;
        this.bilheteServico = bilheteServico;
        this.estatisticaServico = estatisticaServico;
        inicializarUI();
    }

    private void inicializarUI() {
        // 1. Usa o Builder para construir a interface gráfica
        ConstrutorUILogin builder = new ConstrutorUILogin(this);
        builder.construirLayout();

        // 2. Define a ação de sucesso do login (navegação)
        // O Consumer<Usuario> define o que acontece quando o login é bem-sucedido
        ControladorLogin controlador = new ControladorLogin(
            autenticacaoServico,
            this,
            builder.getTxtUsuario(),
            builder.getTxtSenha(),
            builder.getBtnEntrar(),
            builder.getLblMensagem(),
            this::navegarParaTelaPrincipal // Passa o método de navegação como callback
        );

        // 3. Registra as ações de eventos (cliques, teclado)
        controlador.registrarAcoes();
        
        // Foco inicial no campo de usuário
        SwingUtilities.invokeLater(() -> builder.getTxtUsuario().requestFocusInWindow());
    }

    /**
     * Lida com a navegação para a tela principal após o sucesso do login.
     * @param usuario O usuário autenticado.
     */
    private void navegarParaTelaPrincipal(Usuario usuario) {
        // Precisamos garantir que os outros serviços foram injetados antes de navegar
        if (this.bilheteServico == null || this.estatisticaServico == null) {
            // Este é um fallback. O ideal é que a Main sempre injete todos os serviços na TelaLogin.
            // Para este projeto, vamos assumir que isso não acontecerá, mas em um sistema maior,
            // um gerenciador de navegação central lidaria com isso.
            System.err.println("Erro: Tentativa de navegar sem os serviços necessários injetados.");
            JOptionPane.showMessageDialog(this, "Erro de configuração interna. Contate o suporte.", "Erro Crítico", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Cria a próxima tela injetando todas as dependências necessárias
        TelaPrincipalAdmin telaPrincipal = new TelaPrincipalAdmin(
            usuario, autenticacaoServico, bilheteServico, estatisticaServico
        );
        telaPrincipal.setVisible(true);
        this.dispose(); // Fecha a tela de login
    }
}
