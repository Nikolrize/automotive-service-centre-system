package UI;

import Enum.ButtonMode;
import Model.User;
import Service.ReportService;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.List;

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
    private JComboBox<User> employeeComboBox;

    public ReportPanel() {
        this.reportService = new ReportService();

        setLayout(new BorderLayout());
        reportUI();
        loadData();
    }

    private void reportUI() {
        JPanel mainPanel = UIHelper.createSectionPanel("Report");
        mainPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel summaryPanel = new JPanel(new GridLayout(4, 2, 16, 16));
        summaryPanel.setBackground(Color.WHITE);
        summaryPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        summaryPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        summaryPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 380));

        totalUsersLabel = createSummaryLabel("Total Users", "0");
        totalAppointmentsLabel = createSummaryLabel("Total Appointments", "0");
        completedAppointmentsLabel = createSummaryLabel("Completed Appointments", "0");
        totalPaymentsLabel = createSummaryLabel("Total Payments Collected", "RM 0.00");
        normalServicesLabel = createSummaryLabel("Normal Services", "0");
        majorServicesLabel = createSummaryLabel("Major Services", "0");
        averageRatingLabel = createSummaryLabel("Average Customer Rating", "0.00");
        totalVehiclesLabel = createSummaryLabel("Total Vehicles", "0");

        summaryPanel.add(totalUsersLabel);
        summaryPanel.add(totalAppointmentsLabel);
        summaryPanel.add(completedAppointmentsLabel);
        summaryPanel.add(totalPaymentsLabel);
        summaryPanel.add(normalServicesLabel);
        summaryPanel.add(majorServicesLabel);
        summaryPanel.add(averageRatingLabel);
        summaryPanel.add(totalVehiclesLabel);

        JPanel employeePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        employeePanel.setBackground(Color.WHITE);
        employeePanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        employeePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        JLabel employeeLabel = new JLabel("Employee Workload:");
        employeeLabel.setFont(new Font("SansSerif", Font.BOLD, 13));

        employeeComboBox = new JComboBox<>();
        employeeComboBox.setPreferredSize(new Dimension(320, 35));
        employeeComboBox.setFont(new Font("SansSerif", Font.PLAIN, 13));
        employeeComboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                          boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

                if (value instanceof User user) {
                    setText(user.getFirstName() + " " + user.getLastName()
                            + " (" + user.getRoleName() + ") - " + user.getUsername());
                } else {
                    setText("Select Employee");
                }

                return this;
            }
        });

        employeePanel.add(employeeLabel);
        employeePanel.add(employeeComboBox);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        buttonPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));

        JButton viewOverallBtn = UIHelper.createStyledButton("View Overall Report", ButtonMode.PRIMARY);
        viewOverallBtn.setPreferredSize(new Dimension(180, 35));
        viewOverallBtn.addActionListener(e -> handleViewOverallReport());

        JButton viewEmployeeBtn = UIHelper.createStyledButton("View Employee Report", ButtonMode.PRIMARY);
        viewEmployeeBtn.setPreferredSize(new Dimension(200, 35));
        viewEmployeeBtn.addActionListener(e -> handleViewEmployeeReport());

        JButton exportOverallBtn = UIHelper.createStyledButton("Export Overall PDF", ButtonMode.CREATE);
        exportOverallBtn.setPreferredSize(new Dimension(170, 35));
        exportOverallBtn.addActionListener(e -> handleExportOverallPdf());

        JButton exportEmployeeBtn = UIHelper.createStyledButton("Export Employee PDF", ButtonMode.CREATE);
        exportEmployeeBtn.setPreferredSize(new Dimension(200, 35));
        exportEmployeeBtn.addActionListener(e -> handleExportEmployeePdf());

        JButton editNotesBtn = UIHelper.createStyledButton("Edit Notes", ButtonMode.PRIMARY);
        editNotesBtn.setPreferredSize(new Dimension(140, 35));
        editNotesBtn.addActionListener(e -> handleEditNotes());

        buttonPanel.add(viewOverallBtn);
        buttonPanel.add(viewEmployeeBtn);
        buttonPanel.add(exportOverallBtn);
        buttonPanel.add(exportEmployeeBtn);
        buttonPanel.add(editNotesBtn);

        noteArea = new JTextArea();
        noteArea.setEditable(false);
        noteArea.setLineWrap(true);
        noteArea.setWrapStyleWord(true);
        noteArea.setBackground(new Color(248, 248, 248));
        noteArea.setFont(new Font("SansSerif", Font.PLAIN, 13));

        JScrollPane noteScrollPane = new JScrollPane(noteArea);
        noteScrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);
        noteScrollPane.setBorder(BorderFactory.createTitledBorder("Report Notes"));
        noteScrollPane.setPreferredSize(new Dimension(760, 120));
        noteScrollPane.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));

        mainPanel.add(summaryPanel);
        mainPanel.add(Box.createVerticalStrut(12));
        mainPanel.add(employeePanel);
        mainPanel.add(Box.createVerticalStrut(12));
        mainPanel.add(buttonPanel);
        mainPanel.add(Box.createVerticalStrut(12));
        mainPanel.add(noteScrollPane);

        add(mainPanel, BorderLayout.CENTER);
    }

    private JLabel createSummaryLabel(String title, String value) {
        JLabel label = new JLabel();
        label.setOpaque(true);
        label.setBackground(new Color(245, 245, 245));
        label.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 230, 230)),
                BorderFactory.createEmptyBorder(14, 16, 14, 16)
        ));
        label.setFont(new Font("SansSerif", Font.BOLD, 14));
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setVerticalAlignment(SwingConstants.CENTER);
        label.setPreferredSize(new Dimension(320, 82));

        setSummaryLabel(label, title, value);
        return label;
    }

    private void setSummaryLabel(JLabel label, String title, String value) {
        label.setText("""
                <html><div style='text-align:center;'>
                <span style='font-weight:bold;'>%s</span><br>
                <span style='font-size:14px;'>%s</span>
                </div></html>
                """.formatted(title, value));
    }

    public void loadData() {
        setSummaryLabel(totalUsersLabel, "Total Users", String.valueOf(reportService.getTotalUsers()));
        setSummaryLabel(totalAppointmentsLabel, "Total Appointments", String.valueOf(reportService.getTotalAppointments()));
        setSummaryLabel(completedAppointmentsLabel, "Completed Appointments", String.valueOf(reportService.getCompletedAppointments()));
        setSummaryLabel(totalPaymentsLabel, "Total Payments Collected", String.format("RM %.2f", reportService.getTotalPaymentsCollected()));
        setSummaryLabel(normalServicesLabel, "Normal Services", String.valueOf(reportService.getNormalServiceCount()));
        setSummaryLabel(majorServicesLabel, "Major Services", String.valueOf(reportService.getMajorServiceCount()));
        setSummaryLabel(averageRatingLabel, "Average Customer Rating", String.format("%.2f", reportService.getAverageCustomerRating()));
        setSummaryLabel(totalVehiclesLabel, "Total Vehicles", String.valueOf(reportService.getTotalVehicles()));

        noteArea.setText(reportService.getReportNotes());
        populateEmployeeComboBox();
    }

    private void populateEmployeeComboBox() {
        DefaultComboBoxModel<User> model = new DefaultComboBoxModel<>();
        model.addElement(null);

        List<User> employees = reportService.getReportEmployees();
        for (User employee : employees) {
            model.addElement(employee);
        }

        employeeComboBox.setModel(model);
    }

    private void handleViewOverallReport() {
        try {
            showReportDialog("Overall Report", reportService.generateOverallReport());
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleViewEmployeeReport() {
        try {
            User selectedEmployee = (User) employeeComboBox.getSelectedItem();
            String report = reportService.generateEmployeeWorkloadReport(selectedEmployee);
            showReportDialog("Employee Workload Report", report);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleExportOverallPdf() {
        try {
            exportReportAsPdf("overall_report", reportService.generateOverallReport());
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleExportEmployeePdf() {
        try {
            User selectedEmployee = (User) employeeComboBox.getSelectedItem();
            String report = reportService.generateEmployeeWorkloadReport(selectedEmployee);

            String fileName = "employee_report";
            if (selectedEmployee != null) {
                fileName = selectedEmployee.getUsername() + "_workload_report";
            }

            exportReportAsPdf(fileName, report);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void exportReportAsPdf(String defaultFileName, String reportContent) throws Exception {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Export Report as PDF");
        fileChooser.setSelectedFile(new File(defaultFileName + ".pdf"));

        int result = fileChooser.showSaveDialog(this);

        if (result != JFileChooser.APPROVE_OPTION) {
            return;
        }

        File selectedFile = fileChooser.getSelectedFile();
        if (!selectedFile.getName().toLowerCase().endsWith(".pdf")) {
            selectedFile = new File(selectedFile.getAbsolutePath() + ".pdf");
        }

        reportService.exportReportToPdf(reportContent, selectedFile);
        JOptionPane.showMessageDialog(this, "Report exported successfully:\n" + selectedFile.getAbsolutePath());
    }

    private void showReportDialog(String title, String reportContent) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), title, true);
        dialog.setSize(760, 620);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());
        dialog.getContentPane().setBackground(Color.WHITE);

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(135, 90, 224));
        header.setBorder(BorderFactory.createEmptyBorder(14, 18, 14, 18));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLabel.setForeground(Color.WHITE);

        JLabel subtitleLabel = new JLabel("Generated On Demand");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subtitleLabel.setForeground(Color.WHITE);

        header.add(titleLabel, BorderLayout.NORTH);
        header.add(subtitleLabel, BorderLayout.SOUTH);

        JTextArea reportArea = new JTextArea(reportContent);
        reportArea.setEditable(false);
        reportArea.setFont(new Font("Monospaced", Font.PLAIN, 13));
        reportArea.setLineWrap(false);
        reportArea.setCaretPosition(0);
        reportArea.setBackground(Color.WHITE);
        reportArea.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        JScrollPane scrollPane = new JScrollPane(reportArea);
        scrollPane.setBorder(null);

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
        closeBtn.addActionListener(e -> dialog.dispose());

        footer.add(closeBtn);

        dialog.add(header, BorderLayout.NORTH);
        dialog.add(scrollPane, BorderLayout.CENTER);
        dialog.add(footer, BorderLayout.SOUTH);

        dialog.setVisible(true);
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