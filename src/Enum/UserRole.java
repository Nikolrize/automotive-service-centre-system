package Enum;

public enum UserRole {
    MANAGER("Manager", 'M'),
    CUSTOMER("Customer", 'C'),
    TECHNICIAN("Technician", 'T'),
    COUNTER_STAFF("Counter Staff", 'S'),
    USER("User", 'U');

    private final String displayName;
    private final char prefix;

    UserRole(String displayName, char prefix) {
        this.displayName = displayName;
        this.prefix = prefix;
    }

    public String getDisplayName() {
        return displayName;
    }

    public char getPrefix() {
        return prefix;
    }
}