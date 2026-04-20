package com.officesupply.enums;

public enum UserRole {
    ADMIN("Admin"),
    EMPLOYEE("Employee");

    private final String displayName;

    UserRole(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
