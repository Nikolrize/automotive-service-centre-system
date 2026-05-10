//Manager: UI for managing service
package UI;

import Model.Service;
import Service.ServiceManagementService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.util.List;

import Enum.*;

public class ManageServicePanel extends JPanel {
    private final ServiceManagementService serviceManagementService;
    private JTable table;
    private DefaultTableModel model;
    private final JTextField searchField = new JTextField();
    private TableRowSorter<DefaultTableModel> sorter;

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
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = UIHelper.createTable(model);
        sorter = new TableRowSorter<>(model);
        table.setRowSorter(sorter);

        // Search bar
        JPanel searchPanel = UIHelper.createSearchPanel(searchField, this::applyFilter);
        searchPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton createBtn = UIHelper.createStyledButton("Create Service", ButtonMode.CREATE);
        createBtn.setPreferredSize(new Dimension(150,35));
        createBtn.addActionListener(_ -> handleCreateService());

        JButton updatePriceBtn = UIHelper.createStyledButton("Update Price", ButtonMode.PRIMARY);
        updatePriceBtn.setPreferredSize(new Dimension(150, 35));
        updatePriceBtn.addActionListener(_ -> handleUpdatePrice());

        JButton refreshBtn = UIHelper.createStyledButton("Refresh", ButtonMode.CREATE);
        refreshBtn.addActionListener(_ -> loadData());

        buttonPanel.add(createBtn);
        buttonPanel.add(updatePriceBtn);
        buttonPanel.add(refreshBtn);

        mainPanel.add(searchPanel);
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(scrollPane);
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(buttonPanel);

        add(mainPanel, BorderLayout.CENTER);
    }

    private void applyFilter() {
        String text = searchField.getText();

        if (text.trim().isEmpty()) {
            sorter.setRowFilter(null);
        } else {
            sorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
        }
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

    private void handleCreateService() {
        JTextField nameField = new JTextField();
        JTextField typeField = new JTextField();
        JTextField priceField = new JTextField();
        JTextField durationField = new JTextField();

        Object[] fields = {
                "Service Name", nameField,
                "Type (Normal/Major):", typeField,
                "Price (RM):", priceField,
                "Duration (Hours):", durationField
        };

        while (true) {
            int result = JOptionPane.showConfirmDialog(this, fields, "Create Service", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

            if (result != JOptionPane.OK_OPTION) {
                return;
            }

            try {
                serviceManagementService.createService(nameField.getText(), typeField.getText(), priceField.getText(), durationField.getText());
                JOptionPane.showMessageDialog(this, "Service created successfully.");
                loadData();
                return;
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
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

        while (true) { //ensures loop for price input
            String newPrice = JOptionPane.showInputDialog(
                    this,
                    "Enter new price for " + serviceName + ":",
                    currentPrice
            );

            //cancel
            if (newPrice == null) {
                return;
            }

            try {
                serviceManagementService.updateServicePrice(serviceID, newPrice);
                JOptionPane.showMessageDialog(this, "Service price updated successfully.");
                loadData();
                return;
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
