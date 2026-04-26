package UI;

import Enum.*;
import Model.*;
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

        JButton restoreBtn = new JButton("Restore");
        restoreBtn.setBackground(new Color(40,167,69));
        restoreBtn.setForeground(Color.WHITE);
        restoreBtn.addActionListener(_ -> handleRestore());

        // Add to Panels
        buttonPanel.add(createBtn);
        buttonPanel.add(editBtn);
        buttonPanel.add(deleteBtn);
        buttonPanel.add(restoreBtn);

        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(scrollPane);
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(buttonPanel);

        add(mainPanel, BorderLayout.CENTER);
    }

    public void loadData() {
        model.setRowCount(0);

        List<User> users = userService.getActiveUsers();

        for (User u : users) {
            if (u instanceof Customer && role == UserRole.CUSTOMER) {
                addRow(u);
            }

            if (u instanceof CounterStaff && role == UserRole.COUNTER_STAFF) {
                addRow(u);
            }

            if (u instanceof Technician && role == UserRole.TECHNICIAN) {
                addRow(u);
            }

            if (u instanceof Manager && role == UserRole.MANAGER) {
                addRow(u);
            }
        }
    }

    private void addRow(User u) {
        model.addRow(new Object[]{u.getUserID(), u.getRoleID(), u.getUsername(), u.getFirstName(), u.getLastName(), u.getEmail(), u.getContact()});
    }

    private void handleCreate() {
        try {
            UserFormPanel panel = new UserFormPanel(
                    FormMode.CREATE,
                    null,
                    userService,
                    role,
                    () -> {
                        cardLayout.show(rightPanel, "Manage " + role.getDisplayName());
                        loadData();
                    }
            );
            rightPanel.add(panel, "Create User");
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
            UserFormPanel panel = new UserFormPanel(
                    FormMode.EDIT,
                    selectedUser,
                    userService,
                    role,
                    () -> {
                        cardLayout.show(rightPanel, "Manage " + role.getDisplayName());
                        loadData();
                    }
            );
            rightPanel.add(panel, "Edit User");
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

    private void handleRestore() {
        try {
            List<User> inactiveUsers = userService.getInactiveUsersByRole(role);

            if (inactiveUsers.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No inactive " + role.getDisplayName() + " found.");
                return;
            }

            String[] options = new String[inactiveUsers.size()];

            for (int i = 0; i < inactiveUsers.size(); i++) {
                User user = inactiveUsers.get(i);
                options[i] = user.getUserID() + " - " + user.getUsername() + " (" + user.getFirstName() + " " + user.getLastName() + ")";
            }

            String selectedOption = (String) JOptionPane.showInputDialog(this, "Select a user to restore:", "Restore " + role.getDisplayName(),
                    JOptionPane.PLAIN_MESSAGE, null, options,options[0]);

            if (selectedOption == null) {
                return;
            }

            String userID = selectedOption.split(" - ")[0].trim();

            userService.restoreUser(userID);
            JOptionPane.showMessageDialog(this, role.getDisplayName() + " restored successfully." );
            loadData();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}