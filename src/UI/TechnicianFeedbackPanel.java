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

    public TechnicianFeedbackPanel(Technician currentTechnician, FeedbackService feedbackService) {
        this.currentTechnician = currentTechnician;
        this.feedbackService = feedbackService;

        setLayout(new BorderLayout());
        buildUI();
        loadData();

        feedbackService.setOnFeedbackChangedCallback(
                () -> SwingUtilities.invokeLater(this::loadData)
        );
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
        viewBtn.setPreferredSize(new Dimension(155, 35));
        viewBtn.addActionListener(_ -> viewFeedback());

        buttonPanel.add(viewBtn);

        mainPanel.add(searchPanel);
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(scrollPane);
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(buttonPanel);
        mainPanel.add(Box.createVerticalStrut(10));

        add(mainPanel, BorderLayout.CENTER);
    }

    private void viewFeedback() {
        int row = table.getSelectedRow();

        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a feedback entry first.");
            return;
        }

        String feedbackID    = table.getValueAt(row, 0).toString();
        String feedbackText  = table.getValueAt(row, 1).toString();
        String technician    = table.getValueAt(row, 2).toString();
        String customer      = table.getValueAt(row, 3).toString();
        String dateTime      = table.getValueAt(row, 4).toString();
        String appointmentID = table.getValueAt(row, 5).toString();

        new ViewFeedbackDialog(this, feedbackID, feedbackText, technician, customer, dateTime, appointmentID)
                .setVisible(true);
    }

    public void loadData() {
        model.setRowCount(0);

        try {
            List<Feedback> feedbacks = feedbackService.getFeedbacksByTechnician(currentTechnician);

            for (Feedback feedback : feedbacks) {
                String technicianName = feedback.getTechnician().getUsername();

                model.addRow(new Object[]{
                        feedback.getFeedbackID(),
                        feedback.getFeedbackText(),
                        technicianName,
                        feedback.getAppointment().getCustomer().getUsername(),
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


}
