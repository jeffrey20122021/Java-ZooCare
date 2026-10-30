package model;

import java.io.Serializable;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.time.YearMonth;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Random;

import model.activity.Activity;
import model.activity.ActivityStatus;
import model.activity.EnrichmentActivity;
import model.activity.FeedingActivity;
import model.activity.MedicalCheckupActivity;
import model.animal.Animal;
import model.employee.Employee;
import model.employee.Role;

/**
 * Represents a zoo managing habitats, employees, and animal allocations.
 */
public class Zoo implements Serializable, Identifiable {
    private static final long serialVersionUID = 1L;

    private static Random random = new Random();

    private String id;
    private String name;
    private String address;
    private double budget;
    private String city;
    private String nameLegalRepresentative;
    private String idLegalRepresentative;
    private boolean zooProfileConfigured;

    private ArrayList<Habitat> myHabitats;
    private ArrayList<Habitat> retiredHabitats;

    private ArrayList<Employee> myEmployees;
    private Map zooMap;
    private ArrayList<Activity> myActivities;
    private ArrayList<ZooAlert> alerts;
    private transient String lastAddedHabitatId;

    /**
    * Creates a new Zoo instance with general organization details and initializes habitat and employee lists.
     *
     * Preconditions:
     * - 'budget' must be a finite value greater than zero.
     *
     * Postconditions:
    * - Initializes empty habitat and employee lists.
     *
     * @param name                    the zoo name
     * @param address                 the zoo address
     * @param city                    the city where the zoo is located
     * @param budget                  the initial budget
     * @param nameLegalRepresentative the legal representative name
     * @param idLegalRepresentative   the legal representative identifier
     */
    public Zoo(String name, String address, String city, double budget, String nameLegalRepresentative,
            String idLegalRepresentative) {
        if (!ZooValidation.isNonNegativeFinite(budget)) {
            throw new IllegalArgumentException("Zoo budget must be zero or greater.");
        }
        this.name = name;
        this.address = address;
        this.id = generateId();
        this.budget = budget;
        this.city = city;
        this.nameLegalRepresentative = nameLegalRepresentative;
        this.idLegalRepresentative = idLegalRepresentative;
        this.zooProfileConfigured = !("ZooCare".equalsIgnoreCase(name)
                && "Not defined".equalsIgnoreCase(address)
                && "Not defined".equalsIgnoreCase(city));
        myHabitats = new ArrayList<Habitat>();
        retiredHabitats = new ArrayList<Habitat>();
        myEmployees = new ArrayList<Employee>();
        zooMap = new Map();
        myActivities = new ArrayList<Activity>();
        alerts = new ArrayList<ZooAlert>();
        if (zooProfileConfigured && !createVeterinarian()) {
            throw new IllegalStateException("Unable to create the veterinary clinic for the configured zoo.");
        }
    }

    public boolean configureZoo(String name, String address, String city, double budget,
            String legalRepresentative, String legalRepresentativeId) {
        if (zooProfileConfigured || !ZooValidation.isNonBlank(name)
                || !ZooValidation.isNonBlank(address) || !ZooValidation.isNonBlank(city)
                || !ZooValidation.isNonBlank(legalRepresentative)
                || !ZooValidation.isNonBlank(legalRepresentativeId)
                || !ZooValidation.isNonNegativeFinite(budget)) {
            return false;
        }
        String previousName = this.name;
        String previousAddress = this.address;
        String previousCity = this.city;
        double previousBudget = this.budget;
        String previousLegalRepresentative = nameLegalRepresentative;
        String previousLegalRepresentativeId = idLegalRepresentative;
        this.name = name.trim();
        this.address = address.trim();
        this.city = city.trim();
        this.budget = budget;
        nameLegalRepresentative = legalRepresentative.trim();
        idLegalRepresentative = legalRepresentativeId.trim();
        zooProfileConfigured = true;
        if (!createVeterinarian()) {
            this.name = previousName;
            this.address = previousAddress;
            this.city = previousCity;
            this.budget = previousBudget;
            nameLegalRepresentative = previousLegalRepresentative;
            idLegalRepresentative = previousLegalRepresentativeId;
            zooProfileConfigured = false;
            return false;
        }
        return true;
    }

    public boolean isZooProfileConfigured() {
        return zooProfileConfigured;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String generateId() {
        return String.valueOf(random.nextInt(900000) + 100000);
    }

    private void readObject(ObjectInputStream input) throws IOException, ClassNotFoundException {
        input.defaultReadObject();
        if (myHabitats == null) {
            myHabitats = new ArrayList<Habitat>();
        }
        if (retiredHabitats == null) {
            retiredHabitats = new ArrayList<Habitat>();
        }
        if (myEmployees == null) {
            myEmployees = new ArrayList<Employee>();
        }
        if (zooMap == null) {
            zooMap = new Map();
            for (Habitat habitat : myHabitats) {
                if (habitat != null && !zooMap.addHabitat(habitat)) {
                    throw new IOException("Unable to restore habitat placement for " + habitat.getId() + ".");
                }
            }
        }
        if (myActivities == null) {
            myActivities = new ArrayList<Activity>();
        }
        if (alerts == null) {
            alerts = new ArrayList<ZooAlert>();
        }
        if (!zooProfileConfigured && !("ZooCare".equalsIgnoreCase(name)
                && "Not defined".equalsIgnoreCase(address)
                && "Not defined".equalsIgnoreCase(city))) {
            zooProfileConfigured = true;
        }
        CodeGenerator.reserveCode(id);
        for (Habitat habitat : myHabitats) {
            if (habitat != null) {
                zooMap.updateHabitatName(habitat);
                CodeGenerator.reserveCode(habitat.getId());
                for (Animal animal : habitat.getMyAnimals()) {
                    if (animal != null) {
                        CodeGenerator.reserveCode(animal.getId());
                    }
                }
                for (Animal animal : habitat.getWithdrawAnimals()) {
                    if (animal != null) {
                        CodeGenerator.reserveCode(animal.getId());
                    }
                }
            }
        }
        for (Habitat habitat : retiredHabitats) {
            CodeGenerator.reserveCode(habitat.getId());
            for (Animal animal : habitat.getWithdrawAnimals()) {
                if (animal != null) {
                    CodeGenerator.reserveCode(animal.getId());
                }
            }
        }
        for (Employee employee : myEmployees) {
            if (employee != null) {
                CodeGenerator.reserveCode(employee.getId());
            }
        }
        for (Activity activity : myActivities) {
            CodeGenerator.reserveCode(activity.getId());
        }
        for (ZooAlert alert : alerts) {
            CodeGenerator.reserveCode(alert.getId());
        }
        if (zooProfileConfigured && getVeterinaryClinic() == null && !createVeterinarian()) {
            throw new IOException("Unable to restore the veterinary clinic.");
        }
        if (!zooProfileConfigured) {
            for (int i = myHabitats.size() - 1; i >= 0; i--) {
                Habitat habitat = myHabitats.get(i);
                if (habitat != null && habitat.isVeterinaryClinic()) {
                    if (!zooMap.removeHabitat(habitat)) {
                        throw new IOException("Unable to remove the veterinary clinic from an unconfigured zoo.");
                    }
                    myHabitats.remove(i);
                }
            }
        }
    }

    /**
     * Validates if a habitat exists, is active, and matches the specified environment.
     *
     * Preconditions:
     * - 'habitatId' must not be null.
     * - 'environment' should correspond to a valid EnvironmentType name.
     *
     * Postconditions:
     * - Returns true if the habitat matches conditions; false otherwise.
     *
     * @param environment environment type string to compare
     * @param habitatId identifier of the habitat to validate
     * @return true if valid and active; false otherwise
     */
    public boolean validateHabitat(String environment, String habitatId) {
        for (int i = 0; i < myHabitats.size(); i++) {
            Habitat habitat = myHabitats.get(i);
            if (habitat != null && habitatId != null && habitatId.equals(habitat.getId())) {
                if (!isActive(habitat.getStatus())) {
                    registerAlert(AlertType.INVALID_HABITAT_STATUS, habitat.getId(),
                            "Animal assignment attempted to a habitat that is not active.");
                    return false;
                }
                if (!habitat.isVeterinaryClinic()
                        && (environment == null || habitat.getEnvironment() == null
                                || !habitat.getEnvironment().name().equalsIgnoreCase(environment))) {
                    registerAlert(AlertType.ENVIRONMENT_INCOMPATIBILITY, habitat.getId(),
                            "Animal environment does not match the selected habitat.");
                    return false;
                }
                if (!habitat.hasAvailableCapacity(null)) {
                    registerAlert(AlertType.INSUFFICIENT_CAPACITY, habitat.getId(),
                            "The selected habitat has no available animal capacity.");
                    return false;
                }
                return true;
            }
        }

        return false;
    }

    /**
     * Validates that the habitat identified by ID is active and has the environment
     * selected by its 1-based option.
     *
     * Preconditions:
     * - 'environmentOption' is a 1-based option for an EnvironmentType value.
     *
     * Postconditions:
     * - Returns true only if the selected environment matches an active habitat.
     *
     * Error Handling:
     * - Returns false if the option is invalid, the habitat ID is null or unknown,
     *   or the habitat is inactive or has a different environment.
     *
     * @param environmentOption 1-based option for the environment to validate
     * @param habitatId          identifier of the habitat to validate
     * @return true if the habitat is active and has the selected environment;
     *         otherwise, false
     */
    public boolean validateHabitat(int environmentOption, String habitatId) {
        EnvironmentType environment = calculateEnvironmentType(environmentOption);
        return environment != null && validateHabitat(environment.name(), habitatId);
    }

    public String validateAnimalAssignment(int environmentOption, String habitatId, String healthStatus) {
        if (!zooProfileConfigured) {
            return "Register the zoo before registering animals.";
        }
        if (!hasRegisteredEmployees()) {
            return "Register at least one employee before registering animals.";
        }
        EnvironmentType environment = calculateEnvironmentType(environmentOption);
        if (environment == null) {
            return "The selected animal environment is invalid.";
        }
        return validateAnimalTarget(environment, habitatId, healthStatus, true);
    }

    private String validateAnimalTarget(EnvironmentType environment, String habitatId, String healthStatus,
            boolean registerAlert) {
        Habitat target = findHabitatEntityById(habitatId);
        if (target == null) {
            if (registerAlert) {
                registerAlert(AlertType.INVALID_HABITAT_STATUS, habitatId,
                        "Animal assignment was rejected because the selected habitat does not exist.");
            }
            return "The selected habitat does not exist.";
        }
        if (!isActive(target.getStatus())) {
            if (registerAlert) {
                registerAlert(AlertType.INVALID_HABITAT_STATUS, target.getId(),
                        "Animal assignment was rejected because the habitat is not active.");
            }
            return "The habitat is not active.";
        }
        if (!target.isVeterinaryClinic() && target.getEnvironment() != environment) {
            if (registerAlert) {
                registerAlert(AlertType.ENVIRONMENT_INCOMPATIBILITY, target.getId(),
                        "Animal environment does not match the selected habitat.");
            }
            return "The habitat environment is incompatible with the animal.";
        }
        if (isClinicRequired(healthStatus) && !target.isVeterinaryClinic()) {
            if (registerAlert) {
                registerAlert(AlertType.INVALID_HABITAT_STATUS, target.getId(),
                        "Animals in quarantine or recovery must be assigned to the veterinary clinic.");
            }
            return "Animals in quarantine or recovery must be assigned to the veterinary clinic.";
        }
        if (!target.hasAvailableCapacity(null)) {
            if (registerAlert) {
                registerAlert(AlertType.INSUFFICIENT_CAPACITY, target.getId(),
                        "Animal assignment was rejected because the habitat has no available capacity.");
            }
            return "The habitat has no available animal capacity.";
        }
        return null;
    }

    /**
     * Validates compatibility and capacity, then reassigns an animal to a new habitat.
     *
     * Preconditions:
     * - Animal with 'animalId' exists in one of the habitats.
     * - Habitat with 'habitatId' exists and is active.
     *
     * Postconditions:
     * - If valid, the animal is moved between habitat lists and its habitat reference is updated.
     *
     * Error Handling:
     * - Returns false if habitat is inactive, environment mismatches, or full capacity reached.
     *
     * @param animalId  identifier of the animal whose habitat will be changed
     * @param habitatId the requested target habitat identifier
     * @return true if habitat assignment succeeds; otherwise, false
     */
    public boolean validateAndAssignHabitat(String animalId, String habitatId) {
        Animal animal = findAnimalEntityByIdInHabitat(animalId);
        if (animal == null) {
            return false;
        }

        for (int i = 0; i < myHabitats.size(); i++) {
            Habitat targetHabitat = myHabitats.get(i);
            if (targetHabitat != null && habitatId != null && habitatId.equals(targetHabitat.getId())) {
                if (!isActive(targetHabitat.getStatus())) {
                    registerAlert(AlertType.INVALID_HABITAT_STATUS, targetHabitat.getId(),
                            "Animal transfer was rejected because the habitat is not active.");
                    return false;
                }
                boolean clinicRequired = isClinicRequired(animal.getHealthStatus());
                if (clinicRequired && !targetHabitat.isVeterinaryClinic()) {
                    registerAlert(AlertType.INVALID_HABITAT_STATUS, targetHabitat.getId(),
                            "Animals in quarantine or recovery must be transferred to the veterinary clinic.");
                    return false;
                }
                if (!targetHabitat.isVeterinaryClinic()
                        && targetHabitat.getEnvironment() != animal.getEnvironment()) {
                    registerAlert(AlertType.ENVIRONMENT_INCOMPATIBILITY, targetHabitat.getId(),
                            "Animal environment does not match the target habitat.");
                    return false;
                }
                if (!targetHabitat.hasAvailableCapacity(animal.getId())) {
                    registerAlert(AlertType.INSUFFICIENT_CAPACITY, targetHabitat.getId(),
                            "The target habitat has no available animal capacity.");
                    return false;
                }

                Habitat sourceHabitat = null;
                for (int j = 0; j < myHabitats.size(); j++) {
                    if (myHabitats.get(j) != null && myHabitats.get(j).hasAnimal(animal.getId())) {
                        sourceHabitat = myHabitats.get(j);
                        break;
                    }
                }
                if (sourceHabitat == null) {
                    return false;
                }
                if (sourceHabitat == targetHabitat) {
                    return true;
                }
                int previousOccupation = targetHabitat.getTotalOccupation();

                if (!sourceHabitat.removeAnimalForTransfer(animal)) {
                    return false;
                }
                if (!targetHabitat.addAnimal(animal)) {
                    if (!sourceHabitat.addAnimal(animal)) {
                        throw new IllegalStateException("Unable to restore animal to its original habitat.");
                    }
                    return false;
                }

                animal.setHabitat(targetHabitat.getId());
                registerOccupancyAlertIfNeeded(targetHabitat, previousOccupation);
                return true;
            }
        }
        return false;
    }

    /**
     * Returns general information and summary details of the zoo.
     *
     * Preconditions:
     * - None.
     *
     * Postconditions:
     * - Computes total monthly food cost and formats zoo details string.
     *
     * @return the formatted zoo information
     */
    public String getInformation() {
        double monthlyFoodCost = getMonthlyFoodCost();
        return "\n========================================\n"
                + "[ZOO]" + System.lineSeparator()
                + "ID: " + id + System.lineSeparator()
                + "Name: " + name + System.lineSeparator()
                + "Address: " + address + System.lineSeparator()
                + "Budget: " + String.format("%,.2f", budget) + System.lineSeparator()
                + "City: " + city + System.lineSeparator()
                + "Legal representative: " + nameLegalRepresentative + System.lineSeparator()
                + "Representative ID: " + idLegalRepresentative + System.lineSeparator()
                + "Estimated monthly feeding cost: " + monthlyFoodCost + System.lineSeparator()
                + "========================================";
    }

   /**
     * Returns detailed information for all registered animals across all habitats.
     *
     * Preconditions:
    * - 'myHabitats' list should be initialized.
     *
     * Postconditions:
     * - Concatenates animal information strings from each registered habitat.
     *
     * @return the formatted animal information
     */
    public String getAnimalsInformation() {
        String information = "\n===== ANIMALS =====" + System.lineSeparator();
        boolean hasAnimals = false;
        for (int i = 0; i < myHabitats.size(); i++) {
            if (myHabitats.get(i) != null) {
                String habitatAnimals = myHabitats.get(i).getAnimalsInformation();
                if (!habitatAnimals.isEmpty()) {
                    information += habitatAnimals;
                    hasAnimals = true;
                }
            }
        }
        for (Habitat habitat : retiredHabitats) {
            String withdrawnAnimals = habitat.getWithdrawAnimalInformation();
            if (!withdrawnAnimals.isEmpty()) {
                information += "Retired habitat " + habitat.getName() + " - historical animals:"
                        + System.lineSeparator() + withdrawnAnimals;
                hasAnimals = true;
            }
        }
        if (!hasAnimals) {
            information = information + "No animals registered.";
        }
        return information;
    }

    public String getEmployeeListByRole(int roleOption) {
        Role role = Role.calculateRole(roleOption);
        if (role == null) {
            return "";
        }
        StringBuilder information = new StringBuilder();
        for (Employee employee : myEmployees) {
            if (employee != null && isActive(employee.getState()) && employee.getRole() == role) {
                if (information.length() > 0) {
                    information.append(System.lineSeparator());
                }
                information.append(employee.getId()).append('-').append(employee.getFullName());
            }
        }
        return information.toString();
    }

    /**
    * Returns detailed information of all registered employees.
     *
     * Preconditions:
    * - 'myEmployees' list should be initialized.
     *
     * Postconditions:
    * - Concatenates details for each registered employee.
     *
     * @return the formatted employee information
     */
    public String getEmployeeInformation() {
        String information = "\n===== EMPLOYEES =====" + System.lineSeparator();
        boolean hasEmployee = false;
        for (int i = 0; i < myEmployees.size(); i++) {
            if (myEmployees.get(i) != null) {
                information = information + myEmployees.get(i).getInformation()
                        + System.lineSeparator();
                hasEmployee = true;
            }
        }
        if (!hasEmployee) {
            information = information + "No employee registered.";
        }
        return information;
    }

    /**
     * Generates a numbered list of all available EnvironmentType enum values.
     *
     * Preconditions:
     * - None.
     *
     * Postconditions:
     * - Returns a multi-line formatted string containing all EnvironmentType options.
     *
     * @return A string containing the list of all environment types.
     */
    public String getEnvironmentTypeList() {

        String environmentList = "";

        EnvironmentType[] values = EnvironmentType.values();

        for (int i = 0; i < values.length; i++) {

            environmentList += (i + 1) + ". " + values[i].getTypeName() + "\n";

        }

        return environmentList;

    }

    /**
     * Returns detailed information of all registered habitats, including occupation alerts and food costs.
     *
     * Preconditions:
    * - 'myHabitats' list should be initialized.
     *
     * Postconditions:
     * - Appends occupation warnings if occupation percentage is equal to or greater than 90%.
     *
     * @return the formatted habitat information
     */
    public String getHabitatsInformation() {
        String information = "\n===== HABITATS =====" + System.lineSeparator();
        boolean hasHabitats = false;
        for (int i = 0; i < myHabitats.size(); i++) {
            if (myHabitats.get(i) != null) {
                Habitat habitat = myHabitats.get(i);

                double percentualOccupation = habitat.getPercentualOccupation();

                if (percentualOccupation >= 90) {
                    information = information
                            + "ALERT Occupation is equal to or greater than 90 percent"
                            + System.lineSeparator();
                } else if (percentualOccupation < 90) {
                    information = information
                            + "Occupation is below 90 percent"
                            + System.lineSeparator();
                }

                information = information + habitat.getInformation()
                        + "\nEstimated monthly animal feeding cost for habitat "
                        + habitat.getName() + ": " + habitat.getMonthlyFoodCost()
                        + System.lineSeparator();
                hasHabitats = true;
            }
        }
        if (!hasHabitats) {
            information = information + "No habitats registered.";
        }
        return information;
    }

    /**
    * Adds a new habitat to the zoo's habitat list.
     *
     * Preconditions:
    * - The map must have space for the habitat.
     *
     * Postconditions:
     * - Instantiates and places a new Habitat object in the first empty slot.
     *
     * Error Handling:
    * - Returns false if the habitat limit is reached.
     *
     * @param name        the habitat name
     * @param environment the habitat environment
     * @param temperature the habitat temperature
     * @param area        the habitat area
     * @param budget      the habitat budget
     * @param status      the habitat status
     * @param capacity    the habitat capacity
     * @param responsible the responsible person
     * @return true if the habitat is added; otherwise, false
     */

    
    public boolean addHabitat(String name, EnvironmentType environment, double temperature, double area,
            double budget, String status, int capacity, String responsible) {
        lastAddedHabitatId = null;
        boolean veterinaryClinic = "Clínica Veterinaria".equalsIgnoreCase(name)
                && environment == EnvironmentType.MEDICAL;
        if (!ZooValidation.isValidTemperature(temperature) || !ZooValidation.isNonBlank(status)
                || !ZooValidation.isNonBlank(name) || environment == null
                || !isValidHabitatArea(area, veterinaryClinic)
                || !ZooValidation.isNonNegativeFinite(budget) || capacity <= 0
                || (!veterinaryClinic && !hasRegisteredEmployees())
                || (!isActive(status) && !isMaintenance(status) && !isUnderConstruction(status))
                || (veterinaryClinic && getVeterinaryClinic() != null)) {
            return false;
        }
        String assignedName = veterinaryClinic ? "Clínica Veterinaria" : getNextHabitatNameForArea(area);
        if (assignedName == null) {
            return false;
        }
        Habitat habitat = new Habitat(assignedName, environment, temperature, area,
                budget, status, capacity, responsible);
        if (!zooMap.addHabitat(habitat)) {
            return false;
        }
        myHabitats.add(habitat);
        lastAddedHabitatId = habitat.getId();
        return true;
    }

    public boolean addHabitat(int environmentOption, double temperature, double area,
            double budget, String status, int capacity, String responsible) {
        EnvironmentType environment = calculateEnvironmentType(environmentOption);
        if (environment == null || !isValidHabitatArea(area, false)) {
            return false;
        }

        String habitatName = getNextHabitatNameForArea(area);
        return habitatName != null && addHabitat(habitatName, environment, temperature, area,
                budget, status, capacity, responsible);
    }

    public String getNextHabitatNameForArea(double area) {
        return getHabitatNameForArea(area);
    }

    public String getHabitatRegistrationError(double area) {
        if (!hasRegisteredEmployees()) {
            return "Register at least one employee before creating a habitat.";
        }
        if (!isValidHabitatArea(area, false)) {
            return "Habitat area must be greater than 0 and at most 300 m2.";
        }
        if (!zooMap.hasAvailablePlacement(area)) {
            return "Cannot register this habitat: the map has no available contiguous positions "
                    + "within one block for this area.";
        }
        return null;
    }

    private String getHabitatNameForArea(double area) {
        if (!isValidHabitatArea(area, false)) {
            return null;
        }
        if (area > 100 && area <= 200) {
            return "H01";
        }
        if (area <= 100) {
            return "H0" + (random.nextInt(4) + 2);
        }
        if (area > 200 && area <= 300) {
            return "H06";
        }
        return null;
    }

    private boolean isValidHabitatArea(double area, boolean veterinaryClinic) {
        return Double.isFinite(area) && area > 0
                && (veterinaryClinic ? area == 400 : area <= 300);
    }

    public String getLastAddedHabitatId() {
        return lastAddedHabitatId;
    }

    /**
     * Removes an empty habitat and releases its map cells.
     */
    public boolean removeHabitat(String habitatId) {
        for (int i = 0; i < myHabitats.size(); i++) {
            Habitat habitat = myHabitats.get(i);
            if (habitat != null && habitatId != null && habitatId.equals(habitat.getId())) {
                if (habitat.isVeterinaryClinic() || habitat.hasAnimals() || !zooMap.removeHabitat(habitat)) {
                    return false;
                }
                cancelAssignedActivitiesForHabitat(habitat);
                habitat.clearResponsibleEmployees();
                habitat.setStatus("retirado");
                retiredHabitats.add(habitat);
                myHabitats.remove(i);
                return true;
            }
        }
        return false;
    }

    public String getZooMap() {
        return this.zooMap.getMapAsString();
    }

    public int getAvailableMapPositions() {
        return zooMap.countAvailablePositions();
    }

    public boolean addFeedingActivity(String habitatId, String animalId, String employeeId,
            LocalDate schedulingDate, double foodWeight) {
        Habitat habitat = findHabitatEntityById(habitatId);
        Employee responsible = findEmployeeEntityById(employeeId);
        Animal animal = findAnimalEntityByIdInHabitat(animalId);
        if (habitat == null || responsible == null || animal == null
                || !isActive(habitat.getStatus()) || !isActive(responsible.getState())
                || !isActive(animal.getStatus())
                || !habitat.hasAnimal(animalId) || schedulingDate == null
                || schedulingDate.isAfter(LocalDate.now())
                || responsible.getRole() != Role.CARETAKER
                || !ZooValidation.isPositiveFinite(foodWeight)) {
            return false;
        }

        myActivities.add(new FeedingActivity(CodeGenerator.generateUniqueCode(), habitat, responsible,
                schedulingDate, animal, foodWeight));
        return true;
    }

    public boolean finishMedicalCheckupAsEmployee(String activityId, String employeeId, double cost, double weight,
            String healthStatus, String observations, String medication, LocalDate followUpDate) {
        Activity activity = findActivityById(activityId);
        Employee employee = findEmployeeEntityById(employeeId);
        return activity instanceof MedicalCheckupActivity && employee != null
                && activity.getResponsible() == employee && isActive(employee.getState())
                && employee.getRole() == Role.VETERINARIAN
                && finishMedicalCheckup(activityId, cost, weight, healthStatus,
                        observations, medication, followUpDate);
    }

    public boolean isActiveEmployeeWithRole(String employeeId, int roleOption) {
        Employee employee = findEmployeeEntityById(employeeId);
        Role selectedRole = Role.calculateRole(roleOption);
        return employee != null && selectedRole != null && isActive(employee.getState())
                && employee.getRole() == selectedRole;
    }

    public boolean addCleaningActivity(String habitatId, String employeeId, LocalDate schedulingDate,
            String description, String usedProducts) {
        Habitat habitat = findHabitatEntityById(habitatId);
        Employee responsible = findEmployeeEntityById(employeeId);
        if (habitat == null || responsible == null
                || !(isActive(habitat.getStatus()) || isMaintenance(habitat.getStatus()))
                || !isActive(responsible.getState()) || responsible.getRole() != Role.CARETAKER
                || schedulingDate == null || schedulingDate.isAfter(LocalDate.now())
                || !ZooValidation.isNonBlank(description) || !ZooValidation.isNonBlank(usedProducts)) {
            return false;
        }

        myActivities.add(new model.activity.CleaningActivity(CodeGenerator.generateUniqueCode(), habitat, responsible,
                schedulingDate, description, usedProducts));
        return true;
    }

    public boolean addEnrichmentActivity(String habitatId, String employeeId, LocalDate schedulingDate,
            String description, int durationInDays, String usedProducts) {
        Habitat habitat = findHabitatEntityById(habitatId);
        Employee responsible = findEmployeeEntityById(employeeId);
        if (habitat == null || responsible == null || !isActive(habitat.getStatus())
                || !isActive(responsible.getState()) || responsible.getRole() != Role.CARETAKER
                || schedulingDate == null || schedulingDate.isAfter(LocalDate.now())
                || !ZooValidation.isNonBlank(description) || durationInDays <= 0
                || !ZooValidation.isNonBlank(usedProducts)) {
            return false;
        }

        myActivities.add(new EnrichmentActivity(CodeGenerator.generateUniqueCode(), habitat, responsible,
                schedulingDate, description, durationInDays, usedProducts));
        return true;
    }

    public boolean addMedicalCheckupActivity(String habitatId, String animalId, String employeeId,
            LocalDate schedulingDate, String reason) {
        Habitat habitat = findHabitatEntityById(habitatId);
        Employee responsible = findEmployeeEntityById(employeeId);
        Animal animal = findAnimalEntityByIdInHabitat(animalId);
        if (habitat == null || responsible == null || animal == null
                || !isActive(habitat.getStatus()) || !isActive(animal.getStatus())
                || !isActive(responsible.getState()) || responsible.getRole() != Role.VETERINARIAN
                || !habitat.hasAnimal(animalId) || schedulingDate == null
                || schedulingDate.isAfter(LocalDate.now()) || !ZooValidation.isNonBlank(reason)) {
            return false;
        }
        myActivities.add(new MedicalCheckupActivity(CodeGenerator.generateUniqueCode(),
                habitat, responsible, schedulingDate, animal, reason));
        return true;
    }

    public String getActivitiesInformation() {
        if (myActivities.isEmpty()) {
            return "No activities are registered.";
        }
        StringBuilder information = new StringBuilder();
        for (Activity activity : myActivities) {
            if (information.length() > 0) {
                information.append(System.lineSeparator()).append(System.lineSeparator());
            }
            information.append(activity.getInformation());
        }
        return information.toString();
    }

    public String getActivityInformationById(String activityId) {
        Activity activity = findActivityById(activityId);
        return activity == null ? null : activity.getInformation();
    }

    public String getMedicalActivityAnimalId(String activityId) {
        Activity activity = findActivityById(activityId);
        return activity instanceof MedicalCheckupActivity
                ? ((MedicalCheckupActivity) activity).getAnimal().getId()
                : null;
    }

    public String getAssignedActivitiesForEmployee(String employeeId) {
        Employee employee = findEmployeeEntityById(employeeId);
        if (employee == null) {
            return "Employee member not found.";
        }
        StringBuilder information = new StringBuilder();
        for (Activity activity : myActivities) {
            if (activity.getResponsible() == employee && activity.getStatus() == ActivityStatus.ASSIGNED) {
                if (information.length() > 0) {
                    information.append(System.lineSeparator()).append(System.lineSeparator());
                }
                information.append(activity.getInformation());
            }
        }
        return information.length() == 0 ? "No assigned activities." : information.toString();
    }

    public String getOperationalSummary(YearMonth month) {
        if (month == null) {
            throw new IllegalArgumentException("A reporting month is required.");
        }
        int habitatCount = 0;
        int activeHabitats = 0;
        int maintenanceHabitats = 0;
        int constructionHabitats = 0;
        int retiredHabitatCount = retiredHabitats.size();
        StringBuilder healthCounts = new StringBuilder();
        int activeAnimals = 0;
        for (Habitat habitat : myHabitats) {
            if (habitat != null) {
                habitatCount++;
                if (isActive(habitat.getStatus())) {
                    activeHabitats++;
                } else if (isMaintenance(habitat.getStatus())) {
                    maintenanceHabitats++;
                } else if (isUnderConstruction(habitat.getStatus())) {
                    constructionHabitats++;
                } else {
                    retiredHabitatCount++;
                }
                for (Animal animal : habitat.getMyAnimals()) {
                    if (animal != null && isActive(animal.getStatus())) {
                        activeAnimals++;
                        appendCount(healthCounts, animal.getHealthStatus());
                    }
                }
            }
        }
        habitatCount += retiredHabitatCount;
        int assignedActivities = 0;
        double monthlyCost = 0;
        for (Activity activity : myActivities) {
            if (activity.getStatus() == ActivityStatus.ASSIGNED) {
                assignedActivities++;
            }
            if (activity.getStatus() == ActivityStatus.FINISHED && activity.getExecutionDate() != null
                    && YearMonth.from(activity.getExecutionDate()).equals(month)) {
                monthlyCost += activity.getCost();
            }
        }
        return "Operational summary for " + month + System.lineSeparator()
                + "Habitats: " + habitatCount + " (active " + activeHabitats + ", maintenance "
                + maintenanceHabitats + ", construction " + constructionHabitats + ", retired "
                + retiredHabitatCount + ")" + System.lineSeparator()
                + "Active animals: " + activeAnimals + System.lineSeparator()
                + "Active animals by health status: "
                + (healthCounts.length() == 0 ? "none" : healthCounts) + System.lineSeparator()
                + "Assigned activities: " + assignedActivities + System.lineSeparator()
                + "Operational cost for month: " + monthlyCost + " COP";
    }

    private void appendCount(StringBuilder counts, String label) {
        String key = label == null ? "Unknown" : label;
        String[] entries = counts.toString().split(", ");
        for (int i = 0; i < entries.length; i++) {
            String[] parts = entries[i].split("=", 2);
            if (parts.length == 2 && parts[0].equalsIgnoreCase(key)) {
                entries[i] = parts[0] + "=" + (Integer.parseInt(parts[1]) + 1);
                counts.setLength(0);
                counts.append(String.join(", ", entries));
                return;
            }
        }
        if (counts.length() > 0) {
            counts.append(", ");
        }
        counts.append(key).append("=1");
    }

    public String getActivitiesReport(YearMonth month) {
        if (month == null) {
            throw new IllegalArgumentException("A reporting month is required.");
        }
        StringBuilder report = new StringBuilder("Activities for ").append(month);
        for (ActivityStatus status : ActivityStatus.values()) {
            report.append(System.lineSeparator()).append(System.lineSeparator())
                    .append(status.getDescription()).append(':');
            boolean found = false;
            for (Activity activity : myActivities) {
                LocalDate relevantDate = activity.getStatus() == ActivityStatus.FINISHED
                        ? activity.getExecutionDate() : activity.getSchedulingDate();
                if (activity.getStatus() == status && relevantDate != null
                        && YearMonth.from(relevantDate).equals(month)) {
                    report.append(System.lineSeparator()).append(activity.getInformation());
                    found = true;
                }
            }
            if (!found) {
                report.append(System.lineSeparator()).append("None");
            }
        }
        return report.toString();
    }

    public String getAlertsReport(YearMonth month) {
        if (month == null) {
            throw new IllegalArgumentException("A reporting month is required.");
        }
        StringBuilder report = new StringBuilder("Alerts for ").append(month);
        for (AlertType type : AlertType.values()) {
            report.append(System.lineSeparator()).append(System.lineSeparator())
                    .append(type.getDescription()).append(':');
            boolean found = false;
            for (ZooAlert alert : alerts) {
                if (alert.getType() == type && YearMonth.from(alert.getCreatedAt()).equals(month)) {
                    report.append(System.lineSeparator()).append(alert.getInformation());
                    found = true;
                }
            }
            if (!found) {
                report.append(System.lineSeparator()).append("None");
            }
        }
        return report.toString();
    }

    public String getAlertsInformation() {
        if (alerts.isEmpty()) {
            return "No alerts registered.";
        }
        StringBuilder report = new StringBuilder();
        for (ZooAlert alert : alerts) {
            if (report.length() > 0) {
                report.append(System.lineSeparator());
            }
            report.append(alert.getInformation());
        }
        return report.toString();
    }

    public String getVeterinaryFollowUpReport() {
        StringBuilder report = new StringBuilder("Veterinary follow-up");
        boolean found = false;
        for (Habitat habitat : myHabitats) {
            if (habitat == null) {
                continue;
            }
            for (Animal animal : habitat.getMyAnimals()) {
                if (animal != null && isActive(animal.getStatus())
                        && isVeterinaryFollowUpStatus(animal.getHealthStatus())) {
                    report.append(System.lineSeparator()).append(animal.getId()).append(" - ")
                            .append(animal.getName()).append(" - ").append(animal.getHealthStatus())
                            .append(" - Habitat: ").append(habitat.getName());
                    found = true;
                }
            }
        }
        if (!found) {
            report.append(System.lineSeparator()).append("No animals need veterinary follow-up.");
        }
        return report.toString();
    }

    private boolean isVeterinaryFollowUpStatus(String status) {
        return status != null && ("quarantine".equalsIgnoreCase(status)
                || "recovery".equalsIgnoreCase(status)
                || "under observation".equalsIgnoreCase(status)
                || "cuarentena".equalsIgnoreCase(status)
                || "recuperación".equalsIgnoreCase(status)
                || "recuperacion".equalsIgnoreCase(status)
                || "en observación".equalsIgnoreCase(status)
                || "en observacion".equalsIgnoreCase(status));
    }

    public String getAnimalReport(String animalId, YearMonth month) {
        Animal animal = findAnimalById(animalId);
        if (animal == null || month == null) {
            return null;
        }
        StringBuilder report = new StringBuilder(animal.getInformationAnimal())
                .append(System.lineSeparator()).append("Estimated daily ration: ")
                .append(animal.calculateDailyRation()).append(" kg")
                .append(System.lineSeparator()).append("Estimated monthly feeding cost: ")
                .append(animal.calculateMonthlyRationCost()).append(" COP")
                .append(System.lineSeparator()).append("Operational feeding cost for ").append(month).append(": ")
                .append(getMonthlyOperationalFeedingCostForAnimal(month, animalId)).append(" COP")
                .append(System.lineSeparator()).append("Medical checkups for ").append(month).append(':');
        boolean found = false;
        for (Activity activity : myActivities) {
            if (activity instanceof MedicalCheckupActivity
                    && ((MedicalCheckupActivity) activity).getAnimal() == animal
                    && (YearMonth.from(activity.getSchedulingDate()).equals(month)
                            || (activity.getExecutionDate() != null
                                    && YearMonth.from(activity.getExecutionDate()).equals(month)))) {
                report.append(System.lineSeparator()).append(activity.getInformation());
                found = true;
            }
        }
        if (!found) {
            report.append(System.lineSeparator()).append("None");
        }
        return report.toString();
    }

    public double getMonthlyOperationalCost(YearMonth month) {
        return getMonthlyOperationalCost(month, null, null);
    }

    public double getMonthlyOperationalCost(YearMonth month, String habitatId, String animalId) {
        if (month == null) {
            throw new IllegalArgumentException("A reporting month is required.");
        }
        double total = 0;
        for (Activity activity : myActivities) {
            if (activity.getStatus() != ActivityStatus.FINISHED || activity.getExecutionDate() == null
                    || !YearMonth.from(activity.getExecutionDate()).equals(month)
                    || (habitatId != null && !habitatId.equals(activity.getHabitat().getId()))) {
                continue;
            }
            if (animalId != null) {
                boolean matchesAnimal = activity instanceof FeedingActivity
                        && animalId.equals(((FeedingActivity) activity).getAnimal().getId());
                matchesAnimal |= activity instanceof MedicalCheckupActivity
                        && animalId.equals(((MedicalCheckupActivity) activity).getAnimal().getId());
                if (!matchesAnimal) {
                    continue;
                }
            }
            total += activity.getCost();
        }
        return total;
    }

    public String getMonthlyCostReport(YearMonth month) {
        if (month == null) {
            throw new IllegalArgumentException("A reporting month is required.");
        }
        StringBuilder report = new StringBuilder("Operational costs for ").append(month);
        java.util.Map<String, Animal> reportedAnimals = new java.util.LinkedHashMap<String, Animal>();
        for (Habitat habitat : myHabitats) {
            if (habitat != null) {
                appendHabitatOperationalCost(report, habitat, month);
                for (Animal animal : habitat.getMyAnimals()) {
                    if (animal != null) {
                        reportedAnimals.put(animal.getId(), animal);
                    }
                }
                for (Animal animal : habitat.getWithdrawAnimals()) {
                    if (animal != null) {
                        reportedAnimals.put(animal.getId(), animal);
                    }
                }
            }
        }
        for (Habitat habitat : retiredHabitats) {
            appendHabitatOperationalCost(report, habitat, month);
            for (Animal animal : habitat.getWithdrawAnimals()) {
                if (animal != null) {
                    reportedAnimals.put(animal.getId(), animal);
                }
            }
        }
        report.append(System.lineSeparator()).append(System.lineSeparator()).append("Costs by animal:");
        if (reportedAnimals.isEmpty()) {
            report.append(System.lineSeparator()).append("None");
        }
        for (Animal animal : reportedAnimals.values()) {
            report.append(System.lineSeparator()).append(animal.getId()).append(" - ").append(animal.getName())
                    .append(": ").append(getMonthlyOperationalCost(month, null, animal.getId())).append(" COP");
        }
        report.append(System.lineSeparator()).append("Zoo budget: ").append(budget).append(" COP")
                .append(" | Zoo operational cost: ").append(getMonthlyOperationalCost(month)).append(" COP")
                .append(" | Feeding operational cost: ")
                .append(getMonthlyOperationalFeedingCost(month)).append(" COP");
        return report.toString();
    }

    private void appendHabitatOperationalCost(StringBuilder report, Habitat habitat, YearMonth month) {
        report.append(System.lineSeparator()).append(habitat.getName())
                .append(" | Budget: ").append(habitat.getBudget()).append(" COP")
                .append(" | Operational cost: ")
                .append(getMonthlyOperationalCost(month, habitat.getId(), null)).append(" COP")
                .append(" | Feeding operational cost: ")
                .append(getMonthlyOperationalFeedingCostForHabitat(month, habitat.getId())).append(" COP");
    }

    public String getAnnualActivityCostReport(int year) {
        java.util.Map<YearMonth, java.util.Map<String, Double>> monthlyTotals =
                new java.util.TreeMap<YearMonth, java.util.Map<String, Double>>();
        for (Activity activity : myActivities) {
            if (activity.getStatus() == ActivityStatus.FINISHED && activity.getExecutionDate() != null
                    && activity.getExecutionDate().getYear() == year) {
                String type = activity.getClass().getSimpleName();
                YearMonth month = YearMonth.from(activity.getExecutionDate());
                java.util.Map<String, Double> totals = monthlyTotals.get(month);
                if (totals == null) {
                    totals = new java.util.LinkedHashMap<String, Double>();
                    monthlyTotals.put(month, totals);
                }
                totals.put(type, totals.getOrDefault(type, 0.0) + activity.getCost());
            }
        }
        StringBuilder report = new StringBuilder("Activity costs for ").append(year);
        if (monthlyTotals.isEmpty()) {
            return report.append(System.lineSeparator()).append("No finished activities.").toString();
        }
        for (java.util.Map.Entry<YearMonth, java.util.Map<String, Double>> monthEntry : monthlyTotals.entrySet()) {
            report.append(System.lineSeparator()).append(System.lineSeparator()).append(monthEntry.getKey());
            for (java.util.Map.Entry<String, Double> entry : monthEntry.getValue().entrySet()) {
                report.append(System.lineSeparator()).append(entry.getKey()).append(": ")
                        .append(entry.getValue()).append(" COP");
            }
        }
        return report.toString();
    }

    public boolean finishActivity(String activityId, double cost) {
        Activity activity = findActivityById(activityId);
        return activity != null && !(activity instanceof MedicalCheckupActivity)
                && activity.finish(cost);
    }

    public boolean finishActivity(String activityId) {
        return finishActivity(activityId, 0);
    }

    public boolean finishActivityAsEmployee(String activityId, String employeeId, double cost) {
        Activity activity = findActivityById(activityId);
        Employee employee = findEmployeeEntityById(employeeId);
        return activity != null && employee != null && activity.getResponsible() == employee
                && isActive(employee.getState()) && isCorrectRole(activity, employee)
                && finishActivity(activityId, cost);
    }

    public boolean finishMedicalCheckup(String activityId, double cost, double weight, String healthStatus,
            String observations, String medication, LocalDate followUpDate) {
        Activity activity = findActivityById(activityId);
        if (!(activity instanceof MedicalCheckupActivity)) {
            return false;
        }
        MedicalCheckupActivity checkup = (MedicalCheckupActivity) activity;
        Animal animal = checkup.getAnimal();
        Habitat sourceHabitat = findAnimalHabitat(animal);
        boolean clinicRequired = isClinicRequired(healthStatus);
        Habitat clinic = clinicRequired ? getVeterinaryClinic() : null;
        Habitat destination = clinicRequired ? clinic : sourceHabitat;
        Employee responsible = checkup.getResponsible();
        if (!checkup.canComplete(cost, weight, healthStatus, observations, followUpDate)
                || sourceHabitat == null
                || responsible == null || !isActive(responsible.getState())
                || responsible.getRole() != Role.VETERINARIAN
                || (clinicRequired && (clinic == null || !isActive(clinic.getStatus())
                        || (sourceHabitat != clinic && !clinic.hasAvailableCapacity(animal.getId()))))
                || (followUpDate != null && destination == null)) {
            return false;
        }

        MedicalCheckupActivity followUp = null;
        if (followUpDate != null) {
            followUp = new MedicalCheckupActivity(CodeGenerator.generateUniqueCode(), destination,
                    responsible, followUpDate, animal, "Seguimiento médico");
        }

        boolean movedToClinic = clinicRequired && sourceHabitat != clinic;
        if (movedToClinic && !moveAnimalToHabitat(animal, clinic)) {
            return false;
        }
        if (!checkup.complete(cost, weight, healthStatus, observations, medication, followUpDate)) {
            if (movedToClinic && !moveAnimalToHabitat(animal, sourceHabitat)) {
                throw new IllegalStateException("Unable to restore the animal after an invalid medical checkup.");
            }
            return false;
        }
        if (clinicRequired) {
            cancelNonMedicalActivities(animal);
        }
        if (followUp != null) {
            myActivities.add(followUp);
        }
        return true;
    }

    public boolean cancelActivity(String activityId) {
        Activity activity = findActivityById(activityId);
        return activity != null && activity.cancel();
    }

    public boolean reassignActivity(String activityId, String employeeId) {
        Activity activity = findActivityById(activityId);
        Employee employee = findEmployeeEntityById(employeeId);
        if (activity == null || activity.getStatus() != ActivityStatus.ASSIGNED
                || employee == null || !isActive(employee.getState())
                || !isCorrectRole(activity, employee)) {
            return false;
        }
        activity.setResponsible(employee);
        return true;
    }

    private boolean isCorrectRole(Activity activity, Employee employee) {
        return activity.getRequiredRole() == employee.getRole();
    }

    public boolean assignHabitatResponsible(String habitatId, String employeeId) {
        Habitat habitat = findHabitatEntityById(habitatId);
        Employee employee = findEmployeeEntityById(employeeId);
        return habitat != null && employee != null && isActive(employee.getState())
                && habitat.addResponsibleEmployee(employee.getId());
    }

    public boolean areActiveEmployeeIds(String commaSeparatedIds) {
        if (!ZooValidation.isNonBlank(commaSeparatedIds)) {
            return false;
        }
        ArrayList<String> seen = new ArrayList<String>();
        for (String value : commaSeparatedIds.split(",")) {
            String employeeId = value.trim();
            if (!ZooValidation.isNonBlank(employeeId) || seen.contains(employeeId)
                    || findEmployeeEntityById(employeeId) == null
                    || !isActive(findEmployeeEntityById(employeeId).getState())) {
                return false;
            }
            seen.add(employeeId);
        }
        return true;
    }

    public boolean hasRegisteredEmployees() {
        if (myEmployees == null) {
            return false;
        }
        for (Employee employee : myEmployees) {
            if (employee != null) {
                return true;
            }
        }
        return false;
    }

    public boolean hasRegisteredHabitats() {
        if (myHabitats == null) {
            return false;
        }
        for (Habitat habitat : myHabitats) {
            if (habitat != null) {
                return true;
            }
        }
        return false;
    }

    public boolean removeHabitatResponsible(String habitatId, String employeeId) {
        Habitat habitat = findHabitatEntityById(habitatId);
        return habitat != null && ZooValidation.isNonBlank(employeeId)
                && habitat.removeResponsibleEmployee(employeeId.trim());
    }

    public String getHabitatResponsibleList(String habitatId) {
        Habitat habitat = findHabitatEntityById(habitatId);
        if (habitat == null) {
            return null;
        }
        StringBuilder information = new StringBuilder();
        for (String employeeId : habitat.getResponsibleEmployeeIds()) {
            Employee employee = findEmployeeEntityById(employeeId);
            if (information.length() > 0) {
                information.append(System.lineSeparator());
            }
            information.append(employeeId).append(" - ")
                    .append(employee == null ? "Employee record unavailable" : employee.getFullName());
        }
        return information.length() == 0 ? "No responsible employees are assigned to this habitat."
                : information.toString();
    }

    public String getHabitatIdByName(String habitatName) {
        if (habitatName == null) {
            return null;
        }
        for (Habitat habitat : myHabitats) {
            if (habitat != null && habitatName.equalsIgnoreCase(habitat.getName())) {
                return habitat.getId();
            }
        }
        return null;
    }

    public boolean reassignHabitatResponsible(String habitatId, String oldEmployeeId, String newEmployeeId) {
        Habitat habitat = findHabitatEntityById(habitatId);
        Employee employee = findEmployeeEntityById(newEmployeeId);
        return habitat != null && employee != null && isActive(employee.getState())
                && habitat.reassignResponsibleEmployee(oldEmployeeId, newEmployeeId);
    }

    public boolean deactivateEmployee(String employeeId) {
        Employee employee = findEmployeeEntityById(employeeId);
        if (employee == null || hasAssignedActivitiesForEmployee(employee)) {
            return false;
        }
        for (Habitat habitat : myHabitats) {
            if (habitat != null && habitat.hasResponsibleEmployee(employeeId)) {
                return false;
            }
        }
        employee.setState("inactivo");
        return true;
    }

    private boolean hasAssignedActivitiesForEmployee(Employee employee) {
        for (Activity activity : myActivities) {
            if (activity.getResponsible() == employee && activity.getStatus() == ActivityStatus.ASSIGNED) {
                return true;
            }
        }
        return false;
    }

    private Habitat findHabitatEntityById(String habitatId) {
        if (habitatId == null) {
            return null;
        }
        for (Habitat habitat : myHabitats) {
            if (habitat != null && habitatId.equals(habitat.getId())) {
                return habitat;
            }
        }
        return null;
    }

    private Activity findActivityById(String activityId) {
        if (activityId == null) {
            return null;
        }
        for (Activity activity : myActivities) {
            if (activityId.equals(activity.getId())) {
                return activity;
            }
        }
        return null;
    }

    private Animal findAnimalById(String animalId) {
        if (animalId == null) {
            return null;
        }
        for (Habitat habitat : myHabitats) {
            if (habitat == null) {
                continue;
            }
            Animal animal = habitat.findAnimalEntityById(animalId);
            if (animal != null) {
                return animal;
            }
            for (Animal withdrawn : habitat.getWithdrawAnimals()) {
                if (withdrawn != null && animalId.equals(withdrawn.getId())) {
                    return withdrawn;
                }
            }
        }
        for (Habitat habitat : retiredHabitats) {
            for (Animal withdrawn : habitat.getWithdrawAnimals()) {
                if (withdrawn != null && animalId.equals(withdrawn.getId())) {
                    return withdrawn;
                }
            }
        }
        return null;
    }

    private boolean hasActivitiesForHabitat(Habitat habitat) {
        for (Activity activity : myActivities) {
            if (activity.getHabitat() == habitat) {
                return true;
            }
        }
        return false;
    }

    private boolean hasAssignedActivitiesForHabitat(Habitat habitat) {
        for (Activity activity : myActivities) {
            if (activity.getHabitat() == habitat && activity.getStatus() == ActivityStatus.ASSIGNED) {
                return true;
            }
        }
        return false;
    }

    private boolean isActive(String status) {
        return status != null && ("activo".equalsIgnoreCase(status.trim())
                || "active".equalsIgnoreCase(status.trim()));
    }

    private boolean isValidWithdrawalReason(String reason) {
        return reason != null && ("fallecimiento".equalsIgnoreCase(reason.trim())
                || "traslado a otro zoológico".equalsIgnoreCase(reason.trim())
                || "traslado a otro zoologico".equalsIgnoreCase(reason.trim())
                || "death".equalsIgnoreCase(reason.trim())
                || "transfer to another zoo".equalsIgnoreCase(reason.trim()));
    }

    private boolean isMaintenance(String status) {
        return status != null && ("en mantenimiento".equalsIgnoreCase(status.trim())
                || "maintenance".equalsIgnoreCase(status.trim()));
    }

    private boolean isUnderConstruction(String status) {
        return status != null && ("en construcción".equalsIgnoreCase(status.trim())
                || "en construccion".equalsIgnoreCase(status.trim())
                || "under construction".equalsIgnoreCase(status.trim()));
    }

    private boolean isRetired(String status) {
        return status != null && ("retirado".equalsIgnoreCase(status.trim())
                || "retired".equalsIgnoreCase(status.trim()));
    }

    private boolean isClinicRequired(String healthStatus) {
        return healthStatus != null && ("quarantine".equalsIgnoreCase(healthStatus.trim())
                || "recovery".equalsIgnoreCase(healthStatus.trim())
                || "cuarentena".equalsIgnoreCase(healthStatus.trim())
                || "recuperación".equalsIgnoreCase(healthStatus.trim())
                || "recuperacion".equalsIgnoreCase(healthStatus.trim()));
    }

    private Habitat getVeterinaryClinic() {
        for (Habitat habitat : myHabitats) {
            if (habitat != null && habitat.isVeterinaryClinic()) {
                return habitat;
            }
        }
        return null;
    }

    private void registerAlert(AlertType type, String entityId, String description) {
        alerts.add(new ZooAlert(type, entityId, description));
    }

    private void registerOccupancyAlertIfNeeded(Habitat habitat, int previousOccupation) {
        if (habitat == null || habitat.getCapacity() <= 0
                || previousOccupation * 100.0 / habitat.getCapacity() >= 90
                || habitat.getPercentualOccupation() < 90) {
            return;
        }
        registerAlert(AlertType.OCCUPANCY, habitat.getId(),
                "Habitat occupation reached " + habitat.getPercentualOccupation() + "%.");
    }

    public String getOccupancyWarning(String habitatId) {
        Habitat habitat = findHabitatEntityById(habitatId);
        return habitat != null && habitat.getPercentualOccupation() >= 90
                ? "Warning: " + habitat.getName() + " has reached " + habitat.getPercentualOccupation()
                        + "% occupation."
                : null;
    }

    private void cancelNonMedicalActivities(Animal animal) {
        for (Activity activity : myActivities) {
            if (activity.getAnimalId() != null
                    && activity.getAnimalId().equals(animal.getId())
                    && !activity.isMedical()
                    && activity.getStatus() == ActivityStatus.ASSIGNED) {
                activity.cancel();
            }
        }
    }

    private void cancelAssignedActivitiesForAnimal(Animal animal) {
        for (Activity activity : myActivities) {
            if (activity.getAnimalId() != null
                    && activity.getAnimalId().equals(animal.getId())
                    && activity.getStatus() == ActivityStatus.ASSIGNED) {
                activity.cancel();
            }
        }
    }

    private void cancelAssignedActivitiesForHabitat(Habitat habitat) {
        for (Activity activity : myActivities) {
            if (activity.getHabitat() == habitat && activity.getStatus() == ActivityStatus.ASSIGNED) {
                activity.cancel();
            }
        }
    }

    private boolean moveAnimalToHabitat(Animal animal, Habitat targetHabitat) {
        if (animal == null || targetHabitat == null || !isActive(targetHabitat.getStatus())
                || !targetHabitat.hasAvailableCapacity(animal.getId())) {
            return false;
        }
        Habitat sourceHabitat = findHabitatEntityById(animal.getHabitat());
        if (sourceHabitat == null) {
            sourceHabitat = findAnimalHabitat(animal);
        }
        if (sourceHabitat == targetHabitat) {
            return true;
        }
        int previousOccupation = targetHabitat.getTotalOccupation();
        if (sourceHabitat == null || !sourceHabitat.removeAnimalForTransfer(animal)) {
            return false;
        }
        if (!targetHabitat.addAnimal(animal)) {
            if (!sourceHabitat.addAnimal(animal)) {
                throw new IllegalStateException("Unable to restore the animal to its original habitat.");
            }
            return false;
        }
        animal.setHabitat(targetHabitat.getId());
        registerOccupancyAlertIfNeeded(targetHabitat, previousOccupation);
        return true;
    }

    private Habitat findAnimalHabitat(Animal animal) {
        for (Habitat habitat : myHabitats) {
            if (habitat != null && habitat.hasAnimal(animal.getId())) {
                return habitat;
            }
        }
        return null;
    }

    public boolean addHabitat(String name, int environmentOption, double temperature, double area,
            double budget, String status, int capacity, String responsible) {
        EnvironmentType environment = calculateEnvironmentType(environmentOption);
        return environment != null && addHabitat(name, environment, temperature, area,
                budget, status, capacity, responsible);
    }

   /**
     * Checks whether the zoo has at least one registered habitat.
     *
     * Preconditions:
    * - 'myHabitats' list should be initialized.
     *
     * Postconditions:
     * - Returns true if at least one non-null habitat exists in 'myHabitats'; otherwise false.
     *
     * @return true if at least one habitat is registered; otherwise, false
     */
    public boolean hasAvaliableHabitat() {
        if (myHabitats != null) {
            for (int i = 0; i < myHabitats.size(); i++) {
                if (myHabitats.get(i) != null) {
                    return true;
                }
            }
        }
        return false;
    }

 /**
     * Returns a list of all registered habitats formatted as "id-environment".
     *
     * Preconditions:
    * - 'myHabitats' list should be initialized.
     *
     * Postconditions:
     * - Returns a formatted string listing all habitats, or null if no habitats exist.
     *
     * @return the formatted list of "id-environment" for each habitat, or null if empty
     */
    public String getHabitatList() {
        String habitatList = "";
        if (myHabitats != null) {
            for (int i = 0; i < myHabitats.size(); i++) {
                if (myHabitats.get(i) != null) {
                    Habitat habitat = myHabitats.get(i);
                    habitatList += "\n" + habitat.getId() + "-" + habitat.getName()
                            + "-" + habitat.getEnvironment().getTypeName()
                            + "-" + habitat.getArea() + " m2"
                            + "-capacity " + habitat.getCapacity()
                            + "-occupation " + habitat.getTotalOccupation()
                            + " (" + habitat.getPercentualOccupation() + "%)";
                }
            }
        }
        if (habitatList.equals("")) {
            return null;
        }
        return habitatList;
    }

    /**
     * Returns a list of all animals in the zoo across all habitats.
     *
     * Preconditions:
     * - None.
     *
     * Postconditions:
     * - Returns formatted string containing "id-name" for each animal in all active habitats.
     *
     * @return the formatted list of "id-name" for each animal
     */
    public String getAnimalListInHabitat() {
        return getAnimalList();
    }

    /**
     * Concatenates animal lists from all active habitats.
     *
     * Preconditions:
    * - 'myHabitats' list should be initialized.
     *
     * Postconditions:
     * - Returns formatted string containing "id-name" for each animal across all non-null habitats.
     * - Does not modify internal zoo or habitat state.
     *
     * @return formatted string containing "id-name" for each animal
     */
    public String getAnimalList() {
        String animalList = "";
        for (int i = 0; i < myHabitats.size(); i++) {
            if (myHabitats.get(i) != null) {
                animalList += myHabitats.get(i).getAnimalList();
            }
        }
        return animalList;
    }

    public String getActiveAnimalSummary() {
        StringBuilder summary = new StringBuilder();
        for (Habitat habitat : myHabitats) {
            if (habitat == null) {
                continue;
            }
            for (Animal animal : habitat.getMyAnimals()) {
                if (animal != null && isActive(animal.getStatus())) {
                    if (summary.length() > 0) {
                        summary.append(System.lineSeparator());
                    }
                    summary.append(animal.getId()).append(" | ").append(animal.getName())
                            .append(" | ").append(animal.getSpecie())
                            .append(" | Habitat: ").append(habitat.getName())
                            .append(" | Diet: ").append(animal.getDietType().getText())
                            .append(" | Health: ").append(animal.getHealthStatus());
                }
            }
        }
        return summary.length() == 0 ? "No active animals registered." : summary.toString();
    }

    /**
     * Retrieves the monthly food cost for a specific animal in the zoo.
     *
     * Preconditions:
     * - 'id' is a valid animal identifier string.
     *
     * Postconditions:
     * - Returns calculated monthly food cost if animal is found.
     *
     * Error Handling:
     * - Returns -1 if animal is not found in any habitat.
     *
     * @param id the animal identifier
     * @return the animal's monthly food cost, or -1 if it is not found
     */
    public double getMonthlyFoodCostFromAnimal(String id) {
        for (int i = 0; i < myHabitats.size(); i++) {
            if (myHabitats.get(i) != null) {
                double cost = myHabitats.get(i).getAnimalMonthlyFoodCost(id);
                if (cost >= 0) {
                    return cost;
                }
            }
        }
        return -1;
    }

    public double getMonthlyOperationalFeedingCost(YearMonth month) {
        if (month == null) {
            throw new IllegalArgumentException("A reporting month is required.");
        }
        double total = 0;
        for (Activity activity : myActivities) {
            if (activity instanceof FeedingActivity
                    && activity.getStatus() == ActivityStatus.FINISHED
                    && activity.getExecutionDate() != null
                    && YearMonth.from(activity.getExecutionDate()).equals(month)) {
                total += activity.getCost();
            }
        }
        return total;
    }

    public double getMonthlyOperationalFeedingCostForAnimal(YearMonth month, String animalId) {
        if (month == null || !ZooValidation.isNonBlank(animalId)) {
            throw new IllegalArgumentException("A reporting month and animal ID are required.");
        }
        double total = 0;
        for (Activity activity : myActivities) {
            if (activity instanceof FeedingActivity
                    && activity.getStatus() == ActivityStatus.FINISHED
                    && activity.getExecutionDate() != null
                    && YearMonth.from(activity.getExecutionDate()).equals(month)
                    && animalId.equals(((FeedingActivity) activity).getAnimal().getId())) {
                total += activity.getCost();
            }
        }
        return total;
    }

    public double getMonthlyOperationalFeedingCostForHabitat(YearMonth month, String habitatId) {
        if (month == null || !ZooValidation.isNonBlank(habitatId)) {
            throw new IllegalArgumentException("A reporting month and habitat ID are required.");
        }
        double total = 0;
        for (Activity activity : myActivities) {
            if (activity instanceof FeedingActivity
                    && activity.getStatus() == ActivityStatus.FINISHED
                    && activity.getExecutionDate() != null
                    && YearMonth.from(activity.getExecutionDate()).equals(month)
                    && habitatId.equals(activity.getHabitat().getId())) {
                total += activity.getCost();
            }
        }
        return total;
    }

    /**
     * Retrieves detailed information string for an animal specified by ID.
     *
     * Preconditions:
     * - 'id' must be non-null.
     *
     * Postconditions:
     * - Returns formatted animal information string if found.
     *
     * Error Handling:
     * - Returns null if animal is not found.
     *
     * @param id animal identifier to search for
     * @return animal information string, or null if not found
     */
    public String getAnimalInformationById(String id) {
        Animal animal = findAnimalById(id);
        return animal == null ? null : animal.getInformationAnimal();
    }

    public String getAnimalHabitatId(String animalId) {
        Animal animal = findAnimalEntityByIdInHabitat(animalId);
        return animal == null ? null : animal.getHabitat();
    }

    /**
     * Updates a specific attribute of an animal given its ID and option code.
     *
     * Preconditions:
     * - Animal with 'id' exists.
     *
     * Postconditions:
     * - Updates animal's field according to selected option code and returns true.
     *
     * Error Handling:
     * - Returns false if animal is not found or option is invalid.
     *
     * @param id animal identifier
     * @param option numerical option code specifying field to update
     * @param value new attribute value string
     * @return true if update succeeded; false otherwise
     */
    public boolean updateAnimal(String id, int option, String value) {
        if (option == 14) {
            return false;
        }
        if (option == 11) {
            Animal animal = findAnimalEntityByIdInHabitat(id);
            if (animal == null || !ZooValidation.isNonBlank(value)) {
                return false;
            }
            String previousStatus = animal.getHealthStatus();
            animal.setHealthStatus(value.trim());
            if (isClinicRequired(value)) {
                Habitat clinic = getVeterinaryClinic();
                if (clinic == null || !isActive(clinic.getStatus())) {
                    animal.setHealthStatus(previousStatus);
                    registerAlert(AlertType.INVALID_HABITAT_STATUS,
                            clinic == null ? null : clinic.getId(),
                            "Unable to transfer an animal requiring quarantine/recovery because the clinic is unavailable.");
                    return false;
                }
                if (!clinic.hasAnimal(animal.getId()) && !clinic.hasAvailableCapacity(null)) {
                    animal.setHealthStatus(previousStatus);
                    registerAlert(AlertType.INSUFFICIENT_CAPACITY, clinic.getId(),
                            "Unable to transfer an animal requiring quarantine/recovery because the clinic is full.");
                    return false;
                }
                if (!moveAnimalToHabitat(animal, clinic)) {
                    animal.setHealthStatus(previousStatus);
                    return false;
                }
                cancelNonMedicalActivities(animal);
            }
            return true;
        }
        for (int i = 0; i < myHabitats.size(); i++) {
            if (myHabitats.get(i) != null && myHabitats.get(i).updateAnimal(id, option, value)) {
                return true;
            }
        }
        return false;
    }

    public boolean withdrawAnimalById(String animalId, String date, String reason) {
        Animal animal = findAnimalEntityByIdInHabitat(animalId);
        return animal != null && withdrawAnimalInHabitat(animal.getHabitat(), animalId, date, reason);
    }

    /**
     * Returns the animals currently assigned to a given habitat.
     *
     * Preconditions:
     * - 'habitatId' corresponds to an existing habitat.
     *
     * Postconditions:
     * - Returns formatted list of "id-name" for each animal in the specified habitat, or empty string if not found.
     *
     * @param habitatId the habitat id to look up
     * @return the formatted list of "id-name" for each animal in the habitat
     */
    public String getAnimalListByHabitat(String habitatId) {
        for (int i = 0; i < myHabitats.size(); i++) {
            if (myHabitats.get(i) != null && habitatId != null
                    && habitatId.equals(myHabitats.get(i).getId())) {
                String animalList = myHabitats.get(i).getAnimalList();
                return animalList;
            }
        }
        return "";
    }

    /**
     * Removes an animal from a habitat, leaving its slot empty.
     *
     * Preconditions:
     * - Habitat with 'habitatId' and animal with 'animalId' exist.
     *
     * Postconditions:
     * - Animal is archived and removed from habitat list.
     *
     * @param habitatId the habitat id where the animal is registered
     * @param animalId  the animal id to remove
     * @return true if the animal was found and removed; otherwise, false
     */
    public boolean removeAnimalFromHabitat(String habitatId, String animalId) {
        for (int i = 0; i < myHabitats.size(); i++) {
            if (myHabitats.get(i) != null && habitatId != null
                    && habitatId.equals(myHabitats.get(i).getId())) {
                boolean animalRemoved = myHabitats.get(i).removeAnimal(animalId);
                return animalRemoved;
            }
        }
        return false;
    }

    /**
     * Returns information on animals that were withdrawn from a given habitat.
     *
     * Preconditions:
     * - 'habitatId' must correspond to an existing habitat.
     *
     * Postconditions:
     * - Returns detailed information string for withdrawn animals in the specified habitat, or empty string if none/not found.
     *
     * @param habitatId the habitat id to look up
     * @return the complete formatted information for each withdrawn animal
     */
    public String getWithdrawAnimalListByHabitat(String habitatId) {
        for (int i = 0; i < myHabitats.size(); i++) {
            if (myHabitats.get(i) != null && habitatId != null
                    && habitatId.equals(myHabitats.get(i).getId())) {

                String withdrawedAnimal = myHabitats.get(i).getWithdrawAnimalInformation();
                return withdrawedAnimal;
            }
        }
        return "";
    }

    /**
     * Creates and adds a new animal to a designated habitat in the zoo.
     *
     * Preconditions:
     * - Habitat with matching ID exists in 'myHabitats'.
     *
     * Postconditions:
     * - Animal is created and added to target habitat's animal list if capacity permits.
     *
     * Error Handling:
     * - Returns false if target habitat is not found or full.
     *
     * @param name          the animal name
     * @param specie        the animal species
     * @param environment   the environment required by the animal
     * @param sex           the animal sex
     * @param birth         the birth date
     * @param weight        the animal weight
     * @param typeDiet      the diet type
     * @param countryOrigin the country of origin
     * @param entryDate     the date of entry to the zoo
     * @param lifeCycle     the life cycle stage
     * @param healthStatus  the health status
     * @param habitat       the assigned habitat identifier
     * @param status        the current animal status
     * @param withdrawDate  withdrawal date if applicable
     * @param withdrawReason withdrawal reason if applicable
     * @return true if the animal is added; otherwise, false
     */
    public boolean addAnimalInHabitat(String name, String specie, EnvironmentType environment,
            String sex, String birth, double weight, String typeDiet, String countryOrigin,
            String entryDate, String lifeCycle, String healthStatus, String habitat,
            String status, String withdrawDate, String withdrawReason) {

        if (!ZooValidation.isNonBlank(name) || !ZooValidation.isNonBlank(specie)
                || !ZooValidation.isBirthBeforeEntry(birth, entryDate)
                || !ZooValidation.isDateNotAfterToday(birth)
                || !ZooValidation.isDateNotAfterToday(entryDate)
                || !ZooValidation.isPositiveFinite(weight) || environment == null
                || !isActive(status) || myHabitats == null
                || !zooProfileConfigured || !hasRegisteredEmployees()) {
            return false;
        }

        for (int i = 0; i < myHabitats.size(); i++) {
            if (myHabitats.get(i) != null && myHabitats.get(i).getId().equals(habitat)) {
                Habitat target = myHabitats.get(i);
                if (validateAnimalTarget(environment, habitat, healthStatus, true) != null) {
                    return false;
                }

                Animal myAnimal = new Animal(name, specie, environment, sex, birth, weight, typeDiet,
                        countryOrigin, entryDate, lifeCycle, healthStatus, habitat, status, withdrawDate,
                        withdrawReason);

                int previousOccupation = target.getTotalOccupation();
                boolean animalAdded = target.addAnimal(myAnimal);
                if (animalAdded) {
                    registerOccupancyAlertIfNeeded(target, previousOccupation);
                }
                return animalAdded;

            }
        }

        validateAnimalTarget(environment, habitat, healthStatus, true);
        return false;
    }

    public boolean addAnimalInHabitat(String name, String specie, int environmentOption,
            String sex, String birth, double weight, String typeDiet, String countryOrigin,
            String entryDate, String lifeCycle, String healthStatus, String habitat,
            String status, String withdrawDate, String withdrawReason) {
        EnvironmentType environment = calculateEnvironmentType(environmentOption);
        return environment != null && addAnimalInHabitat(name, specie, environment, sex, birth, weight,
                typeDiet, countryOrigin, entryDate, lifeCycle, healthStatus, habitat, status,
                withdrawDate, withdrawReason);
    }

    /**
     * Registers a new employee member in the zoo.
     *
     * Preconditions:
    * - The zoo profile is configured and employee details are valid.
     *
     * Postconditions:
    * - Creates and appends an Employee object to 'myEmployees'.
     *
     * Error Handling:
    * - Returns false if the employee limit is reached.
     *
     * @param fullName the employee member full name
     * @param role     the employee role
     * @param phone    the phone number
     * @param mail     the email address
     * @param state    the current state
     * @return true if the employee member is added; otherwise, false
     */
    public boolean addEmployee(String fullName, String role, String phone, String mail, String state) {
        Role employeeRole = Role.fromName(role);
        if (!zooProfileConfigured || !ZooValidation.isNonBlank(fullName) || employeeRole == null
                || !ZooValidation.isNonBlank(phone) || !ZooValidation.isNonBlank(mail)
                || !ZooValidation.isNonBlank(state)) {
            return false;
        }
        myEmployees.add(new Employee(fullName.trim(), employeeRole, phone.trim(), mail.trim(), state.trim()));
        return true;
    }

    /**
     * Updates the environment of an animal identified by ID.
     *
     * Preconditions:
     * - The animal is registered in one of the zoo's habitats.
     * - 'environmentOption' is a 1-based option for an EnvironmentType value.
     *
     * Postconditions:
     * - If the animal and option are valid, the animal's environment is updated.
     *
     * Error Handling:
     * - Returns false if the option is invalid or no animal with the given ID is found.
     *
     * @param id               identifier of the animal to update
     * @param environmentOption 1-based option for the new environment
     * @return true if the animal's environment is updated; otherwise, false
     */
    public boolean updateAnimalEnvironment(String id, int environmentOption) {
        EnvironmentType environment = calculateEnvironmentType(environmentOption);
        Animal animal = findAnimalEntityByIdInHabitat(id);
        if (environment == null || animal == null) {
            return false;
        }
        animal.setEnvironment(environment);
        return true;
    }

    // Analyzers

    /**
     * Searches for an animal entity across all registered habitats by ID.
     *
     * Preconditions:
     * - 'id' is non-null.
     *
     * Postconditions:
     * - Returns matching Animal instance if found, or null otherwise.
     *
     * @param id the animal id to search for
     * @return the matching animal, or null if no match exists
     */
    private Animal findAnimalEntityByIdInHabitat(String id) {
        for (int i = 0; i < myHabitats.size(); i++) {
            if (myHabitats.get(i) != null) {

                Animal myAnimal = myHabitats.get(i).findAnimalEntityById(id);

                if (myAnimal != null) {
                    return myAnimal;

                }

            }
        }

        return null;
    }

    /**
    * Displays employees whose state is active.
     *
     * Preconditions:
     * - None.
     *
     * Postconditions:
    * - Returns formatted employee information, or a notice message if none are found.
     *
    * @return formatted employee information, or a notice message if none are found
     */
    public String getActiveEmployeeInformation() {
        StringBuilder information = new StringBuilder();
        for (Employee employee : myEmployees) {
            if ("activo".equalsIgnoreCase(employee.getState())) {
                information.append(employee.getInformation()).append(System.lineSeparator());
            }
        }
        return information.length() == 0 ? "No active employees registered" : information.toString();
    }

    /**
     * Displays animals whose status is active across all habitats.
     *
     * Preconditions:
    * - 'myHabitats' list should be initialized.
     *
     * Postconditions:
     * - Returns formatted string of active animals, or notice message if none found.
     *
     * @return formatted string of active animals, or notice message if none found
     */
    public String getActiveAnimalInformation() {
        String activeAnimalsInformation = "";
        for (int i = 0; i < myHabitats.size(); i++) {
            if (myHabitats.get(i) != null) {
                activeAnimalsInformation += myHabitats.get(i).getActiveAnimalInformation();
            }
        }

        if (activeAnimalsInformation.isEmpty()) {
            return "No active animals registered";
        }
        return activeAnimalsInformation;
    }

    /**
     * Retrieves habitat information and percentage occupation by habitat ID.
     *
     * Preconditions:
     * - 'id' must not be null.
     *
     * Postconditions:
     * - Returns formatted habitat information with occupation percentage, or null if not found.
     *
     * @param id habitat identifier
     * @return formatted habitat information with occupation percentage, or null if not found
     */
    public String getHabitatInformationById(String id) {
        for (int i = 0; i < myHabitats.size(); i++) {
            if (myHabitats.get(i) != null && id != null && id.equals(myHabitats.get(i).getId())) {
                return myHabitats.get(i).getInformation() + " Occupation: "
                        + myHabitats.get(i).getPercentualOccupation() + "%";
            }
        }
        for (Habitat habitat : retiredHabitats) {
            if (id != null && id.equals(habitat.getId())) {
                return habitat.getInformation() + " Occupation: " + habitat.getPercentualOccupation() + "%";
            }
        }
        return null;
    }

    /**
     * Retrieves employee member information by employee ID.
     *
     * Preconditions:
     * - 'id' must not be null.
     *
     * Postconditions:
     * - Returns employee member information string, or null if not found.
     *
     * @param id employee member identifier
     * @return employee information string, or null if not found
     */
    public String getEmployeeInformationById(String id) {
        for (int i = 0; i < myEmployees.size(); i++) {
            Employee employee = myEmployees.get(i);
            if (employee != null && id != null && id.equals(employee.getId())) {
                return employee.getInformation();
            }
        }
        return null;
    }

    /**
    * Returns a basic list of employees formatted as "id-fullName".
     *
     * Preconditions:
    * - 'myEmployees' list should be initialized.
     *
     * Postconditions:
    * - Returns concatenated string containing IDs and names of registered employees.
     *
     * @return string containing employee IDs and names
     */
    public String getEmployeeList() {
        String information = "";
        for (int i = 0; i < myEmployees.size(); i++) {
            if (myEmployees.get(i) != null) {
                information += myEmployees.get(i).getId() + "-" + myEmployees.get(i).getFullName() + " ";
            }
        }
        return information;
    }

    /**
     * Maps a 1-based integer selection option to its corresponding EnvironmentType enum value.
     *
     * Preconditions:
     * - None. The option is checked against the available EnvironmentType values.
     *
     * Postconditions:
     * - Returns the EnvironmentType at the specified 1-based position, or null if
     *   the option is outside the valid range.
     *
     * @param environmentOption 1-based index option selected
     * @return the matching EnvironmentType, or null if the option is invalid
     */
    public EnvironmentType calculateEnvironmentType(int environmentOption) {

        EnvironmentType[] values = EnvironmentType.values();
        if (environmentOption < 1 || environmentOption > values.length) {
            return null;
        }
        return values[environmentOption - 1];
    }

    public boolean isValidEnvironmentOption(int environmentOption) {
        return calculateEnvironmentType(environmentOption) != null;
    }

    /**
     * Returns estimated monthly food cost information for each registered habitat.
     *
     * Preconditions:
    * - 'myHabitats' list should be initialized.
     *
     * Postconditions:
     * - Returns formatted string containing habitat IDs and calculated food costs.
     *
     * @return formatted string containing habitat IDs and calculated food costs
     */
    public String getHabitatFoodCostInformation() {
        String information = "";

        for (int i = 0; i < myHabitats.size(); i++) {
            if (myHabitats.get(i) != null) {
                information += "Estimated monthly animal feeding cost for habitat "
                        + myHabitats.get(i).getId() + ": " + myHabitats.get(i).getMonthlyFoodCost()
                        + System.lineSeparator();
            }
        }
        return information;
    }

    /**
     * Updates a field of a employee member identified by ID using an option code.
     *
     * Preconditions:
     * - Employee member with 'id' exists.
     *
     * Postconditions:
     * - Modifies employee member field according to option code and returns true.
     *
     * Error Handling:
     * - Returns false if option code is invalid or employee member is not found.
     *
     * @param id employee member identifier
     * @param option numerical option code specifying field to update
     * @param value new field value string
     * @return true if update succeeded; false otherwise
     */
    public boolean updateEmployee(String id, int option, String value) {
        Employee employee = findEmployeeEntityById(id);
        if (employee != null && ZooValidation.isNonBlank(value)) {
            value = value.trim();
            switch (option) {
                case 2:
                    employee.setFullName(value);
                    break;
                case 3:
                    Role role = Role.fromName(value);
                    if (role == null) {
                        return false;
                    }
                    employee.setRole(role);
                    break;
                case 4:
                    employee.setPhone(value);
                    break;
                case 5:
                    employee.setMail(value);
                    break;
                case 6:
                    if ("inactivo".equalsIgnoreCase(value) || "inactive".equalsIgnoreCase(value)) {
                        return deactivateEmployee(id);
                    }
                    if (!"activo".equalsIgnoreCase(value) && !"active".equalsIgnoreCase(value)) {
                        return false;
                    }
                    employee.setState("activo");
                    break;
                default:
                    return false;
            }
            return true;
        }
        return false;
    }

    /**
     * Updates a field of a habitat identified by ID using an option code.
     *
     * Preconditions:
     * - Habitat with 'id' exists.
     *
     * Postconditions:
     * - Modifies habitat field according to option code and returns true.
     *
     * Error Handling:
     * - Returns false if option code is invalid or habitat is not found.
     *
     * @param id habitat identifier
     * @param option numerical option code specifying field to update
     * @param value new field value string
     * @return true if update succeeded; false otherwise
     */
    public boolean updateHabitat(String id, int option, String value) {
        if (value == null) {
            return false;
        }
        for (int i = 0; i < myHabitats.size(); i++) {
            if (myHabitats.get(i) != null && id != null && id.equals(myHabitats.get(i).getId())) {
                if (myHabitats.get(i).isVeterinaryClinic()) {
                    return false;
                }
                if (isRetired(myHabitats.get(i).getStatus())) {
                    return false;
                }
                switch (option) {
                    case 2:
                        return false;
                    case 3:
                        EnvironmentType environment;
                        try {
                            environment = EnvironmentType.valueOf(value.trim().toUpperCase());
                        } catch (IllegalArgumentException exception) {
                            return false;
                        }
                        if (myHabitats.get(i).hasAnimals()
                                && environment != myHabitats.get(i).getEnvironment()) {
                            return false;
                        }
                        myHabitats.get(i).setEnvironment(environment);
                        break;
                    case 4:
                        try {
                            double temperature = Double.parseDouble(value);
                            if (!ZooValidation.isValidTemperature(temperature)) {
                                return false;
                            }
                            myHabitats.get(i).setTemperature(temperature);
                        } catch (NumberFormatException exception) {
                            return false;
                        }
                        break;
                    case 5:
                        try {
                            double area = Double.parseDouble(value);
                            Habitat habitat = myHabitats.get(i);
                            String newHabitatName = getHabitatNameForArea(area);
                            if (newHabitatName == null || !zooMap.updateHabitatArea(habitat, area)) {
                                return false;
                            }
                            habitat.setArea(area);
                            habitat.setName(newHabitatName);
                            zooMap.updateHabitatName(habitat);
                        } catch (NumberFormatException exception) {
                            return false;
                        }
                        break;
                    case 6:
                        double budget;
                        try {
                            budget = Double.parseDouble(value);
                        } catch (NumberFormatException exception) {
                            return false;
                        }
                        if (!ZooValidation.isNonNegativeFinite(budget)) {
                            return false;
                        }
                        myHabitats.get(i).setBudget(budget);
                        break;
                    case 7:
                        String status = value.trim();
                        boolean retirementStatus = isRetired(status);
                        boolean inactiveStatus = isMaintenance(status) || isUnderConstruction(status)
                                || retirementStatus;
                        if (!isActive(status) && !isMaintenance(status) && !isUnderConstruction(status)
                                && !retirementStatus) {
                            return false;
                        }
                        if (inactiveStatus && myHabitats.get(i).hasAnimals()) {
                            return false;
                        }
                        if (retirementStatus) {
                            return removeHabitat(id);
                        }
                        myHabitats.get(i).setStatus(status);
                        break;
                    case 8:
                        int capacity;
                        try {
                            capacity = Integer.parseInt(value);
                        } catch (NumberFormatException exception) {
                            return false;
                        }
                        if (capacity <= 0 || capacity < myHabitats.get(i).getTotalOccupation()) {
                            return false;
                        }
                        double previousOccupationPercentage = myHabitats.get(i).getPercentualOccupation();
                        myHabitats.get(i).setCapacity(capacity);
                        if (previousOccupationPercentage < 90
                                && myHabitats.get(i).getPercentualOccupation() >= 90) {
                            registerAlert(AlertType.OCCUPANCY, myHabitats.get(i).getId(),
                                    "Habitat occupation reached " + myHabitats.get(i).getPercentualOccupation() + "%.");
                        }
                        break;
                    default:
                        return false;

                }
                return true;
            }
        }
        return false;
    }

    /**
     * Updates the environment of a habitat identified by ID.
     *
     * Preconditions:
     * - A habitat with the given ID is registered in the zoo.
     * - 'environmentOption' is a 1-based option for an EnvironmentType value.
     *
     * Postconditions:
     * - If the habitat and option are valid, the habitat's environment is updated.
     *
     * Error Handling:
     * - Returns false if the option is invalid, the ID is null, or no habitat with
     *   the given ID is found.
     *
     * @param id               identifier of the habitat to update
     * @param environmentOption 1-based option for the new environment
     * @return true if the habitat's environment is updated; otherwise, false
     */
    public boolean updateHabitatEnvironment(String id, int environmentOption) {
        EnvironmentType environment = calculateEnvironmentType(environmentOption);
        if (environment == null || id == null) {
            return false;
        }
        for (int i = 0; i < myHabitats.size(); i++) {
            if (myHabitats.get(i) != null && id.equals(myHabitats.get(i).getId())) {
                if (myHabitats.get(i).isVeterinaryClinic()) {
                    return false;
                }
                if (isRetired(myHabitats.get(i).getStatus())) {
                    return false;
                }
                if (myHabitats.get(i).hasAnimals() && environment != myHabitats.get(i).getEnvironment()) {
                    registerAlert(AlertType.ENVIRONMENT_INCOMPATIBILITY, myHabitats.get(i).getId(),
                            "Habitat environment cannot change while animals with incompatible requirements are assigned.");
                    return false;
                }
                myHabitats.get(i).setEnvironment(environment);
                return true;
            }
        }
        return false;
    }

    /**
     * Withdraws an animal from a specified habitat with date and reason.
     *
     * Preconditions:
     * - Target habitat and animal exist.
     *
     * Postconditions:
     * - Animal is marked as withdrawn and moved to habitat archive.
     *
     * @param habitatId target habitat identifier
     * @param animalId target animal identifier
     * @param date withdrawal date string
     * @param reason withdrawal reason string
     * @return true if withdrawal succeeded; false otherwise
     */
    public boolean withdrawAnimalInHabitat(String habitatId, String animalId, String date, String reason) {
        String normalizedHabitatId = habitatId == null ? null : habitatId.trim();
        String normalizedAnimalId = animalId == null ? null : animalId.trim();
        String normalizedDate = date == null ? null : date.trim();
        for (int i = 0; i < myHabitats.size(); i++) {
            if (myHabitats.get(i) != null && normalizedHabitatId != null
                    && normalizedHabitatId.equals(myHabitats.get(i).getId())) {
                Animal animal = myHabitats.get(i).findAnimalEntityById(normalizedAnimalId);
                if (animal == null || !ZooValidation.isValidDate(normalizedDate)
                        || !ZooValidation.isWithdrawalAfterEntry(normalizedDate, animal.getEntryDate())
                        || !isValidWithdrawalReason(reason)) {
                    return false;
                }
                cancelNonMedicalActivities(animal);
                cancelAssignedActivitiesForAnimal(animal);
                boolean withdrawAnimal = myHabitats.get(i).withdrawAnimal(normalizedAnimalId, normalizedDate, reason);
                return withdrawAnimal;
            }
        }
        return false;
    }

    /**
    * Searches for an employee entity by ID in the employee list.
     *
     * Preconditions:
     * - 'id' must not be null.
     *
     * Postconditions:
     * - Returns matching Employee entity if found, or null otherwise.
     *
     * @param id the employee member id to search for
     * @return the matching employee member, or null if no match exists
     */
    private Employee findEmployeeEntityById(String id) {
        for (int i = 0; i < myEmployees.size(); i++) {
            Employee employee = myEmployees.get(i);

            if (employee != null && id.equals(employee.getId())) {
                return employee;
            }
        }

        return null;
    }

    /**
     * Sets the legal representative name.
     *
     * Postconditions:
     * - Updates 'nameLegalRepresentative' attribute.
     *
     * @param nameLegalRepresentative the new legal representative name
     */
    public void setNameLegalRepresentative(String nameLegalRepresentative) {
        this.nameLegalRepresentative = nameLegalRepresentative;
    }

    /**
     * Sets the legal representative identifier.
     *
     * Postconditions:
     * - Updates 'idLegalRepresentative' attribute.
     *
     * @param idLegalRepresentative the new legal representative id
     */
    public void setIdLegalRepresentative(String idLegalRepresentative) {
        this.idLegalRepresentative = idLegalRepresentative;
    }

    /**
     * Calculates the estimated monthly feeding cost for the zoo across all habitats.
     *
     * Preconditions:
    * - 'myHabitats' list should be initialized.
     *
     * Postconditions:
     * - Returns sum of monthly food costs across all non-null habitats.
     *
     * @return the total monthly feeding cost of all habitats
     */
    public double getMonthlyFoodCost() {
        double monthlyFoodCost = 0;
        for (int i = 0; i < myHabitats.size(); i++) {
            if (myHabitats.get(i) != null) {
                monthlyFoodCost += myHabitats.get(i).getMonthlyFoodCost();
            }
        }

        return monthlyFoodCost;
    }

    // Modifiers

    /**
     * Sets the zoo identifier.
     *
     * Postconditions:
     * - Updates 'id' attribute.
     *
     * @param id the new zoo id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Sets the zoo name.
     *
     * Postconditions:
     * - Updates 'name' attribute.
     *
     * @param name the new zoo name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Sets the zoo address.
     *
     * Postconditions:
     * - Updates 'address' attribute.
     *
     * @param address the new address
     */
    public void setAddress(String address) {
        this.address = address;
    }

    /**
     * Sets the zoo budget.
     *
     * Postconditions:
     * - Updates 'budget' attribute.
     *
     * @param budget the new budget
     */
    public void setBudget(double budget) {
        if (!ZooValidation.isNonNegativeFinite(budget)) {
            throw new IllegalArgumentException("Zoo budget must be zero or greater.");
        }
        this.budget = budget;
    }

    /**
     * Sets the zoo city.
     *
     * Postconditions:
     * - Updates 'city' attribute.
     *
     * @param city the new city
     */
    public void setCity(String city) {
        this.city = city;
    }

    /**
     * Automatically creates and registers the default veterinary habitat in the zoo.
     *
     * Postconditions:
     * - A new Habitat with MEDICAL environment type and default parameters is added.
     *
     * @return true if the veterinary habitat was created; otherwise, false
     */
    public boolean createVeterinarian() {

        if (!zooProfileConfigured) {
            return false;
        }
        if (getVeterinaryClinic() != null) {
            return true;
        }
        String name = "Clínica Veterinaria";
        EnvironmentType enviroment = EnvironmentType.MEDICAL;
        double area = 400;
        double temperature = 27;
        int capacity = 25;
        double budget = 40000000;
        String status = "activo";
        String responsable = "Employee veterinario";
        boolean result = addHabitat(name, enviroment, temperature, area,
                budget, status, capacity, responsable);

        return result;

    }

}