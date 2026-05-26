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
                .filter(User::getIsActive) //checks whether user account is deleted or not
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
            }
        }
        String userID = generateNewID(UserRole.USER);
        String roleID = generateNewID(role);
        User user = createUserByRole(
                userID, username, firstName, lastName,
                password, email, contact, true, roleID
        );

        if (user == null) {
            throw new Exception("Failed to register user.");
        }
        FileHandler.appendToFile(FILE_NAME, formatUser(user));
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
            String userID = generateNewID(UserRole.USER);

            User manager = new Manager(
                    userID,
                    "superuser",
                    "Super",
                    "User",
                    "superuser123",
                    "superuser@gmail.com",
                    "0123456789",
                    true,
                    "M0001"
            );

            FileHandler.appendToFile(FILE_NAME, formatUser(manager));
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
                User user = getUserById(userID);

                user.setUsername(newUsername);
                user.setFirstName(newFirstName);
                user.setLastName(newLastName);
                user.setPassword(newPassword);
                user.setEmail(newEmail);
                user.setContact(newContact);

                users.set(i, formatUser(user));
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
                User user = parseUser(users.get(i));

                if (user == null) {
                    throw new Exception("Failed to load user.");
                }

                user.setIsActive(status);

                users.set(i, formatUser(user));
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

        return createUserByRole(
                userID, username, firstName, lastName,
                password, email, contact, isActive, roleID
        );
    }

    private String formatUser(User user) {
        return String.join(",",
                user.getUserID(),
                user.getUsername(),
                user.getFirstName(),
                user.getLastName(),
                user.getPassword(),
                user.getEmail(),
                user.getContact(),
                String.valueOf(user.getIsActive()),
                user.getRoleID()
        );
    }

    private User createUserByRole(String userID, String username, String firstName,
                                  String lastName, String password, String email,
                                  String contact, boolean isActive, String roleID) {
        return switch (roleID.charAt(0)) {
            case 'M' -> new Manager(userID, username, firstName, lastName, password, email, contact, isActive, roleID);
            case 'S' -> new CounterStaff(userID, username, firstName, lastName, password, email, contact, isActive, roleID);
            case 'T' -> new Technician(userID, username, firstName, lastName, password, email, contact, isActive, roleID);
            case 'C' -> new Customer(userID, username, firstName, lastName, password, email, contact, isActive, roleID);
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
                .filter(user -> switch (role) {
                    case MANAGER -> user instanceof Manager;
                    case COUNTER_STAFF -> user instanceof CounterStaff;
                    case TECHNICIAN -> user instanceof Technician;
                    case CUSTOMER -> user instanceof Customer;
                    default -> false;
                })
                .toList();
    }

    public List<Customer> getAllCustomers() {
        return getActiveUsers()
                .stream()
                .filter(user -> user instanceof Customer)
                .map(user -> (Customer) user)
                .toList();
    }
}
