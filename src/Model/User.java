package Model;

import Interface.RolePermission;

public abstract class User {
    private String userID;
    private String username;
    private String firstName;
    private String lastName;
    private String password;
    private String email;
    private String contact;
    private boolean isActive;
    private String roleID;

    public User(String userID, String username, String firstName, String lastName, String password, String email, String contact, boolean isActive, String roleID) {
        setUserID(userID);
        setUsername(username);
        setFirstName(firstName);
        setLastName(lastName);
        setPassword(password);
        setEmail(email);
        setContact(contact);
        setIsActive(isActive);
        setRoleID(roleID);
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(boolean isActive) {
        this.isActive = isActive;
    }

    public String getRoleID() {
        return roleID;
    }

    public void setRoleID(String roleID) {
        this.roleID = roleID;
    }

    public abstract void openDashboard();

    public abstract String getRoleName();

    public abstract RolePermission getPermission();
}
