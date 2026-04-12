package Model;

import Interface.RolePermission;
import UI.DashboardUI;

public class CounterStaff extends User implements RolePermission {
    public CounterStaff(String userID, String username, String firstName, String lastName, String password, String email, String contact, boolean isActive, String roleID) {
        super(userID, username, firstName, lastName, password, email, contact, isActive, roleID);
    }

    @Override
    public void openDashboard() {
        new DashboardUI(this).showDashboard();
    }

    @Override
    public String getRoleName() {
        return "Counter Staff";
    }

    @Override
    public RolePermission getPermission() {
        return this;
    }

    @Override
    public boolean canEditProfile() {
        return true;
    }

    @Override
    public boolean canManageManager() {
        return false;
    }

    @Override
    public boolean canManageCounterStaff() {
        return false;
    }

    @Override
    public boolean canManageTechnician() {
        return false;
    }

    @Override
    public boolean canManageCustomer() {
        return true;
    }

    @Override
    public boolean canManageAppointment() {
        return true;
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
        return true;
    }

    @Override
    public boolean canViewPaymentHistory() {
        return false;
    }

    @Override
    public boolean canViewFeedback() {
        return false;
    }

    @Override
    public boolean canCreateFeedback() {
        return false;
    }

    @Override
    public boolean canViewReports() {
        return false;
    }

    @Override
    public boolean canSetServicePrice() {
        return false;
    }
}
