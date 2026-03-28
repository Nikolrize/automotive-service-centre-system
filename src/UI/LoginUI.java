package UI;

import Model.User;
import Service.UserService;
import javax.swing.*;
import java.awt.*;

public class LoginUI extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;

    public LoginUI() {
        setTitle("Login");
        setSize(400, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(3, 2));

        usernameField = new JTextField();
        passwordField = new JPasswordField();
        loginButton = new JButton("Login");

        add(new JLabel("Username:"));
        add(usernameField);
        add(new JLabel("Password:"));
        add(passwordField);
        add(loginButton);

        loginButton.addActionListener(e -> {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());

            User user = new UserService().loginUser(username, password);
            if (user != null) {
                user.openMenu();
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Invalid username or password");
            }
        });

        setVisible(true);
    }
}