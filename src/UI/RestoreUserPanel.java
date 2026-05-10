package UI;

import Enum.ButtonMode;
import Model.User;
import Service.UserService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.util.List;

public class RestoreUserPanel extends JPanel {
    private final UserService userService;
    private JTable table;
    private DefaultTableModel model;
    private final JTextField searchField = new JTextField();
    private TableRowSorter<DefaultTableModel> sorter;

    public RestoreUserPanel(UserService userService) {
        this.userService = userService;

        setLayout(new BorderLayout());
        restoreUserUI();
        loadData();
    }

    private void restoreUserUI() {
        JPanel mainPanel = UIHelper.createSectionPanel("Restore Users");

        String[] columns = {"User ID", "Role ID", "Username", "First Name", "Last Name", "Email", "Contact"};
        model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = UIHelper.createTable(model);
        sorter = new TableRowSorter<>(model);
        table.setRowSorter(sorter);

        JPanel searchPanel = UIHelper.createSearchPanel(searchField, this::applyFilter);
        searchPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton restoreBtn = UIHelper.createStyledButton("Restore", ButtonMode.RESTORE);
        restoreBtn.addActionListener(_ -> handleRestore());

        JButton permanentDeleteBtn = UIHelper.createStyledButton("Delete Permanently", ButtonMode.DESTRUCTIVE);
        permanentDeleteBtn.setPreferredSize(new Dimension(180, 35));
        permanentDeleteBtn.addActionListener(_ -> handlePermanentDelete());

        JButton refreshBtn = UIHelper.createStyledButton("Refresh", ButtonMode.CREATE);
        refreshBtn.addActionListener(_ -> loadData());

        buttonPanel.add(restoreBtn);
        buttonPanel.add(permanentDeleteBtn);
        buttonPanel.add(refreshBtn);

        mainPanel.add(searchPanel);
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(scrollPane);
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(buttonPanel);

        add(mainPanel, BorderLayout.CENTER);
    }

    private void applyFilter() {
        String text = searchField.getText();

        if (text.trim().isEmpty()) {
            sorter.setRowFilter(null);
        } else {
            sorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
        }
    }

    public void loadData() {
        model.setRowCount(0);

        List<User> inactiveUsers = userService.getInactiveUsers();

        for (User user : inactiveUsers) {
            model.addRow(new Object[]{
                    user.getUserID(),
                    user.getRoleID(),
                    user.getUsername(),
                    user.getFirstName(),
                    user.getLastName(),
                    user.getEmail(),
                    user.getContact()
            });
        }
    }

    private void handleRestore() {
        int row = table.getSelectedRow();

        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a user first!");
            return;
        }

        String userID = table.getValueAt(row, 0).toString();
        String username = table.getValueAt(row, 2).toString();

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Restore user \"" + username + "\"?",
                "Confirm Restore",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                userService.restoreUser(userID);
                JOptionPane.showMessageDialog(this, "User restored successfully.");
                loadData();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void handlePermanentDelete() {
        int row = table.getSelectedRow();

        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a user first!");
            return;
        }

        String userID = table.getValueAt(row, 0).toString();
        String username = table.getValueAt(row, 2).toString();

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Permanently delete user \"" + username + "\"?\nThis action cannot be undone.",
                "Confirm Permanent Delete",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                userService.hardDeleteUser(userID);
                JOptionPane.showMessageDialog(this, "User permanently deleted.");
                loadData();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
