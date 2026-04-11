package UI;

import Model.User;

import Enum.*;
import javax.swing.*;
import java.util.LinkedHashMap;
import java.util.Map;

public class CounterStaffUI extends DashboardUI {
    public CounterStaffUI(User user) {
        super(user);
    }

    @Override
    protected String getLeftPanelTitle() {
        return "Counter Staff Menu";
    }

    @Override
    protected String[] getMenuItems() {
        return new String[]{"My Profile", "Manage Customer", "Appointments", "Payments"};
    }

    @Override
    protected Map<String, JPanel> getPanels() {
        Map<String, JPanel> panels = new LinkedHashMap<>();
        panels.put("My Profile", UIHelper.userFormPanel(FormMode.EDIT, currentUser, userService, UserRole.COUNTER_STAFF));
        panels.put("Manage Customer", new ManageUserPanel(userService, UserRole.CUSTOMER, rightPanel, cardLayout));
        panels.put("Appointments", appointmentsPanel());
        panels.put("Payments", paymentsPanel());
        return panels;
    }

    private JPanel appointmentsPanel() {
        JPanel mainPanel = UIHelper.createSectionPanel("Appointments");

        return mainPanel;
    }

    private JPanel paymentsPanel() {
        JPanel mainPanel = UIHelper.createSectionPanel("Payments");

        return mainPanel;
    }
}
