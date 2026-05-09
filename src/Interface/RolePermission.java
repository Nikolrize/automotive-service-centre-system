package Interface;

public interface RolePermission {
    // PROFILE
    boolean canEditProfile();

    // USER MANAGEMENT
    boolean canManageManager();
    boolean canManageCounterStaff();
    boolean canManageTechnician();
    boolean canManageCustomer();

    // APPOINTMENTS
    boolean canManageAppointment();
    boolean canViewAssignedAppointments();
    boolean canUpdateAssignedAppointment();

    // PAYMENTS
    boolean canManagePayment();
    boolean canViewPaymentHistory();

    // FEEDBACK
    boolean canCreateFeedback();
    boolean canViewFeedback();

    // COMMENT
    boolean canCreateComment();
    boolean canviewComment();

    // REPORT
    boolean canViewReports();

    // SERVICE
    boolean canSetServicePrice();

    // VEHICLE
    boolean canManageVehicle();
    boolean canViewVehicle();
}