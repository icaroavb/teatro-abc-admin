

import com.teatroabc.admin.infraestrutura.ui_swing.util.ConstantesUI;
import com.teatroabc.admin.infraestrutura.ui_swing.util.ValidadorCampos;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import javax.swing.text.MaskFormatter;
import java.text.ParseException;

/**
 * Componente para filtros de busca de bilhetes.
 * Permite filtrar por CPF, ID do bilhete ou peça, além de mostrar/esconder bilhetes reembolsados.
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
        setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(ConstantesUI.COR_BORDA),
            "Filtros",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 14),
            ConstantesUI.COR_TEXTO_CLARO
        ));
        
        inicializarComponentes();
        configurarAcoes();
    }
    
    private void inicializarComponentes() {
        // Grupo de Busca
        JPanel painelBusca = new JPanel();
        painelBusca.setLayout(new BoxLayout(painelBusca, BoxLayout.Y_AXIS));
        painelBusca.setOpaque(false);
        painelBusca.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Tipo de Busca
        JLabel lblTipoBusca = new JLabel("Tipo de Busca:");
        lblTipoBusca.setFont(new Font("Arial", Font.BOLD, 12));
        lblTipoBusca.setForeground(ConstantesUI.COR_TEXTO_CLARO);
        lblTipoBusca.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        cmbTipoBusca = new JComboBox<>(new String[]{"ID do Bilhete", "CPF do Cliente", "Nome da Peça"});
        cmbTipoBusca.setFont(new Font("Arial", Font.PLAIN, 12));
        cmbTipoBusca.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        cmbTipoBusca.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Campo de Busca
        JLabel lblBusca = new JLabel("Termo de Busca:");
        lblBusca.setFont(new Font("Arial", Font.BOLD, 12));
        lblBusca.setForeground(ConstantesUI.COR_TEXTO_CLARO);
        lblBusca.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        txtBusca = new JTextField();
        txtBusca.setFont(new Font("Arial", Font.PLAIN, 12));
        txtBusca.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        txtBusca.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Opção para mostrar reembolsados
        chkMostrarReembolsados = new JCheckBox("Mostrar Bilhetes Reembolsados");
        chkMostrarReembolsados.setFont(new Font("Arial", Font.PLAIN, 12));
        chkMostrarReembolsados.setForeground(ConstantesUI.COR_TEXTO_CLARO);
        chkMostrarReembolsados.setOpaque(false);
        chkMostrarReembolsados.setAlignmentX(Component.LEFT_ALIGNMENT);
        chkMostrarReembolsados.setSelected(true); // Padrão: mostrar todos
        
        // Botões
        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        painelBotoes.setOpaque(false);
        
        btnBuscar = new JButton("Buscar");
        btnBuscar.setFont(new Font("Arial", Font.BOLD, 12));
        btnBuscar.setBackground(ConstantesUI.COR_BOTAO_PRIMARIO);
        btnBuscar.setForeground(Color.WHITE);
        btnBuscar.setFocusPainted(false);
        btnBuscar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        btnLimpar = new JButton("Limpar");
        btnLimpar.setFont(new Font("Arial", Font.PLAIN, 12));
        btnLimpar.setBackground(ConstantesUI.COR_BOTAO_SECUNDARIO);
        btnLimpar.setForeground(Color.WHITE);
        btnLimpar.setFocusPainted(false);
        btnLimpar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        painelBotoes.add(btnBuscar);
        painelBotoes.add(btnLimpar);
        
        // Adiciona componentes ao painel principal
        painelBusca.add(lblTipoBusca);
        painelBusca.add(Box.createVerticalStrut(5));
        painelBusca.add(cmbTipoBusca);
        painelBusca.add(Box.createVerticalStrut(15));
        painelBusca.add(lblBusca);
        painelBusca.add(Box.createVerticalStrut(5));
        painelBusca.add(txtBusca);
        painelBusca.add(Box.createVerticalStrut(15));
        painelBusca.add(chkMostrarReembolsados);
        painelBusca.add(Box.createVerticalStrut(20));
        painelBusca.add(painelBotoes);
        
        add(painelBusca);
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
                        txtBusca = txtCpf;
                    } catch (ParseException ex) {
                        txtBusca = new JTextField();
                    }
                    break;
                case 2: // Nome da Peça
                    txtBusca = new JTextField();
                    break;
            }
            
            // Configura o novo componente
            txtBusca.setFont(new Font("Arial", Font.PLAIN, 12));
            txtBusca.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
            txtBusca.setAlignmentX(Component.LEFT_ALIGNMENT);
            
            // Adiciona o novo componente na mesma posição
            parent.add(txtBusca, index);
            parent.revalidate();
            parent.repaint();
        }
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