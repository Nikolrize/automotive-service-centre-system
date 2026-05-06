//Manager: View Feedback and Comments
package UI;

import Service.FeedbackService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.List;
import Enum.*;

public class ViewFeedbackPanel extends JPanel{
    private final FeedbackService feedbackService;
    private JTable table;
    private DefaultTableModel model;
    private JLabel totalFeedbackLabel;
    private JLabel averageRatingLabel;

    public ViewFeedbackPanel () {
        this.feedbackService = new FeedbackService();
        setLayout(new BorderLayout());
        viewFeedbackUI();
    }

    private void viewFeedbackUI() {
        JPanel mainPanel = UIHelper.createSectionPanel("View Customer Feedback");

        //summary panels that help manager understand better
        JPanel summaryPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 0));
        summaryPanel.setBackground(Color.WHITE);
        summaryPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        totalFeedbackLabel = new JLabel("Total Feedback: 0");
        totalFeedbackLabel.setFont(new Font("SansSerif", Font.BOLD, 14));

        averageRatingLabel = new JLabel("Average Rating: 0.00");
        averageRatingLabel.setFont(new Font("SansSerif", Font.BOLD, 14));

        summaryPanel.add(totalFeedbackLabel);
        summaryPanel.add(averageRatingLabel);

        //table headers
        String[] columns = {
                "Feedback ID",
                "Appointment ID",
                "Customer ID",
                "Technician ID",
                "Counter Staff ID",
                "Rating",
                "Comment",
        };

        model = new DefaultTableModel(columns, 0) {
            //prevents direct editing
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(model);
        table.setRowHeight(40);
        table.setFont(new Font("SansSerif", Font.PLAIN, 13));
        table.setSelectionBackground(new Color(230,220,255));
        table.setGridColor(new Color(240,240,240));
        table.setShowVerticalLines(false);

        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("SansSerif", Font.BOLD, 14));
        header.setBackground(new Color(245,245,245));

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton refreshBtn = UIHelper.createStyledButton("Refresh", ButtonMode.CREATE);
        refreshBtn.addActionListener(_ -> loadData());

        JButton viewCommmentBtn = UIHelper.createStyledButton("View Comment", ButtonMode.PRIMARY);
        viewCommmentBtn.setPreferredSize(new Dimension(150, 35));
        viewCommmentBtn.addActionListener(_ -> handleViewComment());

        buttonPanel.add(viewCommmentBtn);
        buttonPanel.add(refreshBtn);

        mainPanel.add(summaryPanel);
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(scrollPane);
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(buttonPanel);

        add(mainPanel, BorderLayout.CENTER);

    }

    //temporary cuz appointment service not finalized
    // once finalized, use List<Feedback> instead
    public void loadData() {
        model.setRowCount(0);

        List<String[]> feedbackRecords = feedbackService.getAllFeedbackRecords();

        for (String[] record : feedbackRecords) {
            model.addRow(new Object[]{
                    record[0],
                    record[1],
                    record[2],
                    record[3],
                    record[4],
                    record[5],
                    record[6]
            });
        }

        totalFeedbackLabel.setText("Total Feedback: " + feedbackService.getTotalFeedbackCount());
        averageRatingLabel.setText(String.format("Average Rating: %.2f", feedbackService.getAverageRating()));
    }

    private void handleViewComment() {
        int row = table.getSelectedRow();

        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a feedback record first!");
            return;
        }

        String feedbackID = table.getValueAt(row,0).toString();
        String rating = table.getValueAt(row, 5).toString();
        String comment = table.getValueAt(row, 6).toString();

        //show full comment in dialog in case table column to small
        JOptionPane.showMessageDialog(
                this,
                "Feedback ID: " + feedbackID + "\nRating: " + rating + "\nComment: " + comment,
                "Feedback Details",
                JOptionPane.INFORMATION_MESSAGE
        );
    }
}
