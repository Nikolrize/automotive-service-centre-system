package Model;

public class Customer extends User {
    private String customerID;

    public Customer(String userID, String username, String password, String email, String contact, boolean isActive, String customerID) {
        super(userID, username, password, email, contact, isActive);
        setCustomerID(customerID);
    }

    public String getCustomerID() {
        return customerID;
    }

    public void setCustomerID(String customerID) {
        this.customerID = customerID;
    }
}
