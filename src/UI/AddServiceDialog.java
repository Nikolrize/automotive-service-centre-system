package UI;

import Service.ServiceManagementService;

import javax.swing.*;
import java.awt.*;

public class AddServiceDialog extends JDialog {

    public AddServiceDialog(Component parent,
                            ServiceManagementService serviceManagementService,
                            Runnable onSuccess) {

        super((Frame) SwingUtilities.getWindowAncestor(parent), true);
        setTitle("Create Service");
        setSize(560, 470);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());
        setResizable(false);

        Color headerBg = new Color(135, 90, 224);
        Color bodyBg = Color.WHITE;
        Color primaryBtn = new Color(84, 0, 255);
        Color cancelBtn = new Color(189, 195, 199);

        Font headerFont = new Font("Segoe UI", Font.BOLD, 16);
        Font labelFont = new Font("Segoe UI", Font.PLAIN, 13);
        Font buttonFont = new Font("Segoe UI", Font.BOLD, 13);

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(headerBg);
        header.setBorder(BorderFactory.createEmptyBorder(14, 18, 14, 18));

        JLabel title = new JLabel("Create Service");
        title.setFont(headerFont);
        title.setForeground(Color.WHITE);

        JLabel subtitle = new JLabel("Add a new service for the service catalogue");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subtitle.setForeground(Color.WHITE);

        header.add(title, BorderLayout.NORTH);
        header.add(subtitle, BorderLayout.SOUTH);

        JPanel body = new JPanel();
        body.setBackground(bodyBg);
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setBorder(BorderFactory.createEmptyBorder(18, 18, 14, 18));

        JTextField nameField = new JTextField();
        JComboBox<String> typeComboBox = new JComboBox<>(new String[]{"Normal", "Major"});
        JTextField priceField = new JTextField();
        JTextField durationField = new JTextField();

        body.add(createTextInputField("Service Name:", nameField, labelFont));
        body.add(Box.createVerticalStrut(14));
        body.add(createComboInputField("Type:", typeComboBox, labelFont));
        body.add(Box.createVerticalStrut(14));
        body.add(createTextInputField("Price (RM):", priceField, labelFont));
        body.add(Box.createVerticalStrut(14));
        body.add(createTextInputField("Duration (Hours):", durationField, labelFont));

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        footer.setBackground(bodyBg);
        footer.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(220, 220, 220)));

        JButton cancelButton = new JButton("Cancel");
        cancelButton.setFont(buttonFont);
        cancelButton.setBackground(cancelBtn);
        cancelButton.setForeground(Color.WHITE);
        cancelButton.setFocusPainted(false);
        cancelButton.setBorderPainted(false);
        cancelButton.setPreferredSize(new Dimension(100, 35));
        cancelButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        cancelButton.addActionListener(_ -> dispose());

        JButton createButton = new JButton("Create");
        createButton.setFont(buttonFont);
        createButton.setBackground(primaryBtn);
        createButton.setForeground(Color.WHITE);
        createButton.setFocusPainted(false);
        createButton.setBorderPainted(false);
        createButton.setPreferredSize(new Dimension(100, 35));
        createButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        createButton.addActionListener(_ -> {
            try {
                serviceManagementService.createService(
                        nameField.getText(),
                        typeComboBox.getSelectedItem().toString(),
                        priceField.getText(),
                        durationField.getText()
                );

                dispose();
                onSuccess.run();

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        footer.add(cancelButton);
        footer.add(createButton);

        add(header, BorderLayout.NORTH);
        add(body, BorderLayout.CENTER);
        add(footer, BorderLayout.SOUTH);
    }

    private JPanel createTextInputField(String labelText, JTextField textField, Font labelFont) {
        JPanel panel = new JPanel();
        panel.setBackground(Color.WHITE);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel label = new JLabel(labelText);
        label.setFont(labelFont);
        label.setForeground(new Color(44, 62, 80));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);

        textField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        textField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        textField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(189, 195, 199)),
                BorderFactory.createEmptyBorder(6, 8, 6, 8)
        ));
        textField.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.add(label);
        panel.add(Box.createVerticalStrut(6));
        panel.add(textField);

        return panel;
    }

    private JPanel createComboInputField(String labelText, JComboBox<String> comboBox, Font labelFont) {
        JPanel panel = new JPanel();
        panel.setBackground(Color.WHITE);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel label = new JLabel(labelText);
        label.setFont(labelFont);
        label.setForeground(new Color(44, 62, 80));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);

        comboBox.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        comboBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        comboBox.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.add(label);
        panel.add(Box.createVerticalStrut(6));
        panel.add(comboBox);

        return panel;
    }
}