package UI;

import Enum.ButtonMode;
import Model.Appointment;
import Model.Customer;
import Model.Feedback;
import Service.AppointmentService;
import Service.FeedbackService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.util.List;

public class CustomerFeedbackPanel extends JPanel {
    private final Customer currentCustomer;
    private final AppointmentService appointmentService;
    private final FeedbackService feedbackService;

    private JTable table;
    private DefaultTableModel model;
    private final JTextField searchField = new JTextField();
    private TableRowSorter<DefaultTableModel> sorter;

    public CustomerFeedbackPanel(Customer currentCustomer, AppointmentService appointmentService, FeedbackService feedbackService) {
        this.currentCustomer = currentCustomer;
        this.appointmentService = appointmentService;
        this.feedbackService = feedbackService;

        setLayout(new BorderLayout());
        buildUI();
        loadData();
    }

    private void buildUI() {
        JPanel mainPanel = UIHelper.createSectionPanel("My Feedback");

        String[] columns = {
                "Appointment ID",
                "Service",
                "Technician",
                "Date",
                "Feedback Status"
        };

        model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = UIHelper.createTable(model);
        sorter = new TableRowSorter<>(model);
        table.setRowSorter(sorter);

        JPanel searchPanel = UIHelper.createSearchPanel(searchField, this::applyFilter);
        searchPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton viewBtn = UIHelper.createStyledButton("View Feedback", ButtonMode.PRIMARY);
        viewBtn.setPreferredSize(new Dimension(180, 35));
        viewBtn.addActionListener(_ -> handleViewFeedback());

        buttonPanel.add(viewBtn);

        mainPanel.add(searchPanel);
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(scrollPane);
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(buttonPanel);

        add(mainPanel, BorderLayout.CENTER);
    }

    public void loadData() {
        model.setRowCount(0);

        List<Appointment> appointments = appointmentService.getAppointmentsByCustomer(currentCustomer);

        for (Appointment appointment : appointments) {
            String technicianName = appointment.getTechnician().getFirstName() + " " + appointment.getTechnician().getLastName();
            boolean hasFeedback = hasFeedbackForAppointment(appointment.getAppointmentID());

            model.addRow(new Object[]{
                    appointment.getAppointmentID(),
                    appointment.getService().getName(),
                    technicianName,
                    appointment.getDate(),
                    hasFeedback ? "Available" : "Not Available"
            });
        }
    }

    private boolean hasFeedbackForAppointment(String appointmentID) {
        try {
            List<Feedback> feedbacks = feedbackService.getAllFeedbacks();

            for (Feedback feedback : feedbacks) {
                if (feedback.getAppointment().getAppointmentID().equals(appointmentID)) {
                    return true;
                }
            }
        } catch (Exception e) {
            return false;
        }

        return false;
    }

    private void applyFilter() {
        String text = searchField.getText();

        if (text.trim().isEmpty()) {
            sorter.setRowFilter(null);
        } else {
            sorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
        }
    }

    private void handleViewFeedback() {
        int row = table.getSelectedRow();

        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select an appointment first.");
            return;
        }

        String appointmentID = table.getValueAt(row, 0).toString();

        try {
            List<Feedback> feedbacks = feedbackService.getAllFeedbacks();

            for (Feedback feedback : feedbacks) {
                if (feedback.getAppointment().getAppointmentID().equals(appointmentID)) {
                    new ViewFeedbackDialog(
                            this,
                            feedback.getFeedbackID(),
                            feedback.getFeedbackText(),
                            feedback.getTechnician().getUsername(),
                            feedback.getAppointment().getCustomer().getUsername(),
                            feedback.getDateTime().toString(),
                            feedback.getAppointment().getAppointmentID()
                    ).setVisible(true);
                    return;
                }
            }

            JOptionPane.showMessageDialog(this, "No feedback is available for this appointment yet.");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading feedback: " + e.getMessage());
        }
    }
}