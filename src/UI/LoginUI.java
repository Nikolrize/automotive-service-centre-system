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
        setSize(1280, 720);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        ImageIcon appIcon = new ImageIcon("src/Resource/appIcon.png");
        setIconImage(appIcon.getImage());

        // Main Panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);

        // Left Panel
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBorder(BorderFactory.createEmptyBorder(200, 50, 200, 50));
        leftPanel.setBackground(Color.LIGHT_GRAY);

        JLabel titleLabel = new JLabel("Login", SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 20));

        // Form Panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.LIGHT_GRAY);

        JLabel usernameLabel = new JLabel("Username:");
        usernameField = new JTextField(25);
        usernameField.setPreferredSize(new Dimension(0, 35));

        JLabel passwordLabel = new JLabel("Password:");
        passwordField = new JPasswordField(25);
        passwordField.setPreferredSize(new Dimension(0, 35));
        JCheckBox showPassword = UIHelper.showPassword(passwordField);
        showPassword.setBackground(Color.LIGHT_GRAY);

        // Button Panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(Color.LIGHT_GRAY);

        JButton loginButton = createLoginButton();
        getRootPane().setDefaultButton(loginButton);

        // Right Panel
        JPanel rightPanel = new JPanel();
        rightPanel.setBackground(Color.WHITE);
        rightPanel.setLayout(new GridBagLayout());

        ImageIcon loginIcon = new ImageIcon("src/Resource/login.png");
        Image loginImg = loginIcon.getImage().getScaledInstance(400, 400, Image.SCALE_SMOOTH);
        JLabel loginImgLabel = new JLabel(new ImageIcon(loginImg));

        // Add to Panels
        formPanel.add(usernameLabel, UIHelper.createGbc(0,0,5, 5, 5, 5));
        formPanel.add(usernameField, UIHelper.createGbc(0,1,5, 5, 10, 5));
        formPanel.add(passwordLabel, UIHelper.createGbc(0, 2,10, 5, 5, 5));
        formPanel.add(passwordField, UIHelper.createGbc(0,3,5, 5, 5, 5));
        formPanel.add(showPassword, UIHelper.createGbc(0,4,5, 5, 5, 5));

        buttonPanel.add(loginButton);

        leftPanel.add(titleLabel, BorderLayout.NORTH);
        leftPanel.add(formPanel, BorderLayout.CENTER);
        leftPanel.add(buttonPanel, BorderLayout.SOUTH);

        rightPanel.add(loginImgLabel);

        mainPanel.add(leftPanel, BorderLayout.WEST);
        mainPanel.add(rightPanel, BorderLayout.CENTER);

        add(mainPanel);
        setVisible(true);
    }

    private JButton createLoginButton() {
        JButton loginButton = new JButton("Login");
        loginButton.setBackground(new Color(135, 90, 224));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);
        loginButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        loginButton.setPreferredSize(new Dimension(100, 35));

        loginButton.addActionListener(e -> {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());

            User user = new UserService().loginUser(username, password);
            if (user != null) {
                JOptionPane.showMessageDialog(this, "Successfully logged in as " + username, "Login Success", JOptionPane.INFORMATION_MESSAGE);
                user.openDashboard();
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Invalid username or password", "Login Failed", JOptionPane.ERROR_MESSAGE);
            }
        });
        return loginButton;
    }
}