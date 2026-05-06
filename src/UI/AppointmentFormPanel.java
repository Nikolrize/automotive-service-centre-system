package UI;

import Enum.*;
import Model.*;
import Service.*;
import Util.TextFormatter;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class AppointmentFormPanel extends JPanel {
    private final FormMode mode;
    private final Appointment appointment;
    private final AppointmentService appointmentService;
    private final UserService userService;
    private final ServiceManagementService serviceService;
    private final CounterStaff currentUser;
    private final Runnable onBack;

    private Customer selectedCustomer;
    private Technician selectedTechnician;
    private List<Technician> availableTechnicians = List.of();
    private Service selectedService;

    private final JTextArea customerField = new JTextArea(4, 20);
    private final JTextArea technicianField = new JTextArea(4, 20);
    private final JTextArea serviceField = new JTextArea(4, 20);

    private DateTimePicker dateTimePicker;
    private final JLabel endTimeLabel = new JLabel("End Time: -");

    public AppointmentFormPanel(FormMode mode, Appointment appointment, AppointmentService appointmentService, UserService userService, ServiceManagementService serviceService, CounterStaff currentUser, Runnable onBack) {
        this.mode = mode;
        this.appointment = appointment;
        this.appointmentService = appointmentService;
        this.userService = userService;
        this.serviceService = serviceService;
        this.currentUser = currentUser;
        this.onBack = onBack;

        setLayout(new BorderLayout());
        buildUI();
        attachAutoRefresh();
    }

    private void buildUI() {
        JPanel mainPanel = UIHelper.createSectionPanel(mode == FormMode.EDIT ? "Edit Appointment" : "Create Appointment");

        // Customer Card
        JPanel customerCard = UIHelper.createCardPanel("Customer");
        customerCard.setAlignmentX(Component.LEFT_ALIGNMENT);
        customerField.setFont(new Font("SansSerif", Font.PLAIN, 13));
        customerField.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
        customerField.setAlignmentX(Component.LEFT_ALIGNMENT);
        customerField.setEditable(false);

        JButton selectCustomerBtn = createSelectCustomerButton();

        customerCard.add(customerField);
        customerCard.add(Box.createVerticalStrut(8));
        customerCard.add(selectCustomerBtn);

        // Service
        JPanel serviceCard = UIHelper.createCardPanel("Service");
        serviceCard.setAlignmentX(Component.LEFT_ALIGNMENT);
        serviceField.setFont(new Font("SansSerif", Font.PLAIN, 13));
        serviceField.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
        serviceField.setAlignmentX(Component.LEFT_ALIGNMENT);
        serviceField.setEditable(false);

        JButton selectServiceBtn = createSelectServiceButton();

        serviceCard.add(serviceField);
        serviceCard.add(Box.createVerticalStrut(8));
        serviceCard.add(selectServiceBtn);

        // Schedule Card
        JPanel scheduleCard = UIHelper.createCardPanel("Schedule");
        scheduleCard.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel dateTimeLabel = new JLabel("Date and Time");
        dateTimeLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        endTimeLabel.setFont(new Font("SansSerif", Font.BOLD, 13));
        endTimeLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        dateTimePicker = new DateTimePicker();
        dateTimePicker.setAlignmentX(Component.LEFT_ALIGNMENT);
        dateTimePicker.setOnChange(() -> {
            if (selectedService != null) {
                refreshTechnicians();
                updateEndTime();
            }
        });

        scheduleCard.add(dateTimeLabel);
        scheduleCard.add(Box.createVerticalStrut(8));
        scheduleCard.add(endTimeLabel);
        scheduleCard.add(Box.createVerticalStrut(8));
        scheduleCard.add(dateTimePicker);
        scheduleCard.add(Box.createVerticalStrut(8));

        // Technician Card
        JPanel techCard = UIHelper.createCardPanel("Technician");
        techCard.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton selectTechBtn = createSelectTechnicianButton();

        technicianField.setFont(new Font("SansSerif", Font.PLAIN, 13));
        technicianField.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
        technicianField.setAlignmentX(Component.LEFT_ALIGNMENT);
        technicianField.setEditable(false);

        techCard.add(technicianField);
        techCard.add(Box.createVerticalStrut(8));
        techCard.add(selectTechBtn);

        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton saveBtn = UIHelper.createStyledButton(mode == FormMode.EDIT ? "Update" : "Create", ButtonMode.PRIMARY);
        JButton backBtn = UIHelper.createStyledButton("Back", ButtonMode.PRIMARY);

        buttonPanel.add(backBtn);
        buttonPanel.add(saveBtn);

        saveBtn.addActionListener(_ -> handleSave());
        backBtn.addActionListener(_ -> onBack.run());

        // Add to panel
        mainPanel.add(customerCard);
        mainPanel.add(Box.createVerticalStrut(10));

        mainPanel.add(serviceCard);
        mainPanel.add(Box.createVerticalStrut(10));

        mainPanel.add(scheduleCard);
        mainPanel.add(Box.createVerticalStrut(10));

        mainPanel.add(techCard);
        mainPanel.add(Box.createVerticalStrut(10));

        mainPanel.add(buttonPanel);

        add(new JScrollPane(mainPanel), BorderLayout.CENTER);

        if (mode == FormMode.EDIT && appointment != null) {
            loadAppointmentData();
        }
    }

    private void loadAppointmentData() {
        // Customer
        selectedCustomer = appointment.getCustomer();
        if (selectedCustomer != null) {
            customerField.setText(
                    "User ID: " + selectedCustomer.getUserID() + "\n" +
                    "Customer ID: " + selectedCustomer.getRoleID() + "\n" +
                    "Username: " + selectedCustomer.getUsername() + "\n" +
                    "First Name: " + selectedCustomer.getFirstName() + "\n" +
                    "Last Name: " + selectedCustomer.getLastName() + "\n" +
                    "Email: " + selectedCustomer.getEmail() + "\n" +
                    "Contact: " + selectedCustomer.getContact()
            );
        }

        // Service
        selectedService = appointment.getService();
        if (selectedService != null) {
            serviceField.setText(
                    "Service ID: " + selectedService.getServiceID() + "\n" +
                    "Name: " + selectedService.getName() + "\n" +
                    "Type: " + selectedService.getType() + "\n" +
                    "Price: RM" + selectedService.getPrice() + "\n" +
                    "Duration: " + selectedService.getDuration()
            );
        }

        // Technician
        selectedTechnician = appointment.getTechnician();
        if (selectedTechnician != null) {
            technicianField.setText(
                    "User ID: " + selectedTechnician.getUserID() + "\n" +
                    "Technician ID: " + selectedTechnician.getRoleID() + "\n" +
                    "Username: " + selectedTechnician.getUsername() + "\n" +
                    "First Name: " + selectedTechnician.getFirstName() + "\n" +
                    "Last Name: " + selectedTechnician.getLastName() + "\n" +
                    "Email: " + selectedTechnician.getEmail() + "\n" +
                    "Contact: " + selectedTechnician.getContact()
            );
        }

        // Date and time
        if (dateTimePicker != null) {
            dateTimePicker.setDate(appointment.getDate());
            dateTimePicker.setTime(appointment.getStartTime());
        }
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

    private JButton createSelectTechnicianButton() {
        JButton selectTechnicianBtn = UIHelper.createStyledButton("Select Technician", ButtonMode.PRIMARY);

        selectTechnicianBtn.addActionListener(_ -> {
            if (availableTechnicians == null || availableTechnicians.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No available technicians for selected date/time/service.");
                return;
            }

            SelectionDialog<Technician> dialog = new SelectionDialog<>(
                    (JFrame) SwingUtilities.getWindowAncestor(this),
                    "Select Technician", availableTechnicians,
                    new String[]{"User ID", "Technician ID", "Username", "First Name", "Last Name"},
                    t -> new Object[]{t.getUserID(), t.getRoleID(), t.getUsername(), t.getFirstName(), t.getLastName()}
            );

            dialog.setVisible(true);

            Technician t = dialog.getSelectedItem();
            if (t != null) {
                selectedTechnician = t;
                technicianField.setText(TextFormatter.formatTechnician(t));
            }
        });

        return selectTechnicianBtn;
    }

    private JButton createSelectServiceButton() {
        JButton selectServiceBtn = UIHelper.createStyledButton("Select Service", ButtonMode.PRIMARY);

        selectServiceBtn.addActionListener(_ -> {
            SelectionDialog<Service> dialog = new SelectionDialog<>(
                    (JFrame) SwingUtilities.getWindowAncestor(this),
                    "Select Service", serviceService.getAllServices(),
                    new String[]{"ID", "Name", "Type", "Price", "Duration"},
                    s -> new Object[]{s.getServiceID(), s.getName(), s.getType(), s.getPrice(), s.getDuration()}
            );

            dialog.setVisible(true);

            Service s = dialog.getSelectedItem();
            if (s != null) {
                selectedService = s;
                serviceField.setText(TextFormatter.formatService(s));
                refreshTechnicians();
                updateEndTime();
            }
        });

        return selectServiceBtn;
    }


    private void handleSave() {
        try {
            if (selectedCustomer == null) {
                JOptionPane.showMessageDialog(this, "Please select a customer.");
                return;
            }

            if (selectedService == null) {
                JOptionPane.showMessageDialog(this, "Please select a service.");
                return;
            }

            if (selectedTechnician == null) {
                JOptionPane.showMessageDialog(this, "Please select a technician.");
                return;
            }

            LocalDate date = dateTimePicker.getDate();
            LocalTime time = dateTimePicker.getTime();

            if (date == null || time == null) {
                JOptionPane.showMessageDialog(this, "Please select a valid date and time.");
                return;
            }

            if (mode == FormMode.CREATE) {
                appointmentService.createAppointment(selectedService, selectedCustomer, currentUser, selectedTechnician, date, time);
            } else {
                appointmentService.updateAppointment(appointment.getAppointmentID(), date, time, selectedTechnician);
            }

            onBack.run();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void refreshTechnicians() {
        try {
            if (selectedService == null) return;

            LocalDate date = dateTimePicker.getDate();
            LocalTime time = dateTimePicker.getTime();

            if (date == null || time == null) return;

            availableTechnicians = appointmentService.getAvailableTechnicians(date, time, selectedService);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void attachAutoRefresh() {
        dateTimePicker.addPropertyChangeListener(_ -> refreshTechnicians());
    }

    private void updateEndTime() {
        if (selectedService == null || dateTimePicker.getTime() == null) {
            endTimeLabel.setText("End Time: -");
            return;
        }

        LocalTime endTime = dateTimePicker.getTime()
                .plusMinutes(selectedService.getDuration());

        endTimeLabel.setText("End Time: " + endTime);
    }
}