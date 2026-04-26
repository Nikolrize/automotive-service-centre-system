package Service;

import Enum.UserRole;
import Model.*;
import Util.FileHandler;

import java.util.ArrayList;
import java.util.List;

public class UserService {
    private static final String FILE_NAME = "users.txt";

    // AUTHENTICATION
    public User loginUser(String username, String password) {
        return getAllUsers()
                .stream()
                .filter(u -> u.getUsername().equals(username) && u.getPassword().equals(password))
                .findFirst()
                .orElse(null);
    }

    public void registerUser(String username, String firstName, String lastName, String password, String email, String contact, UserRole role) throws Exception {
        List<String> users = FileHandler.readFile(FILE_NAME);

        for (String line : users) {
            String[] data = line.split(",");
            if (data.length < 9) continue;

            String fileUsername = data[1];

            if (fileUsername.equals(username)) {
                throw new Exception("Username " + username + " already exists!");
            } else {
                String userID = generateNewID(UserRole.USER);
                String roleID = generateNewID(role);
                String newData = String.join(",", userID, username, firstName, lastName, password, email, contact, "true", roleID);
                FileHandler.appendToFile(FILE_NAME, newData);
                break;
            }
        }
    }

    public void generateDefaultManager() {
        List<String> users = FileHandler.readFile(FILE_NAME);
        boolean hasDefaultManager = false;

        for (String line : users) {
            String[] data = line.split(",");
            if (data.length >= 9) {
                String roleID = data[8];
                if ("M0001".equals(roleID)) {
                    hasDefaultManager = true;
                    break;
                }
            }
        }

        if (!hasDefaultManager) {
            String username = "superuser";
            String firstName = "Super";
            String lastName = "User";
            String password = "superuser123";
            String email = "superuser@gmail.com";
            String contact = "0123456789";
            String userID = generateNewID(UserRole.USER);
            String data = String.join(",", userID, username, firstName, lastName, password, email, contact, "true", "M0001");
            FileHandler.appendToFile(FILE_NAME, data);
        }
    }

    private String generateNewID(UserRole role) {
        List<String> users = FileHandler.readFile(FILE_NAME);
        int maxUserID = 0;
        int maxRoleID = 0;

        for (String line : users) {
            String[] data = line.split(",");
            if (data.length < 9) continue;

            // General user ID: U0001
            int userNum = Integer.parseInt(data[0].substring(1));
            if (userNum > maxUserID) maxUserID = userNum;

            // Role-specific ID: M0001, C0002, etc
            String roleID = data[8];
            if (roleID.charAt(0) == role.getPrefix()) {
                int roleNum = Integer.parseInt(roleID.substring(1));
                if (roleNum > maxRoleID) maxRoleID = roleNum;
            }
        }

        if (role == UserRole.USER) return String.format("U%04d", maxUserID + 1);

        return String.format("%c%04d", role.getPrefix(), maxRoleID + 1);
    }

    // UPDATE USER
    public void updateUserProfile(String userID, String newUsername, String newFirstName, String newLastName, String newPassword, String newEmail, String newContact) throws Exception {
        List<String> users = FileHandler.readFile(FILE_NAME);
        boolean updated = false;

        // Check Username
        for (String line : users) {
            String[] data = line.split(",");
            if (data.length < 9) continue;

            String fileUserID = data[0];
            String fileUsername = data[1];

            if (!fileUserID.equals(userID) && fileUsername.equalsIgnoreCase(newUsername)) {
                throw new Exception("Username " + newUsername + " already exists!");
            }
        }

        // Update User
        for (int i = 0; i < users.size(); i++) {
            String[] data = users.get(i).split(",");
            if (data.length < 9) continue;

            if (data[0].equals(userID)) {
                data[1] = newUsername;
                data[2] = newFirstName;
                data[3] = newLastName;
                data[4] = newPassword;
                data[5] = newEmail;
                data[6] = newContact;

                users.set(i, String.join(",", data));
                updated = true;
                break;
            }
        }

        if (!updated) {
            throw new Exception("User not found!");
        }

        FileHandler.writeFile(FILE_NAME, users);
    }

    // DELETE USER
    public void hardDeleteUser(String userID) throws Exception {
        List<String> users = FileHandler.readFile(FILE_NAME);

        boolean removed = users.removeIf(line -> {
            String[] data = line.split(",");
            if (data.length < 9) return false;

            return data[0].trim().equals(userID.trim());
        });

        if (!removed) {
            throw new Exception("User not found!");
        }

        FileHandler.writeFile(FILE_NAME, users);
    }

    public void softDeleteUser(String userID) throws Exception {
        updateUserStatus(userID, false);
    }

    public void restoreUser(String userID) throws Exception {
        updateUserStatus(userID, true);
    }

    private void updateUserStatus(String userID, boolean status) throws Exception {
        List<String> users = FileHandler.readFile(FILE_NAME);

        boolean found = false;

        for (int i = 0; i < users.size(); i++) {
            String[] data = users.get(i).split(",");

            if (data[0].trim().equals(userID.trim())) {
                data[7] = String.valueOf(status);
                users.set(i, String.join(",", data));
                found = true;
                break;
            }
        }

        if (!found) {
            throw new Exception("User not found!");
        }

        FileHandler.writeFile(FILE_NAME, users);
    }

    // GET FUNCTIONS
    public User getUserById(String userID) throws Exception {
        return getAllUsers()
                .stream()
                .filter(u -> u.getUserID().equals(userID))
                .findFirst()
                .orElseThrow(() -> new Exception("User not found!"));
    }

    private User parseUser(String line) {
        String[] data = line.split(",");
        if (data.length < 9) return null;

        String userID = data[0];
        String username = data[1];
        String firstName = data[2];
        String lastName = data[3];
        String password = data[4];
        String email = data[5];
        String contact = data[6];
        boolean isActive = Boolean.parseBoolean(data[7]);
        String roleID = data[8];

        return switch (roleID.charAt(0)) {
            case 'M' ->
                    new Manager(userID, username, firstName, lastName, password, email, contact, isActive, roleID);
            case 'S' ->
                    new CounterStaff(userID, username, firstName, lastName, password, email, contact, isActive, roleID);
            case 'T' ->
                    new Technician(userID, username, firstName, lastName, password, email, contact, isActive, roleID);
            case 'C' ->
                    new Customer(userID, username, firstName, lastName, password, email, contact, isActive, roleID);
            default -> null;
        };
    }

    public List<User> getAllUsers() {
        List<String> lines = FileHandler.readFile(FILE_NAME);
        List<User> users = new ArrayList<>();

        for (String line : lines) {
            User u = parseUser(line);
            if (u != null) users.add(u);
        }

        return users;
    }

    public List<User> getActiveUsers() {
        return getAllUsers()
                .stream()
                .filter(User::getIsActive)
                .toList();
    }

    public List<User> getInactiveUsers() {
        return getAllUsers()
                .stream()
                .filter(u -> !u.getIsActive())
                .toList();
    }

    //returns inactive users only for the selected role
    public List<User> getInactiveUsersByRole(UserRole role) {
        return getInactiveUsers()
                .stream()
                .filter(user -> switch (role){
                    case MANAGER -> user instanceof Manager;
                    case COUNTER_STAFF -> user instanceof  CounterStaff;
                    case TECHNICIAN -> user instanceof Technician;
                    case CUSTOMER -> user instanceof Customer;
                    default -> false;
                })
                .toList();
    }
}
