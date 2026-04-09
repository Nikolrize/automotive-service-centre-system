package UI;

import Model.User;
import Service.UserService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class UIHelper {
    public static GridBagConstraints createGbc(int x, int y, int topInset, int leftInset, int bottomInset, int rightInset) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = x;
        gbc.gridy = y;

        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(topInset, leftInset, bottomInset, rightInset);

        return gbc;
    }

    public static JCheckBox showPassword(JPasswordField passwordField) {
        JCheckBox showPassword = new JCheckBox("Show Password");
        char defaultEchoChar = passwordField.getEchoChar();

        showPassword.addActionListener(e -> {
            if (showPassword.isSelected()) {
                passwordField.setEchoChar((char)0);
            } else {
                passwordField.setEchoChar(defaultEchoChar);
            }
        });

        return showPassword;
    }

    public static void addHoverEffect(JButton button, Color normalColor, Color hoverColor) {
        button.setBackground(normalColor);

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(hoverColor);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(normalColor);
            }
        });
    }

    public static JPanel createSectionPanel(String title) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));
        panel.setBackground(Color.WHITE);

        JLabel titleLabel = new JLabel(title, SwingConstants.LEFT);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JSeparator separator = new JSeparator(SwingConstants.HORIZONTAL);
        separator.setForeground(Color.LIGHT_GRAY);

        panel.add(titleLabel);
        panel.add(Box.createVerticalStrut(10));
        panel.add(separator);

        return panel;
    }

    public static JPanel editProfilePanel(User user, UserService userService) {
        JPanel mainPanel = createSectionPanel("Edit Profile");

        // Form Panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel usernameLabel = new JLabel("Username:");
        JTextField usernameField = new JTextField(user.getUsername());
        usernameField.setPreferredSize(new Dimension(300, 35));

        JLabel emailLabel = new JLabel("Email:");
        JTextField emailField = new JTextField(user.getEmail());
        emailField.setPreferredSize(new Dimension(300, 35));

        JLabel contactLabel = new JLabel("Contact Number:");
        JTextField contactField = new JTextField(user.getContact());
        contactField.setPreferredSize(new Dimension(300, 35));

        JLabel passwordLabel = new JLabel("Password:");
        JPasswordField passwordField = new JPasswordField(user.getPassword());
        passwordField.setPreferredSize(new Dimension(300, 35));
        JCheckBox showPassword = UIHelper.showPassword(passwordField);
        showPassword.setBackground(Color.WHITE);

        // First Name + Last Name Panel
        JPanel namePanel = new JPanel(new GridBagLayout());
        namePanel.setBackground(Color.WHITE);

        JLabel firstNameLabel = new JLabel("First Name:");
        JTextField firstNameField = new JTextField(user.getFirstName());
        firstNameField.setPreferredSize(new Dimension(150, 35));

        JLabel lastNameLabel = new JLabel("Last Name:");
        JTextField lastNameField = new JTextField(user.getLastName());
        lastNameField.setPreferredSize(new Dimension(150, 35));

        // Save Button
        JButton saveBtn = new JButton("Save Changes");
        saveBtn.setBackground(new Color(135, 90, 224));
        saveBtn.setForeground(Color.WHITE);
        saveBtn.setFocusPainted(false);
        saveBtn.setFont(new Font("SansSerif", Font.BOLD, 14));
        saveBtn.setPreferredSize(new Dimension(100, 35));

        saveBtn.addActionListener(e -> {
            try {
                userService.updateUserProfile(user.getUserID(), usernameField.getText(), firstNameField.getText(), lastNameField.getText(), new String(passwordField.getPassword()), emailField.getText(), contactField.getText());
                JOptionPane.showMessageDialog(null, "Profile updated successfully!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Add to Panel
        namePanel.add(firstNameLabel, UIHelper.createGbc(0, 0, 10, 5, 5, 5));
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
        formPanel.add(saveBtn, UIHelper.createGbc(0, 10, 10,5,10,5));

        mainPanel.add(formPanel);

        return mainPanel;
    }
}
