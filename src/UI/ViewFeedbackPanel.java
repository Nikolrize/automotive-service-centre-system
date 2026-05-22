package UI;

import Enum.ButtonMode;
import Model.Comment;
import Model.Feedback;
import Service.CommentService;
import Service.FeedbackService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ViewFeedbackPanel extends JPanel {
    private final FeedbackService feedbackService;
    private final CommentService commentService;

    private JTable feedbackTable;
    private JTable commentTable;

    private DefaultTableModel feedbackModel;
    private DefaultTableModel commentModel;

    private TableRowSorter<DefaultTableModel> feedbackSorter;
    private TableRowSorter<DefaultTableModel> commentSorter;

    private final JTextField feedbackSearchField = new JTextField();
    private final JTextField commentSearchField = new JTextField();

    private JLabel totalFeedbackLabel;
    private JLabel totalCommentLabel;
    private JLabel averageRatingLabel;

    private CardLayout contentCardLayout;
    private JPanel contentPanel;

    private JButton feedbackTabBtn;
    private JButton commentTabBtn;

    private static final DateTimeFormatter DISPLAY_FORMATTER =
            DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a");

    public ViewFeedbackPanel(FeedbackService feedbackService, CommentService commentService) {
        this.feedbackService = feedbackService;
        this.commentService = commentService;

        setLayout(new BorderLayout());
        buildUI();
        loadData();
        showTab("feedback");
    }

    private void buildUI() {
        JPanel mainPanel = UIHelper.createSectionPanel("View Feedback and Comments");

        JPanel summaryPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 0));
        summaryPanel.setBackground(Color.WHITE);
        summaryPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        totalFeedbackLabel = new JLabel("Total Feedback: 0");
        totalFeedbackLabel.setFont(new Font("SansSerif", Font.BOLD, 14));

        totalCommentLabel = new JLabel("Total Comments: 0");
        totalCommentLabel.setFont(new Font("SansSerif", Font.BOLD, 14));

        averageRatingLabel = new JLabel("Average Rating: 0.00");
        averageRatingLabel.setFont(new Font("SansSerif", Font.BOLD, 14));

        summaryPanel.add(totalFeedbackLabel);
        summaryPanel.add(totalCommentLabel);
        summaryPanel.add(averageRatingLabel);

        JPanel selectorPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        selectorPanel.setBackground(Color.WHITE);
        selectorPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        feedbackTabBtn = createTabButton("Technician Feedback");
        feedbackTabBtn.setPreferredSize(new Dimension(180, 36));
        feedbackTabBtn.addActionListener(_ -> showTab("feedback"));

        commentTabBtn = createTabButton("Customer Comments");
        commentTabBtn.setPreferredSize(new Dimension(180, 36));
        commentTabBtn.addActionListener(_ -> showTab("comments"));

        selectorPanel.add(feedbackTabBtn);
        selectorPanel.add(commentTabBtn);

        contentCardLayout = new CardLayout();
        contentPanel = new JPanel(contentCardLayout);
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        contentPanel.add(createFeedbackTab(), "feedback");
        contentPanel.add(createCommentTab(), "comments");

        mainPanel.add(summaryPanel);
        mainPanel.add(Box.createVerticalStrut(12));
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

        if (tabName.equals("feedback")) {
            feedbackTabBtn.setBackground(activeColor);
            commentTabBtn.setBackground(inactiveColor);
        } else {
            feedbackTabBtn.setBackground(inactiveColor);
            commentTabBtn.setBackground(activeColor);
        }
    }

    private JPanel createFeedbackTab() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);

        String[] columns = {
                "Feedback ID",
                "Appointment ID",
                "Technician",
                "Customer",
                "Date Time"
        };

        feedbackModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        feedbackTable = UIHelper.createTable(feedbackModel);
        feedbackSorter = new TableRowSorter<>(feedbackModel);
        feedbackTable.setRowSorter(feedbackSorter);

        JPanel searchPanel = UIHelper.createSearchPanel(feedbackSearchField, this::applyFeedbackFilter);
        searchPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JScrollPane scrollPane = new JScrollPane(feedbackTable);
        scrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton viewBtn = UIHelper.createStyledButton("View Feedback", ButtonMode.PRIMARY);
        viewBtn.setPreferredSize(new Dimension(150, 35));
        viewBtn.addActionListener(_ -> handleViewFeedback());

        buttonPanel.add(viewBtn);

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
                "Customer ID",
                "Technician ID",
                "Counter Staff ID",
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
        loadFeedbackData();
        loadCommentData();

        totalFeedbackLabel.setText("Total Feedback: " + feedbackService.getTotalFeedbackCount());
        totalCommentLabel.setText("Total Comments: " + commentService.getAllComments().size());
        averageRatingLabel.setText(String.format("Average Rating: %.2f", getAverageCommentRating()));
    }

    private void loadFeedbackData() {
        feedbackModel.setRowCount(0);

        try {
            List<Feedback> feedbacks = feedbackService.getAllFeedbacks();

            for (Feedback feedback : feedbacks) {
                feedbackModel.addRow(new Object[]{
                        feedback.getFeedbackID(),
                        feedback.getAppointment().getAppointmentID(),
                        feedback.getTechnician().getUsername(),
                        feedback.getAppointment().getCustomer().getUsername(),
                        feedback.getDateTime().format(DISPLAY_FORMATTER)
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading feedback: " + e.getMessage());
        }
    }

    private void loadCommentData() {
        commentModel.setRowCount(0);

        List<Comment> comments = commentService.getAllComments();

        for (Comment comment : comments) {
            commentModel.addRow(new Object[]{
                    comment.getCommentID(),
                    comment.getAppointment().getAppointmentID(),
                    comment.getAppointment().getCustomer().getUserID(),
                    comment.getAppointment().getTechnician().getUserID(),
                    comment.getAppointment().getCounterStaff().getUserID(),
                    comment.getRating(),
                    comment.getCommentText()
            });
        }
    }

    private void applyFeedbackFilter() {
        String text = feedbackSearchField.getText();

        if (text.trim().isEmpty()) {
            feedbackSorter.setRowFilter(null);
        } else {
            feedbackSorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
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

    private void handleViewFeedback() {
        int row = feedbackTable.getSelectedRow();

        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a feedback record first!");
            return;
        }

        String feedbackID = feedbackTable.getValueAt(row, 0).toString();
        String feedbackText = getSelectedFeedbackText(feedbackID);
        String technician = feedbackTable.getValueAt(row, 2).toString();
        String customer = feedbackTable.getValueAt(row, 3).toString();
        String dateTime = feedbackTable.getValueAt(row, 4).toString();
        String appointmentID = feedbackTable.getValueAt(row, 1).toString();

        new ViewFeedbackDialog(
                this,
                feedbackID,
                feedbackText,
                technician,
                customer,
                dateTime,
                appointmentID
        ).setVisible(true);
    }

    private String getSelectedFeedbackText(String feedbackID) {
        try {
            for (Feedback feedback : feedbackService.getAllFeedbacks()) {
                if (feedback.getFeedbackID().equals(feedbackID)) {
                    return feedback.getFeedbackText();
                }
            }
        } catch (Exception e) {
            return "";
        }

        return "";
    }

    private void handleViewComment() {
        int row = commentTable.getSelectedRow();

        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a comment record first!");
            return;
        }

        String commentID = commentTable.getValueAt(row, 0).toString();
        String appointmentID = commentTable.getValueAt(row, 1).toString();
        String customerID = commentTable.getValueAt(row, 2).toString();
        String rating = commentTable.getValueAt(row, 5).toString();
        String commentText = commentTable.getValueAt(row, 6).toString();

        new ViewCommentDialog(
                this,
                commentID,
                commentText,
                rating,
                customerID,
                appointmentID
        ).setVisible(true);
    }

    private double getAverageCommentRating() {
        List<Comment> comments = commentService.getAllComments();

        if (comments.isEmpty()) {
            return 0.0;
        }

        int total = 0;

        for (Comment comment : comments) {
            total += comment.getRating();
        }

        return (double) total / comments.size();
    }
}