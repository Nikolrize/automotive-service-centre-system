package UI;

import Enum.ButtonMode;
import Enum.FormMode;
import Model.Vehicle;
import Service.UserService;
import Service.VehicleService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;

public class ManageVehiclePanel extends JPanel {
    private final VehicleService vehicleService;
    private final UserService userService;

    private JTable table;
    private DefaultTableModel model;
    private TableRowSorter<DefaultTableModel> sorter;

    private final JTextField searchField = new JTextField();

    private final JPanel rightPanel;
    private final CardLayout cardLayout;

    public ManageVehiclePanel(
            VehicleService vehicleService,
            UserService userService,
            JPanel rightPanel,
            CardLayout cardLayout
    ) {
        this.vehicleService = vehicleService;
        this.userService = userService;
        this.rightPanel = rightPanel;
        this.cardLayout = cardLayout;

        setLayout(new BorderLayout());

        buildUI();
        loadData();
    }

    private void buildUI() {
        JPanel mainPanel = UIHelper.createSectionPanel("Manage Vehicle");

        // Table
        String[] columns = {
                "Vehicle ID",
                "Owner",
                "Plate Number",
                "Brand",
                "Model",
                "Year",
                "Color"
        };

        model = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = UIHelper.createTable(model);
        sorter = new TableRowSorter<>(model);
        table.setRowSorter(sorter);

        // Search
        JPanel searchPanel = UIHelper.createSearchPanel(
                searchField,
                this::applyFilter
        );
        searchPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Scroll
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton createBtn = UIHelper.createStyledButton(
                "Create",
                ButtonMode.CREATE
        );

        JButton editBtn = UIHelper.createStyledButton(
                "Edit",
                ButtonMode.PRIMARY
        );

        JButton deleteBtn = UIHelper.createStyledButton(
                "Delete",
                ButtonMode.DESTRUCTIVE
        );

        createBtn.addActionListener(_ -> handleCreate());
        editBtn.addActionListener(_ -> handleEdit());
        deleteBtn.addActionListener(_ -> handleDelete());

        buttonPanel.add(createBtn);
        buttonPanel.add(editBtn);
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

    private void loadData() {
        model.setRowCount(0);

        for (Vehicle v : vehicleService.getAllVehicles()) {

            model.addRow(new Object[]{
                    v.getVehicleID(),
                    v.getCustomer().getUsername(),
                    v.getPlateNumber(),
                    v.getBrand(),
                    v.getModel(),
                    v.getYear(),
                    v.getColor()
            });
        }
    }

    private Vehicle getSelectedVehicle() {
        int row = table.getSelectedRow();

        if (row == -1) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please select a vehicle first!"
            );
            return null;
        }

        String vehicleID = table
                .getValueAt(row, 0)
                .toString();

        return vehicleService.getVehicleByID(vehicleID);
    }

    private void handleCreate() {
        VehicleFormPanel panel = new VehicleFormPanel(
                FormMode.CREATE,
                null,
                vehicleService,
                userService,
                () -> {
                    cardLayout.show(rightPanel, "Manage Vehicle");
                    loadData();
                }
        );

        rightPanel.add(panel, "Create Vehicle");
        cardLayout.show(rightPanel, "Create Vehicle");
    }

    private void handleEdit() {
        Vehicle vehicle = getSelectedVehicle();

        if (vehicle == null) return;

        VehicleFormPanel panel = new VehicleFormPanel(
                FormMode.EDIT,
                vehicle,
                vehicleService,
                userService,
                () -> {
                    cardLayout.show(rightPanel, "Manage Vehicle");
                    loadData();
                }
        );

        rightPanel.add(panel, "Edit Vehicle");
        cardLayout.show(rightPanel, "Edit Vehicle");
    }

    private void handleDelete() {
        Vehicle vehicle = getSelectedVehicle();

        if (vehicle == null) return;

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Delete this vehicle?",
                "Confirm",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            vehicleService.deleteVehicle(
                    vehicle.getVehicleID()
            );

            loadData();

        } catch (Exception ex) {

            JOptionPane.showMessageDialog(
                    this,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }
}