package Model;

import UI.CounterStaffUI;

public class CounterStaff extends User {
    private String counterStaffID;

    public CounterStaff(String userID, String username, String firstName, String lastName, String password, String email, String contact, boolean isActive, String counterStaffID) {
        super(userID, username, firstName, lastName, password, email, contact, isActive);
        setCounterStaffID(counterStaffID);
    }

    public String getCounterStaffID() {
        return counterStaffID;
    }

    public void setCounterStaffID(String counterStaffID) {
        this.counterStaffID = counterStaffID;
    }

    @Override
    public void openMenu() {
        new CounterStaffUI(this).showMenu();
    }
}
