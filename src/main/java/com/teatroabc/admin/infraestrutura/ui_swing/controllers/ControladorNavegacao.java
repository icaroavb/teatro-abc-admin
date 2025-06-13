package com.teatroabc.admin.infraestrutura.ui_swing.controllers;

import com.teatroabc.admin.infraestrutura.ui_swing.componentes.BarraStatus;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Controla a navegação entre as diferentes telas (JPanels) da aplicação.
 * Isola a lógica de troca de painéis e atualização de estado da UI.
 */
public class ControladorNavegacao {

    private final JPanel painelConteudo;
    private final BarraStatus barraStatus;
    private final Map<String, JButton> botoesMenu;
    private final Map<String, Supplier<JPanel>> fabricasDeTela;
    private JPanel painelAtual;

    /**
     * Construtor do Controlador de Navegação.
     *
     * @param painelConteudo O painel principal onde as telas serão exibidas.
     * @param botoesMenu     O mapa de botões do menu para gerenciamento de estado visual.
     * @param barraStatus    A barra de status para exibir mensagens de navegação.
     */
    public ControladorNavegacao(JPanel painelConteudo, Map<String, JButton> botoesMenu, BarraStatus barraStatus) {
        this.painelConteudo = painelConteudo;
        this.botoesMenu = botoesMenu;
        this.barraStatus = barraStatus;
        this.fabricasDeTela = new HashMap<>();
    }

    /**
     * Registra uma nova tela associada a um nome de menu.
     *
     * @param nomeMenu O nome do menu (e do botão) que ativa esta tela.
     * @param fabrica  Um Supplier que sabe como criar uma nova instância da tela (JPanel).
     */
    public void registrarTela(String nomeMenu, Supplier<JPanel> fabrica) {
        fabricasDeTela.put(nomeMenu, fabrica);
    }

    /**
     * Navega para a tela especificada pelo nome do menu.
     *
     * @param nomeMenu O nome da tela para a qual navegar.
     */
    public void navegarPara(String nomeMenu) {
        Supplier<JPanel> fabrica = fabricasDeTela.get(nomeMenu);
        if (fabrica == null) {
            System.err.println("Nenhuma tela registrada para o nome: " + nomeMenu);
            return;
        }

        // Cria a nova tela usando a fábrica (lazy initialization)
        JPanel novoPainel = fabrica.get();

        // Remove o painel antigo, se houver
        if (painelAtual != null) {
            painelConteudo.remove(painelAtual);
        }

        // Adiciona o novo painel
        painelAtual = novoPainel;
        painelConteudo.add(painelAtual, BorderLayout.CENTER);

        // Atualiza a UI para refletir a mudança
        painelConteudo.revalidate();
        painelConteudo.repaint();

        // Atualiza o estado visual do menu e da barra de status
        destacarBotaoAtivo(nomeMenu);
        barraStatus.setStatus("Visualizando " + nomeMenu);
    }

    /**
     * Atualiza o estado visual dos botões do menu, destacando o que está ativo.
     *
     * @param nomeBotaoAtivo O nome do botão que deve ser destacado.
     */
    private void destacarBotaoAtivo(String nomeBotaoAtivo) {
        botoesMenu.forEach((nome, botao) -> {
            if (nome.equals(nomeBotaoAtivo)) {
                // Estilo do botão ativo
                botao.setBackground(new Color(60, 90, 150));
                botao.setForeground(new Color(239, 125, 0));
                botao.setContentAreaFilled(true);
            } else {
                // Estilo padrão para botões inativos
                botao.setBackground(new Color(23, 42, 58));
                botao.setForeground(Color.WHITE);
                botao.setContentAreaFilled(false);
            }
        });
    }
}
