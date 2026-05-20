package UI;

import Model.Appointment;
import Enum.ButtonMode;

import javax.swing.*;
import java.awt.*;

public class AppointmentInfoPanel extends JPanel {

    public AppointmentInfoPanel(Appointment appt, Runnable onBack) {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        JPanel mainPanel = UIHelper.createSectionPanel("Appointment Details");

        // Set 2x2 grid
        JPanel gridPanel = new JPanel(new GridLayout(2, 2, 5, 5));
        gridPanel.setBackground(Color.WHITE);
        gridPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Card Section: Appointment Info
        JPanel appointmentCard = UIHelper.createCardPanel("Appointment Info");
        appointmentCard.setAlignmentX(Component.LEFT_ALIGNMENT);
        appointmentCard.add(createField("Appointment ID", appt.getAppointmentID()));
        appointmentCard.add(Box.createVerticalStrut(6));
        appointmentCard.add(createBadgeField("Status", appt.getIsCompleted()));
        appointmentCard.add(Box.createVerticalStrut(6));
        appointmentCard.add(createField("Date",       appt.getDate().toString()));
        appointmentCard.add(Box.createVerticalStrut(6));
        appointmentCard.add(createField("Start Time", appt.getStartTime().toString()));
        appointmentCard.add(Box.createVerticalStrut(6));
        appointmentCard.add(createField("End Time",   appt.getEndTime().toString()));

        // Card Section: Customer & Vehicle
        JPanel customerCard = UIHelper.createCardPanel("Customer & Vehicle");
        customerCard.setAlignmentX(Component.LEFT_ALIGNMENT);
        customerCard.add(createField("Customer", appt.getCustomer().getUsername()));
        customerCard.add(Box.createVerticalStrut(6));
        customerCard.add(createField("Vehicle",
                appt.getVehicle() == null
                        ? "-"
                        : appt.getVehicle().getPlateNumber()
                        + " — " + appt.getVehicle().getBrand()
                        + " "   + appt.getVehicle().getModel()));

        // Card Section: Service
        JPanel serviceCard = UIHelper.createCardPanel("Service");
        serviceCard.setAlignmentX(Component.LEFT_ALIGNMENT);
        serviceCard.add(createField("Service Name", appt.getService().getName()));
        serviceCard.add(Box.createVerticalStrut(6));
        serviceCard.add(createField("Type",         appt.getService().getType()));
        serviceCard.add(Box.createVerticalStrut(6));
        serviceCard.add(createField("Duration",     appt.getService().getDuration() + " mins"));
        serviceCard.add(Box.createVerticalStrut(6));
        serviceCard.add(createField("Price",        "RM " + appt.getService().getPrice()));


        // Card Section: Technician
        JPanel techCard = UIHelper.createCardPanel("Assigned Technician");
        techCard.setAlignmentX(Component.LEFT_ALIGNMENT);
        techCard.add(createField("Technician",
                appt.getTechnician() == null
                        ? "-"
                        : appt.getTechnician().getUsername()));

        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton backBtn = UIHelper.createStyledButton("Back", ButtonMode.PRIMARY);
        backBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        backBtn.addActionListener(_ -> onBack.run());
        UIHelper.addHoverEffect(backBtn, new Color(135, 90, 224), new Color(159, 126, 224));

        buttonPanel.add(backBtn);


        mainPanel.add(appointmentCard);
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(customerCard);
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(serviceCard);
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(techCard);
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(buttonPanel);

        add(new JScrollPane(mainPanel), BorderLayout.CENTER);
    }

    private JPanel createField(String label, String value) {
        JPanel row = new JPanel(new BorderLayout());
        row.setBackground(Color.WHITE);
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));

        JLabel labelComp = new JLabel(label);
        labelComp.setFont(new Font("SansSerif", Font.BOLD, 13));
        labelComp.setForeground(new Color(120, 120, 120));
        labelComp.setPreferredSize(new Dimension(150, 24));

        JLabel valueComp = new JLabel(value == null ? "-" : value);
        valueComp.setFont(new Font("SansSerif", Font.PLAIN, 13));
        valueComp.setForeground(new Color(40, 40, 40));

        row.add(labelComp, BorderLayout.WEST);
        row.add(valueComp, BorderLayout.CENTER);

        return row;
    }

    private JPanel createBadgeField(String label, boolean isCompleted) {
        JPanel row = new JPanel(new BorderLayout());
        row.setBackground(Color.WHITE);
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));

        JLabel labelComp = new JLabel(label);
        labelComp.setFont(new Font("SansSerif", Font.BOLD, 13));
        labelComp.setForeground(new Color(120, 120, 120));
        labelComp.setPreferredSize(new Dimension(150, 24));

        Color badgeBg = isCompleted ? new Color(198, 239, 206) : new Color(255, 235, 205);
        Color badgeFg = isCompleted ? new Color(0, 128, 0)     : new Color(180, 100, 0);

        JLabel badge = new JLabel(isCompleted ? "  Completed  " : "  Pending  ");
        badge.setFont(new Font("SansSerif", Font.BOLD, 12));
        badge.setForeground(badgeFg);
        badge.setBackground(badgeBg);
        badge.setOpaque(true);

        JPanel badgeWrapper = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        badgeWrapper.setBackground(Color.WHITE);
        badgeWrapper.add(badge);

        row.add(labelComp,    BorderLayout.WEST);
        row.add(badgeWrapper, BorderLayout.CENTER);

        return row;
    }
}