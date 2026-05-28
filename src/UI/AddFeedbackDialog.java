package UI;

import Model.Appointment;
import Model.Technician;
import Service.FeedbackService;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;

public class AddFeedbackDialog extends JDialog {

    public AddFeedbackDialog(Component parent, Appointment appt,
                             Technician technician, FeedbackService feedbackService,
                             Runnable onSuccess) {

        super((Frame) SwingUtilities.getWindowAncestor(parent), true);
        setTitle("Add Feedback");
        setSize(550, 450);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());
        setResizable(false);

        // Colours
        Color headerBg  = new Color(135, 90, 224);
        Color headerFg  = Color.WHITE;
        Color bodyBg    = Color.WHITE;
        Color accentBtn = new Color(84, 0, 255);
        Color cancelBtn = new Color(189, 195, 199);
        Font  headerFont = new Font("SansSerif", Font.BOLD, 16);
        Font  labelFont  = new Font("SansSerif", Font.PLAIN, 13);
        Font  btnFont    = new Font("SansSerif", Font.BOLD, 13);

        // Dialog's Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(headerBg);
        header.setBorder(BorderFactory.createEmptyBorder(14, 18, 14, 18));

        JLabel title = new JLabel("Add Feedback");
        title.setFont(headerFont);
        title.setForeground(headerFg);

        JLabel subtitle = new JLabel("Appointment: " + appt.getAppointmentID());
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 12));
        subtitle.setForeground(new Color(255, 255, 255));

        header.add(title, BorderLayout.NORTH);
        header.add(subtitle, BorderLayout.SOUTH);

        // Dialog's body
        JPanel body = new JPanel();
        body.setBackground(bodyBg);
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setBorder(BorderFactory.createEmptyBorder(16, 18, 8, 18));

        JLabel label = new JLabel("Feedback Message:");
        label.setFont(labelFont);
        label.setForeground(new Color(44, 62, 80));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);

        JTextArea commentArea = new JTextArea(8, 50);
        commentArea.setFont(new Font("SansSerif", Font.PLAIN, 13));
        commentArea.setLineWrap(true);
        commentArea.setWrapStyleWord(true);
        commentArea.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(189, 195, 199)),
                BorderFactory.createEmptyBorder(6, 8, 6, 8)
        ));

        final int MAX_CHARS = 500;
        commentArea.setDocument(new PlainDocument() {
            @Override
            public void insertString(int offs, String str, AttributeSet a)
                    throws BadLocationException {
                if (str == null) return;
                if ((getLength() + str.length()) <= MAX_CHARS) {
                    super.insertString(offs, str, a);
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(commentArea);
        scrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);
        scrollPane.setBorder(null);

        // Character counter
        JLabel charCountLabel = new JLabel("0 / " + MAX_CHARS + " characters");
        charCountLabel.setFont(new Font("SansSerif", Font.PLAIN, 11));
        charCountLabel.setForeground(new Color(150, 150, 150));
        charCountLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        commentArea.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            private void update() {
                int len = commentArea.getText().length();
                charCountLabel.setText(len + " / " + MAX_CHARS + " characters");
                charCountLabel.setForeground(len >= MAX_CHARS
                        ? new Color(231, 76, 60)
                        : new Color(150, 150, 150));
                // Reset border to normal while user is typing
                commentArea.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(189, 195, 199)),
                        BorderFactory.createEmptyBorder(6, 8, 6, 8)
                ));
            }
            public void insertUpdate(javax.swing.event.DocumentEvent e) { update(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { update(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { update(); }
        });

        body.add(label);
        body.add(Box.createVerticalStrut(8));
        body.add(scrollPane);
        body.add(Box.createVerticalStrut(4));
        body.add(charCountLabel);

        // Dialog's Footer
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        footer.setBackground(bodyBg);
        footer.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(220, 220, 220)));

        JButton cancelButton = new JButton("Cancel");
        cancelButton.setFont(btnFont);
        cancelButton.setBackground(cancelBtn);
        cancelButton.setForeground(new Color(255, 255, 255));
        cancelButton.setFocusPainted(false);
        cancelButton.setBorderPainted(false);
        cancelButton.setPreferredSize(new Dimension(100, 35));
        cancelButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        cancelButton.addActionListener(_ -> dispose());

        JButton submitButton = new JButton("Submit");
        submitButton.setFont(btnFont);
        submitButton.setBackground(accentBtn);
        submitButton.setForeground(Color.WHITE);
        submitButton.setFocusPainted(false);
        submitButton.setBorderPainted(false);
        submitButton.setPreferredSize(new Dimension(100, 35));
        submitButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        submitButton.addActionListener(_ -> {
            String comment = commentArea.getText().trim();

            // Validate: empty
            if (comment.isEmpty()) {
                commentArea.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(231, 76, 60), 2),
                        BorderFactory.createEmptyBorder(6, 8, 6, 8)
                ));
                JOptionPane.showMessageDialog(this,
                        "Feedback message cannot be empty.",
                        "Validation Error",
                        JOptionPane.WARNING_MESSAGE);
                commentArea.requestFocus();
                return;
            }

            // Validate: exceeds max length (safety net beyond the Document filter)
            if (comment.length() > MAX_CHARS) {
                commentArea.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(231, 76, 60), 2),
                        BorderFactory.createEmptyBorder(6, 8, 6, 8)
                ));
                JOptionPane.showMessageDialog(this,
                        "Feedback message cannot exceed " + MAX_CHARS + " characters.\n"
                                + "Current length: " + comment.length() + " characters.",
                        "Validation Error",
                        JOptionPane.WARNING_MESSAGE);
                commentArea.requestFocus();
                return;
            }

            // Reset border on valid submission
            commentArea.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(189, 195, 199)),
                    BorderFactory.createEmptyBorder(6, 8, 6, 8)
            ));

            try {
                feedbackService.addFeedback(appt, technician, comment);
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