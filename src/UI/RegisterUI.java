package UI;

import Service.UserService;
import Model.User;

import javax.swing.*;
import java.awt.*;

public class RegisterUI extends JPanel {

    private JTextField usernameField, emailField, contactField;
    private JPasswordField passwordField;
    private JComboBox<String> roleComboBox;
    private JButton registerButton;

    private UserService userService;

    public RegisterUI(User currentUser) {
        userService = new UserService();
        setLayout(new GridLayout(6, 2, 5, 5));

        usernameField = new JTextField();
        passwordField = new JPasswordField();
        emailField = new JTextField();
        contactField = new JTextField();

        String[] allowedRoles;
        if (currentUser instanceof Model.Manager) {
            allowedRoles = new String[]{"Manager", "CounterStaff", "Technician"};
        } else if (currentUser instanceof Model.CounterStaff) {
            allowedRoles = new String[]{"Customer"};
        } else {
            allowedRoles = new String[0]; // Should never happen
        }

        roleComboBox = new JComboBox<>(allowedRoles);
        registerButton = new JButton("Register");

        add(new JLabel("Username:"));
        add(usernameField);
        add(new JLabel("Password:"));
        add(passwordField);
        add(new JLabel("Email:"));
        add(emailField);
        add(new JLabel("Contact:"));
        add(contactField);
        add(new JLabel("Role:"));
        add(roleComboBox);
        add(new JLabel()); // empty cell
        add(registerButton);

        registerButton.addActionListener(e -> registerUser());
    }

    private void registerUser() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();
        String email = emailField.getText().trim();
        String contact = contactField.getText().trim();
        String role = (String) roleComboBox.getSelectedItem();

        if (username.isEmpty() || password.isEmpty() || email.isEmpty() || contact.isEmpty() || role == null) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields.");
            return;
        }

        userService.registerUser(username, password, email, contact, role);
        JOptionPane.showMessageDialog(this, role + " registered successfully!");

        usernameField.setText("");
        passwordField.setText("");
        emailField.setText("");
        contactField.setText("");
    }
}