package UI;

import Enum.*;
import Model.*;
import Service.UserService;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.util.List;

public class ManageUserPanel extends JPanel {
    private final UserService userService;
    private final UserRole role;
    private final User currentUser;
    private JTable table;
    private DefaultTableModel model;
    private final JPanel rightPanel;
    private final CardLayout cardLayout;
    private final JTextField searchField = new JTextField();
    private TableRowSorter<DefaultTableModel> sorter;

    public ManageUserPanel(UserService userService, UserRole role, User currentUser, JPanel rightPanel, CardLayout cardLayout) {
        this.userService = userService;
        this.role = role;
        this.currentUser = currentUser;
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

        table = UIHelper.createTable(model);
        sorter = new TableRowSorter<>(model);
        table.setRowSorter(sorter);

        //highlights logged in user
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component component = super.getTableCellRendererComponent(table,value, isSelected, hasFocus, row, column);

                String rowUserID = table.getValueAt(row, 0).toString();
                boolean isCurrentUserRow = currentUser != null && currentUser.getUserID().equals(rowUserID);

                if (isSelected) {
                    component.setBackground(table.getSelectionBackground());
                    component.setForeground(Color.BLACK);
                } else if (isCurrentUserRow) {
                    component.setBackground(new Color(255,244,204));
                    component.setForeground(Color.BLACK);
                } else {
                    component.setBackground(Color.WHITE);
                    component.setForeground(Color.BLACK);
                }

                return component;
            }
        });

        // Search bar
        JPanel searchPanel = UIHelper.createSearchPanel(searchField, this::applyFilter);
        searchPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // ScrollPane
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Button Panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton createBtn = UIHelper.createStyledButton("Create", ButtonMode.CREATE);
        createBtn.addActionListener(_ -> handleCreate());

        JButton editBtn = UIHelper.createStyledButton("Edit", ButtonMode.PRIMARY);
        editBtn.addActionListener(_ -> handleEdit());

        JButton deleteBtn = UIHelper.createStyledButton("Delete", ButtonMode.DESTRUCTIVE);
        deleteBtn.addActionListener(_ -> handleDelete());

        // Add to Panels
        buttonPanel.add(createBtn);
        buttonPanel.add(editBtn);
        buttonPanel.add(deleteBtn);

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
        String usernameDisplay = u.getUsername();

        if (currentUser != null && currentUser.getUserID().equals(u.getUserID())) {
            usernameDisplay += " (You)";
        }

        model.addRow(new Object[]{u.getUserID(), u.getRoleID(), usernameDisplay, u.getFirstName(), u.getLastName(), u.getEmail(), u.getContact()});
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

        // Prevent manager from deleting their own account
        if (currentUser != null && currentUser.getUserID().equals(userID)) {
            JOptionPane.showMessageDialog(this, "You cannot delete your own account.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

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