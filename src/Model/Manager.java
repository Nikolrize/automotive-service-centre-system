package Model;

import UI.DashboardUI;

public class Manager extends User {
    public Manager(String userID, String username, String firstName, String lastName, String password, String email, String contact, boolean isActive, String roleID) {
        super(userID, username, firstName, lastName, password, email, contact, isActive, roleID);
    }

    @Override
    public void openDashboard() {
        new DashboardUI(this).showDashboard();
    }

    @Override
    public String getRoleName() {
        return "Manager";
    }

    @Override
    public boolean canEditProfile() {
        return true;
    }

    @Override
    public boolean canManageManager() {
        return true;
    }

    @Override
    public boolean canManageCounterStaff() {
        return true;
    }

    @Override
    public boolean canManageTechnician() {
        return true;
    }

    @Override
    public boolean canManageCustomer() {
        return false;
    }

    @Override
    public boolean canManageAppointment() {
        return false;
    }

    @Override
    public boolean canViewAssignedAppointments() {
        return false;
    }

    @Override
    public boolean canUpdateAssignedAppointment() {
        return false;
    }

    @Override
    public boolean canManagePayment() {
        return false;
    }

    @Override
    public boolean canViewPaymentHistory() {
        return false;
    }

    @Override
    public boolean canViewFeedback() {
        return true;
    }

    @Override
    public boolean canCreateFeedback() {
        return false;
    }

    @Override
    public boolean canViewReports() {
        return true;
    }

    @Override
    public boolean canSetServicePrice() {
        return true;
    }
}
