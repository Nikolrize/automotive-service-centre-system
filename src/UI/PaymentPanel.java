package UI;

import Enum.ButtonMode;
import Model.*;
import Service.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.util.List;

public class PaymentPanel extends JPanel {
    private final PaymentService paymentService;
    private final AppointmentService appointmentService;
    private final User currentUser;

    private JTable table;
    private DefaultTableModel model;
    private final JTextField searchField = new JTextField();
    private TableRowSorter<DefaultTableModel> sorter;

    public PaymentPanel(
            PaymentService paymentService,
            AppointmentService appointmentService,
            User currentUser
    ) {
        this.paymentService = paymentService;
        this.appointmentService = appointmentService;
        this.currentUser = currentUser;

        setLayout(new BorderLayout());
        buildUI();
        loadData();
    }

    private void buildUI() {
        JPanel mainPanel = UIHelper.createSectionPanel(currentUser.canManagePayment() ? "Manage Payment" : "Payment History");

        // Table
        String[] columns = {
                "Payment ID",
                "Appointment ID",
                "Customer",
                "Service",
                "Amount",
                "Method",
                "Date",
                "Time"
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
        JPanel searchPanel = UIHelper.createSearchPanel(searchField, this::applyFilter);
        searchPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Scroll Pane
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        if (currentUser.canManagePayment()) {
            JButton createBtn = UIHelper.createStyledButton("Collect Payment", ButtonMode.CREATE);
            createBtn.setPreferredSize(new Dimension(150, 35));

            JButton receiptBtn = UIHelper.createStyledButton("View Receipt", ButtonMode.PRIMARY);
            receiptBtn.setPreferredSize(new Dimension(150, 35));

            createBtn.addActionListener(_ -> handleCreatePayment());
            receiptBtn.addActionListener(_ -> handleViewReceipt());

            buttonPanel.add(createBtn);
            buttonPanel.add(receiptBtn);
        }

        if (currentUser.canViewPaymentHistory()) {
            JButton receiptBtn = UIHelper.createStyledButton("View Receipt", ButtonMode.PRIMARY);
            receiptBtn.setPreferredSize(new Dimension(150, 35));
            receiptBtn.addActionListener(_ -> handleViewReceipt());

            buttonPanel.add(receiptBtn);
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
        List<Payment> payments;

        if (currentUser instanceof Customer customer) {
            payments = paymentService.getPaymentsByCustomer(customer);
        } else {
            payments = paymentService.getAllPayments();
        }

        for (Payment p : payments) {
            Appointment a = p.getAppointment();

            model.addRow(new Object[]{
                    p.getPaymentID(),
                    a.getAppointmentID(),
                    a.getCustomer().getUsername(),
                    a.getService().getName(),
                    String.format("RM %.2f", p.getAmount()),
                    p.getMethod(),
                    p.getPaymentDate(),
                    p.getPaymentTime()
            });
        }
    }

    private Payment getSelectedPayment() {
        int row = table.getSelectedRow();

        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a payment first.");
            return null;
        }

        String paymentID = table.getValueAt(row, 0).toString();

        return paymentService.getPaymentByID(paymentID);
    }

    private void handleCreatePayment() {
        List<Appointment> appointments = appointmentService.getAllAppointments()
                .stream()
                .filter(a -> !paymentService.isAppointmentPaid(a.getAppointmentID()))
                .toList();

        if (appointments.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No unpaid appointments found.");
            return;
        }

        SelectionDialog<Appointment> dialog = new SelectionDialog<>(
                (JFrame) SwingUtilities.getWindowAncestor(this),
                "Select Appointment for Payment",
                appointments,
                new String[]{
                        "Appointment ID",
                        "Customer",
                        "Vehicle",
                        "Service",
                        "Amount",
                        "Date",
                        "Status"
                },
                a -> new Object[]{
                        a.getAppointmentID(),
                        a.getCustomer().getUsername(),
                        a.getVehicle().getPlateNumber(),
                        a.getService().getName(),
                        String.format("RM %.2f", a.getService().getPrice()),
                        a.getDate(),
                        a.getIsCompleted() ? "Completed" : "Pending"
                }
        );

        dialog.setVisible(true);

        Appointment appointment = dialog.getSelectedItem();

        if (appointment == null) return;

        String[] methods = {"Cash", "Card", "E-Wallet"};

        String method = (String) JOptionPane.showInputDialog(
                this,
                "Select payment method:",
                "Payment Method",
                JOptionPane.PLAIN_MESSAGE,
                null,
                methods,
                methods[0]
        );

        if (method == null) return;

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Collect payment of RM " + String.format("%.2f", appointment.getService().getPrice()) + "?",
                "Confirm Payment",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm != JOptionPane.YES_OPTION) return;

        try {
            paymentService.createPayment(appointment, currentUser, method);
            JOptionPane.showMessageDialog(this, "Payment collected successfully.");
            loadData();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleViewReceipt() {
        Payment payment = getSelectedPayment();

        if (payment == null) return;

        String receipt = paymentService.generateReceipt(payment);

        JTextArea receiptArea = new JTextArea(receipt);
        receiptArea.setEditable(false);
        receiptArea.setFont(new Font("Monospaced", Font.PLAIN, 13));

        JScrollPane scrollPane = new JScrollPane(receiptArea);
        scrollPane.setPreferredSize(new Dimension(500, 400));

        JOptionPane.showMessageDialog(
                this,
                scrollPane,
                "Receipt",
                JOptionPane.INFORMATION_MESSAGE
        );
    }
}