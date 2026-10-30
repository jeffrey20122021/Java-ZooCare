package ui;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Scanner;

import model.Zoo;
import model.ZooValidation;
import model.ZooPersistence;
import model.animal.DietType;
import model.employee.Role;

/*
 * The BioHabitat Foundation manages a zoo dedicated to the conservation, environmental education, and
 * responsible care of wildlife. Currently, much of the information about animals, habitats, feeding,
 * medical checkups, and care activities is recorded in spreadsheets, notebooks, and separate files. This
 * way of working has produced duplicate records, errors when calculating food rations, difficulty knowing
 * the actual occupancy of habitats, untracked care tasks, and loss of information between work shifts.
 *
 * The Foundation requests the development of the ZooCare software, a console application that centralizes
 * the zoo's operational information and supports basic decision-making related to animal welfare. The
 * solution will be built incrementally: it will start as a calculation and temporary-registration
 * prototype, and will evolve into an object-oriented, extensible system capable of persisting its
 * information between executions.
 */
public class ZooCareMain {

    private static Scanner reader = new Scanner(System.in);

    private static Zoo myZoo;

    /**
     * Executes the main entry point of the ZooCare application.
     *
     * Preconditions:
     * - None.
     *
     * Postconditions:
     * - Launches the application interactive menu loop.
     *
     * @param args program arguments
     */
    public static void main(String[] args) {
        try {
            myZoo = ZooPersistence.load();
            if (myZoo == null) {
                myZoo = new Zoo("ZooCare", "Not defined", "Not defined", 0,
                        "Not defined", "Not defined");
            }
        } catch (IOException | ClassNotFoundException exception) {
            System.err.println("Could not load the saved ZooCare state: " + exception.getMessage());
            return;
        }
        menu();
    }

    /**
     * Waits for confirmation before returning to the main menu and clears the console screen.
     *
     * Preconditions:
     * - 'reader' Scanner must be initialized.
     *
     * Postconditions:
     * - Clears the console screen and returns option 1 when user confirms return.
     *
     * Error Handling:
     * - Keeps prompting the user until option 1 is chosen.
     *
     * @return the selected menu option
     */
    public static int clearConsole() {
        int option = 0;

        while (option != 1) {
            System.out.println("\nDo you want to return to the main menu?");
            System.out.println("1. Yes");
            System.out.println("2. No");
            option = reader.nextInt();
            reader.nextLine();
        }

        System.out.print("\033[H\033[2J");

        return option;
    }

    /**
     * Displays the main menu and processes the selected option.
     *
     * Preconditions:
     * - 'reader' Scanner must be initialized.
     *
     * Postconditions:
     * - Navigates to selected registration, modification, or consultation workflows until user exits (0).
     *
     * Error Handling:
     * - Displays an invalid option message when an unmapped integer is chosen.
     */
    public static void menu() {
        int option = 0;
        do {
            System.out.println("Welcome to ZooCare");
            System.out.println("1. Register");
            System.out.println("2. Modify");
            System.out.println("3. Consult");
            System.out.println("4. Reports");
            System.out.println("5. Save data");
            System.out.println("6. Employee activity portal");
            System.out.println("0. Exit");
            option = reader.nextInt();
            reader.nextLine();

            switch (option) {
                case 1:
                    registerData();
                    break;
                case 2:
                    modifyData();
                    break;
                case 3:
                    showData();
                    break;
                case 4:
                    showReports();
                    break;
                case 5:
                    saveZoo();
                    break;
                case 6:
                    employeeActivityPortal();
                    break;
                case 0:
                    if (saveZoo()) {
                        System.out.println("Goodbye");
                    }
                    break;
                default:
                    System.out.println("Invalid option");
                    break;
            }
            if (option != 0) {
                clearConsole();
            }
        } while (option != 0);
    }

    /**
     * Displays the registration menu options and routes execution accordingly.
     *
     * Preconditions:
     * - 'reader' Scanner must be initialized.
     *
     * Postconditions:
     * - Directs the user to register zoo, animal, employee, or habitat information.
     *
     * Error Handling:
     * - Displays an invalid option message if choice is out of range.
     */
    public static void registerData() {
        System.out.println("1. Register zoo information");
        System.out.println("2. Register animal information");
        System.out.println("3. Register employee information");
        System.out.println("4. Register habitat information");
        System.out.println("5. Register care activity");

        int option = reader.nextInt();
        reader.nextLine();
        switch (option) {
            case 1:
                registerZooInformation();
                break;
            case 2:
                registerAnimal();
                break;
            case 3:
                registerEmployee();
                break;
            case 4:
                registerHabitat();
                break;
            case 5:
                registerActivity();
                break;
            default:
                System.out.println("Invalid option");
                break;
        }
    }

    /**
     * Displays the consultation menu and processes the selected option.
     *
     * Preconditions:
     * - 'reader' Scanner must be initialized.
     *
     * Postconditions:
     * - Directs control to the requested information display workflow.
     *
     * Error Handling:
     * - Displays an invalid option message if choice is out of range.
     */
    public static void showData() {
        System.out.println("1. Show zoo information");
        System.out.println("2. Show animal information - A list of Animals Active and another list of All");
        System.out.println("3. Show employee information - A list of Employee Active and another list of All");
        System.out.println("4. Show habitat information");
        System.out.println("5. Show withdrawn animals");
        System.out.println("6. Show one object");
        System.out.println("7. Show zoo map");
        System.out.println("8. Show care activities");

        int option = reader.nextInt();
        reader.nextLine();
        switch (option) {
            case 1:
                showZooInformation();
                break;
            case 2:
                showAnimalInformation();
                break;
            case 3:
                showEmployeeInformation();
                break;
            case 4:
                showHabitatInformation();
                break;
            case 5:
                showWithdrawAnimalInformation();
                break;
            case 6:
                showOneData();
                break;
            case 7:
                showZooMap();
                break;
            case 8:
                showActivities();
                break;
            default:
                System.out.println("Invalid option");
                break;
        }
    }

    /**
     * Displays one animal, habitat, or employee member selected by its identifier.
     *
     * Preconditions:
     * - 'reader' Scanner must be initialized.
     *
     * Postconditions:
     * - Prints detailed information for the chosen entity ID if found.
     *
     * Error Handling:
     * - Prints "Zoo is not set" if 'myZoo' is null.
     * - Prints entity not found message if ID does not exist.
     * - Displays "Invalid option" for unmapped choices.
     */
    public static void showOneData() {
        if (myZoo != null) {

            String animalList = myZoo.getAnimalList();
            String habitatList = myZoo.getHabitatList();
            String employeeList = myZoo.getEmployeeList();

            System.out.print("1. Show one animal: ");
            if (animalList != null) {
                System.out.println(animalList);

            } else {
                System.out.println("No animals registered");

            }

            System.out.print("2. Show one habitat: ");
            if (habitatList != null) {
                System.out.println(habitatList);

            } else {
                System.out.println("No habitats registered");
            }

            System.out.print("3. Show one employee member: ");
            if (employeeList != null) {
                System.out.println(employeeList);

            } else {
                System.out.println("No employee registered");

            }
            int option = reader.nextInt();
            reader.nextLine();

            System.out.println("Type the object id");
            String id = reader.nextLine();

            switch (option) {
                case 1:
                    String animalInformation = myZoo.getAnimalInformationById(id);
                    if (animalInformation != null) {
                        System.out.println(animalInformation);
                        System.out.println("Calculated monthly food cost: "
                                + myZoo.getMonthlyFoodCostFromAnimal(id));
                    } else {
                        System.out.println("Animal not found");
                    }
                    break;
                case 2:
                    String habitatInformation = myZoo.getHabitatInformationById(id);
                    if (habitatInformation != null) {
                        System.out.println(habitatInformation);
                    } else {
                        System.out.println("Habitat not found");
                    }
                    break;
                case 3:
                    String employeeInformation = myZoo.getEmployeeInformationById(id);
                    if (employeeInformation != null) {
                        System.out.println(employeeInformation);
                    } else {
                        System.out.println("Employee member not found");
                    }
                    break;
                default:
                    System.out.println("Invalid option");
                    break;
            }
        } else {
            System.out.println("Zoo is not set");
        }
    }

    /**
     * Displays the zoo's general information.
     *
     * Preconditions:
     * - None.
     *
     * Postconditions:
     * - Prints full zoo summary to console if zoo is registered.
     *
     * Error Handling:
     * - Displays "Zoo is not set" if 'myZoo' is null.
     */
    public static void showZooInformation() {
        if (myZoo != null) {
            String information = myZoo.getInformation();
            System.out.println(information);
        } else {
            System.out.println("Zoo is not set");
        }

    }

    /**
     * Displays active and registered animal information.
     *
     * Preconditions:
     * - None.
     *
     * Postconditions:
     * - Prints active animals and all registered animals details to console.
     *
     * Error Handling:
     * - Displays "Zoo is not set" if 'myZoo' is null.
     * - Displays "No animals registered" if animal list is empty.
     */
    public static void showAnimalInformation() {
        if (myZoo != null) {
            System.out.println("\n===== ACTIVE ANIMALS =====");
            System.out.println(myZoo.getActiveAnimalInformation());

            String information = myZoo.getAnimalsInformation();
            if (information.isEmpty()) {
                System.out.println("No animals registered");
            } else {
                System.out.println("\n===== ALL ANIMALS =====");
                System.out.println(information);
            }
        } else {
            System.out.println("Zoo is not set");
        }
    }

    /**
     * Displays active and registered employee information.
     *
     * Preconditions:
     * - None.
     *
     * Postconditions:
     * - Prints active employee and all registered employee details to console.
     *
     * Error Handling:
     * - Displays "Zoo is not set" if 'myZoo' is null.
     * - Displays "No employee registered" if employee list is empty.
     */
    public static void showEmployeeInformation() {
        if (myZoo != null) {
            System.out.println("\n===== ACTIVE EMPLOYEES =====");
            System.out.println(myZoo.getActiveEmployeeInformation());

            String information = myZoo.getEmployeeInformation();
            if (information.isEmpty()) {
                System.out.println("No employee registered");
            } else {
                System.out.println("\n===== ALL EMPLOYEES =====");
                System.out.println(information);
            }
        } else {
            System.out.println("Zoo is not set");
        }
    }

    /**
     * Displays registered habitat information and feeding costs.
     *
     * Preconditions:
     * - None.
     *
     * Postconditions:
     * - Prints habitat occupation, details, and estimated food costs to console.
     *
     * Error Handling:
     * - Displays "Zoo is not set" if 'myZoo' is null.
     * - Displays "No habitats registered" if habitat list is empty.
     */
    public static void showHabitatInformation() {
        if (myZoo != null) {
            String information = myZoo.getHabitatsInformation();
            if (information.isEmpty()) {
                System.out.println("No habitats registered");
            } else {
                System.out.println(information);

                System.out.println("\n========================================\n");
                System.out.println("\nResumen\n");
                System.out.println(myZoo.getHabitatFoodCostInformation());
            }
        } else {
            System.out.println("Zoo is not set");
        }
    }

    /**
     * Displays the animals withdrawn from a given habitat.
     *
     * Preconditions:
     * - 'reader' Scanner must be initialized.
     *
     * Postconditions:
     * - Prints withdrawn animal information for the specified habitat ID.
     *
     * Error Handling:
     * - Displays "Zoo is not set" if 'myZoo' is null.
     * - Displays notice if no withdrawn animals exist for the given habitat.
     */
    public static void showWithdrawAnimalInformation() {
        if (myZoo != null) {
            System.out.println("Type the habitat id");
            String habitatList = myZoo.getHabitatList();
            System.out.println(habitatList);
            String habitatId = reader.nextLine();
            String withdrawAnimalList = myZoo.getWithdrawAnimalListByHabitat(habitatId);
            if (withdrawAnimalList.isEmpty()) {
                System.out.println("No withdrawn animals registered for this habitat");
            } else {
                System.out.println(withdrawAnimalList);
            }
        } else {
            System.out.println("Zoo is not set");
        }
    }

    /**
     * Displays the modification menu.
     *
     * Preconditions:
     * - 'reader' Scanner must be initialized.
     *
     * Postconditions:
     * - Routes execution to modification method for zoo, animal, employee, or habitat.
     *
     * Error Handling:
     * - Displays "Zoo is not set" if 'myZoo' is null.
     * - Displays "Invalid option" if choice is out of range.
     */
    public static void modifyData() {
        if (myZoo != null) {
            System.out.println("1. Modify zoo information");
            System.out.println("2. Modify animal information");
            System.out.println("3. Modify employee information");
            System.out.println("4. Modify habitat information");
            System.out.println("5. Finish or cancel a care activity");

            int option = reader.nextInt();
            reader.nextLine();
            switch (option) {
                case 1:
                    modifyZooInformation();
                    break;
                case 2:
                    modifyAnimalInformation();
                    break;
                case 3:
                    modifyEmployeeInformation();
                    break;
                case 4:
                    modifyHabitatInformation();
                    break;
                case 5:
                    modifyActivityStatus();
                    break;
                default:
                    System.out.println("Invalid option");
                    break;
            }
        } else {
            System.out.println("Zoo is not set");
        }
    }

    /**
     * Modifies one selected animal attribute.
     *
     * Preconditions:
     * - 'reader' Scanner must be initialized.
     * - Target animal must exist in 'myZoo'.
     *
     * Postconditions:
     * - Updates chosen animal attribute if input and conditions are valid.
     *
     * Error Handling:
     * - Displays "Zoo is not set" if 'myZoo' is null.
     * - Displays "Animal not found" if animal ID does not exist.
     * - Prevents ID modification.
     * - Re-prompts for invalid weight or selection options.
     */
    public static void modifyAnimalInformation() {

        if (myZoo != null) {
            String animalList = myZoo.getAnimalList();
            System.out.println(animalList);
            System.out.println("Type the animal id");
            String animalId = reader.nextLine();
            if (myZoo.getAnimalInformationById(animalId) != null) {

                System.out.println("1. Id (immutable)  2. Name  3. Specie  4. Sex  5. Birth");
                System.out.println("6. Weight  7. Diet  8. Country  9. Entry date");
                System.out.println("10. Life cycle  11. Health status  12. Habitat  13. Environment  14. Status");
                int option = reader.nextInt();

                reader.nextLine();
                boolean condition = false;
                switch (option) {
                    case 1:
                        System.out.println("The animal ID cannot be modified");
                        return;
                    case 2:
                        condition = myZoo.updateAnimal(animalId, 2,
                                readRequiredText("Type the new animal name"));
                        break;
                    case 3:
                        condition = myZoo.updateAnimal(animalId, 3,
                                readRequiredText("Type the new species"));
                        break;
                    case 4:
                        System.out.println("Type the new sex");
                        condition = myZoo.updateAnimal(animalId, 4, reader.nextLine());
                        break;
                    case 5:
                        condition = myZoo.updateAnimal(animalId, 5,
                                readDate("Type the new birth date (dd/MM/yyyy)"));
                        break;
                    case 6:
                        System.out.println("Type the new weight");

                        double newWeight = 0;

                        while (newWeight <= 0) {

                            System.out.print("Enter the weight (kg): ");
                            newWeight = reader.nextDouble();
                            reader.nextLine();

                            if (newWeight <= 0) {
                                System.out.println("The weight must be greater than 0.");
                            }

                        }
                        condition = myZoo.updateAnimal(animalId, 6, String.valueOf(newWeight));

                        break;
                    case 7:
                        String diet = readDietOption();
                        if (diet != null) {

                            condition = myZoo.updateAnimal(animalId, 7, diet);
                        }
                        break;
                    case 8:
                        System.out.println("Type the country of origin");
                        condition = myZoo.updateAnimal(animalId, 8, reader.nextLine());
                        break;
                    case 9:
                        condition = myZoo.updateAnimal(animalId, 9,
                                readDate("Type the new entry date (dd/MM/yyyy)"));
                        break;
                    case 10:
                        System.out.println("Select the new life cycle:");
                        System.out.println("1. Juvenile");
                        System.out.println("2. Adult");
                        int lifeStageOption = reader.nextInt();
                        reader.nextLine();
                        switch (lifeStageOption) {
                            case 1:
                                condition = myZoo.updateAnimal(animalId, 10, "Juvenile");
                                break;
                            case 2:
                                condition = myZoo.updateAnimal(animalId, 10, "Adult");
                                break;
                            default:
                                System.out.println("Invalid life cycle option");
                                return;
                        }
                        break;
                    case 11:
                        System.out.println("Select the new health status:");
                        System.out.println("1. Healthy");
                        System.out.println("2. Quarantine");
                        System.out.println("3. Under Observation");
                        System.out.println("4. Recovery");
                        int healthStatusOption = reader.nextInt();
                        reader.nextLine();
                        switch (healthStatusOption) {
                            case 1:
                                condition = myZoo.updateAnimal(animalId, 11, "Healthy");
                                break;
                            case 2:
                                condition = myZoo.updateAnimal(animalId, 11, "Quarantine");
                                break;
                            case 3:
                                condition = myZoo.updateAnimal(animalId, 11, "Under Observation");
                                break;
                            case 4:
                                condition = myZoo.updateAnimal(animalId, 11, "Recovery");
                                break;
                            default:
                                System.out.println("Invalid health status option");
                                return;
                        }
                        break;
                    case 12:
                        System.out.println("Type the new habitat id");
                        String habitatList = myZoo.getHabitatList();
                        System.out.println(habitatList);
                        String habitatId = reader.nextLine();
                        if (myZoo.validateAndAssignHabitat(animalId, habitatId)) {
                            System.out.println("Animal and Habitat matched");
                            printOccupancyWarning(habitatId);
                            condition = true;
                        } else {
                            System.out.println("The habitat is invalid, inactive, full, or incompatible");
                        }

                        break;
                    case 13:
                        int animalEnvironmentOption = readEnvironmentOption();
                        if (animalEnvironmentOption > 0) {
                            condition = myZoo.updateAnimalEnvironment(animalId, animalEnvironmentOption);
                        }
                        break;
                    case 14:
                        String withdrawalDate = readDate("Enter the withdrawal date (dd/MM/yyyy)");
                        condition = myZoo.withdrawAnimalById(
                                animalId, withdrawalDate, readWithdrawalReason());

                        break;
                    default:
                        System.out.println("Invalid option");
                        return;
                }
                if (condition) {
                    System.out.println("Animal updated");
                    if (option == 11) {
                        printOccupancyWarning(myZoo.getAnimalHabitatId(animalId));
                    }
                } else if (option == 2 || option == 3 || option == 5 || option == 9) {
                    System.out.println(
                            "Invalid value. Names cannot be blank; dates must be valid and birth must precede entry.");
                }
            } else {
                System.out.println("Animal not found");
            }
        } else {
            System.out.println("Zoo is not set");
        }
    }

    /**
     * Modifies one selected employee member attribute.
     *
     * Preconditions:
     * - 'reader' Scanner must be initialized.
     * - Employee member with target ID must exist in 'myZoo'.
     *
     * Postconditions:
     * - Updates selected employee member field if input option is valid.
     *
     * Error Handling:
     * - Displays "Zoo is not set" if 'myZoo' is null.
     * - Displays "Employee is not registered" if employee member ID is not found.
     * - Prevents ID modification.
     */
    public static void modifyEmployeeInformation() {

        if (myZoo != null && !myZoo.hasRegisteredEmployees())  {
            String employeeList = myZoo.getEmployeeList();
            System.out.println(employeeList);
            System.out.println("Type the employee id");
            String employeeId = reader.nextLine();
            if (myZoo.getEmployeeInformationById(employeeId) != null) {

                System.out.println("1. Id (immutable)  2. Full name  3. Role  4. Phone  5. Email  6. State");
                int option = reader.nextInt();
                reader.nextLine();
                boolean condition = false;
                switch (option) {
                    case 1:
                        System.out.println("The employee member ID cannot be modified");
                        return;
                    case 2:
                        condition = myZoo.updateEmployee(employeeId, 2,
                                readRequiredText("Type the new full name"));
                        break;
                    case 3:
                        condition = myZoo.updateEmployee(employeeId, 3,
                                readRoleOption().getText());
                        break;
                    case 4:
                        condition = myZoo.updateEmployee(employeeId, 4,
                                readRequiredText("Type the new phone number"));
                        break;
                    case 5:
                        condition = myZoo.updateEmployee(employeeId, 5,
                                readRequiredText("Type the new email"));
                        break;
                    case 6:
                        condition = myZoo.updateEmployee(employeeId, 6,
                                readRequiredText("Type the new state (free text)"));
                        break;
                    default:
                        System.out.println("Invalid option");
                        return;
                }
                if (condition) {
                    System.out.println("Employee Member updated");
                } else {
                    System.out.println("Invalid value. Fields cannot be blank.");
                }

            } else {
                System.out.println("Employee is not registered");

            }
        } else {
            System.out.println("Zoo is not set");
        }
    }

    /**
     * Modifies one selected habitat attribute or withdraws an animal from the habitat.
     *
     * Preconditions:
     * - 'reader' Scanner must be initialized.
     * - Target habitat must exist in 'myZoo'.
     *
     * Postconditions:
     * - Updates habitat field or archives a withdrawn animal upon successful selection.
     *
     * Error Handling:
     * - Displays "Zoo is not set" if 'myZoo' is null.
     * - Displays "Habitat not found" if habitat ID does not exist.
     * - Re-prompts for invalid area, budget, or capacity inputs.
     */
    public static void modifyHabitatInformation() {

        if (myZoo != null) {
            String habitatList = myZoo.getHabitatList();
            System.out.println(habitatList);
            System.out.println("Type the habitat id");
            String habitatId = reader.nextLine();
            if (myZoo.getHabitatInformationById(habitatId) != null) {

                System.out.println("1. Id (immutable)  2. Name (automatic, immutable)  3. Environment  4. Temperature  5. Area");
                System.out.println("6. Budget  7. Status  8. Capacity  9. Eliminate Animal");
                System.out.println("10. Manage responsible employee  11. Remove habitat");
                int option = reader.nextInt();
                reader.nextLine();
                boolean condition = false;
                switch (option) {
                    case 1:
                        System.out.println("The habitat ID cannot be modified");
                        return;
                    case 2:
                        System.out.println("Habitat names are assigned automatically and cannot be changed.");
                        return;
                    case 3:
                        int habitatEnvironmentOption = readEnvironmentOption();
                        if (habitatEnvironmentOption > 0) {
                            condition = myZoo.updateHabitatEnvironment(habitatId, habitatEnvironmentOption);
                        }
                        break;
                    case 4:
                        double newTemperature = readTemperature(
                                "Type the new temperature in Celsius (-10 to 30)");
                        condition = myZoo.updateHabitat(habitatId, 4, String.valueOf(newTemperature));
                        break;
                    case 5:
                        double area;
                        do {
                            System.out.println("Type the new area");
                            area = reader.nextDouble();

                            if (area <= 0 || area > 300) {
                                System.out.println("Habitat area must be greater than 0 and at most 300 m2.");
                            }
                        } while (area <= 0 || area > 300);
                        condition = myZoo.updateHabitat(habitatId, 5, String.valueOf(area));
                        reader.nextLine();
                        break;
                    case 6:
                        double budget;
                        do {
                            System.out.println("Type the new budget");
                            budget = reader.nextDouble();

                            if (budget < 0 || !Double.isFinite(budget)) {
                                System.out.println("Budget must be zero or greater.");
                            }
                        } while (budget < 0 || !Double.isFinite(budget));
                        condition = myZoo.updateHabitat(habitatId, 6, String.valueOf(budget));
                        reader.nextLine();
                        break;
                    case 7:
                        System.out.println("1. Active  2. Maintenance  3. Under construction");
                        int habitatStateOption = reader.nextInt();
                        reader.nextLine();
                        String habitatStatus;
                        if (habitatStateOption == 1) {
                            habitatStatus = "activo";
                        } else if (habitatStateOption == 2) {
                            habitatStatus = "en mantenimiento";
                        } else if (habitatStateOption == 3) {
                            habitatStatus = "en construcción";
                        } else {
                            System.out.println("Invalid habitat status.");
                            return;
                        }
                        condition = myZoo.updateHabitat(habitatId, 7, habitatStatus);

                        break;
                    case 8:
                        int capacity;
                        do {
                            System.out.println("Type the new capacity");
                            capacity = reader.nextInt();

                            if (capacity <= 0) {
                                System.out.println("Capacity must be greater than zero.");
                            }
                        } while (capacity <= 0);
                        condition = myZoo.updateHabitat(habitatId, 8, String.valueOf(capacity));
                        reader.nextLine();
                        break;
                    case 9:
                        String animalList = myZoo.getAnimalListByHabitat(habitatId);
                        if (animalList.isEmpty()) {
                            System.out.println("No animals registered in this habitat");
                            break;
                        }
                        System.out.println(animalList);
                        System.out.println("Type the animal id you want to withdraw");
                        String animalIdToRemove = reader.nextLine().trim();
                        String dateWithdraw = readDate("Input withdraw date (dd/MM/yyyy)");
                        System.out.println("1. Fallecimiento  2. Traslado a otro zoológico");
                        int withdrawalReasonOption = reader.nextInt();
                        reader.nextLine();
                        String reasonWithdraw = withdrawalReasonOption == 1 ? "fallecimiento"
                                : withdrawalReasonOption == 2 ? "traslado a otro zoológico" : null;
                        if (reasonWithdraw == null) {
                            System.out.println("Invalid withdrawal reason.");
                            return;
                        }
                        if (myZoo.withdrawAnimalInHabitat(habitatId, animalIdToRemove, dateWithdraw, reasonWithdraw)) {

                            System.out.println("Animal withdrawn");
                        } else {
                            System.out.println(
                                    "Could not withdraw the animal. Verify that it belongs to this habitat and that the withdrawal date is valid and after its entry date.");
                        }
                        break;
                    case 10:
                        System.out.println("1. Assign employee  2. Reassign employee  3. Remove assignment");
                        int responsibleOption = reader.nextInt();
                        reader.nextLine();
                        String employeeId;
                        boolean responsibleUpdated;
                        if (responsibleOption == 1) {
                            System.out.println(myZoo.getEmployeeList());
                            employeeId = readRequiredText("Enter an active employee ID to assign");
                            responsibleUpdated = myZoo.assignHabitatResponsible(habitatId, employeeId);
                        } else if (responsibleOption == 2) {
                            System.out.println("Currently assigned employee:");
                            System.out.println(myZoo.getHabitatResponsibleList(habitatId));
                            String oldEmployeeId = readRequiredText("Enter the assigned employee ID to replace");
                            System.out.println(myZoo.getEmployeeList());
                            employeeId = readRequiredText("Enter the new active employee ID");
                            responsibleUpdated = myZoo.reassignHabitatResponsible(habitatId, oldEmployeeId, employeeId);
                        } else if (responsibleOption == 3) {
                            System.out.println("Currently assigned employee:");
                            System.out.println(myZoo.getHabitatResponsibleList(habitatId));
                            employeeId = readRequiredText("Enter the ID of the responsible employee to remove");
                            responsibleUpdated = myZoo.removeHabitatResponsible(habitatId, employeeId);
                        } else {
                            System.out.println("Invalid option.");
                            return;
                        }
                        if (responsibleUpdated) {
                            System.out.println("Habitat responsibility updated.");
                        } else if (responsibleOption == 3) {
                            System.out.println("Assignment was not removed: the entered employee ID is not "
                                    + "currently assigned to this habitat. Use an ID from the list above.");
                        } else {
                            System.out.println("Could not update habitat responsibility. Verify that the employee "
                                    + "member exists, is active, and that the assignment is valid.");
                        }
                        return;
                    case 11:
                        condition = myZoo.removeHabitat(habitatId);
                        if (!condition) {
                            System.out.println("Habitat cannot be removed if it is the veterinary clinic or still has animals.");
                        }
                        break;

                    default:
                        System.out.println("Invalid option");
                        return;
                }
                if (condition) {
                    System.out.println("Habitat updated");
                }
            }

            else {
                System.out.println("Habitat not found");
            }
        } else {
            System.out.println("Zoo is not set");
        }
    }

    /**
     * Displays the menu for modifying zoo information.
     *
     * Preconditions:
     * - 'reader' Scanner must be initialized.
     *
     * Postconditions:
     * - Routes execution to specific zoo modification method based on option chosen.
     *
     * Error Handling:
     * - Displays "Zoo is not set" if 'myZoo' is null.
     * - Displays "Invalid option" if selection is invalid.
     */
    public static void modifyZooInformation() {
        if (myZoo != null) {
            System.out.println("1. Modify zoo name");
            System.out.println("2. Modify zoo address");
            System.out.println("3. Modify zoo budget");
            System.out.println("4. Modify zoo city");
            System.out.println("5. Modify legal representative name");
            System.out.println("6. Modify legal representative id");

            int option = reader.nextInt();
            reader.nextLine();
            switch (option) {
                case 1:
                    modifyZooName();
                    break;
                case 2:
                    modifyZooAddress();
                    break;
                case 3:
                    modifyZooBudget();
                    break;
                case 4:
                    modifyZooCity();
                    break;
                case 5:
                    modifyLegalRepresentativeName();
                    break;
                case 6:
                    modifyLegalRepresentativeId();
                    break;
                default:
                    System.out.println("Invalid option");
                    break;
            }

        } else {
            System.out.println("Zoo is not set");
        }
    }

    /**
     * Modifies the zoo name.
     *
     * Preconditions:
     * - 'myZoo' instance must be initialized.
     * - 'reader' Scanner must be initialized.
     *
     * Postconditions:
     * - Updates the name attribute of 'myZoo'.
     */
    public static void modifyZooName() {
        System.out.println("Type the new zoo name");
        myZoo.setName(reader.nextLine());
    }

    /**
     * Modifies the zoo address.
     *
     * Preconditions:
     * - 'myZoo' instance must be initialized.
     * - 'reader' Scanner must be initialized.
     *
     * Postconditions:
     * - Updates the address attribute of 'myZoo'.
     */
    public static void modifyZooAddress() {
        System.out.println("Type the new zoo address");
        myZoo.setAddress(reader.nextLine());
    }

    /**
     * Modifies the zoo budget.
     *
     * Preconditions:
     * - 'reader' Scanner must be initialized.
     *
     * Postconditions:
     * - Updates the budget attribute of 'myZoo'.
     *
     * Error Handling:
     * - Displays "Zoo is not set" if 'myZoo' is null.
     */
    public static void modifyZooBudget() {
        if (myZoo != null) {
            double budget = readNonNegativeBudget("Type a new budget (zero or greater)");
            myZoo.setBudget(budget);
        } else {
            System.out.println("Zoo is not set");
        }
    }

    /**
     * Modifies the zoo city.
     *
     * Preconditions:
     * - 'myZoo' instance must be initialized.
     * - 'reader' Scanner must be initialized.
     *
     * Postconditions:
     * - Updates the city attribute of 'myZoo'.
     */
    public static void modifyZooCity() {
        System.out.println("Type the new zoo city");
        myZoo.setCity(reader.nextLine());
    }

    /**
     * Modifies the legal representative name.
     *
     * Preconditions:
     * - 'myZoo' instance must be initialized.
     * - 'reader' Scanner must be initialized.
     *
     * Postconditions:
     * - Updates the legal representative name attribute of 'myZoo'.
     */
    public static void modifyLegalRepresentativeName() {
        System.out.println("Type the new legal representative name");
        myZoo.setNameLegalRepresentative(reader.nextLine());
    }

    /**
     * Modifies the legal representative identifier.
     *
     * Preconditions:
     * - 'myZoo' instance must be initialized.
     * - 'reader' Scanner must be initialized.
     *
     * Postconditions:
     * - Updates the legal representative ID attribute of 'myZoo'.
     */
    public static void modifyLegalRepresentativeId() {
        System.out.println("Type the new legal representative id");
        myZoo.setIdLegalRepresentative(reader.nextLine());
    }

    /**
     * Registers a new habitat in the zoo after requesting user inputs.
     *
     * Preconditions:
     * - 'myZoo' instance must be initialized.
     * - 'reader' Scanner must be initialized.
     *
     * Postconditions:
     * - Creates and registers a new Habitat instance with the entered data if the environment option is valid.
     *
     * Error Handling:
     * - Displays "Zoo is not set" if 'myZoo' is null.
     * - Re-prompts for invalid area, capacity, or budget inputs.
     * - Displays an error message if the habitat could not be registered.
     */
    public static void registerHabitat() {
        if (myZoo != null) {
            if (!myZoo.hasRegisteredEmployees()) {
                System.out.println("Register at least one employee before creating a habitat.");
                return;
            }
            System.out.println(myZoo.getEnvironmentTypeList());
            int environmentOption = reader.nextInt();
            reader.nextLine();

            if (myZoo.isValidEnvironmentOption(environmentOption)) {

                double area = 0;

                while (area <= 0 || area > 300) {

                    System.out.print("Enter the area in square meters: ");
                    area = reader.nextDouble();
                    reader.nextLine();

                    if (area <= 0 || area > 300) {
                        System.out.println("The area must be greater than 0 and less than or equal to 300.");
                    }
                }

                String registrationError = myZoo.getHabitatRegistrationError(area);
                if (registrationError != null) {
                    System.out.println(registrationError);
                    return;
                }

                double temperature = readTemperature("Type the habitat temperature in Celsius (-10 to 30)");

                int capacity;
                do {
                    System.out.println("Type the new capacity");
                    capacity = reader.nextInt();

                    if (capacity <= 0) {
                        System.out.println("Capacity must be greater than zero.");
                    }
                } while (capacity <= 0);
                reader.nextLine();

                double budget = readNonNegativeBudget("Type the habitat budget (zero or greater)");

                String status = "activo";
                System.out.println("Available employee:");
                System.out.println(myZoo.getEmployeeList());
                String responsible = readRequiredText(
                        "Enter one or more responsible employee IDs separated by commas");
                if (!myZoo.areActiveEmployeeIds(responsible)) {
                    System.out.println("Habitat was not registered: enter unique IDs of active employee members.");
                    return;
                }
                boolean result = myZoo.addHabitat(environmentOption, temperature, area,
                        budget, status, capacity, responsible);

                if (result) {
                    String habitatId = myZoo.getLastAddedHabitatId();
                    boolean assignmentsCreated = habitatId != null;
                    for (String employeeId : responsible.split(",")) {
                        if (assignmentsCreated
                                && !myZoo.assignHabitatResponsible(habitatId, employeeId.trim())) {
                            assignmentsCreated = false;
                        }
                    }
                    if (assignmentsCreated) {
                        System.out.println("Habitat was registered");
                    } else {
                        if (habitatId != null) {
                            myZoo.removeHabitat(habitatId);
                        }
                        System.out.println("Habitat was not registered: assign active employee members first.");
                    }

                } else {
                    System.out.println("Habitat was not registered. Verify the habitat details and map availability.");
                }
            }
        } else {
            System.out.println("Zoo is not set");
        }
    }

    /**
     * Registers an animal in the zoo after requesting user inputs and validating target habitat availability.
     *
     * Preconditions:
     * - 'myZoo' must be initialized and contain at least one registered habitat.
     * - 'reader' Scanner must be initialized.
     *
     * Postconditions:
     * - Creates and registers a new Animal instance into the specified target habitat if compatible and valid.
     *
     * Error Handling:
     * - Displays error message if zoo is not set.
     * - Displays error message if no habitats are registered or target habitat is invalid/full/incompatible.
     */
    public static void registerAnimal() {
        if (myZoo == null) {
            System.out.println("Register the zoo before registering animals.");
            return;
        }
        if (!myZoo.isZooProfileConfigured()) {
            System.out.println("Register the zoo before registering animals.");
            return;
        }
        if (!myZoo.hasRegisteredEmployees()) {
            System.out.println("Register at least one employee before registering animals.");
            return;
        }
        if (myZoo.getHabitatList() == null) {
            System.out.println("Register a habitat before registering animals.");
            return;
        }

        String name = readRequiredText("Type the animal name");
            String specie = readRequiredText("Type the species");
            System.out.println("Type the sex");
            String sex = reader.nextLine();
            String birth = readDate("Type the birth date (dd/MM/yyyy)");
            System.out.println("Type the country of origin");
            String countryOrigin = reader.nextLine();
            String entryDate = readDate("Type the entry date (dd/MM/yyyy)");
            while (!ZooValidation.isBirthBeforeEntry(birth, entryDate)) {
                System.out.println("Birth date must be before entry date.");
                entryDate = readDate("Type an entry date after birth (dd/MM/yyyy)");
            }
            double weight = 0;

            while (weight <= 0) {

                System.out.print("Enter the weight (kg): ");
                weight = reader.nextDouble();
                reader.nextLine();

                if (weight <= 0) {
                    System.out.println("The weight must be greater than 0.");
                }
            }
            String typeDiet = readDietOption();

            System.out.print("\nEnter the animal's life stage:\n");
            System.out.println("1. Juvenile");
            System.out.println("2. Adult");

            int lifeStageOption = reader.nextInt();
            reader.nextLine();

            String lifeCycle = "";

            switch (lifeStageOption) {

                case 1:
                    lifeCycle = "Juvenile";
                    break;

                case 2:
                    lifeCycle = "Adult";
                    break;

                default:
                    System.out.println("Invalid option.");
                    return;
            }

            int healthStatusOption;
            String healthStatus = "";

            System.out.println("\nEnter the animal's health status:");
            System.out.println("1. Healthy");
            System.out.println("2. Quarantine");
            System.out.println("3. Under Observation");
            System.out.println("4. Recovery");

            healthStatusOption = reader.nextInt();
            reader.nextLine();

            switch (healthStatusOption) {
                case 1:
                    healthStatus = "Healthy";

                    break;
                case 2:
                    healthStatus = "Quarantine";
                    break;
                case 3:
                    healthStatus = "Under Observation";

                    break;
                case 4:
                    healthStatus = "Recovery";

                    break;
                default:
                    System.out.println("Invalid option.");
                    return;
            }

            System.out.print("\nEnter the animal's required environment: \n");
            int environmentOption = readEnvironmentOption();
            if (environmentOption > 0) {

                System.out.println("Type the habitat id");
                System.out.println("Reviewed the listed habitats");
                System.out.println(myZoo.getHabitatList());

                String habitatId = reader.nextLine();

                String assignmentError = myZoo.validateAnimalAssignment(environmentOption, habitatId, healthStatus);
                if (assignmentError != null) {
                    System.out.println(assignmentError);
                    return;
                }
                System.out.println("Animal and Habitat matched");
                String status = "activo";
                String withdrawDate = null;
                String withdrawReason = null;

                if (myZoo.hasAvaliableHabitat()) {

                    boolean result = myZoo.addAnimalInHabitat(name, specie, environmentOption, sex, birth, weight,
                            typeDiet,
                            countryOrigin, entryDate, lifeCycle, healthStatus, habitatId, status, withdrawDate,
                            withdrawReason);
                    if (result) {
                        System.out.println("Animal registrado exitosamente");
                        printOccupancyWarning(habitatId);
                    } else {
                        System.out.println("Error! No se pudo registrar el animal");
                    }

                } else {
                    System.out.println("Error! Habitats aun no registrados");
            }
        }
    }

    /**
     * Registers a employee member in the zoo.
     *
     * Preconditions:
     * - 'reader' Scanner must be initialized.
     *
     * Postconditions:
     * - Instantiates and adds a new Employee to 'myZoo' employee list if capacity allows.
     *
     * Error Handling:
     * - Displays "Zoo is not set" if 'myZoo' is null.
     * - Displays error message if employee member array capacity is full.
     */
    public static void registerEmployee() {
        if (myZoo == null || !myZoo.isZooProfileConfigured()) {
            System.out.println("Register the zoo before registering employees.");
            return;
        }

        String fullName = readRequiredText("Type the employee member's full name");
        Role role = readRoleOption();
        String phone = readRequiredText("Type the phone number");
        String mail = readRequiredText("Type the email");
        String state = "activo";

        boolean result = myZoo.addEmployee(fullName, role.getText(), phone, mail, state);
        if (result) {
            System.out.println("Employee was registered");
        } else {
            System.out.println("Employee was not registered. Verify the employee details and available capacity.");
        }
    }

    /**
     * Registers the zoo information and creates default structures.
     *
     * Preconditions:
     * - 'reader' Scanner must be initialized.
     *
     * Postconditions:
     * - Instantiates global 'myZoo' object and automatically registers default veterinary habitat.
     */
    public static void registerZooInformation() {
        if (myZoo == null) {
            myZoo = new Zoo("ZooCare", "Not defined", "Not defined", 0,
                    "Not defined", "Not defined");
        }
        String name = readRequiredText("Enter the zoo name");
        String address = readRequiredText("Enter the zoo address");
        double budget = readNonNegativeBudget("Enter a zoo budget (zero or greater)");
        String city = readRequiredText("Enter the zoo city");
        String nameLegalRepresentative = readRequiredText("Enter the legal representative name");
        String idLegalRepresentative = readRequiredText("Enter the legal representative id");
        if (myZoo.configureZoo(name, address, city, budget,
                nameLegalRepresentative, idLegalRepresentative)) {
            System.out.println("Zoo was registered");
        } else {
            System.out.println("Zoo profile is already configured or contains invalid data.");
        }
    }

    public static void registerActivity() {
        if (myZoo == null) {
            System.out.println("Zoo is not set");
            return;
        }
        if (!myZoo.hasRegisteredEmployees()) {
            System.out.println("Register at least one employee before registering a care activity.");
            return;
        }
        if (!myZoo.hasRegisteredHabitats()) {
            System.out.println("Register at least one habitat before registering a care activity.");
            return;
        }

        System.out.println("1. Feeding");
        System.out.println("2. Cleaning");
        System.out.println("3. Enrichment");
        System.out.println("4. Medical checkup");
        int type = reader.nextInt();
        reader.nextLine();

        if (type < 1 || type > 4) {
            System.out.println("Invalid activity type.");
            return;
        }

        System.out.println("Available habitats:");
        System.out.println(myZoo.getHabitatList());
        String habitatId = readRequiredText("Enter the habitat ID");

        System.out.println("Available employee:");
        System.out.println(myZoo.getEmployeeList());
        String employeeId = readRequiredText("Enter the responsible employee ID");
        LocalDate scheduledDate = LocalDate.parse(
                readDate("Enter the scheduled date (dd/MM/yyyy)"),
                DateTimeFormatter.ofPattern("dd/MM/uuuu"));

        boolean registered;
        if (type == 1 || type == 4) {
            System.out.println("Available animals:");
            System.out.println(myZoo.getAnimalList());
            String animalId = readRequiredText("Enter the animal ID");
            if (type == 1) {
                double foodWeight = readPositiveNumber("Enter the food weight in kilograms");
                registered = myZoo.addFeedingActivity(habitatId, animalId, employeeId, scheduledDate, foodWeight);
            } else {
                String reason = readRequiredText("Enter the medical checkup reason");
                registered = myZoo.addMedicalCheckupActivity(habitatId, animalId, employeeId, scheduledDate, reason);
            }
        } else if (type == 2) {
            String description = readRequiredText("Enter the cleaning description");
            String products = readRequiredText("Enter the products used");
            registered = myZoo.addCleaningActivity(habitatId, employeeId, scheduledDate, description, products);
        } else {
            String description = readRequiredText("Enter the enrichment description");
            int duration = readPositiveInteger("Enter the duration in days");
            String products = readRequiredText("Enter the products used");
            registered = myZoo.addEnrichmentActivity(
                    habitatId, employeeId, scheduledDate, description, duration, products);
        }

        System.out.println(registered ? "Activity registered successfully."
                : "Activity could not be registered. Check that the selected records are active and compatible.");
    }

    public static void showZooMap() {
        if (myZoo == null) {
            System.out.println("Zoo is not set");
            return;
        }
        System.out.println(myZoo.getZooMap());
        System.out.println("Available map positions: " + myZoo.getAvailableMapPositions());
    }

    private static void printOccupancyWarning(String habitatId) {
        String warning = myZoo.getOccupancyWarning(habitatId);
        if (warning != null) {
            System.out.println(warning);
        }
    }

    private static void showReports() {
        if (myZoo == null) {
            System.out.println("Zoo is not set");
            return;
        }
        System.out.println("1. Operational summary");
        System.out.println("2. Animal report");
        System.out.println("3. Monthly activities");
        System.out.println("4. Monthly alert history");
        System.out.println("5. Veterinary follow-up");
        System.out.println("6. Monthly operational costs");
        System.out.println("7. Annual activity costs");
        int option = readMenuOption();
        String report;
        String type;
        switch (option) {
            case 1:
                report = myZoo.getOperationalSummary(YearMonth.now());
                type = "OperationalSummary";
                break;
            case 2:
                String animalId = readRequiredText("Enter the animal ID");
                report = myZoo.getAnimalReport(animalId, readYearMonth());
                if (report == null) {
                    System.out.println("Animal not found.");
                    return;
                }
                type = "AnimalReport";
                break;
            case 3:
                report = myZoo.getActivitiesReport(readYearMonth());
                type = "Activities";
                break;
            case 4:
                report = myZoo.getAlertsReport(readYearMonth());
                type = "Alerts";
                break;
            case 5:
                report = myZoo.getVeterinaryFollowUpReport();
                type = "VeterinaryFollowUp";
                break;
            case 6:
                report = myZoo.getMonthlyCostReport(readYearMonth());
                type = "MonthlyCosts";
                break;
            case 7:
                int year = readPositiveInteger("Enter the reporting year");
                report = myZoo.getAnnualActivityCostReport(year);
                type = "AnnualActivityCosts";
                break;
            default:
                System.out.println("Invalid option.");
                return;
        }
        System.out.println(report);
        exportReport(type, report);
    }

    private static void employeeActivityPortal() {
        System.out.println("1. Caretaker  2. Veterinarian");
        int roleOption = reader.nextInt();
        reader.nextLine();
        if (roleOption != 1 && roleOption != 2) {
            System.out.println("Invalid role.");
            return;
        }
        String employeeList = myZoo.getEmployeeListByRole(roleOption);
        if (employeeList.isEmpty()) {
            System.out.println("No active employee members are registered for this role.");
            return;
        }
        System.out.println(employeeList);
        String employeeId = readRequiredText("Enter your employee ID");
        if (!myZoo.isActiveEmployeeWithRole(employeeId, roleOption)) {
            System.out.println("The selected employee ID is not active or does not have the selected role.");
            return;
        }
        String activities = myZoo.getAssignedActivitiesForEmployee(employeeId);
        System.out.println(activities);
        if ("No assigned activities.".equals(activities)) {
            return;
        }
        String activityId = readRequiredText("Enter the activity ID to finish");
        String activityInformation = myZoo.getActivityInformationById(activityId);
        if (activityInformation == null) {
            System.out.println("Activity not found.");
            return;
        }
        boolean finished;
        String medicalAnimalId = myZoo.getMedicalActivityAnimalId(activityId);
        if (activityInformation.startsWith("[MEDICAL CHECKUP]")) {
            double cost = readNonNegativeBudget("Enter treatment cost (COP; zero or greater)");
            double weight = readPositiveNumber("Enter the animal's recorded weight (kg)");
            String healthStatus = readHealthStatus();
            String observations = readRequiredText("Enter observations or diagnosis");
            String medication = readRequiredText("Enter medication, or type None");
            LocalDate followUpDate = readOptionalFollowUpDate();
            finished = myZoo.finishMedicalCheckupAsEmployee(activityId, employeeId, cost, weight,
                    healthStatus, observations, medication, followUpDate);
        } else {
            double cost = readNonNegativeBudget("Enter the activity cost (COP; zero or greater)");
            finished = myZoo.finishActivityAsEmployee(activityId, employeeId, cost);
        }
        System.out.println(finished ? "Activity finished successfully."
                : "Activity could not be finished. Verify assignment, role, dates, and status.");
        if (finished && medicalAnimalId != null) {
            printOccupancyWarning(myZoo.getAnimalHabitatId(medicalAnimalId));
        }
    }

    private static LocalDate readOptionalFollowUpDate() {
        while (true) {
            System.out.println("Schedule a medical follow-up? 1. Yes  2. No");
            int option = reader.nextInt();
            reader.nextLine();
            if (option == 2) {
                return null;
            }
            if (option != 1) {
                System.out.println("Invalid option.");
                continue;
            }
            String date = readDate("Enter the follow-up date (dd/MM/yyyy)");
            LocalDate followUpDate = LocalDate.parse(date, DateTimeFormatter.ofPattern("dd/MM/uuuu"));
            if (followUpDate.isAfter(LocalDate.now())) {
                return followUpDate;
            }
            System.out.println("Follow-up date must be after today.");
        }
    }

    private static void exportReport(String type, String report) {
        try {
            Path file = ReportExporter.export(type, report);
            System.out.println("Report saved to " + file.toAbsolutePath());
        } catch (IOException exception) {
            System.out.println("Report could not be saved: " + exception.getMessage());
        }
    }

    private static boolean saveZoo() {
        if (myZoo == null) {
            System.out.println("There is no ZooCare state to save.");
            return false;
        }
        try {
            ZooPersistence.save(myZoo);
            System.out.println("ZooCare state saved.");
            return true;
        } catch (IOException exception) {
            System.out.println("ZooCare state could not be saved: " + exception.getMessage());
            return false;
        }
    }

    private static YearMonth readYearMonth() {
        while (true) {
            String value = readRequiredText("Enter the reporting month (yyyy-MM)");
            try {
                return YearMonth.parse(value);
            } catch (DateTimeParseException exception) {
                System.out.println("Enter a valid month in yyyy-MM format.");
            }
        }
    }

    private static int readMenuOption() {
        System.out.println("Select an option");
        int option = reader.nextInt();
        reader.nextLine();
        return option;
    }

    private static String readHealthStatus() {
        while (true) {
            System.out.println("Select the resulting health status:");
            System.out.println("1. Healthy");
            System.out.println("2. Quarantine");
            System.out.println("3. Under Observation");
            System.out.println("4. Recovery");
            int option = reader.nextInt();
            reader.nextLine();
            switch (option) {
                case 1:
                    return "Healthy";
                case 2:
                    return "Quarantine";
                case 3:
                    return "Under Observation";
                case 4:
                    return "Recovery";
                default:
                    System.out.println("Invalid health status option.");
            }
        }
    }

    private static String readWithdrawalReason() {
        System.out.println("1. Death  2. Transfer to another zoo");
        int option = reader.nextInt();
        reader.nextLine();
        if (option == 1) {
            return "death";
        }
        if (option == 2) {
            return "transfer to another zoo";
        }
        System.out.println("Invalid withdrawal reason.");
        return "";
    }

    public static void showActivities() {
        if (myZoo == null) {
            System.out.println("Zoo is not set");
            return;
        }
        System.out.println(myZoo.getActivitiesInformation());
    }

    public static void modifyActivityStatus() {
        if (myZoo == null) {
            System.out.println("Zoo is not set");
            return;
        }
        String activities = myZoo.getActivitiesInformation();
        System.out.println(activities);
        if ("No activities are registered.".equals(activities)) {
            return;
        }
        String activityId = readRequiredText("Enter the activity ID");
        System.out.println("1. Cancel");
        System.out.println("2. Reassign");
        int option = reader.nextInt();
        reader.nextLine();

        boolean updated;
        if (option == 1) {
            updated = myZoo.cancelActivity(activityId);
        } else if (option == 2) {
            System.out.println(myZoo.getEmployeeList());
            String employeeId = readRequiredText("Enter the new responsible employee ID");
            updated = myZoo.reassignActivity(activityId, employeeId);
        } else {
            System.out.println("Invalid option.");
            return;
        }
        System.out.println(updated ? "Activity status updated."
                : "Activity not found or is no longer assigned.");
    }

    private static double readPositiveNumber(String prompt) {
        double value;
        do {
            System.out.println(prompt);
            value = reader.nextDouble();
            reader.nextLine();
            if (!ZooValidation.isPositiveFinite(value)) {
                System.out.println("Enter a finite value greater than zero.");
            }
        } while (!ZooValidation.isPositiveFinite(value));
        return value;
    }

    private static int readPositiveInteger(String prompt) {
        int value;
        do {
            System.out.println(prompt);
            value = reader.nextInt();
            reader.nextLine();
            if (value <= 0) {
                System.out.println("Enter a value greater than zero.");
            }
        } while (value <= 0);
        return value;
    }

    /**
     * Reads a required text value, repeating until it contains non-whitespace characters.
     *
     * @param prompt prompt to display
     * @return trimmed non-empty input
     */
    private static String readRequiredText(String prompt) {
        while (true) {
            System.out.println(prompt);
            String value = reader.nextLine();
            if (ZooValidation.isNonBlank(value)) {
                return value.trim();
            }
            System.out.println("This field cannot be blank.");
        }
    }

    /**
     * Reads a real calendar date in dd/MM/yyyy format.
     *
     * @param prompt prompt to display
     * @return valid date input
     */
    private static String readDate(String prompt) {
        while (true) {
            String value = readRequiredText(prompt);
            if (ZooValidation.isValidDate(value)) {
                return value;
            }
            System.out.println("Enter a valid date in dd/MM/yyyy format.");
        }
    }

    private static double readNonNegativeBudget(String prompt) {
        double budget;
        do {
            System.out.println(prompt);
            budget = reader.nextDouble();
            reader.nextLine();
            if (!ZooValidation.isNonNegativeFinite(budget)) {
                System.out.println("Budget must be a finite value of zero or greater.");
            }
        } while (!ZooValidation.isNonNegativeFinite(budget));
        return budget;
    }

    private static double readTemperature(String prompt) {
        double temperature;
        do {
            System.out.println(prompt);
            temperature = reader.nextDouble();
            reader.nextLine();
            if (!ZooValidation.isValidTemperature(temperature)) {
                System.out.println("Temperature must be between -10 and 30 Celsius.");
            }
        } while (!ZooValidation.isValidTemperature(temperature));
        return temperature;
    }

    /**
     * Reads and maps an environment type selection from the user console input.
     *
     * Preconditions:
     * - 'reader' Scanner must be initialized.
     *
     * Postconditions:
    * - Returns the selected environment option as an integer.
     *
     * Error Handling:
    * - Displays "Invalid environment option" and returns -1 if option is invalid.
     *
    * @return the selected environment option, or -1 if choice is invalid
     */
    private static int readEnvironmentOption() {
        System.out.println("1. Terrestrial");
        System.out.println("2. Aquatic");
        System.out.println("3. Aviary");
        System.out.println("4. Medical");
        int option = reader.nextInt();
        reader.nextLine();

        switch (option) {
            case 1:
            case 2:
            case 3:
            case 4:
                return option;
            default:
                System.out.println("Invalid environment option");
                return -1;
        }
    }

    /**
     * Reads and maps a diet type selection from the user console input.
     *
     * Preconditions:
     * - 'reader' Scanner must be initialized.
     *
     * Postconditions:
     * - Returns corresponding diet type string representation.
     *
     * Error Handling:
     * - Displays "Invalid diet option" and returns null if option is invalid.
     *
     * @return diet name string, or null if choice is invalid
     */
    private static String readDietOption() {
        while (true) {
            System.out.println(DietType.getOptionList());
            int option = reader.nextInt();
            reader.nextLine();
            DietType dietType = DietType.calculateDietType(option);
            if (dietType != null) {
                return dietType.getText();
            }
            System.out.println("Invalid diet option");
        }
    }

    private static Role readRoleOption() {
        while (true) {
            System.out.println(Role.getOptionList());
            int option = reader.nextInt();
            reader.nextLine();
            Role role = Role.calculateRole(option);
            if (role != null) {
                return role;
            }
            System.out.println("Invalid role option");
        }
    }
}