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

        JButton deleteBtn = UIHelper.createStyledButton("Delete Service", ButtonMode.DESTRUCTIVE);
        deleteBtn.setPreferredSize(new Dimension(150, 35));
        deleteBtn.addActionListener(_ -> handleDeleteService());

        buttonPanel.add(createBtn);
        buttonPanel.add(updatePriceBtn);
        buttonPanel.add(deleteBtn);

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
        AddServiceDialog dialog = new AddServiceDialog(
                this,
                serviceManagementService,
                () -> {
                    loadData();
                    JOptionPane.showMessageDialog(this, "Service created successfully.");
                }
        );

        dialog.setVisible(true);
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

        JTextField priceField = new JTextField(currentPrice, 20);

        JPanel formPanel = new JPanel();
        formPanel.setBackground(Color.WHITE);
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        JLabel serviceLabel = new JLabel("Service: " + serviceName);
        serviceLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        serviceLabel.setForeground(new Color(44, 62, 80));
        serviceLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel priceLabel = new JLabel("New Price (RM):");
        priceLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        priceLabel.setForeground(new Color(44, 62, 80));
        priceLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        priceField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        priceField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        priceField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(189, 195, 199)),
                BorderFactory.createEmptyBorder(6, 8, 6, 8)
        ));
        priceField.setAlignmentX(Component.LEFT_ALIGNMENT);

        formPanel.add(serviceLabel);
        formPanel.add(Box.createVerticalStrut(12));
        formPanel.add(priceLabel);
        formPanel.add(Box.createVerticalStrut(6));
        formPanel.add(priceField);

        while (true) {
            int result = JOptionPane.showConfirmDialog(
                    this,
                    formPanel,
                    "Update Price",
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.PLAIN_MESSAGE
            );

            if (result != JOptionPane.OK_OPTION) {
                return;
            }

            try {
                serviceManagementService.updateServicePrice(serviceID, priceField.getText());
                JOptionPane.showMessageDialog(this, "Service price updated successfully.");
                loadData();
                return;
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void handleDeleteService() {
        int row = table.getSelectedRow();

        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a service first!");
            return;
        }

        String serviceID = table.getValueAt(row, 0).toString();
        String serviceName = table.getValueAt(row, 1).toString();

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete this service?\n\n"
                        + "Service ID: " + serviceID + "\n"
                        + "Service Name: " + serviceName,
                "Delete Service",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            serviceManagementService.deleteService(serviceID);
            JOptionPane.showMessageDialog(this, "Service deleted successfully.");
            loadData();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
