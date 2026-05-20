package UI;

import Model.*;
import Service.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

import Enum.*;

public class TechnicianFeedbackPanel extends JPanel {

    private final Technician currentTechnician;
    private final FeedbackService feedbackService;

    private JTable table;
    private DefaultTableModel model;
    private final JTextField searchField = new JTextField();
    private TableRowSorter<DefaultTableModel> sorter;
    private static final DateTimeFormatter DISPLAY_FORMATTER = DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a");
    private Timer autoRefreshTimer;

    public TechnicianFeedbackPanel(Technician currentTechnician, AppointmentService appointmentService, FeedbackService feedbackService) {
        this.currentTechnician = currentTechnician;
        this.feedbackService = feedbackService;

        setLayout(new BorderLayout());
        buildUI();
        loadData();
    }

    private void buildUI() {
        JPanel mainPanel = UIHelper.createSectionPanel("My Feedback History");

        String[] columns = {"Feedback ID", "Feedback Message", "Technician", "Customer" ,"Date & Time", "Appointment ID",};

        model = new DefaultTableModel(columns,0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        autoRefreshTimer = new Timer(1000, _ -> loadData());
        autoRefreshTimer.start();

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

        mainPanel.add(searchPanel);
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(scrollPane);
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(buttonPanel);

        add(mainPanel, BorderLayout.CENTER);
    }

    public void loadData() {
        model.setRowCount(0);

        try {
            List<Feedback> feedbacks = feedbackService.getFeedbacksByTechnician(currentTechnician);

            for (Feedback feedback : feedbacks) {
                String technicianName = feedback.getTechnician().getFirstName() + " " + feedback.getTechnician().getLastName();

                model.addRow(new Object[]{
                        feedback.getFeedbackID(),
                        feedback.getFeedbackText(),
                        technicianName,
                        feedback.getAppointment().getCustomer().getFirstName(),
                        feedback.getDateTime().format(DISPLAY_FORMATTER),
                        feedback.getAppointment().getAppointmentID()
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading feedback: " + e.getMessage());
            e.printStackTrace();
        }
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

        if (row == -1 ) {
            JOptionPane.showMessageDialog(this, "Please select an appointment first.");
            return;
        }

        String appointmentID = table.getValueAt(row, 0).toString();
        String feedbackText = feedbackService.getFeedbackTextByAppointmentID(appointmentID);

        if (feedbackText == null || feedbackText.isBlank()) {
            JOptionPane.showMessageDialog(this, "No feedback is available for this appointment yet.");
            return;
        }

        JOptionPane.showMessageDialog(this, "Appointment ID: " + appointmentID + "\nFeedback: " + feedbackText, "My Feedback", JOptionPane.INFORMATION_MESSAGE);
    }
}
