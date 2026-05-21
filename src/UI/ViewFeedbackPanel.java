package UI;

import Enum.ButtonMode;
import Service.CommentService;
import Service.FeedbackService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableRowSorter;
import java.awt.*;
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

    public ViewFeedbackPanel(FeedbackService feedbackService, CommentService commentService) {
        this.feedbackService = feedbackService;
        this.commentService = commentService;

        setLayout(new BorderLayout());
        buildUI();
        loadData();
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

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setAlignmentX(Component.LEFT_ALIGNMENT);

        tabbedPane.addTab("Technician Feedback", createFeedbackTab());
        tabbedPane.addTab("Customer Comments", createCommentTab());

        mainPanel.add(summaryPanel);
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(tabbedPane);

        add(mainPanel, BorderLayout.CENTER);
    }

    private JPanel createFeedbackTab() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);

        String[] columns = {
                "Feedback ID",
                "Appointment ID",
                "Technician ID",
                "Feedback",
                "Date Time"
        };

        feedbackModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        feedbackTable = new JTable(feedbackModel);
        feedbackTable.setRowHeight(40);
        feedbackTable.setFont(new Font("SansSerif", Font.PLAIN, 13));
        feedbackTable.setSelectionBackground(new Color(230, 220, 255));
        feedbackTable.setGridColor(new Color(240, 240, 240));
        feedbackTable.setShowVerticalLines(false);

        JTableHeader header = feedbackTable.getTableHeader();
        header.setFont(new Font("SansSerif", Font.BOLD, 14));
        header.setBackground(new Color(245, 245, 245));

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

        commentTable = new JTable(commentModel);
        commentTable.setRowHeight(40);
        commentTable.setFont(new Font("SansSerif", Font.PLAIN, 13));
        commentTable.setSelectionBackground(new Color(230, 220, 255));
        commentTable.setGridColor(new Color(240, 240, 240));
        commentTable.setShowVerticalLines(false);

        JTableHeader header = commentTable.getTableHeader();
        header.setFont(new Font("SansSerif", Font.BOLD, 14));
        header.setBackground(new Color(245, 245, 245));

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
        totalCommentLabel.setText("Total Comments: " + commentService.getAllComments().size());
        averageRatingLabel.setText(String.format("Average Rating: %.2f", getAverageCommentRating()));
    }

    private void loadFeedbackData() {
        feedbackModel.setRowCount(0);

        List<String[]> feedbackRecords = feedbackService.getAllFeedbackRecords();

        for (String[] record : feedbackRecords) {
            feedbackModel.addRow(new Object[]{
                    record[0],
                    record[1],
                    record[2],
                    record[3],
                    record[4]
            });
        }
    }

    private void loadCommentData() {
        commentModel.setRowCount(0);

        List<Model.Comment> comments = commentService.getAllComments();

        for (Model.Comment comment : comments) {
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
        String appointmentID = feedbackTable.getValueAt(row, 1).toString();
        String technicianID = feedbackTable.getValueAt(row, 2).toString();
        String feedbackText = feedbackTable.getValueAt(row, 3).toString();
        String dateTime = feedbackTable.getValueAt(row, 4).toString();

        JOptionPane.showMessageDialog(
                this,
                "Feedback ID: " + feedbackID +
                        "\nAppointment ID: " + appointmentID +
                        "\nTechnician ID: " + technicianID +
                        "\nDate Time: " + dateTime +
                        "\nFeedback: " + feedbackText,
                "Feedback Details",
                JOptionPane.INFORMATION_MESSAGE
        );
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
        String technicianID = commentTable.getValueAt(row, 3).toString();
        String counterStaffID = commentTable.getValueAt(row, 4).toString();
        String rating = commentTable.getValueAt(row, 5).toString();
        String commentText = commentTable.getValueAt(row, 6).toString();

        JOptionPane.showMessageDialog(
                this,
                "Comment ID: " + commentID +
                        "\nAppointment ID: " + appointmentID +
                        "\nCustomer ID: " + customerID +
                        "\nTechnician ID: " + technicianID +
                        "\nCounter Staff ID: " + counterStaffID +
                        "\nRating: " + rating +
                        "\nComment: " + commentText,
                "Comment Details",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    private double getAverageCommentRating() {
        List<Model.Comment> comments = commentService.getAllComments();

        if (comments.isEmpty()) {
            return 0.0;
        }

        int total = 0;

        for (Model.Comment comment : comments) {
            total += comment.getRating();
        }

        return (double) total / comments.size();
    }
}
