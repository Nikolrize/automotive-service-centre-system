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

    private CardLayout contentCardLayout;
    private JPanel contentPanel;

    private JButton appointmentTabBtn;
    private JButton commentTabBtn;

    public CustomerCommentPanel(Customer currentCustomer, AppointmentService appointmentService, CommentService commentService) {
        this.currentCustomer = currentCustomer;
        this.appointmentService = appointmentService;
        this.commentService = commentService;

        setLayout(new BorderLayout());
        buildUI();
        loadData();
        showTab("appointments");
    }

    private void buildUI() {
        JPanel mainPanel = UIHelper.createSectionPanel("My Comments");

        JPanel selectorPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        selectorPanel.setBackground(Color.WHITE);
        selectorPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        appointmentTabBtn = createTabButton("Eligible Appointments");
        appointmentTabBtn.setPreferredSize(new Dimension(190, 36));
        appointmentTabBtn.addActionListener(_ -> showTab("appointments"));

        commentTabBtn = createTabButton("My Submitted Comments");
        commentTabBtn.setPreferredSize(new Dimension(210, 36));
        commentTabBtn.addActionListener(_ -> showTab("comments"));

        selectorPanel.add(appointmentTabBtn);
        selectorPanel.add(commentTabBtn);

        contentCardLayout = new CardLayout();
        contentPanel = new JPanel(contentCardLayout);
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        contentPanel.add(createAppointmentTab(), "appointments");
        contentPanel.add(createCommentTab(), "comments");

        mainPanel.add(selectorPanel);
        mainPanel.add(Box.createVerticalStrut(12));
        mainPanel.add(contentPanel);

        add(mainPanel, BorderLayout.CENTER);
    }

    private JButton createTabButton(String text) {
        JButton button = new JButton(text);
        button.setFocusPainted(false);
        button.setFont(new Font("SansSerif", Font.BOLD, 13));
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(189, 195, 199));
        button.setBorderPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return button;
    }

    private void showTab(String tabName) {
        contentCardLayout.show(contentPanel, tabName);

        Color activeColor = new Color(135, 90, 224);
        Color inactiveColor = new Color(189, 195, 199);

        if (tabName.equals("appointments")) {
            appointmentTabBtn.setBackground(activeColor);
            commentTabBtn.setBackground(inactiveColor);
        } else {
            appointmentTabBtn.setBackground(inactiveColor);
            commentTabBtn.setBackground(activeColor);
        }
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

        JScrollPane scrollPane = new JScrollPane(appointmentTable);
        scrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton createBtn = UIHelper.createStyledButton("Submit Comment", ButtonMode.CREATE);
        createBtn.setPreferredSize(new Dimension(180, 35));
        createBtn.addActionListener(_ -> handleCreateComment());

        buttonPanel.add(createBtn);

        panel.add(searchPanel);
        panel.add(Box.createVerticalStrut(10));
        panel.add(scrollPane);
        panel.add(Box.createVerticalStrut(10));
        panel.add(buttonPanel);

        return panel;
    }

    private JPanel createCommentTab() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);

        String[] columns = {
                "Comment ID",
                "Appointment ID",
                "Service",
                "Rating",
                "Comment"
        };

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

        JScrollPane scrollPane = new JScrollPane(commentTable);
        scrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton viewBtn = UIHelper.createStyledButton("View Comment", ButtonMode.PRIMARY);
        viewBtn.setPreferredSize(new Dimension(150, 35));
        viewBtn.addActionListener(_ -> handleViewComment());

        buttonPanel.add(viewBtn);

        panel.add(searchPanel);
        panel.add(Box.createVerticalStrut(10));
        panel.add(scrollPane);
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

        AddCommentDialog dialog = new AddCommentDialog(
                this,
                currentCustomer,
                appointment,
                commentService,
                () -> {
                    JOptionPane.showMessageDialog(this, "Comment submitted successfully.");
                    loadData();
                    showTab("comments");
                }
        );

        dialog.setVisible(true);
    }

    private void handleViewComment() {
        Comment comment = getSelectedComment();
        if (comment == null) return;

        new ViewCommentDialog(
                this,
                comment.getCommentID(),
                comment.getCommentText(),
                String.valueOf(comment.getRating()),
                comment.getAppointment().getCustomer().getUsername(),
                comment.getAppointment().getAppointmentID(),
                comment.getAppointment().getService().getName()
        ).setVisible(true);
    }
}