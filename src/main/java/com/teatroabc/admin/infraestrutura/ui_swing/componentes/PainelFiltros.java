package com.teatroabc.admin.infraestrutura.ui_swing.componentes;

import com.teatroabc.admin.infraestrutura.ui_swing.util.ValidadorCampos;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.text.MaskFormatter;
import java.awt.*;
import java.awt.event.ActionListener;
import java.text.ParseException;

/**
 * Componente para filtros de busca de bilhetes.
 * Versão corrigida com interface melhorada.
 */
public class PainelFiltros extends JPanel {
    private JTextField txtBusca;
    private JComboBox<String> cmbTipoBusca;
    private JCheckBox chkMostrarReembolsados;
    private JButton btnBuscar;
    private JButton btnLimpar;
    
    private final ActionListener acaoBuscar;
    
    /**
     * Construtor do painel de filtros.
     * @param acaoBuscar ActionListener para a ação de busca
     */
    public PainelFiltros(ActionListener acaoBuscar) {
        this.acaoBuscar = acaoBuscar;
        
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setOpaque(false);
        setPreferredSize(new Dimension(250, 0));
        
        // Borda com título
        setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(60, 80, 100), 1),
            "Filtros de Busca",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 14),
            new Color(180, 200, 220)
        ));
        
        inicializarComponentes();
        configurarAcoes();
    }
    
    private void inicializarComponentes() {
        // Painel de conteúdo com espaçamento
        JPanel painelConteudo = new JPanel();
        painelConteudo.setLayout(new BoxLayout(painelConteudo, BoxLayout.Y_AXIS));
        painelConteudo.setOpaque(false);
        painelConteudo.setBorder(new EmptyBorder(20, 15, 15, 15));
        
        // Tipo de Busca
        JLabel lblTipoBusca = new JLabel("Tipo de Busca:");
        lblTipoBusca.setFont(new Font("Arial", Font.BOLD, 14));
        lblTipoBusca.setForeground(new Color(180, 200, 220));
        lblTipoBusca.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        cmbTipoBusca = new JComboBox<>(new String[]{"ID do Bilhete", "CPF do Cliente", "Nome da Peça"});
        cmbTipoBusca.setFont(new Font("Arial", Font.PLAIN, 14));
        cmbTipoBusca.setForeground(Color.WHITE);
        cmbTipoBusca.setBackground(new Color(40, 60, 85));
        cmbTipoBusca.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        cmbTipoBusca.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Campo de Busca
        JLabel lblBusca = new JLabel("Termo de Busca:");
        lblBusca.setFont(new Font("Arial", Font.BOLD, 14));
        lblBusca.setForeground(new Color(180, 200, 220));
        lblBusca.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblBusca.setBorder(new EmptyBorder(15, 0, 5, 0));
        
        txtBusca = new JTextField();
        txtBusca.setFont(new Font("Arial", Font.PLAIN, 14));
        txtBusca.setForeground(Color.WHITE);
        txtBusca.setBackground(new Color(40, 60, 85));
        txtBusca.setCaretColor(Color.WHITE);
        txtBusca.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(60, 80, 120), 1),
            new EmptyBorder(8, 10, 8, 10)
        ));
        txtBusca.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        txtBusca.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Opção para mostrar reembolsados
        chkMostrarReembolsados = new JCheckBox("Mostrar Bilhetes Reembolsados");
        chkMostrarReembolsados.setFont(new Font("Arial", Font.PLAIN, 14));
        chkMostrarReembolsados.setForeground(new Color(180, 200, 220));
        chkMostrarReembolsados.setOpaque(false);
        chkMostrarReembolsados.setAlignmentX(Component.LEFT_ALIGNMENT);
        chkMostrarReembolsados.setSelected(true); // Padrão: mostrar todos
        chkMostrarReembolsados.setBorder(new EmptyBorder(15, 0, 15, 0));
        
        // Botões
        JPanel painelBotoes = new JPanel(new GridLayout(1, 2, 10, 0));
        painelBotoes.setOpaque(false);
        painelBotoes.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        btnBuscar = new JButton("Buscar");
        btnBuscar.setFont(new Font("Arial", Font.BOLD, 14));
        btnBuscar.setBackground(new Color(46, 204, 113));
        btnBuscar.setForeground(Color.WHITE);
        btnBuscar.setFocusPainted(false);
        btnBuscar.setBorderPainted(true);
        btnBuscar.setBorder(new LineBorder(new Color(36, 174, 93), 1));
        btnBuscar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        btnLimpar = new JButton("Limpar");
        btnLimpar.setFont(new Font("Arial", Font.PLAIN, 14));
        btnLimpar.setBackground(new Color(52, 152, 219));
        btnLimpar.setForeground(Color.WHITE);
        btnLimpar.setFocusPainted(false);
        btnLimpar.setBorderPainted(true);
        btnLimpar.setBorder(new LineBorder(new Color(42, 128, 185), 1));
        btnLimpar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        painelBotoes.add(btnBuscar);
        painelBotoes.add(btnLimpar);
        
        // Adiciona componentes ao painel principal
        painelConteudo.add(lblTipoBusca);
        painelConteudo.add(Box.createVerticalStrut(5));
        painelConteudo.add(cmbTipoBusca);
        painelConteudo.add(lblBusca);
        painelConteudo.add(Box.createVerticalStrut(5));
        painelConteudo.add(txtBusca);
        painelConteudo.add(chkMostrarReembolsados);
        painelConteudo.add(painelBotoes);
        
        add(painelConteudo);
        add(Box.createVerticalGlue()); // Empurra tudo para cima
    }
    
    private void configurarAcoes() {
        // Quando o tipo de busca muda, atualiza o campo
        cmbTipoBusca.addActionListener(e -> {
            int selectedIndex = cmbTipoBusca.getSelectedIndex();
            atualizarCampoBusca(selectedIndex);
        });
        
        // Quando o botão Buscar é pressionado
        btnBuscar.addActionListener(e -> {
            if (validarCampos()) {
                acaoBuscar.actionPerformed(e); // Dispara a ação para o listener externo
            }
        });
        
        // Quando o botão Limpar é pressionado
        btnLimpar.addActionListener(e -> {
            limparCampos();
        });
        
        // Configuração inicial do campo
        atualizarCampoBusca(cmbTipoBusca.getSelectedIndex());
    }
    
    private void atualizarCampoBusca(int tipoIndex) {
        // Remove o componente atual
        Container parent = txtBusca.getParent();
        int index = -1;
        Component[] components = parent.getComponents();
        for (int i = 0; i < components.length; i++) {
            if (components[i] == txtBusca) {
                index = i;
                break;
            }
        }
        
        if (index >= 0) {
            parent.remove(txtBusca);
            
            // Cria o novo componente com base no tipo selecionado
            switch (tipoIndex) {
                case 0: // ID do Bilhete
                    txtBusca = new JTextField();
                    break;
                case 1: // CPF do Cliente
                    try {
                        MaskFormatter mask = new MaskFormatter("###.###.###-##");
                        mask.setPlaceholderCharacter('_');
                        JFormattedTextField txtCpf = new JFormattedTextField(mask);
                        estilizarCampo(txtCpf);
                        txtBusca = txtCpf;
                    } catch (ParseException ex) {
                        txtBusca = new JTextField();
                    }
                    break;
                case 2: // Nome da Peça
                    txtBusca = new JTextField();
                    break;
            }
            
            // Estiliza o campo se for um JTextField normal
            if (!(txtBusca instanceof JFormattedTextField)) {
                estilizarCampo(txtBusca);
            }
            
            // Adiciona o novo componente na mesma posição
            parent.add(txtBusca, index);
            parent.revalidate();
            parent.repaint();
        }
    }
    
    private void estilizarCampo(JTextField campo) {
        campo.setFont(new Font("Arial", Font.PLAIN, 14));
        campo.setForeground(Color.WHITE);
        campo.setBackground(new Color(40, 60, 85));
        campo.setCaretColor(Color.WHITE);
        campo.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(60, 80, 120), 1),
            new EmptyBorder(8, 10, 8, 10)
        ));
        campo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        campo.setAlignmentX(Component.LEFT_ALIGNMENT);
    }
    
    private boolean validarCampos() {
        String termo = txtBusca.getText().trim();
        int tipoIndex = cmbTipoBusca.getSelectedIndex();
        
        if (termo.isEmpty()) {
            JOptionPane.showMessageDialog(
                this,
                "Digite um termo de busca",
                "Campo Vazio",
                JOptionPane.WARNING_MESSAGE
            );
            txtBusca.requestFocus();
            return false;
        }
        
        // Validações específicas por tipo
        switch (tipoIndex) {
            case 0: // ID do Bilhete
                // Sem validação específica, qualquer ID é válido
                break;
            case 1: // CPF do Cliente
                if (!ValidadorCampos.validarCPF(termo)) {
                    JOptionPane.showMessageDialog(
                        this,
                        "CPF inválido. Digite um CPF válido.",
                        "CPF Inválido",
                        JOptionPane.WARNING_MESSAGE
                    );
                    txtBusca.requestFocus();
                    return false;
                }
                break;
            case 2: // Nome da Peça
                if (termo.length() < 3) {
                    JOptionPane.showMessageDialog(
                        this,
                        "Digite pelo menos 3 caracteres para buscar por nome de peça.",
                        "Termo Muito Curto",
                        JOptionPane.WARNING_MESSAGE
                    );
                    txtBusca.requestFocus();
                    return false;
                }
                break;
        }
        
        return true;
    }
    
    public void limparCampos() {
        txtBusca.setText("");
        cmbTipoBusca.setSelectedIndex(0);
        chkMostrarReembolsados.setSelected(true);
    }
    
    /**
     * Retorna o termo de busca informado.
     * @return String com o termo de busca
     */
    public String getTermoBusca() {
        return txtBusca.getText().trim();
    }
    
    /**
     * Retorna o tipo de busca selecionado como string para o serviço.
     * @return String identificando o tipo de busca (id, cpf, peca)
     */
    public String getTipoBusca() {
        int index = cmbTipoBusca.getSelectedIndex();
        switch (index) {
            case 0: return "id";
            case 1: return "cpf";
            case 2: return "peca";
            default: return "id";
        }
    }
    
    /**
     * Verifica se devem ser mostrados os bilhetes reembolsados.
     * @return true se devem ser mostrados, false caso contrário
     */
    public boolean getMostrarReembolsados() {
        return chkMostrarReembolsados.isSelected();
    }
}