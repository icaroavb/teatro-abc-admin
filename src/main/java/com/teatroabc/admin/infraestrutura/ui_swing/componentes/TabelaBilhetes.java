package com.teatroabc.admin.infraestrutura.ui_swing.componentes;

import com.teatroabc.admin.dominio.entidades.BilheteVendido;
import com.teatroabc.admin.infraestrutura.ui_swing.util.ConstantesUI;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Componente de tabela para exibição de bilhetes.
 * Versão corrigida com melhor visualização.
 */
public class TabelaBilhetes extends JTable {
    private final DefaultTableModel modelo;
    private List<BilheteVendido> bilhetes;
    private final SimpleDateFormat formatoData = new SimpleDateFormat("dd/MM/yyyy HH:mm");
    private final DecimalFormat formatoMoeda = new DecimalFormat("R$ #,##0.00");
    
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
        modelo.addColumn("Valor");
        modelo.addColumn("Reembolsado");
        
        setModel(modelo);
        configurarTabela();
    }
    
    private void configurarTabela() {
        // Configurações visuais
        setRowHeight(40);
        setShowGrid(false);
        setIntercellSpacing(new Dimension(0, 0));
        setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        setAutoCreateRowSorter(true);
        setFillsViewportHeight(true);
        
        // Fonte e cores
        setFont(new Font("Arial", Font.PLAIN, 14));
        setBackground(new Color(25, 40, 55));
        setForeground(Color.WHITE);
        setSelectionBackground(new Color(60, 90, 150));
        setSelectionForeground(Color.WHITE);
        
        // Configuração do cabeçalho
        JTableHeader header = getTableHeader();
        header.setFont(new Font("Arial", Font.BOLD, 14));
        header.setBackground(new Color(30, 45, 60));
        header.setForeground(new Color(180, 200, 220));
        header.setBorder(BorderFactory.createEmptyBorder());
        header.setPreferredSize(new Dimension(header.getWidth(), 45));
        
        // Tamanho das colunas
        TableColumnModel colunas = getColumnModel();
        colunas.getColumn(0).setPreferredWidth(80);  // ID
        colunas.getColumn(1).setPreferredWidth(130); // CPF
        colunas.getColumn(2).setPreferredWidth(200); // Peça
        colunas.getColumn(3).setPreferredWidth(100); // Turno
        colunas.getColumn(4).setPreferredWidth(120); // Poltronas
        colunas.getColumn(5).setPreferredWidth(100); // Valor
        colunas.getColumn(6).setPreferredWidth(120); // Reembolsado
        
        // Renderer personalizado para as células
        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                          boolean isSelected, boolean hasFocus, int row, int column) {
                
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                JLabel label = (JLabel) c;
                
                // Remove a borda do foco
                label.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 5));
                
                // Obtém o índice do modelo, para quando a tabela estiver ordenada
                int modelRow = table.convertRowIndexToModel(row);
                
                // Define a cor de fundo com base no estado
                if (!isSelected) {
                    boolean reembolsado = (boolean) modelo.getValueAt(modelRow, 6);
                    if (reembolsado) {
                        label.setBackground(new Color(60, 30, 30)); // Vermelho escuro para reembolsados
                        label.setForeground(new Color(220, 180, 180)); // Texto mais claro
                    } else {
                        // Alterna cores para melhor legibilidade
                        label.setBackground(row % 2 == 0 ? new Color(25, 40, 55) : new Color(30, 45, 60));
                        label.setForeground(Color.WHITE);
                    }
                }
                
                // Formata a coluna de valor
                if (column == 5 && value != null) {
                    if (value instanceof Double) {
                        label.setText(formatoMoeda.format(value));
                    }
                    label.setHorizontalAlignment(SwingConstants.RIGHT);
                } else if (column == 6) {
                    // Centraliza a coluna de reembolsado
                    label.setHorizontalAlignment(SwingConstants.CENTER);
                } else {
                    label.setHorizontalAlignment(SwingConstants.LEFT);
                }
                
                return label;
            }
        };
        
        // Aplica o renderer a todas as colunas exceto a de reembolso
        for (int i = 0; i < getColumnCount(); i++) {
            if (i != 6) { // Exceto a coluna de reembolso
                getColumnModel().getColumn(i).setCellRenderer(renderer);
            }
        }
        
        // Renderer para a coluna "Reembolsado" (checkbox)
        getColumnModel().getColumn(6).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                          boolean isSelected, boolean hasFocus, int row, int column) {
                
                // Cria um checkbox estilizado para a coluna de reembolso
                JCheckBox checkBox = new JCheckBox();
                checkBox.setSelected((Boolean) value);
                checkBox.setHorizontalAlignment(SwingConstants.CENTER);
                
                // Cor de fundo baseada no status
                int modelRow = table.convertRowIndexToModel(row);
                boolean reembolsado = (boolean) modelo.getValueAt(modelRow, 6);
                
                if (isSelected) {
                    checkBox.setBackground(table.getSelectionBackground());
                    checkBox.setForeground(table.getSelectionForeground());
                } else {
                    if (reembolsado) {
                        checkBox.setBackground(new Color(60, 30, 30));
                        checkBox.setForeground(new Color(220, 180, 180));
                    } else {
                        checkBox.setBackground(row % 2 == 0 ? new Color(25, 40, 55) : new Color(30, 45, 60));
                        checkBox.setForeground(Color.WHITE);
                    }
                }
                
                return checkBox;
            }
        });
        
        // Remove a grade e linhas divisórias
        setShowHorizontalLines(false);
        setShowVerticalLines(false);
        
        // Configurar o cursor como mão ao passar sobre as linhas
        setCursor(new Cursor(Cursor.HAND_CURSOR));
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