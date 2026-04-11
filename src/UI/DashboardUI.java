package UI;

import Service.UserService;
import Model.User;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public abstract class DashboardUI extends JFrame {
    protected final User currentUser;
    protected final UserService userService;
    protected JPanel leftPanel;
    protected JPanel rightPanel;
    protected CardLayout cardLayout;
    protected Map<String, JButton> menuButtons = new HashMap<>();

    public DashboardUI(User user) {
        this.currentUser = user;
        this.userService = new UserService();
    }

    public void showMenu() {
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

        JLabel titleLabel = new JLabel(getLeftPanelTitle(), SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);

        JSeparator separator = new JSeparator(SwingConstants.HORIZONTAL);
        separator.setForeground(Color.LIGHT_GRAY);

        leftPanel.add(titleLabel);
        leftPanel.add(Box.createVerticalStrut(10));
        leftPanel.add(separator);
        leftPanel.add(Box.createVerticalStrut(10));

        // Add menu buttons from GetMenuItems()
        for (String menuName : getMenuItems()) {
            JButton btn = new JButton(menuName);
            btn.setBackground(new Color(135, 90, 224));
            btn.setForeground(Color.WHITE);
            btn.setFocusPainted(false);
            btn.setBorderPainted(false);
            btn.setFont(new Font("SansSerif", Font.BOLD, 14));
            btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));

            menuButtons.put(menuName, btn);
            UIHelper.addHoverEffect(btn, new Color(135, 90, 224), new Color(159, 126, 224));

            leftPanel.add(btn);
        }

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

                    cardLayout.show(rightPanel, entry.getKey());
                });
            }
        }
    }

    protected void handleLogout() {
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to log out?", "Logout", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            this.dispose();
            new LoginUI().setVisible(true);
        }
    }

    protected abstract String getLeftPanelTitle();

    protected abstract String[] getMenuItems();

    protected abstract Map<String, JPanel> getPanels();
}