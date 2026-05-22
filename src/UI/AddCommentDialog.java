package UI;

import Model.Appointment;
import Model.Customer;
import Service.CommentService;

import javax.swing.*;
import java.awt.*;

public class AddCommentDialog extends JDialog {
    private int selectedRating = 0;

    public AddCommentDialog(Component parent,
                            Customer customer,
                            Appointment appointment,
                            CommentService commentService,
                            Runnable onSuccess) {

        super((Frame) SwingUtilities.getWindowAncestor(parent), "Submit Comment", true);
        setSize(580, 460);
        setLocationRelativeTo(parent);
        setResizable(false);
        setLayout(new BorderLayout());

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

        JLabel title = new JLabel("Submit Comment");
        title.setFont(headerFont);
        title.setForeground(Color.WHITE);

        JLabel subtitle = new JLabel("Appointment: " + appointment.getAppointmentID());
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subtitle.setForeground(Color.WHITE);

        header.add(title, BorderLayout.NORTH);
        header.add(subtitle, BorderLayout.SOUTH);

        JPanel body = new JPanel();
        body.setBackground(bodyBg);
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setBorder(BorderFactory.createEmptyBorder(16, 18, 8, 18));

        JLabel serviceLabel = new JLabel("Service: " + appointment.getService().getName());
        serviceLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        serviceLabel.setForeground(new Color(44, 62, 80));
        serviceLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel ratingLabel = new JLabel("Rating:");
        ratingLabel.setFont(labelFont);
        ratingLabel.setForeground(new Color(44, 62, 80));
        ratingLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel ratingPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        ratingPanel.setBackground(Color.WHITE);
        ratingPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        ButtonGroup ratingGroup = new ButtonGroup();

        for (int i = 1; i <= 5; i++) {
            JRadioButton radioButton = new JRadioButton(String.valueOf(i));
            radioButton.setBackground(Color.WHITE);
            radioButton.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            int ratingValue = i;

            radioButton.addActionListener(_ -> selectedRating = ratingValue);

            ratingGroup.add(radioButton);
            ratingPanel.add(radioButton);
        }

        JLabel commentLabel = new JLabel("Comment:");
        commentLabel.setFont(labelFont);
        commentLabel.setForeground(new Color(44, 62, 80));
        commentLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JTextArea commentArea = new JTextArea(7, 50);
        commentArea.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        commentArea.setLineWrap(true);
        commentArea.setWrapStyleWord(true);
        commentArea.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(189, 195, 199)),
                BorderFactory.createEmptyBorder(6, 8, 6, 8)
        ));

        JScrollPane scrollPane = new JScrollPane(commentArea);
        scrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);
        scrollPane.setBorder(null);

        body.add(serviceLabel);
        body.add(Box.createVerticalStrut(14));
        body.add(ratingLabel);
        body.add(Box.createVerticalStrut(6));
        body.add(ratingPanel);
        body.add(Box.createVerticalStrut(14));
        body.add(commentLabel);
        body.add(Box.createVerticalStrut(6));
        body.add(scrollPane);

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

        JButton submitButton = new JButton("Submit");
        submitButton.setFont(buttonFont);
        submitButton.setBackground(primaryBtn);
        submitButton.setForeground(Color.WHITE);
        submitButton.setFocusPainted(false);
        submitButton.setBorderPainted(false);
        submitButton.setPreferredSize(new Dimension(100, 35));
        submitButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        submitButton.addActionListener(_ -> {
            try {
                commentService.createComment(
                        customer,
                        appointment,
                        String.valueOf(selectedRating),
                        commentArea.getText()
                );

                dispose();
                onSuccess.run();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        footer.add(cancelButton);
        footer.add(submitButton);

        add(header, BorderLayout.NORTH);
        add(body, BorderLayout.CENTER);
        add(footer, BorderLayout.SOUTH);
    }
}