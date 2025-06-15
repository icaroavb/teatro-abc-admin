package com.teatroabc.admin.infraestrutura.ui_swing.telas;

import com.teatroabc.admin.aplicacao.interfaces.IAutenticacaoServico;
import com.teatroabc.admin.aplicacao.interfaces.IBilheteServico;
import com.teatroabc.admin.aplicacao.interfaces.IEstatisticaServico;
import com.teatroabc.admin.dominio.entidades.Usuario;
import com.teatroabc.admin.infraestrutura.ui_swing.builders.ConstrutorUIPrincipal;
import com.teatroabc.admin.infraestrutura.ui_swing.componentes.BarraStatus;
import com.teatroabc.admin.infraestrutura.ui_swing.controllers.ControladorNavegacao;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Tela principal do sistema administrativo.
 * Após a refatoração, esta classe atua como um orquestrador, delegando a construção
 * da UI, o gerenciamento da barra de status e a navegação para classes especializadas.
 * Sua responsabilidade principal é gerenciar o ciclo de vida da janela (JFrame) e
 * coordenar a inicialização dos componentes principais.
 */
public class TelaPrincipalAdmin extends JFrame {

    // Serviços injetados via construtor (Inversão de Dependência)
    private final IAutenticacaoServico autenticacaoServico;
    private final IBilheteServico bilheteServico;
    private final IEstatisticaServico estatisticaServico;

    // Componentes e Controladores
    private final Usuario usuarioLogado;
    private final BarraStatus barraStatus;
    private final ControladorNavegacao controladorNavegacao;

    /**
     * Construtor da tela principal.
     *
     * @param usuario              O usuário autenticado.
     * @param autenticacaoServico  O serviço de autenticação.
     * @param bilheteServico       O serviço de bilhetes.
     * @param estatisticaServico   O serviço de estatísticas.
     */
    public TelaPrincipalAdmin(Usuario usuario, 
                             IAutenticacaoServico autenticacaoServico,
                             IBilheteServico bilheteServico, 
                             IEstatisticaServico estatisticaServico) {
        this.usuarioLogado = usuario;
        
        // Injeção de Dependência
        this.autenticacaoServico = autenticacaoServico;
        this.bilheteServico = bilheteServico;
        this.estatisticaServico = estatisticaServico;

        configurarJanela();

        // 1. Cria o mapa de ações para o menu
        Map<String, ActionListener> acoesMenu = criarAcoesMenu();

        // 2. Usa o Builder para construir a UI
        ConstrutorUIPrincipal builder = new ConstrutorUIPrincipal(this, usuario, acoesMenu);
        builder.construirLayout();

        // 3. Adiciona a Barra de Status
        this.barraStatus = new BarraStatus();
        this.add(barraStatus, BorderLayout.SOUTH);

        // 4. Configura o Controlador de Navegação
        this.controladorNavegacao = new ControladorNavegacao(
                builder.getPainelConteudo(),
                builder.getBotoesMenu(),
                this.barraStatus
        );
        registrarTelas();

        // 5. Inicia a navegação na tela padrão
        controladorNavegacao.navegarPara("Estatísticas");
    }

    /**
     * Configura as propriedades básicas do JFrame.
     */
    private void configurarJanela() {
        setTitle("Teatro ABC - Sistema Administrativo");
        setSize(1280, 800);
        setMinimumSize(new Dimension(1024, 768));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE); // Controlamos o fechamento manualmente

        // Adiciona um listener para lidar com o evento de fechamento da janela
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                sair();
            }
        });
    }

    /**
     * Cria um mapa que associa nomes de menu às suas respectivas ações de navegação.
     * @return O mapa de ações.
     */
    private Map<String, ActionListener> criarAcoesMenu() {
        // Usamos LinkedHashMap para manter a ordem de inserção dos botões no menu
        Map<String, ActionListener> acoes = new LinkedHashMap<>();
        acoes.put("Estatísticas", e -> controladorNavegacao.navegarPara("Estatísticas"));
        acoes.put("Gerenciar Bilhetes", e -> controladorNavegacao.navegarPara("Gerenciar Bilhetes"));
        acoes.put("Sair", e -> sair());
        return acoes;
    }

    /**
     * Registra as telas disponíveis no controlador de navegação.
     * Usa Suppliers para lazy initialization das telas.
     */
    private void registrarTelas() {
        controladorNavegacao.registrarTela("Estatísticas",
                () -> new TelaEstatisticas(estatisticaServico, bilheteServico)
        );
        controladorNavegacao.registrarTela("Gerenciar Bilhetes",
                () -> new TelaGerenciamentoBilhetes(bilheteServico)
        );
        // Novas telas podem ser adicionadas aqui sem modificar outras partes do código
    }

    /**
     * Executa o processo de logout e fechamento da aplicação.
     */
    private void sair() {
        int confirmacao = JOptionPane.showConfirmDialog(
                this,
                "Deseja realmente sair do sistema?",
                "Confirmar Saída",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (confirmacao == JOptionPane.YES_OPTION) {
            // Para o timer da barra de status para liberar recursos
            barraStatus.pararTimer();
            // Libera os recursos da janela atual
            dispose();
            // Encerra a sessão do usuário
            autenticacaoServico.encerrarSessao(usuarioLogado.getToken());
            // Abre a tela de login novamente
            SwingUtilities.invokeLater(() -> new TelaLogin(autenticacaoServico).setVisible(true));
        }
    }
}