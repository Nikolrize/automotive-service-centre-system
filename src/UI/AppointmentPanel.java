package UI;

import Model.*;
import Service.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.util.List;

import Enum.*;

public class AppointmentPanel extends JPanel {
    private final AppointmentService appointmentService;
    private final UserService userService;
    private final ServiceManagementService serviceManagementService;
    private final VehicleService vehicleService;
    private final User currentUser;
    private JTable table;
    private DefaultTableModel model;
    private final JPanel rightPanel;
    private final CardLayout cardLayout;
    private final JTextField searchField = new JTextField();
    private TableRowSorter<DefaultTableModel> sorter;

    public AppointmentPanel(AppointmentService appointmentService, UserService userService, ServiceManagementService serviceManagementService, VehicleService vehicleService, User currentUser, JPanel rightPanel, CardLayout cardLayout) {
        this.appointmentService = appointmentService;
        this.serviceManagementService = serviceManagementService;
        this.userService = userService;
        this.vehicleService = vehicleService;
        this.currentUser = currentUser;
        this.rightPanel = rightPanel;
        this.cardLayout = cardLayout;

        setLayout(new BorderLayout());
        AppointmentUI();
        loadData();
    }

    private void AppointmentUI() {
        JPanel mainPanel = UIHelper.createSectionPanel("Manage Appointment");

        String[] columns = {"ID", "Service", "Customer", "Technician", "Date", "Start", "End", "Status"};

        model = new DefaultTableModel(columns, 0) {
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

        // ScrollPane
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Button Panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        if (currentUser instanceof CounterStaff) {
            JButton createBtn = UIHelper.createStyledButton("Create", ButtonMode.CREATE);
            createBtn.addActionListener(_ -> handleCreate());

            JButton editBtn = UIHelper.createStyledButton("Edit", ButtonMode.PRIMARY);
            editBtn.addActionListener(_ -> handleEdit());

            JButton deleteBtn = UIHelper.createStyledButton("Delete", ButtonMode.DESTRUCTIVE);
            deleteBtn.addActionListener(_ -> handleDelete());

            // Add to Panels
            buttonPanel.add(createBtn);
            buttonPanel.add(editBtn);
            buttonPanel.add(deleteBtn);
        }

        if (currentUser instanceof Technician) {
            JButton completeBtn = UIHelper.createStyledButton("Mark Completed", ButtonMode.RESTORE);
            completeBtn.setPreferredSize(new Dimension(150, 35));
            completeBtn.addActionListener(_ -> markCompleted());

            buttonPanel.add(completeBtn);
        }

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

        List<Appointment> list =
                currentUser instanceof Customer c ? appointmentService.getAppointmentsByCustomer(c) :
                currentUser instanceof Technician t ? appointmentService.getAppointmentsByTechnician(t) :
                appointmentService.getAllAppointments();

        for (Appointment a : list) {
            model.addRow(new Object[]{a.getAppointmentID(), a.getService().getName(), a.getCustomer().getUsername(), a.getTechnician() == null ? "-" : a.getTechnician().getUsername(), a.getDate(), a.getStartTime(), a.getEndTime(), a.getIsCompleted() ? "Completed" : "Pending"});
        }
    }

    private void openForm(FormMode mode, Appointment appt) {
        AppointmentFormPanel panel = new AppointmentFormPanel(
                mode,
                appt,
                appointmentService,
                userService,
                vehicleService,
                serviceManagementService,
                (CounterStaff) currentUser,
                () -> {
                    cardLayout.show(rightPanel, "Manage Appointment");
                    loadData();
                }
        );

        rightPanel.add(panel, "AppointmentForm");
        cardLayout.show(rightPanel, "AppointmentForm");
    }

    private Appointment getSelectedAppointment() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select appointment first!");
            return null;
        }

        String id = table.getValueAt(row, 0).toString();

        return appointmentService.getAllAppointments()
                .stream()
                .filter(a -> a.getAppointmentID().equals(id))
                .findFirst()
                .orElse(null);
    }

    private void handleCreate() {
        openForm(FormMode.CREATE, null);
    }

    private void handleEdit() {
        Appointment appt = getSelectedAppointment();
        if (appt != null) {
            openForm(FormMode.EDIT, appt);
        }
    }

    private void handleDelete() {
        Appointment appt = getSelectedAppointment();
        if (appt == null) return;

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Delete this appointment?",
                "Confirm",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                appointmentService.deleteAppointment(appt.getAppointmentID());
                loadData();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void markCompleted() {
        Appointment appt = getSelectedAppointment();
        if (appt == null) return;

        if (appt.getIsCompleted()) {
            JOptionPane.showMessageDialog(
                    this,
                    "This appointment is already completed.",
                    "Invalid Action",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Mark this appointment as completed?",
                "Confirm Completion",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            appointmentService.markCompleted(
                    appt.getAppointmentID(),
                    (Technician) currentUser
            );
            loadData();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}