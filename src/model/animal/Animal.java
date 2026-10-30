package model.animal;

import java.io.Serializable;
import java.time.LocalDateTime;


import model.EnvironmentType;
import model.Identifiable;
import model.ZooValidation;



/**
 * Represents an animal registered in the zoo, storing its physical, dietary,
 * health, and origin specifications, as well as calculating ration costs.
 */
public class Animal implements Serializable,Identifiable {
    private static final long serialVersionUID = 1L;
    private static final int DAYS_IN_MONTH = 30;
    private static final double CARNIVOROUS_RATION_FACTOR = 0.03;
    private static final double HERBIVOROUS_RATION_FACTOR = 0.04;
    private static final double OMNIVOROUS_RATION_FACTOR = 0.035;
    private static final double INSECTIVOROUS_RATION_FACTOR = 0.025;
    private static final double CARNIVOROUS_FOOD_UNIT_COST = 25500;
    private static final double HERBIVOROUS_FOOD_UNIT_COST = 8800;
    private static final double OMNIVOROUS_FOOD_UNIT_COST = 15200;
    private static final double INSECTIVOROUS_FOOD_UNIT_COST = 18300;
    private static final double JUVENILE_INCREMENT = 0.2;
    private static final double RECOVERY_INCREMENT = 0.15;
    private static final double OBSERVATION_INCREMENT = 0.1;

    private String id;
    private String name;
    private String specie;
    private EnvironmentType environment;
    private String sex;
    private String birth;
    private double weight;
    private DietType typeDiet;
    private String countryOrigin;
    private String entryDate;
    private String lifeCycle;
    private String healthStatus;
    private String habitat;
    private String status;
    private String withdrawDate;
    private String withdrawReason;

    /**
     * Creates a new animal with all required data.
     *
     * Preconditions:
     * - 'weight' must be a positive double value.
     *
     * Postconditions:
     * - Instantiates an Animal object with all attributes initialized.
     *
     * @param name the animal name
     * @param specie the animal species
     * @param environment the environment required by the animal
     * @param sex the animal sex
     * @param birth the date of birth
     * @param weight the animal weight in kilograms
     * @param typeDiet the type of diet
     * @param countryOrigin the country of origin
     * @param entryDate the date the animal entered the zoo
     * @param lifeCycle the current life cycle stage
     * @param healthStatus the health status
     * @param habitat the assigned habitat identifier
     * @param status the current animal status
     * @param withdrawDate the withdrawal date if applicable
     * @param withdrawReason the withdrawal reason if applicable
     */
    public Animal(String name, String specie, EnvironmentType environment, String sex, String birth,
            double weight, String typeDiet, String countryOrigin, String entryDate,
            String lifeCycle, String healthStatus, String habitat,
            String status, String withdrawDate, String withdrawReason) {
        if (!ZooValidation.isNonBlank(name) || !ZooValidation.isNonBlank(specie)
                || !ZooValidation.isBirthBeforeEntry(birth, entryDate)
                || !ZooValidation.isDateNotAfterToday(birth)
                || !ZooValidation.isDateNotAfterToday(entryDate)
                || !ZooValidation.isPositiveFinite(weight)
                || environment == null
                || !ZooValidation.isNonBlank(sex)
                || !ZooValidation.isNonBlank(countryOrigin)
                || !isValidLifeCycle(lifeCycle)
                || !isValidHealthStatus(healthStatus)
                || !ZooValidation.isNonBlank(habitat)
                || !isActiveStatus(status)) {
            throw new IllegalArgumentException("Animal data or dates are outside the allowed ranges.");
        }
        id = generateId();
        this.name = name.trim();
        this.specie = specie.trim();
        this.environment = environment;
        this.sex = sex;
        this.birth = birth.trim();
        this.weight = weight;
        this.typeDiet = DietType.fromName(typeDiet);
        if (this.typeDiet == null) {
            throw new IllegalArgumentException("Animal diet must be a recognized diet type.");
        }
        this.countryOrigin = countryOrigin;
        this.entryDate = entryDate.trim();
        this.lifeCycle = lifeCycle;
        this.healthStatus = healthStatus;
      
        this.habitat = habitat;
        this.status = status;
        this.withdrawDate = withdrawDate;
        this.withdrawReason = withdrawReason;
    }

    /**
     * Returns the animal information in a single formatted string.
     *
     * Postconditions:
     * - Formats all attribute values into a detailed multi-line string.
     *
     * @return the formatted animal details
     */
    public String getInformationAnimal() {
        return "\n========================================\n"
                + "[ANIMAL]" + System.lineSeparator()
                + "ID: " + id + System.lineSeparator()
                + "Name: " + name + System.lineSeparator()
                + "Specie: " + specie + System.lineSeparator()
                + "Environment: " + environment.getTypeName() + System.lineSeparator()
                + "Sex: " + sex + System.lineSeparator()
                + "Birth: " + birth + System.lineSeparator()
                + "Weight: " + weight + System.lineSeparator()
                + "Diet: " + typeDiet.getText() + System.lineSeparator()
                + "Country: " + countryOrigin + System.lineSeparator()
                + "Entry date: " + entryDate + System.lineSeparator()
                + "Life cycle: " + lifeCycle + System.lineSeparator()
                + "Health status: " + healthStatus + System.lineSeparator()
                + "Status: " + status + System.lineSeparator()
               
                + "Habitat: " + habitat + System.lineSeparator()
                + "WithdraDate: " + withdrawDate + System.lineSeparator()
                + "WithdraReason: " + withdrawReason + System.lineSeparator()
                + "========================================";
    }

    // Analyzers

    /**
     * Returns the animal identifier.
     *
     * @return the animal identifier
     */
    @Override 
    public String getId() {
        return id;
    }

    /**
     * Returns the animal name.
     *
     * @return the animal name
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the animal species.
     *
     * @return the animal species
     */
    public String getSpecie() {
        return specie;
    }

    /**
     * Returns the animal sex.
     *
     * @return the animal sex
     */
    public String getSex() {
        return sex;
    }

    /**
     * Returns the animal birth date.
     *
     * @return the animal birth date
     */
    public String getBirth() {
        return birth;
    }

    /**
     * Returns the animal weight in kilograms.
     *
     * @return the animal weight
     */
    public double getWeight() {
        return weight;
    }

    /**
     * Returns the animal diet type.
     *
     * @return the animal diet type
     */
    public String getTypeDiet() {
        return typeDiet.getText();
    }

    public DietType getDietType() {
        return typeDiet;
    }

    /**
     * Returns the animal country of origin.
     *
     * @return the animal country of origin
     */
    public String getCountryOrigin() {
        return countryOrigin;
    }

    /**
     * Returns the date the animal entered the zoo.
     *
     * @return the date the animal entered the zoo
     */
    public String getEntryDate() {
        return entryDate;
    }

    /**
     * Returns the animal life cycle stage.
     *
     * @return the animal life cycle stage
     */
    public String getLifeCycle() {
        return lifeCycle;
    }

    /**
     * Returns the animal health status.
     *
     * @return the animal health status
     */
    public String getHealthStatus() {
        return healthStatus;
    }

    /**
     * Returns the animal status.
     *
     * @return the animal status
     */
    public String getStatus() {
        return status;
    }

    /**
     * Returns the animal environment.
     *
     * @return the environment required by the animal
     */
    public EnvironmentType getEnvironment() {
        return environment;
    }

    /**
     * Calculates the animal's estimated monthly food cost.
     *
     * Preconditions:
     * - 'typeDiet' should correspond to a recognized diet type.
     * - 'weight' must be a valid positive value.
     *
     * Postconditions:
     * - Computes food cost using weight, diet factors, unit costs, and health/age increments over 30 days.
     *
     * @return the monthly food cost based on the daily ration and diet type
     */
    public double calculateMonthlyRationCost() {
        return calculateDailyRation() * getFoodUnitCost() * DAYS_IN_MONTH;
    }

    public double calculateDailyRation() {
        double rationFactor = 0.0;
        double incrementPercentage = 0.0;

        switch (typeDiet) {
            case CARNIVOROUS:
                rationFactor = CARNIVOROUS_RATION_FACTOR;
                break;
            case HERBIVOROUS:
                rationFactor = HERBIVOROUS_RATION_FACTOR;
                break;
            case OMNIVOROUS:
                rationFactor = OMNIVOROUS_RATION_FACTOR;
                break;
            case INSECTIVOROUS:
                rationFactor = INSECTIVOROUS_RATION_FACTOR;
                break;
            default:
                break;
        }

        if ("Juvenile".equalsIgnoreCase(lifeCycle)) {
            incrementPercentage = JUVENILE_INCREMENT;
        } else if ("Quarantine".equalsIgnoreCase(healthStatus)
                || "Recovery".equalsIgnoreCase(healthStatus)) {
            incrementPercentage = RECOVERY_INCREMENT;
        } else if ("Under Observation".equalsIgnoreCase(healthStatus)) {
            incrementPercentage = OBSERVATION_INCREMENT;
        }

        return weight * rationFactor * (1 + incrementPercentage);
    }

    private double getFoodUnitCost() {
        switch (typeDiet) {
            case CARNIVOROUS:
                return CARNIVOROUS_FOOD_UNIT_COST;
            case HERBIVOROUS:
                return HERBIVOROUS_FOOD_UNIT_COST;
            case OMNIVOROUS:
                return OMNIVOROUS_FOOD_UNIT_COST;
            case INSECTIVOROUS:
                return INSECTIVOROUS_FOOD_UNIT_COST;
            default:
                throw new IllegalStateException("Unsupported diet type.");
        }
    }

    /**
     * Updates an animal's attribute based on the provided option code.
     *
     * Preconditions:
     * - 'option' corresponds to a valid editable property.
     * - 'value' is formatted appropriately for the target field (e.g., parsable double for weight).
     *
     * Postconditions:
     * - Modifies the corresponding attribute of the animal.
     *
     * Error Handling:
     * - Returns false if 'option' is not recognized.
     *
     * @param option numerical code representing the attribute to update
     * @param value new attribute value string
     * @return true if update succeeded; false if option code is invalid
     */
    public boolean updateInformation(int option, String value) {
        switch (option) {
            case 2:
                if (!ZooValidation.isNonBlank(value)) {
                    return false;
                }
                setName(value.trim());
                break;
            case 3:
                if (!ZooValidation.isNonBlank(value)) {
                    return false;
                }
                setSpecie(value.trim());
                break;
            case 4:
                setSex(value);
                break;
            case 5:
                if (!ZooValidation.isBirthBeforeEntry(value, entryDate)) {
                    return false;
                }
                setBirth(value);
                break;
            case 6:
                setWeight(Double.parseDouble(value));
                break;
            case 7:
                if (DietType.fromName(value) == null) {
                    return false;
                }
                setTypeDiet(value);
                break;
            case 8:
                setCountryOrigin(value);
                break;
            case 9:
                if (!ZooValidation.isBirthBeforeEntry(birth, value)) {
                    return false;
                }
                setEntryDate(value);
                break;
            case 10:
                setLifeCycle(value);
                break;
            case 11:
                setHealthStatus(value);
                break;
            case 13:
                setEnvironment(EnvironmentType.valueOf(value.toUpperCase()));
                break;
            case 14:
                setStatus(value);
                break;
            default:
                return false;
        }
        return true;
    }

    /**
     * Withdraws the animal from active status by registering withdrawal date and reason.
     *
     * Postconditions:
     * - Sets animal status to "Desvinculado".
     * - Updates 'withdrawDate' and 'withdrawReason' attributes.
     *
     * @param date date of withdrawal
     * @param reason reason for withdrawal
     */
    public void withdraw(String date, String reason) {
        if (!ZooValidation.isWithdrawalAfterEntry(date, entryDate)) {
            throw new IllegalArgumentException("Withdrawal date must be after entry date.");
        }
        setStatus("Desvinculado");
        setWithdrawDate(date.trim());
        setWithdrawReason(reason);
    }

    /**
     * Returns the assigned habitat identifier.
     *
     * @return the assigned habitat identifier
     */
    public String getHabitat() {
        return habitat;
    }

    // Modifiers

    /**
     * Sets the animal name.
     *
     * @param name the new animal name
     */
    public void setName(String name) {
        if (!ZooValidation.isNonBlank(name)) {
            throw new IllegalArgumentException("Animal name cannot be blank.");
        }
        this.name = name.trim();
    }

    /**
     * Sets the animal species.
     *
     * @param specie the new species
     */
    public void setSpecie(String specie) {
        if (!ZooValidation.isNonBlank(specie)) {
            throw new IllegalArgumentException("Animal species cannot be blank.");
        }
        this.specie = specie.trim();
    }

    /**
     * Sets the animal environment.
     *
     * @param environment the new environment
     */
    public void setEnvironment(EnvironmentType environment) {
        this.environment = environment;
    }

    /**
     * Sets the animal sex.
     *
     * @param sex the new sex
     */
    public void setSex(String sex) {
        this.sex = sex;
    }

    /**
     * Sets withdraw reason.
     *
     * @param withdrawReason the reason for withdrawal
     */
    public void setWithdrawReason(String withdrawReason) {
        this.withdrawReason = withdrawReason;
    }

    /**
     * Sets withdraw date.
     *
     * @param withdrawDate the withdrawal date
     */
    public void setWithdrawDate(String withdrawDate) {
        this.withdrawDate = withdrawDate;
    }

    /**
     * Sets the animal birth date.
     *
     * @param birth the new birth date
     */
    public void setBirth(String birth) {
        if (!ZooValidation.isBirthBeforeEntry(birth, entryDate)) {
            throw new IllegalArgumentException("Birth date must be valid and before entry date.");
        }
        this.birth = birth.trim();
    }

    /**
     * Sets the animal weight.
     *
     * @param weight the new weight
     */
    public void setWeight(double weight) {
        if (!ZooValidation.isPositiveFinite(weight)) {
            throw new IllegalArgumentException("Animal weight must be greater than zero.");
        }
        this.weight = weight;
    }

    /**
     * Sets the type of diet.
     *
     * @param typeDiet the new diet type
     */
    public void setTypeDiet(String typeDiet) {
        DietType parsedDiet = DietType.fromName(typeDiet);
        if (parsedDiet == null) {
            throw new IllegalArgumentException("Animal diet must be a recognized diet type.");
        }
        this.typeDiet = parsedDiet;
    }

    /**
     * Sets the country of origin.
     *
     * @param countryOrigin the new country of origin
     */
    public void setCountryOrigin(String countryOrigin) {
        this.countryOrigin = countryOrigin;
    }

    /**
     * Sets the entry date.
     *
     * @param entryDate the new entry date
     */
    public void setEntryDate(String entryDate) {
        if (!ZooValidation.isBirthBeforeEntry(birth, entryDate)) {
            throw new IllegalArgumentException("Entry date must be valid and after birth date.");
        }
        this.entryDate = entryDate.trim();
    }

    /**
     * Sets the life cycle stage.
     *
     * @param lifeCycle the new life cycle
     */
    public void setLifeCycle(String lifeCycle) {
        this.lifeCycle = lifeCycle;
    }

    /**
     * Sets the health status.
     *
     * @param healthStatus the new health status
     */
    public void setHealthStatus(String healthStatus) {
        if (!isValidHealthStatus(healthStatus)) {
            throw new IllegalArgumentException("Animal health status is not supported.");
        }
        this.healthStatus = healthStatus.trim();
    }

    /**
     * Sets the assigned habitat identifier.
     *
     * @param habitat the new habitat identifier
     */
    public void setHabitat(String habitat) {
        this.habitat = habitat;
    }

    /**
     * Sets the animal status.
     *
     * @param status the new animal status
     */
    public void setStatus(String status) {
        if (!isActiveStatus(status) && !isRetiredStatus(status)) {
            throw new IllegalArgumentException("Animal status must be active or retired.");
        }
        this.status = status.trim();
    }

    private boolean isValidLifeCycle(String value) {
        return value != null && ("juvenile".equalsIgnoreCase(value.trim())
                || "adult".equalsIgnoreCase(value.trim()));
    }

    private boolean isValidHealthStatus(String value) {
        return value != null && ("healthy".equalsIgnoreCase(value.trim())
                || "quarantine".equalsIgnoreCase(value.trim())
                || "recovery".equalsIgnoreCase(value.trim())
                || "under observation".equalsIgnoreCase(value.trim())
                || "cuarentena".equalsIgnoreCase(value.trim())
                || "recuperación".equalsIgnoreCase(value.trim())
                || "recuperacion".equalsIgnoreCase(value.trim())
                || "en observación".equalsIgnoreCase(value.trim())
                || "en observacion".equalsIgnoreCase(value.trim()));
    }

    private boolean isActiveStatus(String value) {
        return value != null && ("active".equalsIgnoreCase(value.trim())
                || "activo".equalsIgnoreCase(value.trim()));
    }

    private boolean isRetiredStatus(String value) {
        return value != null && ("retired".equalsIgnoreCase(value.trim())
                || "retirado".equalsIgnoreCase(value.trim())
                || "desvinculado".equalsIgnoreCase(value.trim()));
    }

         /**
     * Generates a unique identifier based on the current date and time.
     *
     * @return A unique identifier as a String, generated using the current minute,
     *         second, and nanosecond.
     */

    @Override
    public String generateId() {
       return "A" + LocalDateTime.now().getMinute() + LocalDateTime.now().getSecond() + LocalDateTime.now().getNano();
    }
}