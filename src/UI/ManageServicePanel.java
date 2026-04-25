//Manager: UI for managing service
package UI;

import Model.Service;
import Service.ServiceManagementService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.List;

public class ManageServicePanel extends JPanel {
    private final ServiceManagementService serviceManagementService;
    private JTable table;
    private DefaultTableModel model;

    public ManageServicePanel() {
        this.serviceManagementService = new ServiceManagementService();

        setLayout(new BorderLayout());
        manageServiceUI();
        loadData();
    }

    private void manageServiceUI() {
        JPanel mainPanel = UIHelper.createSectionPanel("Manage Service Prices");

        //service table to view price and duration
        String[] columns = {"Service ID", "Service Name", "Type", "Price (RM)", "Duration(Hours)"};

        model = new DefaultTableModel(columns, 0 ) {
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

        JButton updatePriceBtn = new JButton("Update Price");
        updatePriceBtn.setBackground(new Color (135,90,224));
        updatePriceBtn.setForeground(Color.WHITE);
        updatePriceBtn.addActionListener(_ -> handleUpdatePrice());

        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.setBackground(new Color(66,135,245));
        refreshBtn.setForeground(Color.WHITE);
        refreshBtn.addActionListener(_ -> loadData());

        buttonPanel.add(updatePriceBtn);
        buttonPanel.add(refreshBtn);

        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(scrollPane);
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(buttonPanel);

        add(mainPanel, BorderLayout.CENTER);
    }

    public void loadData() {
        model.setRowCount(0);

        List<Service> services = serviceManagementService.getAllServices();

        for (Service service : services) {
            model.addRow(new Object[] {
                    service.getServiceID(),
                    service.getName(),
                    service.getType(),
                    String.format("%.2f", service.getPrice()),
                    service.getDuration()
            });
        }
    }

    private void handleUpdatePrice() {
        int row = table.getSelectedRow();

        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a service first!");
            return;
        }

        String serviceID = table.getValueAt(row, 0).toString();
        String serviceName = table.getValueAt(row, 1).toString();
        String currentPrice = table.getValueAt(row, 3).toString();

        String newPrice = JOptionPane.showInputDialog(
                this,
                "Enter new price for " + serviceName + ":",
                currentPrice
        );

        if (newPrice == null) {
            return;
        }

        try {
            serviceManagementService.updateServicePrice(serviceID, newPrice);
            JOptionPane.showMessageDialog(this, "Service price updated successfully.");
            loadData();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
