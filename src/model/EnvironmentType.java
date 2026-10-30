package model;


/**
 * Enum representing different types of environments.
 * Each environment type has a corresponding name.
 */
public enum EnvironmentType  {

    /**
     * Represents a terrestrial environment.
     */
    LAND("Terrestre"),

    /**
     * Represents an aquatic environment.
     */
    WATER("Acuatico"),

    /**
     * Represents an aviary environment.
     */
    AVIARY("Aviario"),
    /**
     * Represents a medical environment.
     */
    MEDICAL("Medico");

    /**
     * The name of the environment type.
     */
    private String typeName;

    /**
     * Constructor for the EnvironmentType enum.
     *
     * @param typeName the name of the environment type
     */
    private EnvironmentType(String typeName) {
        this.typeName = typeName;
    }

    /**
     * Gets the name of the environment type.
     *
     * @return the name of the environment type
     */
    public String getTypeName() {
        return typeName;
    }

}
