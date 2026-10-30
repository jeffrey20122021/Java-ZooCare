package model.employee;

/**
 * Employee roles supported by ZooCare.
 */
public enum Role {
    CARETAKER("Caretaker"),
    VETERINARIAN("Veterinarian");

    private final String text;

    Role(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public static Role fromName(String value) {
        if (value == null) {
            return null;
        }
        for (Role role : values()) {
            if (role.name().equalsIgnoreCase(value.trim())
                    || role.text.equalsIgnoreCase(value.trim())) {
                return role;
            }
        }
        return null;
    }

    public static String getOptionList() {
        StringBuilder options = new StringBuilder();
        Role[] values = values();
        for (int i = 0; i < values.length; i++) {
            options.append(i + 1).append(". ").append(values[i].getText()).append(System.lineSeparator());
        }
        return options.toString();
    }

    public static Role calculateRole(int option) {
        return option >= 1 && option <= values().length ? values()[option - 1] : null;
    }
}
