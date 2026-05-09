package UI;

import Enum.ButtonMode;
import Enum.FormMode;
import Model.Customer;
import Model.Vehicle;
import Service.UserService;
import Service.VehicleService;
import Util.TextFormatter;

import javax.swing.*;
import java.awt.*;

public class VehicleFormPanel extends JPanel {

    private final VehicleService vehicleService;
    private final UserService userService;
    private final FormMode mode;
    private final Vehicle vehicle;
    private final Runnable onBack;

    private Customer selectedCustomer;

    private JTextField plateNumberField;
    private JTextField brandField;
    private JTextField modelField;
    private JTextField colorField;
    private JTextField yearField;

    private JTextArea customerField;

    public VehicleFormPanel(
            FormMode mode,
            Vehicle vehicle,
            VehicleService vehicleService,
            UserService userService,
            Runnable onBack
    ) {
        this.mode = mode;
        this.vehicle = vehicle;
        this.vehicleService = vehicleService;
        this.userService = userService;
        this.onBack = onBack;

        setLayout(new BorderLayout());
        buildUI();
    }

    private void buildUI() {
        JPanel mainPanel = UIHelper.createSectionPanel(
                mode == FormMode.EDIT
                        ? "Edit Vehicle"
                        : "Add Vehicle"
        );

        // Main Content Panel
        JPanel contentPanel = new JPanel(new GridLayout(1, 2, 15, 0));
        contentPanel.setBackground(Color.WHITE);

        // Customer Card
        JPanel customerCard = UIHelper.createCardPanel("Customer");
        customerCard.setAlignmentX(Component.LEFT_ALIGNMENT);

        customerField = new JTextArea(6, 20);
        customerField.setEditable(false);
        customerField.setFont(new Font("SansSerif", Font.PLAIN, 13));
        customerField.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        customerField.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton selectCustomerBtn = createSelectCustomerButton();

        customerCard.add(customerField);

        if (mode == FormMode.CREATE) {
            customerCard.add(Box.createVerticalStrut(10));
            customerCard.add(selectCustomerBtn);
        }

        // Vehicle Card
        JPanel vehicleCard = UIHelper.createCardPanel("Vehicle Details");
        vehicleCard.setAlignmentX(Component.LEFT_ALIGNMENT);

        plateNumberField = createField();
        brandField = createField();
        modelField = createField();
        colorField = createField();
        yearField = createField();

        JPanel vehicleForm = new JPanel(new GridBagLayout());
        vehicleForm.setBackground(Color.WHITE);
        vehicleForm.setAlignmentX(Component.LEFT_ALIGNMENT);

        int y = 0;

        addField(vehicleForm, "Plate Number:", plateNumberField, y);
        y += 2;

        addField(vehicleForm, "Brand:", brandField, y);
        y += 2;

        addField(vehicleForm, "Model:", modelField, y);
        y += 2;

        addField(vehicleForm, "Color:", colorField, y);
        y += 2;

        addField(vehicleForm, "Year:", yearField, y);

        vehicleCard.add(vehicleForm);

        // Load Data
        if (mode == FormMode.EDIT && vehicle != null) {
            loadVehicleData();
        }

        // Content Panel
        contentPanel.add(customerCard);
        contentPanel.add(vehicleCard);

        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(Color.WHITE);

        JButton saveBtn = createSaveButton();
        saveBtn.setPreferredSize(new Dimension(150, 35));

        JButton backBtn = createBackButton();

        buttonPanel.add(backBtn);
        buttonPanel.add(saveBtn);

        // Layout
        mainPanel.add(contentPanel);
        mainPanel.add(Box.createVerticalStrut(15));
        mainPanel.add(buttonPanel);

        add(mainPanel, BorderLayout.CENTER);

    }

    private JTextField createField() {
        JTextField field = new JTextField();
        field.setPreferredSize(new Dimension(400, 35));
        return field;
    }

    private void addField(
            JPanel panel,
            String label,
            JTextField field,
            int y
    ) {
        panel.add(
                new JLabel(label),
                UIHelper.createGbc(0, y, 10, 5, 5, 5)
        );

        panel.add(
                field,
                UIHelper.createGbc(0, y + 1, 5, 5, 10, 5)
        );
    }

    private JButton createSelectCustomerButton() {
        JButton selectCustomerBtn = UIHelper.createStyledButton("Select Customer", ButtonMode.PRIMARY);

        selectCustomerBtn.addActionListener(_ -> {
            SelectionDialog<Customer> dialog = new SelectionDialog<>(
                    (JFrame) SwingUtilities.getWindowAncestor(this),
                    "Select Customer", userService.getAllCustomers(),
                    new String[]{"User ID", "Customer ID", "Username", "First Name", "Last Name", "Email", "Contact"},
                    c -> new Object[]{c.getUserID(), c.getRoleID(), c.getUsername(), c.getFirstName(), c.getLastName(), c.getEmail(), c.getContact()}
            );

            dialog.setVisible(true);

            Customer c = dialog.getSelectedItem();
            if (c != null) {
                selectedCustomer = c;
                customerField.setText(TextFormatter.formatCustomer(c));
            }
        });

        return selectCustomerBtn;
    }

    private void loadVehicleData() {
        selectedCustomer = vehicle.getCustomer();

        customerField.setText(TextFormatter.formatCustomer(selectedCustomer));
        plateNumberField.setText(vehicle.getPlateNumber());
        brandField.setText(vehicle.getBrand());
        modelField.setText(vehicle.getModel());
        colorField.setText(vehicle.getColor());
        yearField.setText(String.valueOf(vehicle.getYear()));
    }

    private JButton createSaveButton() {
        JButton saveBtn = UIHelper.createStyledButton(
                mode == FormMode.EDIT
                        ? "Save Changes"
                        : "Add Vehicle",
                ButtonMode.PRIMARY
        );

        saveBtn.addActionListener(_ -> {
            try {
                if (selectedCustomer == null) {
                    throw new Exception("Please select a customer.");
                }

                String plate = plateNumberField.getText().trim();
                String brand = brandField.getText().trim();
                String model = modelField.getText().trim();
                String color = colorField.getText().trim();
                String year = yearField.getText().trim();

                if (
                        plate.isEmpty() ||
                        brand.isEmpty() ||
                        model.isEmpty() ||
                        color.isEmpty() ||
                        year.isEmpty()
                ) {
                    throw new Exception("Please fill in all vehicle details.");
                }

                int yearInt = Integer.parseInt(
                        yearField.getText().trim()
                );

                if (mode == FormMode.CREATE) {
                    vehicleService.createVehicle(
                            selectedCustomer,
                            plate,
                            brand,
                            model,
                            yearInt,
                            color
                    );

                    JOptionPane.showMessageDialog(
                            this,
                            "Vehicle added successfully!"
                    );

                } else {
                    vehicleService.updateVehicle(
                            vehicle.getVehicleID(),
                            plate,
                            brand,
                            model,
                            yearInt,
                            color
                    );

                    JOptionPane.showMessageDialog(
                            this,
                            "Vehicle updated successfully!"
                    );
                }

                if (onBack != null) {
                    onBack.run();
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(
                        this,
                        "Year must be a valid number.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(
                        this,
                        ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        });

        return saveBtn;
    }

    private JButton createBackButton() {
        JButton backBtn = UIHelper.createStyledButton(
                "Back",
                ButtonMode.PRIMARY
        );

        backBtn.addActionListener(_ -> onBack.run());

        return backBtn;
    }
}