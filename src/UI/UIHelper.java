package UI;

import Enum.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
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

    public static JTable createTable(DefaultTableModel model) {
        JTable table = new JTable(model);

        table.setRowHeight(40);
        table.setFont(new Font("SansSerif", Font.PLAIN, 13));
        table.setSelectionBackground(new Color(230, 220, 255));
        table.setGridColor(new Color(240, 240, 240));
        table.setShowVerticalLines(false);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("SansSerif", Font.BOLD, 14));
        header.setBackground(new Color(245, 245, 245));

        table.setAutoCreateRowSorter(true);

        return table;
    }

    public static JPanel createSearchPanel(JTextField searchField, Runnable onSearch) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setMaximumSize(new Dimension(450, 35));

        JLabel searchLabel = new JLabel("Search:");
        searchLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));

        searchField.setPreferredSize(new Dimension(250, 35));

        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                onSearch.run();
            }

            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                onSearch.run();
            }

            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                onSearch.run();
            }
        });

        panel.add(searchLabel, BorderLayout.WEST);
        panel.add(searchField, BorderLayout.CENTER);
        return panel;
    }

    public static JPanel createCardPanel(String title) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 230, 230)),
                BorderFactory.createEmptyBorder(12, 14, 12, 14)
        ));

        if (title != null) {
            JLabel label = new JLabel(title);
            label.setFont(new Font("SansSerif", Font.BOLD, 14));
            label.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
            label.setAlignmentX(Component.LEFT_ALIGNMENT);
            panel.add(label);
        }

        return panel;
    }

    public static JButton createStyledButton(String text, ButtonMode mode) {
        JButton btn = new JButton(text);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setFont(new Font("SansSerif", Font.BOLD, 14));
        btn.setPreferredSize(new Dimension(100, 35));

        if (mode.equals(ButtonMode.PRIMARY)) {
            btn.setBackground(new Color(135, 90, 224));
        } else if (mode.equals(ButtonMode.CREATE)) {
            btn.setBackground(new Color(66, 135, 245));
        } else if (mode.equals(ButtonMode.DESTRUCTIVE)){
            btn.setBackground(new Color(220, 53, 69));
        } else if (mode.equals(ButtonMode.RESTORE)){
            btn.setBackground(new Color(40, 167, 69));
        }

        return btn;
    }
}
