package com.officesupply.enums;

public enum AuditAction {
    REQUEST_CREATED("Request Created"),
    REQUEST_APPROVED("Request Approved"),
    REQUEST_REJECTED("Request Rejected"),
    INVENTORY_UPDATED("Inventory Updated"),
    USER_CREATED("User Created"),
    USER_UPDATED("User Updated");

    private final String displayName;

    AuditAction(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
