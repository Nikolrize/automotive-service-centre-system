package UI;

import Model.*;
import Enum.ButtonMode;
import Service.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class AppointmentInfoPanel extends JPanel {

    private final FeedbackService feedbackService;
    private final CommentService commentService;
    private final UserService userService;
    private static final DateTimeFormatter DISPLAY_FORMATTER = DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a");

    private JTable feedbackTable;
    private DefaultTableModel feedbackModel;

    private JTable commentTable;
    private DefaultTableModel commentModel;

    public AppointmentInfoPanel(Appointment appt, FeedbackService feedbackService, CommentService commentService, UserService userService, Runnable onBack) {
        this.feedbackService = feedbackService;
        this.commentService = commentService;
        this.userService = userService;

        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        JPanel mainPanel = UIHelper.createSectionPanel("Appointment Details");

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
        JPanel customerCard = UIHelper.createCardPanel("Vehicle");
        customerCard.setAlignmentX(Component.LEFT_ALIGNMENT);
        customerCard.add(createField("Plate Number",appt.getVehicle().getPlateNumber()));
        customerCard.add(Box.createVerticalStrut(6));
        customerCard.add(createField("Brand",appt.getVehicle().getBrand()));
        customerCard.add(Box.createVerticalStrut(6));
        customerCard.add(createField("Model",appt.getVehicle().getModel()));
        customerCard.add(Box.createVerticalStrut(6));
        customerCard.add(createField("Colour",appt.getVehicle().getColor()));
        customerCard.add(Box.createVerticalStrut(6));
        customerCard.add(createField("Year", String.valueOf(appt.getVehicle().getYear())));
        customerCard.add(Box.createVerticalStrut(6));

        // Card Section: Service
        JPanel serviceCard = UIHelper.createCardPanel("Service");
        serviceCard.setAlignmentX(Component.LEFT_ALIGNMENT);
        serviceCard.add(createField("Service Name", appt.getService().getName()));
        serviceCard.add(Box.createVerticalStrut(6));
        serviceCard.add(createField("Type",         appt.getService().getType()));
        serviceCard.add(Box.createVerticalStrut(6));
        serviceCard.add(createField("Duration",     appt.getService().getDuration() + " hour(s)"));
        serviceCard.add(Box.createVerticalStrut(6));
        serviceCard.add(createField("Price",        "RM " + appt.getService().getPrice()));

        // Card Section: Technician
        JPanel techCard = UIHelper.createCardPanel("Assigned Technician");
        techCard.setAlignmentX(Component.LEFT_ALIGNMENT);
        techCard.add(createField("Technician",
                appt.getTechnician() == null
                        ? "-"
                        : appt.getTechnician().getUsername()));

        // Card Section: Feedback
        JPanel feedbackCard = UIHelper.createCardPanel("Feedback");
        feedbackCard.setAlignmentX(Component.LEFT_ALIGNMENT);

        String[] feedbackColumns = {"Feedback ID", "Technician", "Feedback Message", "Date & Time"};
        feedbackModel = new DefaultTableModel(feedbackColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        feedbackTable = UIHelper.createTable(feedbackModel);
        feedbackTable.setPreferredScrollableViewportSize(new Dimension(Integer.MAX_VALUE, 150));

        JScrollPane feedbackScrollPane = new JScrollPane(feedbackTable);
        feedbackScrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);

        loadFeedbackData(appt.getAppointmentID());
        feedbackCard.add(feedbackScrollPane);

        // Card Section: Comment
        JPanel commentCard = UIHelper.createCardPanel("Customer Comment");
        commentCard.setAlignmentX(Component.LEFT_ALIGNMENT);

        Comment comment = commentService.getCommentByAppointmentID(appt.getAppointmentID());

        if (comment != null) {
            String[] commentColumns = {"Comment ID", "Customer", "Rating", "Comment"};
            commentModel = new DefaultTableModel(commentColumns, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };
        }

        commentTable = UIHelper.createTable(commentModel);
        commentTable.setPreferredScrollableViewportSize(new Dimension(Integer.MAX_VALUE, 100));

        commentTable = UIHelper.createTable(commentModel);
        commentTable.setPreferredScrollableViewportSize(new Dimension(Integer.MAX_VALUE, 100));

        JScrollPane commentScrollPane = new JScrollPane(commentTable);
        commentScrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);

        loadCommentData(appt.getAppointmentID());
        commentCard.add(commentScrollPane);

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
        mainPanel.add(feedbackCard);
        mainPanel.add(Box.createVerticalStrut(10));
        if (comment != null) {
            mainPanel.add(commentCard);
            mainPanel.add(Box.createVerticalStrut(10));
        }
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(buttonPanel);

        add(new JScrollPane(mainPanel), BorderLayout.CENTER);
    }

    private void loadFeedbackData(String appointmentID) {
        feedbackModel.setRowCount(0);

        List<String[]> records = feedbackService.getFeedbacksByAppointment(appointmentID);

        if (records.isEmpty()) {
            feedbackModel.addRow(new Object[]{"—", "—", "No feedback yet", "—"});
            return;
        }

        for (String[] record : records) {
            String feedbackID   = record.length > 0 ? record[0].trim() : "-";
            String technicianID = record.length > 2 ? record[2].trim() : "-";
            String feedbackText = record.length > 3 ? record[3].trim() : "-";
            String dateTime     = record.length > 4 ? record[4].trim() : "-";

            String technician = technicianID;
            try {
                User user = userService.getUserById(technicianID);
                if (user != null) technician = user.getUsername();
            } catch (Exception ignored) {}

            String formattedDate = dateTime;
            try {
                formattedDate = LocalDateTime.parse(dateTime).format(DISPLAY_FORMATTER);
            } catch (Exception ignored) {}

            feedbackModel.addRow(new Object[]{
                    feedbackID,
                    technician,
                    feedbackText,
                    formattedDate
            });
        }
    }

    private void loadCommentData(String appointmentID) {
        if (commentModel == null) return;

        commentModel.setRowCount(0);

        Comment comment = commentService.getCommentByAppointmentID(appointmentID);

        if (comment == null) {
            commentModel.addRow(new Object[]{"—", "—", "—", "No comment yet"});
            return;
        }

        commentModel.addRow(new Object[]{
                comment.getCommentID(),
                comment.getAppointment().getCustomer().getUsername(),
                comment.getRating() + " / 5",
                comment.getCommentText()
        });
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

        JLabel badge = new JLabel(isCompleted ? "  Completed  " : "  In progress  ");
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