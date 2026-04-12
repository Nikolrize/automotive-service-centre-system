package Model;

import Interface.RolePermission;
import UI.DashboardUI;

public class Technician extends User implements RolePermission {
    public Technician(String userID, String username, String firstName, String lastName, String password, String email, String contact, boolean isActive, String roleID) {
        super(userID, username, firstName, lastName, password, email, contact, isActive, roleID);
    }

    @Override
    public void openDashboard() {
        new DashboardUI(this).showDashboard();
    }

    @Override
    public String getRoleName() {
        return "Technician";
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
        return false;
    }

    @Override
    public boolean canManageAppointment() {
        return false;
    }

    @Override
    public boolean canViewAssignedAppointments() {
        return true;
    }

    @Override
    public boolean canUpdateAssignedAppointment() {
        return true;
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
        return false;
    }

    @Override
    public boolean canCreateFeedback() {
        return true;
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
