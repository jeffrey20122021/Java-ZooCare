package model.animal;

/**
 * Supported animal diets and their display names.
 */
public enum DietType {
    HERBIVOROUS("Herbivorous"),
    CARNIVOROUS("Carnivorous"),
    OMNIVOROUS("Omnivorous"),
    INSECTIVOROUS("Insectivorous");

    private final String text;

    DietType(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public static DietType fromName(String value) {
        if (value == null) {
            return null;
        }
        for (DietType dietType : values()) {
            if (dietType.name().equalsIgnoreCase(value.trim())
                    || dietType.text.equalsIgnoreCase(value.trim())) {
                return dietType;
            }
        }
        return null;
    }

    public static String getOptionList() {
        StringBuilder options = new StringBuilder();
        DietType[] values = values();
        for (int i = 0; i < values.length; i++) {
            options.append(i + 1).append(". ").append(values[i].getText()).append(System.lineSeparator());
        }
        return options.toString();
    }

    public static DietType calculateDietType(int option) {
        return option >= 1 && option <= values().length ? values()[option - 1] : null;
    }
}
