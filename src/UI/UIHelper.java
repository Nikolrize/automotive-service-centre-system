package UI;

import Enum.*;

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

        showPassword.addActionListener(_ -> {
            if (showPassword.isSelected()) {
                passwordField.setEchoChar((char) 0);
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
        panel.add(Box.createVerticalStrut(10));

        return panel;
    }
}
