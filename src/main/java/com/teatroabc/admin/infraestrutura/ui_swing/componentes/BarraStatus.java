package com.teatroabc.admin.infraestrutura.ui_swing.componentes;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Componente que representa a barra de status inferior da aplicação.
 * Exibe mensagens de status e a hora atual.
 * Encapsula a lógica do Timer e da formatação de data/hora
 */
public class BarraStatus extends JPanel {

    private final JLabel lblStatus;
    private final JLabel lblHora;
    private final Timer timer;

    /**
     * Construtor da BarraStatus.
     */
    public BarraStatus() {
        // Configuração do painel principal
        this.setLayout(new BorderLayout());
        this.setBackground(new Color(34, 49, 63));
        this.setBorder(new EmptyBorder(8, 20, 8, 20));

        // Label para exibir mensagens de status
        lblStatus = new JLabel("Pronto");
        lblStatus.setFont(new Font("Arial", Font.PLAIN, 12));
        lblStatus.setForeground(Color.WHITE);

        // Label para exibir a hora atual
        lblHora = new JLabel();
        lblHora.setFont(new Font("Arial", Font.PLAIN, 12));
        lblHora.setForeground(Color.WHITE);

        // Adiciona os labels ao painel
        this.add(lblStatus, BorderLayout.WEST);
        this.add(lblHora, BorderLayout.EAST);

        // Configura e inicia o Timer para atualizar a hora a cada segundo
        this.timer = new Timer(1000, e -> atualizarHora());
        this.timer.start();

        // Atualiza a hora uma vez na inicialização para exibição imediata
        atualizarHora();
    }

    /**
     * Atualiza a mensagem de status exibida na barra.
     * Este método é público para ser chamado por outras partes do sistema.
     *
     * @param mensagem A nova mensagem a ser exibida.
     */
    public void setStatus(String mensagem) {
        // Garante que o texto nunca seja nulo ou vazio na interface
        if (mensagem != null && !mensagem.trim().isEmpty()) {
            lblStatus.setText(mensagem);
        } else {
            lblStatus.setText("Pronto");
        }
    }

    /**
     * Para o Timer quando o componente for destruído para liberar recursos.
     * Importante para evitar memory leaks.
     */
    public void pararTimer() {
        if (this.timer != null && this.timer.isRunning()) {
            this.timer.stop();
        }
    }

    /**
     * Método privado que atualiza o label da hora.
     * É chamado pelo Timer a cada segundo.
     */
    private void atualizarHora() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        lblHora.setText(sdf.format(new Date()));
    }
}
