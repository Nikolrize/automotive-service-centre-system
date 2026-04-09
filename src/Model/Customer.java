package Model;

import UI.CustomerUI;

public class Customer extends User {
    private String customerID;

    public Customer(String userID, String username, String firstName, String lastName, String password, String email, String contact, boolean isActive, String customerID) {
        super(userID, username, firstName, lastName, password, email, contact, isActive);
        setCustomerID(customerID);
    }

    public String getCustomerID() {
        return customerID;
    }

    public void setCustomerID(String customerID) {
        this.customerID = customerID;
    }

    @Override
    public void openMenu() {
        new CustomerUI().showMenu();
    }
}
