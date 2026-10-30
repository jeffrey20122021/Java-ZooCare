package model;

public enum AlertType {
    OCCUPANCY("Occupancy"),
    ENVIRONMENT_INCOMPATIBILITY("Environmental incompatibility"),
    INVALID_HABITAT_STATUS("Invalid habitat status"),
    INSUFFICIENT_CAPACITY("Insufficient habitat capacity");

    private final String description;

    AlertType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
