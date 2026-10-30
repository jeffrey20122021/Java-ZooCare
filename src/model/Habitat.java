package model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Locale;
import java.util.ArrayList;

import model.animal.Animal;

/**
 * Represents a habitat within the zoo.
 * Manages environmental properties, assigned animals, and withdrawn animal records.
 */
public class Habitat implements Serializable,Identifiable {
    private static final long serialVersionUID = 1L;
    private static final int MAX_ANIMALS = 100;
    private String id;
    private String name;
    private EnvironmentType environment;
    private double temperature;
    private double area;
    private double budget;
    private String status;
    private int capacity;
    private String responsible;
    private ArrayList<String> responsibleEmployeeIds = new ArrayList<String>();
    private ArrayList<Animal> myAnimals;
    private Animal[] withdrawAnimals;

    /**
     * Constructs a new Habitat with all its specifications and initializes animal storage.
     *
     * Preconditions:
     * - 'environment' must be a valid EnvironmentType value.
     * - 'capacity' must be a non-negative integer.
     * - 'temperature' must be between -10 and 30 degrees Celsius.
     *
     * Postconditions:
     * - Initializes 'myAnimals' list to store active animals.
     * - Allocates fixed-size array 'withdrawAnimals' for archiving withdrawn animals.
     *
     * @param name the habitat name
     * @param environment the environment type of the habitat
     * @param temperature the controlled temperature in Celsius
     * @param area the physical space area in square meters
     * @param budget the monthly operating budget allocated
     * @param status the operational status of the habitat
     * @param capacity maximum number of animals allowed
     * @param responsible name of the employee member responsible
     */
    public Habitat(String name, EnvironmentType environment, double temperature, double area,
            double budget, String status, int capacity, String responsible) {
        if (!ZooValidation.isValidTemperature(temperature) || !ZooValidation.isNonBlank(status)) {
            throw new IllegalArgumentException(
                    "Habitat temperature must be between -10 and 30 Celsius and status cannot be blank.");
        }
        boolean veterinaryClinic = isVeterinaryClinicName(name, environment);
        if (environment == null || !ZooValidation.isNonBlank(name)
                || !isValidArea(area, veterinaryClinic)
                || !ZooValidation.isNonNegativeFinite(budget) || capacity <= 0) {
            throw new IllegalArgumentException("Habitat values are outside the allowed ranges.");
        }
        this.id = generateId();
        myAnimals = new ArrayList<Animal>();
        this.withdrawAnimals = new Animal[MAX_ANIMALS];
        this.environment = environment;
        this.temperature = temperature;
        this.name = name;
        this.area = area;
        this.budget = budget;
        setStatus(status);
        this.capacity = capacity;
        this.responsible = responsible;
    }

    /**
     * Returns a formatted summary string containing habitat details and occupation statistics.
     *
     * Preconditions:
     * - The habitat instance must be properly initialized.
     *
     * Postconditions:
     * - Returns a multi-line formatted representation of the habitat data.
     *
     * @return the formatted habitat details string
     */
    public String getInformation() {
        return "\n========================================\n"
                + "[HABITAT]" + System.lineSeparator()
                + "ID: " + id + System.lineSeparator()
                + "Name: " + name + System.lineSeparator()
                + "Environment: " + environment.getTypeName() + System.lineSeparator()
                + "Temperature: " + temperature + System.lineSeparator()
                + "Area: " + area + System.lineSeparator()
                + "Required positions: " + getRequiredPositions() + System.lineSeparator()
                + "Budget: " + String.format("%,.2f", budget) + System.lineSeparator()
                + "Status: " + status + System.lineSeparator()
                + "Capacity: " + capacity + System.lineSeparator()
                + "Absolute occupation: " + getTotalOccupation() + System.lineSeparator()
                + "Percentage occupation: " + getPercentualOccupation() + "%" + System.lineSeparator()
                + "Responsible: " + responsible + System.lineSeparator()
                + "========================================";
    }

    /**
     * Adds an animal to the habitat list if capacity permits.
     *
     * Preconditions:
     * - 'myAnimal' must be a valid non-null Animal instance.
     *
     * Postconditions:
     * - If space is available, the animal is registered in 'myAnimals'.
     *
     * Error Handling:
     * - Returns false if current animals reach or exceed the capacity limit.
     *
     * @param myAnimal the animal instance to be added
     * @return true if animal was successfully added; false otherwise
     */
    public boolean addAnimal(Animal myAnimal) {
        if (myAnimal != null && myAnimals.size() < capacity
                && !hasAnimal(myAnimal.getId())) {
            return myAnimals.add(myAnimal);
        }

        return false;
    }

    /**
     * Checks whether the habitat contains an animal with the specified ID.
     *
     * Preconditions:
     * - 'id' should be a valid String identifier.
     *
     * Postconditions:
     * - Returns true if an animal with matching ID exists; false otherwise.
     *
     * @param id the animal identifier to search for
     * @return true if the animal is found; false otherwise
     */
    public boolean hasAnimal(String id) {
        return findAnimalEntityById(id) != null;
    }

    public boolean hasAnimals() {
        return !myAnimals.isEmpty();
    }

    public boolean addResponsibleEmployee(String employeeId) {
        if (!ZooValidation.isNonBlank(employeeId) || responsibleEmployeeIds.contains(employeeId)) {
            return false;
        }
        responsibleEmployeeIds.add(employeeId);
        return true;
    }

    public boolean hasResponsibleEmployee(String employeeId) {
        return responsibleEmployeeIds.contains(employeeId);
    }

    public boolean reassignResponsibleEmployee(String oldEmployeeId, String newEmployeeId) {
        int index = responsibleEmployeeIds.indexOf(oldEmployeeId);
        if (index < 0 || !ZooValidation.isNonBlank(newEmployeeId)) {
            return false;
        }
        if (!responsibleEmployeeIds.contains(newEmployeeId)) {
            responsibleEmployeeIds.set(index, newEmployeeId);
        } else {
            responsibleEmployeeIds.remove(index);
        }
        return true;
    }

    public boolean removeResponsibleEmployee(String employeeId) {
        return responsibleEmployeeIds.remove(employeeId);
    }

    public boolean hasResponsibleEmployees() {
        return !responsibleEmployeeIds.isEmpty();
    }

    public void clearResponsibleEmployees() {
        responsibleEmployeeIds.clear();
    }

    public ArrayList<String> getResponsibleEmployeeIds() {
        return new ArrayList<String>(responsibleEmployeeIds);
    }

    public boolean isVeterinaryClinic() {
        return isVeterinaryClinicName(name, environment);
    }

    private static boolean isVeterinaryClinicName(String name, EnvironmentType environment) {
        String normalizedName = name == null ? "" : name.replace("í", "i").replace("Í", "I");
        return "Clinica Veterinaria".equalsIgnoreCase(normalizedName)
                && environment == EnvironmentType.MEDICAL;
    }

    private static boolean isValidArea(double area, boolean veterinaryClinic) {
        return Double.isFinite(area) && area > 0 && area <= (veterinaryClinic ? 400 : 300);
    }

    /**
     * Searches for an animal entity by its identifier.
     *
     * Preconditions:
     * - 'id' is a non-null string specifying the target animal.
     *
     * Postconditions:
     * - Returns the matching Animal reference, or null if no match is found.
     *
     * @param id the animal id to search for
     * @return matching Animal instance, or null if not found
     */
    public Animal findAnimalEntityById(String id) {
        for (int i = 0; i < myAnimals.size(); i++) {
            Animal animal = myAnimals.get(i);
            if (animal != null && animal.getId().equalsIgnoreCase(id)) {
                return animal;
            }
        }
        return null;
    }

    /**
     * Returns a summary list of all registered active animals in "id-name" format.
     *
     * Preconditions:
     * - 'myAnimals' list must be initialized.
     *
     * Postconditions:
     * - Concatenates ID and name for every non-null animal in the habitat.
     *
     * @return formatted string listing all animal IDs and names
     */
    public String getAnimalList() {
        String animalList = "";
        for (int i = 0; i < myAnimals.size(); i++) {
            Animal animal = myAnimals.get(i);
            if (animal != null) {
                animalList += "\n" + animal.getId() + "-" + animal.getName();
            }
        }
        return animalList;
    }

    /**
     * Retrieves full information string for a specific animal.
     *
     * Preconditions:
     * - 'id' must be a valid non-null identifier string.
     *
     * Postconditions:
     * - Returns the matching animal's formatted information if found.
     *
     * Error Handling:
     * - Returns null if animal with given ID is not present in habitat.
     *
     * @param id animal identifier
     * @return formatted information string of the specified animal, or null if not found
     */
    public String getAnimalInformationById(String id) {
        for (int i = 0; i < myAnimals.size(); i++) {
            Animal animal = myAnimals.get(i);
            if (animal != null && id != null && id.equalsIgnoreCase(animal.getId())) {
                return animal.getInformationAnimal();
            }
        }
        return null;
    }

    /**
     * Retrieves detailed information string for all active animals in this habitat.
     *
     * Preconditions:
     * - 'myAnimals' list should be initialized.
     *
     * Postconditions:
     * - Concatenates information string of every animal registered in 'myAnimals'.
     *
     * @return combined information string of all animals
     */
    public String getAnimalsInformation() {
        String information = "";
        for (int i = 0; i < myAnimals.size(); i++) {
            Animal animal = myAnimals.get(i);
            if (animal != null) {
                information += animal.getInformationAnimal() + System.lineSeparator();
            }
        }
        return information;
    }

    /**
     * Retrieves detailed information string for active animals that have "activo" status.
     *
     * Preconditions:
     * - 'myAnimals' list should be initialized.
     *
     * Postconditions:
     * - Concatenates details of active animals matching status "activo".
     *
     * @return combined information string of active animals
     */
    public String getActiveAnimalInformation() {
        String information = "";
        for (int i = 0; i < myAnimals.size(); i++) {
            Animal animal = myAnimals.get(i);
            if (animal != null && "activo".equalsIgnoreCase(animal.getStatus())) {
                information += animal.getInformationAnimal() + System.lineSeparator();
            }
        }
        return information;
    }

    /**
     * Updates specific information field of an animal in this habitat.
     *
     * Preconditions:
     * - 'id' identifies an existing animal.
     * - 'option' corresponds to a valid editable attribute code.
     *
     * Postconditions:
     * - Updates the matching animal's attribute according to the given option.
     *
     * Error Handling:
     * - Returns false if animal is not found or update fails.
     *
     * @param id identifier of the animal to update
     * @param option numerical option specifying attribute to change
     * @param value new value to set
     * @return true if update succeeded; false otherwise
     */
    public boolean updateAnimal(String id, int option, String value) {
        Animal animal = findAnimalEntityById(id);
        if (animal != null) {
            return animal.updateInformation(option, value);
        } else {
            return false;
        }
    }

    /**
     * Retrieves the monthly food cost for a specific animal in the habitat.
     *
     * Preconditions:
     * - 'id' is a target animal identifier.
     *
     * Postconditions:
     * - Returns the calculated monthly ration cost for the matching animal.
     *
     * Error Handling:
     * - Returns -1 if the specified animal is not found.
     *
     * @param id the animal identifier
     * @return the animal's monthly food cost, or -1 if not found
     */
    public double getAnimalMonthlyFoodCost(String id) {
        Animal animal = findAnimalEntityById(id);
        if (animal != null) {
            return animal.calculateMonthlyRationCost();
        } else {
            return -1;
        }
    }

    /**
     * Removes an animal from active list and archives it into withdraw array.
     *
     * Preconditions:
     * - Animal with 'id' exists in 'myAnimals'.
     *
     * Postconditions:
     * - Animal is added to 'withdrawAnimals' archive array.
     * - Animal is removed from 'myAnimals' ArrayList.
     *
     * Error Handling:
     * - Returns false if animal archive fails (e.g. archive array full) or ID not found.
     *
     * @param id identifier of animal to remove
     * @return true if animal was removed and archived successfully; false otherwise
     */
    public boolean removeAnimal(String id) {
        for (int i = 0; i < myAnimals.size(); i++) {
            if (myAnimals.get(i) != null && myAnimals.get(i).getId().equalsIgnoreCase(id)) {
                if (!addWithdrawAnimal(myAnimals.get(i))) {
                    return false;
                }
                myAnimals.remove(i);
                return true;
            }
        }
        return false;
    }

    boolean removeAnimalForTransfer(Animal animal) {
        return myAnimals.remove(animal);
    }

    /**
     * Withdraws an animal by setting reason/date and removing it from active list.
     *
     * Preconditions:
     * - Animal with specified 'id' must exist.
     *
     * Postconditions:
     * - Animal withdraw record is filled with date and reason.
     * - Animal is moved to withdrawn archive.
     *
     * @param id identifier of animal to withdraw
     * @param date date of withdrawal
     * @param reason reason for withdrawal
     * @return true if withdrawal succeeded; false otherwise
     */
    public boolean withdrawAnimal(String id, String date, String reason) {
        String normalizedId = id == null ? null : id.trim();
        String normalizedDate = date == null ? null : date.trim();
        Animal animal = findAnimalEntityById(normalizedId);
        if (animal != null && ZooValidation.isValidDate(normalizedDate)
                && ZooValidation.isWithdrawalAfterEntry(normalizedDate, animal.getEntryDate())
                && hasAvailableWithdrawArchiveSlot()) {
            animal.withdraw(normalizedDate, reason);
            return removeAnimal(normalizedId);
        } else {
            return false;
        }
    }

    private boolean hasAvailableWithdrawArchiveSlot() {
        for (Animal withdrawnAnimal : withdrawAnimals) {
            if (withdrawnAnimal == null) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns a formatted summary string of all withdrawn animals from this habitat.
     *
     * Preconditions:
     * - 'withdrawAnimals' array should be initialized.
     *
     * Postconditions:
     * - Returns combined details of all entries in 'withdrawAnimals'.
     *
     * @return summary string of withdrawn animals
     */
    public String getWithdrawAnimalInformation() {
        String information = "";
        for (int i = 0; i < withdrawAnimals.length; i++) {
            Animal animal = withdrawAnimals[i];
            if (animal != null) {
                information += animal.getInformationAnimal() + System.lineSeparator();
            }
        }
        return information;
    }

    /**
     * Stores a withdrawn animal into the fixed array archive.
     *
     * Preconditions:
     * - 'animal' must be non-null.
     *
     * Postconditions:
     * - Places animal in first available null position of 'withdrawAnimals'.
     *
     * Error Handling:
     * - Returns false if 'withdrawAnimals' array is full.
     *
     * @param animal animal instance to archive
     * @return true if stored successfully; false if array is full
     */
    public boolean addWithdrawAnimal(Animal animal) {
        for (int i = 0; i < withdrawAnimals.length; i++) {
            if (withdrawAnimals[i] == null) {
                withdrawAnimals[i] = animal;
                return true;
            }
        }
        return false;
    }

    /**
     * Returns array of animals that were withdrawn from this habitat.
     *
     * @return array containing archived withdrawn animals
     */
    public Animal[] getWithdrawAnimals() {
        return withdrawAnimals;
    }

    // Analyzers

    /**
     * Returns the habitat identifier.
     *
     * @return the habitat identifier
     */
    @Override 
    public String getId() {
        return id;
    }

    /**
     * Returns the habitat name.
     *
     * @return the habitat name
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the habitat environment type.
     *
     * @return the environment type
     */
    public EnvironmentType getEnvironment() {
        return environment;
    }

    /**
     * Returns the habitat temperature.
     *
     * @return habitat temperature in Celsius
     */
    public double getTemperature() {
        return temperature;
    }

    /**
     * Returns the habitat area.
     *
     * @return habitat area in square meters
     */
    public double getArea() {
        return area;
    }

    /**
     * Returns current habitat operational status.
     *
     * @return status string
     */
    public String getStatus() {
        return status;
    }

    /**
     * Returns name of person responsible for habitat.
     *
     * @return responsible person's name
     */
    public String getResponsible() {
        return responsible;
    }

    /**
     * Returns total habitat budget.
     *
     * @return budget value
     */
    public double getBudget() {
        return budget;
    }

    /**
     * Returns total capacity of habitat.
     *
     * @return capacity limit
     */
    public int getCapacity() {
        return capacity;
    }

    /**
     * Calculates combined monthly feeding cost for all current active animals.
     *
     * Preconditions:
     * - 'myAnimals' list should be initialized.
     *
     * Postconditions:
     * - Sums food costs computed by each assigned animal instance.
     *
     * @return total monthly food expenditure double value
     */
    public double getMonthlyFoodCost() {
        double monthlyFoodCost = 0;
        for (int i = 0; i < myAnimals.size(); i++) {
            if (myAnimals.get(i) != null) {
                monthlyFoodCost += myAnimals.get(i).calculateMonthlyRationCost();
            }
        }

        return monthlyFoodCost;
    }

    /**
     * Returns number of active animal slots currently occupied.
     *
     * @return count of non-null animals currently assigned
     */
    public int getTotalOccupation() {
        int occupation = 0;
        for (int i = 0; i < myAnimals.size(); i++) {
            if (myAnimals.get(i) != null) {
                occupation++;
            }
        }
        return occupation;
    }

    /**
     * Checks if habitat has space available, option to ignore an animal ID.
     *
     * Preconditions:
     * - 'animalIdToIgnore' can be null or valid string ID.
     *
     * @param animalIdToIgnore ID of animal to exclude from count, or null
     * @return true if available capacity exists; false otherwise
     */
    public boolean hasAvailableCapacity(String animalIdToIgnore) {
        int occupation = 0;
        for (int i = 0; i < myAnimals.size(); i++) {
            if (myAnimals.get(i) != null) {
                if (animalIdToIgnore == null || !myAnimals.get(i).getId().equalsIgnoreCase(animalIdToIgnore)) {
                    occupation++;
                }
            }
        }
        return occupation < capacity;
    }

    /**
     * Calculates percentage of capacity currently occupied.
     *
     * Preconditions:
     * - None.
     *
     * Postconditions:
     * - Returns occupation percentage based on total animals and capacity.
     *
     * Error Handling:
     * - Avoids division by zero if capacity is 0.
     *
     * @return percentage double value (0.0 to 100.0)
     */
    public double getPercentualOccupation() {
        if (capacity > 0) {
            return getTotalOccupation() * 100.0 / capacity;
        }
        return 0.0;
    }

    /**
     * Calculates number of required employee positions based on habitat area size.
     *
     * Preconditions:
     * - None.
     *
     * Postconditions:
     * - Returns required employee positions based on the area range.
     *
     * @return integer representing calculated required positions
     */
    public int getRequiredPositions() {

        int positions=0;
        if (this.area > 0 && this.area <= 100) {
              positions = 1;
        } else if (this.area > 100 && this.area <= 200) {
              positions = 2;
        } else if (this.area > 200 && this.area <= 300) {
              positions = 3;
        } else if (this.area > 300 && this.area <= 400) {
             positions = 4;
        }
        return positions ;
    }

    /**
     * Returns active animals list.
     *
     * @return ArrayList of assigned animals
     */
    public ArrayList<Animal> getMyAnimals() {
        return myAnimals;
    }

    // Modifiers

    /**
     * Sets habitat name.
     *
     * @param name new habitat name
     */
    public void setName(String name) {
        if (!ZooValidation.isNonBlank(name)) {
            throw new IllegalArgumentException("Habitat name cannot be blank.");
        }
        this.name = name;
    }

    /**
     * Sets habitat environment type.
     *
     * @param environment new environment type
     */
    public void setEnvironment(EnvironmentType environment) {
        this.environment = environment;
    }

    /**
     * Sets habitat temperature.
     *
     * @param temperature new temperature value
     */
    public void setTemperature(Double temperature) {
        if (temperature == null || !ZooValidation.isValidTemperature(temperature)) {
            throw new IllegalArgumentException("Habitat temperature must be between -10 and 30 Celsius.");
        }
        this.temperature = temperature;
    }

    /**
     * Sets habitat area size.
     *
     * @param area new area in square meters
     */
    public void setArea(Double area) {
        if (area == null || !isValidArea(area, isVeterinaryClinic())) {
            throw new IllegalArgumentException("Habitat area is outside the allowed range.");
        }
        this.area = area;
    }

    /**
     * Sets habitat operational budget.
     *
     * @param budget new budget value
     */
    public void setBudget(Double budget) {
        if (budget == null || !Double.isFinite(budget) || budget < 0) {
            throw new IllegalArgumentException("Habitat budget must be zero or greater.");
        }
        this.budget = budget;
    }

    /**
     * Sets habitat status.
     *
     * @param status new status string
     */
    public void setStatus(String status) {
        if (!ZooValidation.isNonBlank(status)) {
            throw new IllegalArgumentException("Habitat status cannot be blank.");
        }
        String normalized = status.trim().toLowerCase(Locale.ROOT);
        if (!normalized.equals("activo") && !normalized.equals("active")
                && !normalized.equals("en mantenimiento") && !normalized.equals("maintenance")
                && !normalized.equals("en construcción") && !normalized.equals("en construccion")
                && !normalized.equals("under construction")
                && !normalized.equals("retirado") && !normalized.equals("retired")) {
            throw new IllegalArgumentException("Habitat state is not supported.");
        }
        this.status = status.trim();
    }

    /**
     * Sets habitat total capacity.
     *
     * @param capacity new capacity limit
     */
    public void setCapacity(int capacity) {
        if (capacity <= 0 || capacity < getTotalOccupation()) {
            throw new IllegalArgumentException("Capacity must be positive and not below current occupation.");
        }
        this.capacity = capacity;
    }

    /**
     * Sets person responsible for habitat.
     *
     * @param responsible name of responsible person
     */
    public void setResponsible(String responsible) {
        this.responsible = responsible;
    }

    /**
     * Replaces current list of active animals.
     *
     * @param myAnimals new list of animals
     */
    public void setMyAnimals(ArrayList<Animal> myAnimals) {
        this.myAnimals = myAnimals;
    }

    @Override
    public String generateId() {
       return "H" + LocalDateTime.now().getMinute() + LocalDateTime.now().getSecond() + LocalDateTime.now().getNano();
    }
}