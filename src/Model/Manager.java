package Model;

import UI.ManagerUI;

public class Manager extends User {
    private String managerID;

    public Manager(String userID, String username, String firstName, String lastName, String password, String email, String contact, boolean isActive, String managerID) {
        super(userID, username, firstName, lastName, password, email, contact, isActive);
        setManagerID(managerID);
    }

    public String getManagerID() {
        return managerID;
    }

    public void setManagerID(String managerID) {
        this.managerID = managerID;
    }

    @Override
    public void openMenu() {
        new ManagerUI().showMenu();
    }
}
