package model.activity;

public enum ActivityStatus {
    ASSIGNED("Assigned"),
    FINISHED("Finished"),
    CANCELED("Canceled");

    private final String description;

    ActivityStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
