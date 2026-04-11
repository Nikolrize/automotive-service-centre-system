package Model;

import UI.CounterStaffUI;

public class CounterStaff extends User {
    public CounterStaff(String userID, String username, String firstName, String lastName, String password, String email, String contact, boolean isActive, String roleID) {
        super(userID, username, firstName, lastName, password, email, contact, isActive, roleID);
    }

    @Override
    public void openMenu() {
        new CounterStaffUI(this).showMenu();
    }
}
