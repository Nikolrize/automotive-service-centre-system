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

            if (data.length < 7) continue;

            String userID = data[0];
            String fileUsername = data[1];
            String filePassword = data[2];
            String email = data[3];
            String contact = data[4];
            boolean isActive = Boolean.parseBoolean(data[5]);
            String specificID = data[6];
            char role = specificID.charAt(0);


            if (fileUsername.equals(username) && filePassword.equals(password)) {
                switch (role) {
                    case 'M':
                        return new Manager(userID, fileUsername, filePassword, email, contact, isActive, specificID);
                    case 'C':
                        return new Customer(userID, fileUsername, filePassword, email, contact, isActive, specificID);
                    case 'T':
                        return new Technician(userID, fileUsername, filePassword, email, contact, isActive, specificID);
                    case 'S':
                        return new CounterStaff(userID, fileUsername, filePassword, email, contact, isActive, specificID);
                }
            }
        }

        return null;
    }

    public void registerUser(String username, String password, String email, String contact, String role) {
        String userID = generateNewID("User");
        String specificID = generateNewID(role);
        String data = String.join(",", userID, username, password, email, contact, "true", specificID);

        FileHandler.appendToFile(FILE_NAME, data);
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
            if (data.length >= 7) {
                String specificID = data[6];
                if ("M0001".equals(specificID)) {
                    hasDefaultManager = true;
                    break;
                }
            }
        }

        if (!hasDefaultManager) {
            String username = "manager";
            String password = "manager123";
            String email = "manager@gmail.com";
            String contact = "0123456789";
            String userID = generateNewID("User");
            String data = String.join(",", userID, username, password, email, contact, "true", "M0001");
            FileHandler.appendToFile(FILE_NAME, data);
        }
    }
}
