package UI;

import javax.swing.*;
import java.awt.*;

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
}
