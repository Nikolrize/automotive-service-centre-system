package UI;

import Model.CounterStaff;
import Model.Customer;
import Model.Technician;
import Service.*;
import Model.User;
import Enum.*;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class DashboardUI extends JFrame {
    private final User currentUser;
    private final UserService userService;
    private final AppointmentService appointmentService;
    private final ServiceManagementService serviceManagementService;
    private final VehicleService vehicleService;
    private final PaymentService paymentService;
    private final CommentService commentService;
    private final FeedbackService feedbackService;
    private JPanel leftPanel;
    private JPanel rightPanel;
    private CardLayout cardLayout;
    private final Map<String, JButton> menuButtons = new HashMap<>();

    public DashboardUI(User user) {
        this.currentUser = user;
        this.userService = new UserService();
        this.serviceManagementService = new ServiceManagementService();
        this.vehicleService = new VehicleService(userService);
        this.appointmentService = new AppointmentService(userService, serviceManagementService, vehicleService);
        this.paymentService = new PaymentService(appointmentService, userService);
        this.commentService = new CommentService(appointmentService);
        this.feedbackService = new FeedbackService(userService, appointmentService);
    }

    public void showDashboard() {
        setTitle("APU Automotive Service Centre (APU – ASC)");
        setSize(1280, 720);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        ImageIcon appIcon = new ImageIcon("src/Resource/appIcon.png");
        setIconImage(appIcon.getImage());

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);

        createLeftPanel();
        createRightPanel();

        mainPanel.add(leftPanel, BorderLayout.WEST);
        mainPanel.add(rightPanel, BorderLayout.CENTER);

        add(mainPanel);
        setVisible(true);
    }

    private void createLeftPanel() {
        leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setBorder(BorderFactory.createEmptyBorder(25, 20, 25, 20));
        leftPanel.setBackground(new Color(135, 90, 224));

        JLabel titleLabel = new JLabel(currentUser.getRoleName());
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);

        JSeparator separator = new JSeparator(SwingConstants.HORIZONTAL);
        separator.setForeground(Color.LIGHT_GRAY);

        leftPanel.add(titleLabel);
        leftPanel.add(Box.createVerticalStrut(10));
        leftPanel.add(separator);
        leftPanel.add(Box.createVerticalStrut(10));

        assignMenuButton();

        JButton logoutBtn = new JButton("Log Out");
        logoutBtn.setBackground(new Color(135, 90, 224));
        logoutBtn.setForeground(Color.WHITE);
        logoutBtn.setFocusPainted(false);
        logoutBtn.setBorderPainted(false);
        logoutBtn.setFont(new Font("SansSerif", Font.BOLD, 14));
        logoutBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        logoutBtn.addActionListener(_ -> handleLogout());

        UIHelper.addHoverEffect(logoutBtn, new Color(135, 90, 224), new Color(159, 126, 224));
        leftPanel.add(logoutBtn);
    }

    private void createRightPanel() {
        rightPanel = new JPanel();
        cardLayout = new CardLayout();
        rightPanel.setLayout(cardLayout);
        rightPanel.setBackground(Color.WHITE);

        for (Map.Entry<String, JPanel> entry : getPanels().entrySet()) {
            rightPanel.add(entry.getValue(), entry.getKey());
            JButton btn = menuButtons.get(entry.getKey());
            if (btn != null) {
                btn.addActionListener(_ -> {
                    JPanel panel = entry.getValue();

                    if (panel instanceof ManageUserPanel managePanel) {
                        managePanel.loadData();
                    }

                    if (panel instanceof RestoreUserPanel restoreUserPanel) {
                        restoreUserPanel.loadData();
                    }

                    if (panel instanceof ManageServicePanel manageServicePanel) {
                        manageServicePanel.loadData();
                    }

                    if (panel instanceof ViewFeedbackPanel viewFeedbackPanel) {
                        viewFeedbackPanel.loadData();
                    }

                    if (panel instanceof ReportPanel reportPanel) {
                        reportPanel.loadData();
                    }

                    cardLayout.show(rightPanel, entry.getKey());
                });
            }
        }
    }

    private Map<String, JPanel> getPanels() {
        Map<String, JPanel> panels = new LinkedHashMap<>();

        // Profile
        if (currentUser.canEditProfile()) {
            panels.put("My Profile", new UserFormPanel(FormMode.EDIT, currentUser, userService, null, null));

        }

        // User Management
        if (currentUser.canManageManager()) {
            panels.put("Manage Manager", new ManageUserPanel(userService, UserRole.MANAGER, currentUser, rightPanel, cardLayout));

        }
        if (currentUser.canManageCounterStaff()) {
            panels.put("Manage Counter Staff", new ManageUserPanel(userService, UserRole.COUNTER_STAFF, currentUser, rightPanel, cardLayout));

        }
        if (currentUser.canManageTechnician()) {
            panels.put("Manage Technician", new ManageUserPanel(userService, UserRole.TECHNICIAN, currentUser, rightPanel, cardLayout));

        }
        if (currentUser.canManageManager()) {
            panels.put("Restore Users", new RestoreUserPanel(userService));

        }
        if (currentUser.canManageCustomer()) {
            panels.put("Manage Customer", new ManageUserPanel(userService, UserRole.CUSTOMER, currentUser, rightPanel, cardLayout));

        }

        // Vehicle
        if (currentUser.canManageVehicle()) {
            panels.put("Manage Vehicle", new ManageVehiclePanel(vehicleService, userService, currentUser, rightPanel, cardLayout));
        }

        if (currentUser.canViewVehicle()) {
            panels.put("View Vehicle", new ManageVehiclePanel(vehicleService, userService, currentUser, rightPanel, cardLayout));
        }

        // Service
        if (currentUser.canSetServicePrice()) {
            panels.put("Manage Service", new ManageServicePanel());
        }

        // Feedback (checks if its customer, if not it will go to manager feedback, later need to add/change when adding technician feedback)
        if (currentUser.canViewFeedback()) {
            if (currentUser instanceof Customer customer) {
                panels.put("View Feedback", new CustomerFeedbackPanel(customer, appointmentService, feedbackService));
            } else if (currentUser instanceof Technician technician) {
                panels.put("View Feedback", new TechnicianFeedbackPanel(technician, feedbackService));
            } else {
                panels.put("View Feedback", new ViewFeedbackPanel(feedbackService, commentService));
            }
        }

        // Report
        if (currentUser.canViewReports()) {
            panels.put("View Report", new ReportPanel());
        }

        // Appointment
        if (currentUser.canManageAppointment()) {
            panels.put("Manage Appointment", new AppointmentPanel(appointmentService, userService, serviceManagementService, vehicleService, feedbackService, commentService,currentUser, rightPanel, cardLayout));
        }

        if (currentUser.canUpdateAssignedAppointment()) {
            panels.put("Update Appointment", new AppointmentPanel(appointmentService, userService, serviceManagementService, vehicleService, feedbackService, commentService, currentUser, rightPanel, cardLayout));
        }

        if (currentUser.canViewAssignedAppointments()) {
            panels.put("View Appointment", new AppointmentPanel(appointmentService, userService, serviceManagementService, vehicleService, feedbackService, commentService, currentUser, rightPanel, cardLayout));
        }

        // Payment
        if (currentUser.canManagePayment()) {
            panels.put("Manage Payment", new PaymentPanel(paymentService, appointmentService, currentUser));
        }

        if (currentUser.canViewPaymentHistory()) {
            panels.put("Payment History", new PaymentPanel(paymentService, appointmentService, currentUser));
        }

        //comment
        if (currentUser.canCreateComment() && currentUser instanceof Model.Customer customer) {
            panels.put("Provide Comment", new CustomerCommentPanel(customer, appointmentService, commentService));
        }

        if (currentUser.canViewComment() && currentUser instanceof Model.Technician technician) {
            panels.put("View Comment", new ViewCommentPanel(technician, commentService));
        }
        return panels;
    }

    private void assignMenuButton() {
        // Profile
        if (currentUser.canEditProfile()) {
            addMenuButton("My Profile");
        }

        // User Management
        if (currentUser.canManageManager()) {
            addMenuButton("Manage Manager");
        }
        if (currentUser.canManageCounterStaff()) {
            addMenuButton("Manage Counter Staff");
        }
        if (currentUser.canManageTechnician()) {
            addMenuButton("Manage Technician");
        }
        if (currentUser.canManageManager()) {
            addMenuButton("Restore Users");
        }
        if (currentUser.canManageCustomer()) {
            addMenuButton("Manage Customer");
        }

        // Vehicle
        if (currentUser.canManageVehicle()) {
            addMenuButton("Manage Vehicle");
        }

        if (currentUser.canViewVehicle()) {
            addMenuButton("View Vehicle");
        }

        // Service
        if (currentUser.canSetServicePrice()) {
            addMenuButton("Manage Service");
        }

        // Appointment
        if (currentUser.canManageAppointment()) {
            addMenuButton("Manage Appointment");
        }

        if (currentUser.canUpdateAssignedAppointment()) {
            addMenuButton("Update Appointment");
        }

        if (currentUser.canViewAssignedAppointments()) {
            addMenuButton("View Appointment");
        }

        // Payment
        if (currentUser.canManagePayment()) {
            addMenuButton("Manage Payment");
        }
        if (currentUser.canViewPaymentHistory()) {
            addMenuButton("Payment History");
        }

        // Feedback
        if (currentUser.canCreateFeedback()) {
            addMenuButton("Create Feedback");
        }

        if (currentUser.canViewFeedback()) {
            addMenuButton("View Feedback");
        }

        // Report
        if (currentUser.canViewReports()) {
            addMenuButton("View Report");
        }

        //comment
        if (currentUser.canCreateComment()) {
            addMenuButton("Provide Comment");
        }

        if (currentUser.canViewComment()) {
            addMenuButton("View Comment");
        }
    }

    private void addMenuButton(String buttonName) {
        JButton btn = new JButton(buttonName);
        btn.setBackground(new Color(135, 90, 224));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setFont(new Font("SansSerif", Font.BOLD, 14));
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));

        menuButtons.put(buttonName, btn);
        UIHelper.addHoverEffect(btn, new Color(135, 90, 224), new Color(159, 126, 224));

        leftPanel.add(btn);
    }

    private void handleLogout() {
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to log out?", "Logout", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            this.dispose();
            new LoginUI().setVisible(true);
        }
    }
}