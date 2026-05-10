package UI;

import Enum.ButtonMode;
import Model.Appointment;
import Model.Comment;
import Model.Customer;
import Service.AppointmentService;
import Service.CommentService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.util.List;

public class CustomerCommentPanel extends JPanel {
    private final Customer currentCustomer;
    private final AppointmentService appointmentService;
    private final CommentService commentService;

    private JTable appointmentTable;
    private JTable commentTable;
    private DefaultTableModel appointmentModel;
    private DefaultTableModel commentModel;

    private final JTextField appointmentSearchField = new JTextField();
    private final JTextField commentSearchField = new JTextField();

    private TableRowSorter<DefaultTableModel> appointmentSorter;
    private TableRowSorter<DefaultTableModel> commentSorter;

    public CustomerCommentPanel(Customer currentCustomer, AppointmentService appointmentService, CommentService commentService) {
        this.currentCustomer = currentCustomer;
        this.appointmentService = appointmentService;
        this.commentService = commentService;

        setLayout(new BorderLayout());
        buildUI();
        loadData();
    }

    private void buildUI() {
        JPanel mainPanel = UIHelper.createSectionPanel("My Comments");

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setAlignmentX(Component.LEFT_ALIGNMENT);

        tabbedPane.addTab("Eligible Appointments", createAppointmentTab());
        tabbedPane.addTab("My Submitted Comments", createCommentTab());

        mainPanel.add(tabbedPane);

        add(mainPanel, BorderLayout.CENTER);
    }

    private JPanel createAppointmentTab() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);

        String[] columns = {
                "Appointment ID",
                "Service",
                "Technician",
                "Counter Staff",
                "Date",
                "Completed",
                "Comment Status"
        };

        appointmentModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        appointmentTable = UIHelper.createTable(appointmentModel);
        appointmentSorter = new TableRowSorter<>(appointmentModel);
        appointmentTable.setRowSorter(appointmentSorter);

        JPanel searchPanel = UIHelper.createSearchPanel(appointmentSearchField, this::applyAppointmentFilter);
        searchPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton createBtn = UIHelper.createStyledButton("Submit Comment", ButtonMode.CREATE);
        createBtn.setPreferredSize(new Dimension(180, 35));
        createBtn.addActionListener(_ -> handleCreateComment());

        JButton refreshBtn = UIHelper.createStyledButton("Refresh", ButtonMode.PRIMARY);
        refreshBtn.addActionListener(_ -> loadData());

        buttonPanel.add(createBtn);
        buttonPanel.add(refreshBtn);

        panel.add(searchPanel);
        panel.add(Box.createVerticalStrut(10));
        panel.add(new JScrollPane(appointmentTable));
        panel.add(Box.createVerticalStrut(10));
        panel.add(buttonPanel);

        return panel;
    }

    private JPanel createCommentTab() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);

        String[] columns = {"Comment ID", "Appointment ID", "Service", "Rating", "Comment"};

        commentModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        commentTable = UIHelper.createTable(commentModel);
        commentSorter = new TableRowSorter<>(commentModel);
        commentTable.setRowSorter(commentSorter);

        JPanel searchPanel = UIHelper.createSearchPanel(commentSearchField, this::applyCommentFilter);
        searchPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton viewBtn = UIHelper.createStyledButton("View Comment", ButtonMode.PRIMARY);
        viewBtn.setPreferredSize(new Dimension(150, 35));
        viewBtn.addActionListener(_ -> handleViewComment());

        JButton refreshBtn = UIHelper.createStyledButton("Refresh", ButtonMode.CREATE);
        refreshBtn.addActionListener(_ -> loadData());

        buttonPanel.add(viewBtn);
        buttonPanel.add(refreshBtn);

        panel.add(searchPanel);
        panel.add(Box.createVerticalStrut(10));
        panel.add(new JScrollPane(commentTable));
        panel.add(Box.createVerticalStrut(10));
        panel.add(buttonPanel);

        return panel;
    }

    public void loadData() {
        loadAppointments();
        loadComments();
    }

    private void loadAppointments() {
        appointmentModel.setRowCount(0);

        List<Appointment> appointments = appointmentService.getAppointmentsByCustomer(currentCustomer);

        for (Appointment appointment : appointments) {
            String technicianName = appointment.getTechnician().getFirstName() + " " + appointment.getTechnician().getLastName();
            String counterStaffName = appointment.getCounterStaff().getFirstName() + " " + appointment.getCounterStaff().getLastName();
            String commentStatus = commentService.hasCommentForAppointment(appointment.getAppointmentID()) ? "Submitted" : "Not Submitted";

            appointmentModel.addRow(new Object[]{
                    appointment.getAppointmentID(),
                    appointment.getService().getName(),
                    technicianName,
                    counterStaffName,
                    appointment.getDate(),
                    appointment.getIsCompleted() ? "Yes" : "No",
                    commentStatus
            });
        }
    }

    private void loadComments() {
        commentModel.setRowCount(0);

        List<Comment> comments = commentService.getCommentsByCustomer(currentCustomer);

        for (Comment comment : comments) {
            commentModel.addRow(new Object[]{
                    comment.getCommentID(),
                    comment.getAppointment().getAppointmentID(),
                    comment.getAppointment().getService().getName(),
                    comment.getRating(),
                    comment.getCommentText()
            });
        }
    }

    private void applyAppointmentFilter() {
        String text = appointmentSearchField.getText();

        if (text.trim().isEmpty()) {
            appointmentSorter.setRowFilter(null);
        } else {
            appointmentSorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
        }
    }

    private void applyCommentFilter() {
        String text = commentSearchField.getText();

        if (text.trim().isEmpty()) {
            commentSorter.setRowFilter(null);
        } else {
            commentSorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
        }
    }

    private Appointment getSelectedAppointment() {
        int row = appointmentTable.getSelectedRow();

        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select an appointment first.");
            return null;
        }

        String appointmentID = appointmentTable.getValueAt(row, 0).toString();
        return appointmentService.getAppointmentByID(appointmentID);
    }

    private Comment getSelectedComment() {
        int row = commentTable.getSelectedRow();

        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a comment first.");
            return null;
        }

        String commentID = commentTable.getValueAt(row, 0).toString();

        for (Comment comment : commentService.getCommentsByCustomer(currentCustomer)) {
            if (comment.getCommentID().equals(commentID)) {
                return comment;
            }
        }

        return null;
    }

    private void handleCreateComment() {
        Appointment appointment = getSelectedAppointment();
        if (appointment == null) return;

        JTextField ratingField = new JTextField();
        JTextArea commentArea = new JTextArea(5, 25);
        commentArea.setLineWrap(true);
        commentArea.setWrapStyleWord(true);

        JScrollPane commentScrollPane = new JScrollPane(commentArea);

        Object[] fields = {
                "Rating (1-5):", ratingField,
                "Comment:", commentScrollPane
        };

        while (true) {
            int result = JOptionPane.showConfirmDialog(
                    this,
                    fields,
                    "Submit Comment",
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.PLAIN_MESSAGE
            );

            if (result != JOptionPane.OK_OPTION) {
                return;
            }

            try {
                commentService.createComment(
                        currentCustomer,
                        appointment,
                        ratingField.getText(),
                        commentArea.getText()
                );

                JOptionPane.showMessageDialog(this, "Comment submitted successfully.");
                loadData();
                return;
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void handleViewComment() {
        Comment comment = getSelectedComment();
        if (comment == null) return;

        JOptionPane.showMessageDialog(
                this,
                "Comment ID: " + comment.getCommentID() +
                        "\nAppointment ID: " + comment.getAppointment().getAppointmentID() +
                        "\nService: " + comment.getAppointment().getService().getName() +
                        "\nRating: " + comment.getRating() +
                        "\nComment: " + comment.getCommentText(),
                "Comment Details",
                JOptionPane.INFORMATION_MESSAGE
        );
    }
}
