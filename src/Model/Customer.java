package Model;

import UI.CustomerUI;

public class Customer extends User {
    public Customer(String userID, String username, String firstName, String lastName, String password, String email, String contact, boolean isActive, String roleID) {
        super(userID, username, firstName, lastName, password, email, contact, isActive, roleID);
    }

    @Override
    public void openMenu() {
        new CustomerUI().showMenu();
    }
}
