package UI;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.util.List;

public class SelectionDialog<T> extends JDialog {
    private T selectedItem;
    private final TableRowSorter<DefaultTableModel> sorter;
    private final JTextField searchField = new JTextField();

    public SelectionDialog(
            JFrame parent,
            String title,
            List<T> items,
            String[] columns,
            java.util.function.Function<T, Object[]> rowMapper
    ) {
        super(parent, title, true);
        setSize(750, 450);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());

        // Main Panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        mainPanel.setBackground(Color.WHITE);

        // Search bar
        JPanel searchPanel = UIHelper.createSearchPanel(searchField, this::applyFilter);
        searchPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        searchPanel.setBorder(BorderFactory.createEmptyBorder(0,0,10,0));

        // Table
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable table = UIHelper.createTable(model);
        sorter = new TableRowSorter<>(model);
        table.setRowSorter(sorter);

        // Load Data
        for (T item : items) {
            model.addRow(rowMapper.apply(item));
        }

        // Button
        JButton selectBtn = new JButton("Select");
        selectBtn.setFocusPainted(false);
        selectBtn.setBackground(new Color(135, 90, 224));
        selectBtn.setForeground(Color.WHITE);
        selectBtn.setFont(new Font("SansSerif", Font.BOLD, 14));
        selectBtn.setPreferredSize(new Dimension(120, 35));

        selectBtn.addActionListener(_ -> selectRow(table, items));

        // Double Click
        table.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) {
                    selectRow(table, items);
                }
            }
        });

        // Layout
        mainPanel.add(searchPanel, BorderLayout.NORTH);
        mainPanel.add(new JScrollPane(table), BorderLayout.CENTER);
        mainPanel.add(selectBtn, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private void applyFilter() {
        String text = searchField.getText();

        if (text.trim().isEmpty()) {
            sorter.setRowFilter(null);
        } else {
            sorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
        }
    }

    private void selectRow(JTable table, List<T> items) {
        int row = table.getSelectedRow();
        if (row != -1) {
            selectedItem = items.get(table.convertRowIndexToModel(row));
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Please select an item");
        }
    }

    public T getSelectedItem() {
        return selectedItem;
    }
}
