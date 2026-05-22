package UI;

import javax.swing.*;
import java.awt.*;

public class ViewCommentDialog extends JDialog {

    // constructor for technician
    public ViewCommentDialog(Component parent, String commentID, String commentText,
                             String rating, String customer, String appointmentID) {
        this(parent, commentID, commentText, rating, customer, appointmentID, "-"); //dash to replace the service
    }

    // constructor for customer / richer detail view
    public ViewCommentDialog(Component parent, String commentID, String commentText,
                             String rating, String customer, String appointmentID, String service) {

        super((Frame) SwingUtilities.getWindowAncestor(parent), "Comment Details", true);
        setSize(580, 440);
        setLocationRelativeTo(parent);
        setResizable(false);
        setLayout(new BorderLayout());

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(135, 90, 224));
        header.setBorder(BorderFactory.createEmptyBorder(14, 18, 14, 18));

        JLabel titleLabel = new JLabel("Comment Details");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLabel.setForeground(Color.WHITE);

        JLabel subtitleLabel = new JLabel("Appointment: " + appointmentID);
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subtitleLabel.setForeground(Color.WHITE);

        header.add(titleLabel, BorderLayout.NORTH);
        header.add(subtitleLabel, BorderLayout.SOUTH);

        JPanel body = new JPanel(new BorderLayout());
        body.setBackground(Color.WHITE);
        body.setBorder(BorderFactory.createEmptyBorder(16, 18, 16, 18));

        JPanel fieldsPanel = new JPanel();
        fieldsPanel.setBackground(Color.WHITE);
        fieldsPanel.setLayout(new BoxLayout(fieldsPanel, BoxLayout.Y_AXIS));

        fieldsPanel.add(createField("Comment ID", commentID));
        fieldsPanel.add(Box.createVerticalStrut(8));
        fieldsPanel.add(createField("Customer", customer));
        fieldsPanel.add(Box.createVerticalStrut(8));
        fieldsPanel.add(createField("Service", service));
        fieldsPanel.add(Box.createVerticalStrut(8));
        fieldsPanel.add(createField("Rating", rating));
        fieldsPanel.add(Box.createVerticalStrut(12));

        JPanel msgPanel = new JPanel(new BorderLayout());
        msgPanel.setBackground(Color.WHITE);

        JLabel msgLabel = new JLabel("Comment:");
        msgLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        msgLabel.setForeground(new Color(120, 120, 120));
        msgLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 6, 0));

        JTextArea msgArea = new JTextArea(commentText);
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

        msgPanel.add(msgLabel, BorderLayout.NORTH);
        msgPanel.add(msgScroll, BorderLayout.CENTER);

        body.add(fieldsPanel, BorderLayout.NORTH);
        body.add(msgPanel, BorderLayout.CENTER);

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
        add(body, BorderLayout.CENTER);
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