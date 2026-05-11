package Model;

import UI.DashboardUI;

public class Technician extends User {
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
        return false;
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
        return true;
    }

    @Override
    public boolean canCreateComment() {
        return false;
    }

    @Override
    public boolean canviewComment() {
        return true;
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

    @Override
    public boolean canManageVehicle() {
        return false;
    }

    @Override
    public boolean canViewVehicle() {
        return false;
    }
}
