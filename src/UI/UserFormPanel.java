package UI;

import Model.User;
import Service.UserService;
import Enum.*;

import javax.swing.*;
import java.awt.*;

public class UserFormPanel extends JPanel {

    private final UserService userService;
    private final UserRole role;
    private final FormMode mode;
    private final User user;

    private JTextField usernameField;
    private JTextField emailField;
    private JTextField contactField;
    private JPasswordField passwordField;
    private JTextField firstNameField;
    private JTextField lastNameField;

    private final Runnable onBack;

    public UserFormPanel(FormMode mode, User user, UserService userService, UserRole role, Runnable onBack) {
        this.mode = mode;
        this.user = user;
        this.userService = userService;
        this.role = role;
        this.onBack = onBack;

        setLayout(new BorderLayout());
        userFormUI();
    }

    private void userFormUI() {
        JPanel mainPanel = UIHelper.createSectionPanel(mode == FormMode.EDIT ? "Edit " + user.getUsername() : "Create " + role.getDisplayName());

        // Form Panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel usernameLabel = new JLabel("Username:");
        usernameField = new JTextField();
        usernameField.setPreferredSize(new Dimension(400, 35));

        JLabel emailLabel = new JLabel("Email:");
        emailField = new JTextField();
        emailField.setPreferredSize(new Dimension(400, 35));

        JLabel contactLabel = new JLabel("Contact Number:");
        contactField = new JTextField();
        contactField.setPreferredSize(new Dimension(400, 35));

        JLabel passwordLabel = new JLabel("Password:");
        passwordField = new JPasswordField();
        passwordField.setPreferredSize(new Dimension(400, 35));
        JCheckBox showPassword = UIHelper.showPassword(passwordField);
        showPassword.setBackground(Color.WHITE);

        // First Name + Last Name Panel
        JPanel namePanel = new JPanel(new GridBagLayout());
        namePanel.setBackground(Color.WHITE);

        JLabel firstNameLabel = new JLabel("First Name:");
        firstNameField = new JTextField();
        firstNameField.setPreferredSize(new Dimension(200, 35));

        JLabel lastNameLabel = new JLabel("Last Name:");
        lastNameField = new JTextField();
        lastNameField.setPreferredSize(new Dimension(200, 35));

        if (mode == FormMode.EDIT) {
            usernameField.setText(user.getUsername());
            emailField.setText(user.getEmail());
            contactField.setText(user.getContact());
            firstNameField.setText(user.getFirstName());
            lastNameField.setText(user.getLastName());
            passwordField.setText(user.getPassword());
        }

        // Buttons
        JButton saveBtn = createSaveButton();
        if (onBack != null) {
            JButton backBtn = createBackButton();
            mainPanel.add(backBtn, BorderLayout.SOUTH);
        }

        // Add to Panel
        namePanel.add(firstNameLabel, UIHelper.createGbc(0, 0, 0, 5, 5, 5));
        namePanel.add(lastNameLabel, UIHelper.createGbc(1, 0, 10, 5, 5, 5));
        namePanel.add(firstNameField, UIHelper.createGbc(0, 1, 5, 5, 10, 5));
        namePanel.add(lastNameField, UIHelper.createGbc(1, 1, 5, 5, 10, 5));

        formPanel.add(namePanel, UIHelper.createGbc(0, 0, 10, 0, 0, 0));
        formPanel.add(usernameLabel, UIHelper.createGbc(0, 1, 10, 5, 5, 5));
        formPanel.add(usernameField, UIHelper.createGbc(0, 2, 5, 5, 10, 5));
        formPanel.add(emailLabel, UIHelper.createGbc(0, 3, 10, 5, 5, 5));
        formPanel.add(emailField, UIHelper.createGbc(0, 4, 5, 5, 10, 5));
        formPanel.add(contactLabel, UIHelper.createGbc(0, 5, 10, 5, 5, 5));
        formPanel.add(contactField, UIHelper.createGbc(0, 6, 5, 5, 10, 5));
        formPanel.add(passwordLabel, UIHelper.createGbc(0, 7, 10, 5, 5, 5));
        formPanel.add(passwordField, UIHelper.createGbc(0, 8, 5, 5, 5, 5));
        formPanel.add(showPassword, UIHelper.createGbc(0, 9, 5, 5, 10, 5));
        formPanel.add(saveBtn, UIHelper.createGbc(0, 10, 10, 5, 0, 5));

        mainPanel.add(formPanel);
        add(mainPanel, BorderLayout.CENTER);
    }

    private JButton createSaveButton() {
        JButton saveBtn = new JButton(mode == FormMode.EDIT ? "Save Changes" : "Create " + role.getDisplayName());
        saveBtn.setBackground(new Color(135, 90, 224));
        saveBtn.setForeground(Color.WHITE);
        saveBtn.setFocusPainted(false);
        saveBtn.setFont(new Font("SansSerif", Font.BOLD, 14));
        saveBtn.setPreferredSize(new Dimension(100, 35));

        saveBtn.addActionListener(_ -> {
            try {
                if (mode == FormMode.EDIT) {
                    userService.updateUserProfile(user.getUserID(), usernameField.getText(), firstNameField.getText(), lastNameField.getText(), new String(passwordField.getPassword()), emailField.getText(), contactField.getText());
                    JOptionPane.showMessageDialog(null, "Profile updated successfully!");
                } else {
                    new UserService().registerUser(usernameField.getText(), firstNameField.getText(), lastNameField.getText(), new String(passwordField.getPassword()), emailField.getText(), contactField.getText(), role);
                    JOptionPane.showMessageDialog(null, role.getDisplayName() + " created successfully!");

                    if (onBack != null) {
                        onBack.run();
                    }
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        return saveBtn;
    }

    private JButton createBackButton() {
        JButton backBtn = new JButton("Back");
        backBtn.setBackground(new Color(135, 90, 224));
        backBtn.setForeground(Color.WHITE);
        backBtn.setFocusPainted(false);
        backBtn.setFont(new Font("SansSerif", Font.BOLD, 14));
        backBtn.setPreferredSize(new Dimension(100, 35));

        backBtn.addActionListener(_ -> onBack.run());

        return backBtn;
    }
}