package Service;

import Model.*;
import Util.FileHandler;

import java.util.List;

public class UserService {
    private static final String FILE_NAME = "users.txt";

    public User loginUser(String username, String password) {
        List<String> users = FileHandler.readFile(FILE_NAME);

        for (String line : users) {
            String[] data = line.split(",");

            if (data.length < 9) continue;

            String userID = data[0];
            String fileUsername = data[1];
            String firstName = data[2];
            String lastName = data[3];
            String filePassword = data[4];
            String email = data[5];
            String contact = data[6];
            boolean isActive = Boolean.parseBoolean(data[7]);
            String specificID = data[8];
            char role = specificID.charAt(0);


            if (fileUsername.equals(username) && filePassword.equals(password)) {
                switch (role) {
                    case 'M':
                        return new Manager(userID, fileUsername, firstName, lastName, filePassword, email, contact, isActive, specificID);
                    case 'C':
                        return new Customer(userID, fileUsername, firstName, lastName, filePassword, email, contact, isActive, specificID);
                    case 'T':
                        return new Technician(userID, fileUsername, firstName, lastName, filePassword, email, contact, isActive, specificID);
                    case 'S':
                        return new CounterStaff(userID, fileUsername, firstName, lastName, filePassword, email, contact, isActive, specificID);
                }
            }
        }

        return null;
    }

    public void registerUser(String username, String firstName, String lastName, String password, String email, String contact, String role) throws Exception {
        List<String> users = FileHandler.readFile(FILE_NAME);

        for (String line : users) {
            String[] data = line.split(",");

            if (data.length < 9) continue;

            String fileUsername = data[1];

            if (fileUsername.equals(username)) {
                throw new Exception("Username " + username + " already exists!");
            } else {
                String userID = generateNewID("User");
                String specificID = generateNewID(role);
                String newData = String.join(",", userID, username, firstName, lastName, password, email, contact, "true", specificID);
                FileHandler.appendToFile(FILE_NAME, newData);
            }
        }
    }

    private String generateNewID(String role) {
        List<String> users = FileHandler.readFile(FILE_NAME);
        int maxUserID = 0;
        int maxSpecificID = 0;

        for (String line : users) {
            String[] data = line.split(",");
            if (data.length < 7) continue;

            // General user ID: U0001
            int userNum = Integer.parseInt(data[0].substring(1));
            if (userNum > maxUserID) maxUserID = userNum;

            // Role-specific ID: M0001, C0002, etc
            String specificID = data[6];
            if (specificID.startsWith(role.substring(0, 1))) {
                int specNum = Integer.parseInt(specificID.substring(1));
                if (specNum > maxSpecificID) maxSpecificID = specNum;
            }
        }

        if (role.equals("User")) return String.format("U%04d", maxUserID + 1);

        char prefix = switch (role) {
            case "Manager" -> 'M';
            case "Customer" -> 'C';
            case "Technician" -> 'T';
            case "CounterStaff" -> 'S';
            default -> 'U';
        };

        return String.format("%c%04d", prefix, maxSpecificID + 1);
    }

    public void generateDefaultManager() {
        List<String> users = FileHandler.readFile(FILE_NAME);
        boolean hasDefaultManager = false;

        for (String line : users) {
            String[] data = line.split(",");
            if (data.length >= 9) {
                String specificID = data[8];
                if ("M0001".equals(specificID)) {
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
            String userID = generateNewID("User");
            String data = String.join(",", userID, username, firstName, lastName, password, email, contact, "true", "M0001");
            FileHandler.appendToFile(FILE_NAME, data);
        }
    }

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
}
