package Util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class InputValidator {

    public static String validateID(String id, String prefix) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("ID cannot be empty.");
        }

        if (!id.matches(prefix + "\\d{4}")) {
            throw new IllegalArgumentException(
                    "ID must follow format " + prefix + "0001."
            );
        }

        return id.trim();
    }

    public static String validateText(String text, String fieldName) {
        if (text == null || text.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " cannot be empty.");
        }

        return text.trim();
    }

    public static <T> T validateObject(T object, String fieldName) {
        if (object == null) {
            throw new IllegalArgumentException(fieldName + " cannot be null.");
        }

        return object;
    }

    public static LocalDate validateDate(LocalDate date) {
        if (date == null) {
            throw new IllegalArgumentException("Date cannot be null.");
        }

        return date;
    }

    public static LocalTime validateTime(LocalTime time, String fieldName) {
        if (time == null) {
            throw new IllegalArgumentException(fieldName + " cannot be null.");
        }

        return time;
    }

    public static void validateTimeRange(LocalTime start, LocalTime end) {
        if (!start.isBefore(end)) {
            throw new IllegalArgumentException(
                    "Start time must be before end time."
            );
        }
    }

    public static int validateRating(int rating) {
        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException(
                    "Rating must be between 1 and 5."
            );
        }

        return rating;
    }

    public static LocalDateTime validateDateTime(LocalDateTime dateTime, String fieldName) {
        if (dateTime == null) {
            throw new IllegalArgumentException(fieldName + " cannot be null.");
        }

        return dateTime;
    }

    public static double validateAmount(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException(
                    "Amount must be greater than 0."
            );
        }

        return amount;
    }

    public static String validateServiceType(String type) {
        if (type == null || type.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Service type cannot be empty."
            );
        }

        type = type.trim();

        if (!type.equalsIgnoreCase("Normal")
                && !type.equalsIgnoreCase("Major")) {

            throw new IllegalArgumentException(
                    "Service type must be either Normal or Major."
            );
        }

        return Character.toUpperCase(type.charAt(0))
                + type.substring(1).toLowerCase();
    }

    public static int validateDuration(int duration) {
        if (duration <= 0) {
            throw new IllegalArgumentException(
                    "Duration must be greater than 0."
            );
        }

        return duration;
    }

    public static String validateUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be empty.");
        }

        username = username.trim();

        if (username.length() < 4) {
            throw new IllegalArgumentException("Username must be at least 4 characters.");
        }

        return username;
    }

    public static String validateName(String name, String fieldName) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " cannot be empty.");
        }

        name = name.trim();

        if (!name.matches("[a-zA-Z ]+")) {
            throw new IllegalArgumentException(fieldName + " can only contain letters and spaces.");
        }

        return name;
    }

    public static String validatePassword(String password) {
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("Password cannot be empty.");
        }

        if (password.length() < 6) {
            throw new IllegalArgumentException("Password must be at least 6 characters.");
        }

        return password;
    }

    public static String validateEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be empty.");
        }

        email = email.trim();

        if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            throw new IllegalArgumentException("Email format is invalid.");
        }

        return email;
    }

    public static String validateContact(String contact) {
        if (contact == null || contact.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Contact number cannot be empty."
            );
        }

        contact = contact.trim();

        if (!contact.matches("01\\d{8,9}")) {
            throw new IllegalArgumentException(
                    "Invalid contact number format. Correct format example: 0123456789"
            );
        }

        return contact;
    }

    public static String validateRoleID(String roleID) {
        if (roleID == null || roleID.trim().isEmpty()) {
            throw new IllegalArgumentException("Role ID cannot be empty.");
        }

        roleID = roleID.trim();

        if (!roleID.matches("[MSCT]\\d{4}")) {
            throw new IllegalArgumentException("Role ID must follow format M0001, S0001, C0001, or T0001.");
        }

        return roleID;
    }

    public static String validatePlateNumber(String plateNumber) {
        if (plateNumber == null || plateNumber.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Plate number cannot be empty."
            );
        }

        plateNumber = plateNumber.trim().toUpperCase();

        if (!plateNumber.matches("[A-Z]{1,3}\\d{1,4}[A-Z]?")) {
            throw new IllegalArgumentException(
                    "Invalid vehicle plate number format."
            );
        }

        return plateNumber;
    }

    public static int validateVehicleYear(int year) {
        int currentYear = java.time.Year.now().getValue();

        if (year < 1900 || year > currentYear + 1) {
            throw new IllegalArgumentException(
                    "Invalid vehicle year."
            );
        }

        return year;
    }
}