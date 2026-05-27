package UI;

import Model.Comment;
import Model.Technician;
import Service.CommentService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.util.List;

import Enum.*;

public class ViewCommentPanel extends JPanel {

    private final Technician currentTechnician;
    private final CommentService commentService;

    private JTable table;
    private DefaultTableModel model;
    private final JTextField searchField = new JTextField();
    private TableRowSorter<DefaultTableModel> sorter;

    public ViewCommentPanel(Technician currentTechnician, CommentService commentService) {
        this.currentTechnician = currentTechnician;
        this.commentService = commentService;

        setLayout(new BorderLayout());
        buildUI();
        loadData();

        commentService.setOnCommentChangedCallback(
                () -> SwingUtilities.invokeLater(this::loadData)
        );
    }

    private void buildUI() {
        JPanel mainPanel = UIHelper.createSectionPanel("View Appointment Comments");

        String[] columns = {"Comment ID", "Comment", "Rating", "Customer", "Appointment ID","Service"};

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

        JButton viewButton = UIHelper.createStyledButton("View Comment",ButtonMode.PRIMARY);
        viewButton.addActionListener(_ -> handleViewComment());
        viewButton.setPreferredSize(new Dimension(155, 35));
        buttonPanel.add(viewButton);

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
            List<Comment> comments = commentService.getCommentsByTechnician(currentTechnician);

            for (Comment comment : comments) {
                model.addRow(new Object[]{
                        comment.getCommentID(),
                        comment.getCommentText(),
                        comment.getRating() + " / 5",
                        comment.getAppointment().getCustomer().getUsername(),
                        comment.getAppointment().getAppointmentID(),
                        comment.getAppointment().getService().getName()
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading comments: " + e.getMessage());
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

    private void handleViewComment() {
        int row = table.getSelectedRow();

        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a comment first.");
            return;
        }

        String commentID     = table.getValueAt(row, 0).toString();
        String commentText   = table.getValueAt(row, 1).toString();
        String rating        = table.getValueAt(row, 2).toString();
        String customer      = table.getValueAt(row, 3).toString();
        String appointmentID = table.getValueAt(row, 4).toString();
        String service       = table.getValueAt(row, 5).toString();

        new ViewCommentDialog(this, commentID, commentText, rating, customer, appointmentID, service)
                .setVisible(true);
    }
}