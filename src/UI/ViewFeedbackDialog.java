package UI;

import javax.swing.*;
import java.awt.*;

public class ViewFeedbackDialog extends JDialog {

    public ViewFeedbackDialog(Component parent, String feedbackID, String feedbackText,
                              String technician, String customer,
                              String dateTime, String appointmentID) {

        super((Frame) SwingUtilities.getWindowAncestor(parent), "Feedback Details", true);
        setSize(580, 450);
        setLocationRelativeTo(parent);
        setResizable(false);
        setLayout(new BorderLayout());

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(135, 90, 224));
        header.setBorder(BorderFactory.createEmptyBorder(14, 18, 14, 18));

        JLabel titleLabel = new JLabel("Feedback Details");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLabel.setForeground(Color.WHITE);

        JLabel subtitleLabel = new JLabel("Appointment: " + appointmentID);
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subtitleLabel.setForeground(Color.WHITE);

        header.add(titleLabel,    BorderLayout.NORTH);
        header.add(subtitleLabel, BorderLayout.SOUTH);

        // Body
        JPanel body = new JPanel(new BorderLayout());
        body.setBackground(Color.WHITE);
        body.setBorder(BorderFactory.createEmptyBorder(16, 18, 16, 18));

        JPanel fieldsPanel = new JPanel();
        fieldsPanel.setBackground(Color.WHITE);
        fieldsPanel.setLayout(new BoxLayout(fieldsPanel, BoxLayout.Y_AXIS));

        fieldsPanel.add(createField("Feedback ID", feedbackID));
        fieldsPanel.add(Box.createVerticalStrut(8));
        fieldsPanel.add(createField("Technician",  technician));
        fieldsPanel.add(Box.createVerticalStrut(8));
        fieldsPanel.add(createField("Customer",    customer));
        fieldsPanel.add(Box.createVerticalStrut(8));
        fieldsPanel.add(createField("Date & Time", dateTime));
        fieldsPanel.add(Box.createVerticalStrut(12));

        // Message section
        JPanel msgPanel = new JPanel(new BorderLayout());
        msgPanel.setBackground(Color.WHITE);

        JLabel msgLabel = new JLabel("Feedback Message:");
        msgLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        msgLabel.setForeground(new Color(120, 120, 120));
        msgLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 6, 0));

        JTextArea msgArea = new JTextArea(feedbackText);
        msgArea.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        msgArea.setLineWrap(true);
        msgArea.setWrapStyleWord(true);
        msgArea.setEditable(false);
        msgArea.setFocusable(false);
        msgArea.setBackground(new Color(245, 245, 250));
        msgArea.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220)),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));

        JScrollPane msgScroll = new JScrollPane(msgArea);
        msgScroll.setBorder(null);

        msgPanel.add(msgLabel,  BorderLayout.NORTH);
        msgPanel.add(msgScroll, BorderLayout.CENTER);

        body.add(fieldsPanel, BorderLayout.NORTH);
        body.add(msgPanel,    BorderLayout.CENTER);

        // Footer
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        footer.setBackground(Color.WHITE);
        footer.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(220, 220, 220)));

        JButton closeBtn = new JButton("Close");
        closeBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        closeBtn.setBackground(new Color(189, 195, 199));
        closeBtn.setForeground(Color.WHITE);
        closeBtn.setFocusPainted(false);
        closeBtn.setBorderPainted(false);
        closeBtn.setPreferredSize(new Dimension(100, 35));
        closeBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        closeBtn.addActionListener(_ -> dispose());
        footer.add(closeBtn);

        add(header, BorderLayout.NORTH);
        add(body,   BorderLayout.CENTER);   // ← no JScrollPane wrapper
        add(footer, BorderLayout.SOUTH);
    }

    private JPanel createField(String label, String value) {
        JPanel row = new JPanel(new BorderLayout());
        row.setBackground(Color.WHITE);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 24));

        JLabel labelComp = new JLabel(label + ":");
        labelComp.setFont(new Font("Segoe UI", Font.BOLD, 13));
        labelComp.setForeground(new Color(120, 120, 120));
        labelComp.setPreferredSize(new Dimension(120, 24));

        JLabel valueComp = new JLabel(value == null ? "-" : value);
        valueComp.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        valueComp.setForeground(new Color(40, 40, 40));

        row.add(labelComp, BorderLayout.WEST);
        row.add(valueComp, BorderLayout.CENTER);
        return row;
    }
}