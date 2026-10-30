package model;

import java.io.Serializable;
import java.time.LocalDateTime;

public class ZooAlert implements Serializable,Identifiable {
    private static final long serialVersionUID = 1L;

    private final String id;
    private final AlertType type;
    private final LocalDateTime createdAt;
    private final String entityId;
    private final String description;

    public ZooAlert(AlertType type, String entityId, String description) {
        if (type == null || !ZooValidation.isNonBlank(description)) {
            throw new IllegalArgumentException("Alert type and description are required.");
        }
        id = generateId();
        this.type = type;
        createdAt = LocalDateTime.now();
        this.entityId = entityId;
        this.description = description.trim();
    }

    @Override

    public String getId() {
        return id;
    }

    public AlertType getType() {
        return type;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public String getEntityId() {
        return entityId;
    }

    public String getDescription() {
        return description;
    }

    public String getInformation() {
        return id + " | " + type.getDescription() + " | " + createdAt + " | "
                + (entityId == null ? "N/A" : entityId) + " | " + description;
    }

    @Override
    public String generateId() {
      
       return "AL" + LocalDateTime.now().getMinute() + LocalDateTime.now().getSecond() + LocalDateTime.now().getNano();
    
    }
}
