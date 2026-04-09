package UI;

import Model.User;

import javax.swing.*;
import java.awt.*;
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
        return new String[] {"Edit Profile", "Manage Customer", "Appointments", "Payments"};
    }

    @Override
    protected Map<String, JPanel> getPanels() {
        Map<String, JPanel> panels = new LinkedHashMap<>();
        panels.put("Edit Profile", UIHelper.editProfilePanel(currentUser, userService));
        panels.put("Manage Customer", manageCustomerPanel());
        panels.put("Appointments", appointmentsPanel());
        panels.put("Payments", paymentsPanel());
        return panels;
    }

    private JPanel manageCustomerPanel() {
        JPanel mainPanel = UIHelper.createSectionPanel("Manage Customer");

        return mainPanel;
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
