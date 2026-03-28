package Model;

public abstract class User {
    protected String userID;
    protected String username;
    protected String password;
    protected String email;
    protected String contact;
    protected boolean isActive;

    public User(String userID, String username, String password, String email, String contact, boolean isActive) {
        setUserID(userID);
        setUsername(username);
        setPassword(password);
        setEmail(email);
        setContact(contact);
        setIsActive(isActive);
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

    public abstract void openMenu();
}
