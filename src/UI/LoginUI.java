package UI;

import Model.User;
import Service.UserService;

import javax.swing.*;
import java.awt.*;

public class LoginUI extends JFrame {
    private final JTextField usernameField;
    private final JPasswordField passwordField;

    public LoginUI() {
        setTitle("APU Automotive Service Centre (APU – ASC)");
        setSize(400, 250);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // Main Panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(Color.WHITE);

        JLabel titleLabel = new JLabel("Login", SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        titleLabel.setForeground(new Color(50, 50, 50));

        // Form Panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);

        JLabel usernameLabel = new JLabel("Username:");
        usernameField = new JTextField(15);

        JLabel passwordLabel = new JLabel("Password:");
        passwordField = new JPasswordField(15);

        // Button Panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(Color.WHITE);

        JButton loginButton = new JButton("Login");
        loginButton.setBackground(new Color(70, 130, 180));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);
        loginButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        loginButton.setPreferredSize(new Dimension(100, 35));

        // Action listener
        loginButton.addActionListener(e -> {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());

            User user = new UserService().loginUser(username, password);
            if (user != null) {
                JOptionPane.showMessageDialog(this, "Successfully logged in as " + username, "Login Success", JOptionPane.INFORMATION_MESSAGE);
                user.openMenu();
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Invalid username or password", "Login Failed", JOptionPane.ERROR_MESSAGE);
            }
        });

        formPanel.add(usernameLabel, UIHelper.createGbc(0, 0));
        formPanel.add(usernameField, UIHelper.createGbc(1, 0));
        formPanel.add(passwordLabel, UIHelper.createGbc(0, 1));
        formPanel.add(passwordField, UIHelper.createGbc(1, 1));

        buttonPanel.add(loginButton);

        mainPanel.add(titleLabel, BorderLayout.NORTH);
        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
        setVisible(true);
    }
}