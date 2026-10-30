package model.activity;

import java.time.LocalDate;

import model.Habitat;
import model.ZooValidation;
import model.employee.Employee;
import model.employee.Role;

public class CleaningActivity extends Activity {
    private static final long serialVersionUID = 1L;
    private final String description;
    private final String usedProducts;

    public CleaningActivity(String id, Habitat habitat, Employee responsible, LocalDate schedulingDate,
            String description, String usedProducts) {
        super(id, habitat, responsible, schedulingDate);
        if (!ZooValidation.isNonBlank(description) || !ZooValidation.isNonBlank(usedProducts)) {
            throw new IllegalArgumentException("Cleaning description and products are required.");
        }
        this.description = description.trim();
        this.usedProducts = usedProducts.trim();
    }

    @Override
    public String getInformation() {
        return "[CLEANING]" + System.lineSeparator() + getCommonInformation() + System.lineSeparator()
                + "Description: " + description + System.lineSeparator()
                + "Products: " + usedProducts;
    }

    @Override
    public Role getRequiredRole() {
        return Role.CARETAKER;
    }
}
