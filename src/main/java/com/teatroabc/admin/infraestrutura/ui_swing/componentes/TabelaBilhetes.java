
import com.teatroabc.admin.dominio.entidades.BilheteVendido;
import com.teatroabc.admin.infraestrutura.ui_swing.util.ConstantesUI;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Componente de tabela para exibição de bilhetes.
 * Inclui formatação personalizada e ordenação.
 */
public class TabelaBilhetes extends JTable {
    private final DefaultTableModel modelo;
    private List<BilheteVendido> bilhetes;
    private final SimpleDateFormat formatoData = new SimpleDateFormat("dd/MM/yyyy HH:mm");
    
    /**
     * Construtor da tabela de bilhetes.
     */
    public TabelaBilhetes() {
        // Cria o modelo de tabela não editável
        modelo = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Nenhuma célula é editável
            }
            
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                // Define o tipo de cada coluna para ordenação correta
                switch (columnIndex) {
                    case 0: return String.class;   // ID
                    case 1: return String.class;   // CPF
                    case 2: return String.class;   // Peça
                    case 3: return String.class;   // Turno
                    case 4: return String.class;   // Poltronas
                    case 5: return Double.class;   // Valor
                    case 6: return Boolean.class;  // Reembolsado
                    default: return Object.class;
                }
            }
        };
        
        // Define as colunas
        modelo.addColumn("ID");
        modelo.addColumn("CPF Cliente");
        modelo.addColumn("Peça");
        modelo.addColumn("Turno");
        modelo.addColumn("Poltronas");
        modelo.addColumn("Valor (R$)");
        modelo.addColumn("Reembolsado");
        
        setModel(modelo);
        configurarTabela();
    }
    
    private void configurarTabela() {
        // Configurações visuais
        setRowHeight(30);
        setShowGrid(true);
        setGridColor(ConstantesUI.COR_BORDA);
        setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        setAutoCreateRowSorter(true);
        
        // Fonte
        setFont(new Font("Arial", Font.PLAIN, 12));
        getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        
        // Cores
        setBackground(Color.WHITE);
        setForeground(Color.BLACK);
        getTableHeader().setBackground(ConstantesUI.COR_FUNDO_ESCURO);
        getTableHeader().setForeground(Color.WHITE);
        
        // Tamanho das colunas
        TableColumnModel colunas = getColumnModel();
        colunas.getColumn(0).setPreferredWidth(100);  // ID
        colunas.getColumn(1).setPreferredWidth(120);  // CPF
        colunas.getColumn(2).setPreferredWidth(180);  // Peça
        colunas.getColumn(3).setPreferredWidth(100);  // Turno
        colunas.getColumn(4).setPreferredWidth(120);  // Poltronas
        colunas.getColumn(5).setPreferredWidth(100);  // Valor
        colunas.getColumn(6).setPreferredWidth(100);  // Reembolsado
        
        // Renderer para formatação personalizada
        setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                
                Component c = super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, column);
                
                // Obtém o índice do modelo, para quando a tabela estiver ordenada
                int modelRow = table.convertRowIndexToModel(row);
                
                // Define a cor de fundo com base no estado
                if (!isSelected) {
                    boolean reembolsado = (boolean) modelo.getValueAt(modelRow, 6);
                    if (reembolsado) {
                        c.setBackground(new Color(250, 220, 220)); // Vermelho claro para reembolsados
                    } else {
                        c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(240, 240, 240)); // Listrado
                    }
                }
                
                // Alinhamento
                if (c instanceof JLabel) {
                    JLabel label = (JLabel) c;
                    
                    if (column == 5) { // Coluna de Valor
                        label.setHorizontalAlignment(SwingConstants.RIGHT); // Alinha à direita
                    } else if (column == 6) { // Coluna de Reembolsado
                        label.setHorizontalAlignment(SwingConstants.CENTER); // Centraliza
                    } else {
                        label.setHorizontalAlignment(SwingConstants.LEFT); // Alinha à esquerda
                    }
                }
                
                return c;
            }
        });
        
        // Renderer especial para a coluna "Reembolsado" (checkbox)
        colunas.getColumn(6).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                
                JCheckBox checkBox = new JCheckBox();
                checkBox.setSelected((Boolean) value);
                checkBox.setHorizontalAlignment(SwingConstants.CENTER);
                
                // Cor de fundo
                int modelRow = table.convertRowIndexToModel(row);
                boolean reembolsado = (boolean) modelo.getValueAt(modelRow, 6);
                
                if (isSelected) {
                    checkBox.setBackground(table.getSelectionBackground());
                    checkBox.setForeground(table.getSelectionForeground());
                } else {
                    if (reembolsado) {
                        checkBox.setBackground(new Color(250, 220, 220));
                    } else {
                        checkBox.setBackground(row % 2 == 0 ? Color.WHITE : new Color(240, 240, 240));
                    }
                    checkBox.setForeground(table.getForeground());
                }
                
                return checkBox;
            }
        });
    }
    
    /**
     * Atualiza os dados da tabela.
     * @param bilhetes Lista de bilhetes a ser exibida
     */
    public void atualizarDados(List<BilheteVendido> bilhetes) {
        this.bilhetes = bilhetes;
        modelo.setRowCount(0); // Limpa a tabela
        
        for (BilheteVendido bilhete : bilhetes) {
            Object[] linha = {
                bilhete.getIdIngresso(),
                formatarCPF(bilhete.getCpf()),
                bilhete.getNomePeca(),
                bilhete.getTurno(),
                bilhete.getNumeroPoltronas(),
                bilhete.getPreco().doubleValue(), // Para ordenação como número
                bilhete.isReembolsado()
            };
            modelo.addRow(linha);
        }
        
        // Recriar o sorter
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(modelo);
        setRowSorter(sorter);
    }
    
    /**
     * Formata o CPF para exibição.
     * @param cpf CPF a ser formatado
     * @return CPF formatado
     */
    private String formatarCPF(String cpf) {
        if (cpf == null || cpf.length() != 11) {
            return cpf;
        }
        return cpf.substring(0, 3) + "." + 
               cpf.substring(3, 6) + "." + 
               cpf.substring(6, 9) + "-" + 
               cpf.substring(9, 11);
    }
    
    /**
     * Obtém o bilhete selecionado na tabela.
     * @return O bilhete selecionado ou null se não houver seleção
     */
    public BilheteVendido getBilheteSelecionado() {
        int linhaSelecionada = getSelectedRow();
        if (linhaSelecionada >= 0) {
            int indiceLinhaSelecionada = convertRowIndexToModel(linhaSelecionada);
            return bilhetes.get(indiceLinhaSelecionada);
        }
        return null;
    }
}