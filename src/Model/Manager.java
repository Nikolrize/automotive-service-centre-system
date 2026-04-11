package Model;

import UI.ManagerUI;

public class Manager extends User {
    public Manager(String userID, String username, String firstName, String lastName, String password, String email, String contact, boolean isActive, String roleID) {
        super(userID, username, firstName, lastName, password, email, contact, isActive, roleID);
    }

    @Override
    public void openMenu() {
        new ManagerUI().showMenu();
    }
}
