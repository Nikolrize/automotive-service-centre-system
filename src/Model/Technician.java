package Model;

import UI.TechnicianUI;

public class Technician extends User {
    private String technicianID;

    public Technician(String userID, String username, String firstName, String lastName, String password, String email, String contact, boolean isActive, String technicianID) {
        super(userID, username, firstName, lastName, password, email, contact, isActive);
        setTechnicianID(technicianID);
    }

    public String getTechnicianID() {
        return technicianID;
    }

    public void setTechnicianID(String technicianID) {
        this.technicianID = technicianID;
    }

    @Override
    public void openMenu() {
        new TechnicianUI().showMenu();
    }
}
