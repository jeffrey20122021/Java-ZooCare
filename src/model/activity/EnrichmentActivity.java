package model.activity;

import java.time.LocalDate;

import model.Habitat;
import model.employee.Employee;
import model.ZooValidation;
import model.employee.Role;

public class EnrichmentActivity extends Activity {
    private static final long serialVersionUID = 1L;
    private final String description;
    private final int durationInDays;
    private final String usedProducts;

    public EnrichmentActivity(String id, Habitat habitat, Employee responsible, LocalDate schedulingDate,
            String description, int durationInDays, String usedProducts) {
        super(id, habitat, responsible, schedulingDate);
        if (!ZooValidation.isNonBlank(description) || !ZooValidation.isNonBlank(usedProducts)
                || durationInDays <= 0) {
            throw new IllegalArgumentException("Enrichment description, products, and positive duration are required.");
        }
        this.description = description.trim();
        this.durationInDays = durationInDays;
        this.usedProducts = usedProducts.trim();
    }

    @Override
    public String getInformation() {
        return "[ENRICHMENT]" + System.lineSeparator() + getCommonInformation() + System.lineSeparator()
                + "Description: " + description + System.lineSeparator()
                + "Duration: " + durationInDays + " day(s)" + System.lineSeparator()
                + "Products: " + usedProducts;
    }

    @Override
    public Role getRequiredRole() {
        return Role.CARETAKER;
    }
}
