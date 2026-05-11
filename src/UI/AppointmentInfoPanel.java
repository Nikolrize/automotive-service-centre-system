package UI;

import Model.Appointment;

import javax.swing.*;
import java.awt.*;

public class AppointmentInfoPanel extends JPanel {

    public AppointmentInfoPanel(Appointment appt, Runnable onBack) {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // Color Theme Define
        Color primary      = new Color(135, 90, 224);
        Color primaryLight = new Color(159, 126, 224);
        Color bodyBg       = Color.WHITE;
        Color borderCol    = new Color(220, 220, 220);
        Color keyCol       = new Color(120, 120, 120);
        Color valCol       = new Color(40, 40, 40);

        Font headerFont = new Font("SansSerif", Font.BOLD, 16);
        Font subFont    = new Font("SansSerif", Font.PLAIN, 12);
        Font keyFont    = new Font("SansSerif", Font.BOLD, 13);
        Font valFont    = new Font("SansSerif", Font.PLAIN, 13);
        Font btnFont    = new Font("SansSerif", Font.BOLD, 13);

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(primaryLight);
        header.setBorder(BorderFactory.createEmptyBorder(14, 18, 14, 18));

        JLabel title = new JLabel("Appointment Details");
        title.setFont(headerFont);
        title.setForeground(Color.WHITE);

        JLabel subtitle = new JLabel("ID: " + appt.getAppointmentID());
        subtitle.setFont(subFont);
        subtitle.setForeground(new Color(255, 255, 255));

        header.add(title, BorderLayout.NORTH);
        header.add(subtitle, BorderLayout.SOUTH);

        // Main Body
        JPanel body = new JPanel();
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setBackground(bodyBg);
        body.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));

        body.add(createRow("Appointment ID", appt.getAppointmentID(),         keyFont, valFont, keyCol, valCol, borderCol));
        body.add(Box.createVerticalStrut(4));
        body.add(createRow("Service",        appt.getService().getName(),      keyFont, valFont, keyCol, valCol, borderCol));
        body.add(Box.createVerticalStrut(4));
        body.add(createRow("Customer",       appt.getCustomer().getUsername(), keyFont, valFont, keyCol, valCol, borderCol));
        body.add(Box.createVerticalStrut(4));
        body.add(createRow("Technician",     appt.getTechnician() == null
                ? "-"
                : appt.getTechnician().getUsername(), keyFont, valFont, keyCol, valCol, borderCol));
        body.add(Box.createVerticalStrut(4));
        body.add(createRow("Date",           appt.getDate().toString(),        keyFont, valFont, keyCol, valCol, borderCol));
        body.add(Box.createVerticalStrut(4));
        body.add(createRow("Start Time",     appt.getStartTime().toString(),   keyFont, valFont, keyCol, valCol, borderCol));
        body.add(Box.createVerticalStrut(4));
        body.add(createRow("End Time",       appt.getEndTime().toString(),     keyFont, valFont, keyCol, valCol, borderCol));
        body.add(Box.createVerticalStrut(4));

        // Status row with coloured badge
        body.add(createStatusRow(appt.getIsCompleted(), keyFont, keyCol, borderCol));

        // Footer Section
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.LEFT, 18, 10));
        footer.setBackground(bodyBg);
        footer.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, borderCol));

        JButton backBtn = new JButton("← Back");
        backBtn.setFont(btnFont);
        backBtn.setBackground(primary);
        backBtn.setForeground(Color.WHITE);
        backBtn.setFocusPainted(false);
        backBtn.setBorderPainted(false);
        backBtn.setPreferredSize(new Dimension(110, 35));
        backBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        backBtn.addActionListener(_ -> onBack.run());
        UIHelper.addHoverEffect(backBtn, primary, primaryLight);

        footer.add(backBtn);

        add(header, BorderLayout.NORTH);
        add(new JScrollPane(body), BorderLayout.CENTER);
        add(footer, BorderLayout.SOUTH);
    }

    private JPanel createRow(String labelText, String value,
                             Font keyFont, Font valFont,
                             Color keyCol, Color valCol, Color borderCol) {
        JPanel row = new JPanel(new BorderLayout(16, 0));
        row.setBackground(Color.WHITE);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        row.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, borderCol),
                BorderFactory.createEmptyBorder(8, 0, 8, 0)
        ));

        JLabel key = new JLabel(labelText);
        key.setFont(keyFont);
        key.setForeground(keyCol);
        key.setPreferredSize(new Dimension(140, 24));

        JLabel val = new JLabel(value);
        val.setFont(valFont);
        val.setForeground(valCol);

        row.add(key, BorderLayout.WEST);
        row.add(val, BorderLayout.CENTER);

        return row;
    }

    private JPanel createStatusRow(boolean isCompleted,
                                   Font keyFont, Color keyCol, Color borderCol) {
        JPanel row = new JPanel(new BorderLayout(16, 0));
        row.setBackground(Color.WHITE);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        row.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, borderCol),
                BorderFactory.createEmptyBorder(8, 0, 8, 0)
        ));

        JLabel key = new JLabel("Status");
        key.setFont(keyFont);
        key.setForeground(keyCol);
        key.setPreferredSize(new Dimension(140, 24));

        // Coloured pill badge
        JLabel badge = getJLabel(isCompleted);

        row.add(key, BorderLayout.WEST);
        row.add(badge, BorderLayout.CENTER);

        return row;
    }

    private JLabel getJLabel(boolean isCompleted) {
        Color badgeBg  = isCompleted ? new Color(198, 239, 206) : new Color(255, 235, 205);
        Color badgeFg  = isCompleted ? new Color(0, 128, 0)     : new Color(180, 100, 0);
        String badgeText = isCompleted ? "Completed" : "Pending";

        JLabel badge = new JLabel(badgeText, SwingConstants.CENTER);
        badge.setFont(new Font("SansSerif", Font.BOLD, 12));
        badge.setForeground(badgeFg);
        badge.setBackground(badgeBg);
        badge.setOpaque(true);
        badge.setPreferredSize(new Dimension(90, 24));
        badge.setBorder(BorderFactory.createEmptyBorder(2, 10, 2, 10));
        return badge;
    }
}