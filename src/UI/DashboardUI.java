package UI;

import Interface.RolePermission;
import Service.UserService;
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
    private JPanel leftPanel;
    private JPanel rightPanel;
    private CardLayout cardLayout;
    private final Map<String, JButton> menuButtons = new HashMap<>();

    public DashboardUI(User user) {
        this.currentUser = user;
        this.userService = new UserService();
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

                    if (panel instanceof ManageServicePanel manageServicePanel) {
                        manageServicePanel.loadData();
                    }

                    if (panel instanceof  ViewFeedbackPanel viewFeedbackPanel) {
                        viewFeedbackPanel.loadData();
                    }

                    cardLayout.show(rightPanel, entry.getKey());
                });
            }
        }
    }

    private Map<String, JPanel> getPanels() {
        Map<String, JPanel> panels = new LinkedHashMap<>();
        RolePermission perm = currentUser.getPermission();

        // Profile
        if (perm.canEditProfile()) {
            panels.put("My Profile", new UserFormPanel(FormMode.EDIT, currentUser, userService, null, null));

        }

        // User Management
        if (perm.canManageManager()) {
            panels.put("Manage Manager", new ManageUserPanel(userService, UserRole.MANAGER, rightPanel, cardLayout));

        }
        if (perm.canManageCounterStaff()) {
            panels.put("Manage Counter Staff", new ManageUserPanel(userService, UserRole.COUNTER_STAFF, rightPanel, cardLayout));

        }
        if (perm.canManageTechnician()) {
            panels.put("Manage Technician", new ManageUserPanel(userService, UserRole.TECHNICIAN, rightPanel, cardLayout));

        }
        if (perm.canManageCustomer()) {
            panels.put("Manage Customer", new ManageUserPanel(userService, UserRole.CUSTOMER, rightPanel, cardLayout));

        }

        //Manage Service
        if (perm.canSetServicePrice()) {
            panels.put("Manage Service", new ManageServicePanel());
        }

        // ViewFeedback
        if (perm.canViewFeedback()) {
            panels.put("Feedback", new ViewFeedbackPanel());
        }

        return panels;
    }

    private void assignMenuButton() {
        RolePermission perm = currentUser.getPermission();

        // Profile
        if (perm.canEditProfile()) {
            addMenuButton("My Profile");
        }

        // User Management
        if (perm.canManageManager()) {
            addMenuButton("Manage Manager");
        }
        if (perm.canManageCounterStaff()) {
            addMenuButton("Manage Counter Staff");
        }
        if (perm.canManageTechnician()) {
            addMenuButton("Manage Technician");
        }
        if (perm.canManageCustomer()) {
            addMenuButton("Manage Customer");
        }

        // Service
        if (perm.canSetServicePrice()) {
            addMenuButton("Manage Service");
        }

        // Appointment
        if (perm.canManageAppointment() || perm.canUpdateAssignedAppointment() || perm.canViewAssignedAppointments()) {
            addMenuButton("Appointments");
        }

        // Payment
        if (perm.canManagePayment()) {
            addMenuButton("Payments");
        }
        if (perm.canViewPaymentHistory()) {
            addMenuButton("Payment History");
        }

        // Feedback
        if (perm.canCreateFeedback() || perm.canViewFeedback()) {
            addMenuButton("Feedback");
        }

        // Report
        if (perm.canViewReports()) {
            addMenuButton("View Report");
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