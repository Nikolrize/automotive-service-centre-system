package UI;

import Enum.*;
import Model.User;
import Service.UserService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.List;

public class ManageUserPanel extends JPanel {
    private final UserService userService;
    private final UserRole role;
    private JTable table;
    private DefaultTableModel model;
    private final JPanel rightPanel;
    private final CardLayout cardLayout;

    public ManageUserPanel(UserService userService, UserRole role, JPanel rightPanel, CardLayout cardLayout) {
        this.userService = userService;
        this.role = role;
        this.rightPanel = rightPanel;
        this.cardLayout = cardLayout;

        setLayout(new BorderLayout());
        manageUserUI();
        loadData();
    }

    private void manageUserUI() {
        JPanel mainPanel = UIHelper.createSectionPanel("Manage " + role.getDisplayName());

        // Table
        String[] columns = {"User ID", "Role ID", "Username", "First Name", "Last Name", "Email", "Contact"};

        model = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(model);
        table.setRowHeight(40);
        table.setFont(new Font("SansSerif", Font.PLAIN, 13));
        table.setSelectionBackground(new Color(230, 220, 255));
        table.setGridColor(new Color(240, 240, 240));
        table.setShowVerticalLines(false);

        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("SansSerif", Font.BOLD, 14));
        header.setBackground(new Color(245, 245, 245));

        // ScrollPane
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Button Panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton createBtn = new JButton("Create");
        createBtn.setBackground(new Color(66, 135, 245));
        createBtn.setForeground(Color.WHITE);
        createBtn.addActionListener(_ -> handleCreate());

        JButton editBtn = new JButton("Edit");
        editBtn.setBackground(new Color(135, 90, 224));
        editBtn.setForeground(Color.WHITE);
        editBtn.addActionListener(_ -> handleEdit());

        JButton deleteBtn = new JButton("Delete");
        deleteBtn.setBackground(new Color(220, 53, 69));
        deleteBtn.setForeground(Color.WHITE);
        deleteBtn.addActionListener(_ -> handleDelete());

        // Add to Panels
        buttonPanel.add(createBtn);
        buttonPanel.add(editBtn);
        buttonPanel.add(deleteBtn);

        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(scrollPane);
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(buttonPanel);

        add(mainPanel, BorderLayout.CENTER);
    }

    public void loadData() {
        model.setRowCount(0);

        List<User> users = switch (role) {
            case UserRole.MANAGER -> userService.getUsersByRole(UserRole.MANAGER);
            case UserRole.COUNTER_STAFF -> userService.getUsersByRole(UserRole.COUNTER_STAFF);
            case UserRole.TECHNICIAN -> userService.getUsersByRole(UserRole.TECHNICIAN);
            case UserRole.CUSTOMER -> userService.getUsersByRole(UserRole.CUSTOMER );
            default -> userService.getUsersByRole(UserRole.USER);
        };

        for (User u : users) {
            model.addRow(new Object[]{u.getUserID(), u.getRoleID(), u.getUsername(), u.getFirstName(), u.getLastName(), u.getEmail(), u.getContact()});
        }
    }

    private void handleCreate() {
        try {
            JPanel createPanel = UIHelper.userFormPanel(FormMode.CREATE, null, userService, role);
            rightPanel.add(createPanel, "Create User");
            cardLayout.show(rightPanel, "Create User");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleEdit() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a user first!");
            return;
        }

        String userID = table.getValueAt(row, 0).toString();

        try {
            User selectedUser = userService.getUserById(userID);
            JPanel editPanel = UIHelper.userFormPanel(FormMode.EDIT, selectedUser, userService, role);

            JButton backBtn = new JButton("Back");
            backBtn.setBackground(new Color(135, 90, 224));
            backBtn.setForeground(Color.WHITE);
            backBtn.setFocusPainted(false);
            backBtn.setFont(new Font("SansSerif", Font.BOLD, 14));
            backBtn.setPreferredSize(new Dimension(100, 35));

            backBtn.addActionListener(_ -> {
                cardLayout.show(rightPanel, "Manage " + role.getDisplayName());
                loadData();
            });

            editPanel.add(backBtn);
            rightPanel.add(editPanel, "Edit User");
            cardLayout.show(rightPanel, "Edit User");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleDelete() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a user first!");
            return;
        }

        String userID = table.getValueAt(row, 0).toString();

        int confirm = JOptionPane.showConfirmDialog(this, "Delete this user?", "Confirm", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                userService.softDeleteUser(userID);
                loadData();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}