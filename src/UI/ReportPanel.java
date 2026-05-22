package UI;

import Service.ReportService;

import javax.swing.*;
import java.awt.*;

import Enum.*;

public class ReportPanel extends JPanel {
    private final ReportService reportService;

    private JLabel totalUsersLabel;
    private JLabel totalAppointmentsLabel;
    private JLabel completedAppointmentsLabel;
    private JLabel totalPaymentsLabel;
    private JLabel normalServicesLabel;
    private JLabel majorServicesLabel;
    private JLabel averageRatingLabel;
    private JLabel totalVehiclesLabel;

    private JTextArea noteArea;

    public ReportPanel() {
        this.reportService = new ReportService();

        setLayout(new BorderLayout());
        reportUI();
        loadData();
    }

    private void reportUI() {
        JPanel mainPanel = UIHelper.createSectionPanel("Report");

        JPanel summaryWrapperPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        summaryWrapperPanel.setBackground(Color.WHITE);
        summaryWrapperPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel summaryPanel = new JPanel(new GridLayout(4, 2, 20, 20));
        summaryPanel.setBackground(Color.WHITE);
        summaryPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        summaryPanel.setPreferredSize(new Dimension(760, 320));

        totalUsersLabel = createSummaryLabel("Total Users: 0");
        totalAppointmentsLabel = createSummaryLabel("Total Appointments: 0");
        completedAppointmentsLabel = createSummaryLabel("Completed Appointments: 0");
        totalPaymentsLabel = createSummaryLabel("Total Payments Collected: RM 0.00");
        normalServicesLabel = createSummaryLabel("Normal Services: 0");
        majorServicesLabel = createSummaryLabel("Major Services: 0");
        averageRatingLabel = createSummaryLabel("Average Customer Rating: 0.00");
        totalVehiclesLabel = createSummaryLabel("Total Vehicles: 0");

        summaryPanel.add(totalUsersLabel);
        summaryPanel.add(totalAppointmentsLabel);
        summaryPanel.add(completedAppointmentsLabel);
        summaryPanel.add(totalPaymentsLabel);
        summaryPanel.add(normalServicesLabel);
        summaryPanel.add(majorServicesLabel);
        summaryPanel.add(averageRatingLabel);
        summaryPanel.add(totalVehiclesLabel);

        summaryWrapperPanel.add(summaryPanel);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton editNotesBtn = UIHelper.createStyledButton("Edit Notes", ButtonMode.PRIMARY);
        editNotesBtn.setPreferredSize(new Dimension(150, 35));
        editNotesBtn.addActionListener(_ -> handleEditNotes());

        buttonPanel.add(editNotesBtn);

        noteArea = new JTextArea();
        noteArea.setEditable(false);
        noteArea.setLineWrap(true);
        noteArea.setWrapStyleWord(true);
        noteArea.setBackground(new Color(248, 248, 248));
        noteArea.setFont(new Font("SansSerif", Font.PLAIN, 13));

        JScrollPane noteScrollPane = new JScrollPane(noteArea);
        noteScrollPane.setAlignmentX(Component.CENTER_ALIGNMENT);
        noteScrollPane.setBorder(BorderFactory.createTitledBorder("Report Notes"));
        noteScrollPane.setPreferredSize(new Dimension(760, 120));
        noteScrollPane.setMaximumSize(new Dimension(760, 120));

        mainPanel.add(summaryWrapperPanel);
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(buttonPanel);
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(noteScrollPane);

        add(mainPanel, BorderLayout.CENTER);
    }

    private JLabel createSummaryLabel(String text) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setOpaque(true);
        label.setBackground(new Color(245, 245, 245));
        label.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        label.setFont(new Font("SansSerif", Font.BOLD, 15));
        label.setPreferredSize(new Dimension(360, 65));
        return label;
    }

    public void loadData() {
        totalUsersLabel.setText("Total Users: " + reportService.getTotalUsers());
        totalAppointmentsLabel.setText("Total Appointments: " + reportService.getTotalAppointments());
        completedAppointmentsLabel.setText("Completed Appointments: " + reportService.getCompletedAppointments());
        totalPaymentsLabel.setText(String.format("Total Payments Collected: RM %.2f", reportService.getTotalPaymentsCollected()));
        normalServicesLabel.setText("Normal Services: " + reportService.getNormalServiceCount());
        majorServicesLabel.setText("Major Services: " + reportService.getMajorServiceCount());
        averageRatingLabel.setText(String.format("Average Customer Rating: %.2f", reportService.getAverageCustomerRating()));
        totalVehiclesLabel.setText("Total Vehicles: " + reportService.getTotalVehicles());
        noteArea.setText(reportService.getReportNotes());
    }

    private void handleEditNotes() {
        JTextArea editArea = new JTextArea(noteArea.getText(), 10, 35);
        editArea.setLineWrap(true);
        editArea.setWrapStyleWord(true);
        editArea.setFont(new Font("SansSerif", Font.PLAIN, 13));

        JScrollPane scrollPane = new JScrollPane(editArea);

        int result = JOptionPane.showConfirmDialog(
                this,
                scrollPane,
                "Edit Report Notes",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (result == JOptionPane.OK_OPTION) {
            try {
                reportService.saveReportNotes(editArea.getText());
                noteArea.setText(reportService.getReportNotes());
                JOptionPane.showMessageDialog(this, "Report notes saved successfully.");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}