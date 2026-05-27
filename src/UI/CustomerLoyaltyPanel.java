package UI;

import Enum.ButtonMode;
import Model.Customer;
import Model.LoyaltyReward;
import Service.LoyaltyService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.util.List;

public class CustomerLoyaltyPanel extends JPanel {
    private final Customer currentCustomer;
    private final LoyaltyService loyaltyService;

    private JLabel pointsLabel;
    private JLabel rewardLabel;

    private JTable rewardTable;
    private DefaultTableModel rewardModel;
    private final JTextField searchField = new JTextField();
    private TableRowSorter<DefaultTableModel> sorter;

    public CustomerLoyaltyPanel(Customer currentCustomer, LoyaltyService loyaltyService) {
        this.currentCustomer = currentCustomer;
        this.loyaltyService = loyaltyService;

        setLayout(new BorderLayout());
        buildUI();
        loadData();
    }

    private void buildUI() {
        JPanel mainPanel = UIHelper.createSectionPanel("My Loyalty Rewards");

        JPanel summaryPanel = new JPanel(new GridLayout(1, 2, 15, 0));
        summaryPanel.setBackground(Color.WHITE);
        summaryPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        pointsLabel = new JLabel();
        rewardLabel = new JLabel();

        summaryPanel.add(createInfoCard("Current Points", pointsLabel));
        summaryPanel.add(createInfoCard("Next Reward", rewardLabel));

        String[] columns = {
                "Reward ID",
                "Reward Name",
                "Discount Amount",
                "Points Required",
                "Status"
        };

        rewardModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        rewardTable = UIHelper.createTable(rewardModel);
        sorter = new TableRowSorter<>(rewardModel);
        rewardTable.setRowSorter(sorter);

        JPanel searchPanel = UIHelper.createSearchPanel(searchField, this::applyFilter);
        searchPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JScrollPane scrollPane = new JScrollPane(rewardTable);
        scrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton redeemBtn = UIHelper.createStyledButton("Redeem Reward", ButtonMode.CREATE);
        redeemBtn.setPreferredSize(new Dimension(170, 35));
        redeemBtn.addActionListener(_ -> handleRedeemReward());

        JButton refreshBtn = UIHelper.createStyledButton("Refresh", ButtonMode.PRIMARY);
        refreshBtn.setPreferredSize(new Dimension(120, 35));
        refreshBtn.addActionListener(_ -> loadData());

        buttonPanel.add(redeemBtn);
        buttonPanel.add(refreshBtn);

        mainPanel.add(summaryPanel);
        mainPanel.add(Box.createVerticalStrut(15));
        mainPanel.add(searchPanel);
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(scrollPane);
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(buttonPanel);

        add(mainPanel, BorderLayout.CENTER);
    }

    private JPanel createInfoCard(String title, JLabel valueLabel) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(new Color(245, 245, 245));
        card.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 14));

        valueLabel.setFont(new Font("SansSerif", Font.BOLD, 22));
        valueLabel.setForeground(new Color(135, 90, 224));

        card.add(titleLabel);
        card.add(Box.createVerticalStrut(8));
        card.add(valueLabel);

        return card;
    }

    public void loadData() {
        loadSummary();
        loadRewards();
    }

    private void loadSummary() {
        int points = loyaltyService.getPoints(currentCustomer);
        pointsLabel.setText(points + " points");

        if (points < 100) {
            rewardLabel.setText((100 - points) + " points to RM20 Voucher");
        } else if (points < 200) {
            rewardLabel.setText((200 - points) + " points to RM50 Voucher");
        } else if (points < 300) {
            rewardLabel.setText((300 - points) + " points to RM100 Voucher");
        } else {
            rewardLabel.setText("Highest reward unlocked");
        }
    }

    private void loadRewards() {
        rewardModel.setRowCount(0);

        int points = loyaltyService.getPoints(currentCustomer);

        addRedeemableReward("RM20 Discount Voucher", 20.00, 100, points);
        addRedeemableReward("RM50 Discount Voucher", 50.00, 200, points);
        addRedeemableReward("RM100 Discount Voucher", 100.00, 300, points);

        List<LoyaltyReward> rewards = loyaltyService.getRewardsByCustomer(currentCustomer);

        for (LoyaltyReward reward : rewards) {
            int pointsRequired = getPointsRequired(reward.getRewardName());

            rewardModel.addRow(new Object[]{
                    reward.getRewardID(),
                    reward.getRewardName(),
                    String.format("RM %.2f", reward.getDiscountAmount()),
                    pointsRequired,
                    reward.getStatus()
            });
        }
    }

    private void addRedeemableReward(String rewardName, double discountAmount, int pointsRequired, int currentPoints) {
        if (currentPoints >= pointsRequired) {
            rewardModel.addRow(new Object[]{
                    "-",
                    rewardName,
                    String.format("RM %.2f", discountAmount),
                    pointsRequired,
                    "Available"
            });
        }
    }

    private int getPointsRequired(String rewardName) {
        return switch (rewardName) {
            case "RM20 Discount Voucher" -> 100;
            case "RM50 Discount Voucher" -> 200;
            case "RM100 Discount Voucher" -> 300;
            default -> 0;
        };
    }

    private void applyFilter() {
        String text = searchField.getText();

        if (text.trim().isEmpty()) {
            sorter.setRowFilter(null);
        } else {
            sorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
        }
    }

    private void handleRedeemReward() {
        int row = rewardTable.getSelectedRow();

        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select one reward to redeem.");
            return;
        }

        row = rewardTable.convertRowIndexToModel(row);

        String rewardID = rewardModel.getValueAt(row, 0).toString();
        String rewardName = rewardModel.getValueAt(row, 1).toString();
        String discountText = rewardModel.getValueAt(row, 2).toString().replace("RM", "").trim();
        String pointsText = rewardModel.getValueAt(row, 3).toString();
        String status = rewardModel.getValueAt(row, 4).toString();

        if (!rewardID.equals("-") || !status.equalsIgnoreCase("Available")) {
            JOptionPane.showMessageDialog(this, "You can only redeem rewards with Available status.");
            return;
        }

        double discountAmount = Double.parseDouble(discountText);
        int pointsNeeded = Integer.parseInt(pointsText);

        try {
            String message = loyaltyService.redeemReward(
                    currentCustomer,
                    rewardName,
                    discountAmount,
                    pointsNeeded
            );

            JOptionPane.showMessageDialog(this, message);
            loadData();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }
}