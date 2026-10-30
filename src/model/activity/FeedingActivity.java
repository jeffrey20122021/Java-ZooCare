package model.activity;

import java.time.LocalDate;

import model.Habitat;
import model.animal.Animal;
import model.employee.Employee;
import model.ZooValidation;
import model.employee.Role;

public class FeedingActivity extends Activity {
    private static final long serialVersionUID = 1L;
    private final Animal animal;
    private final double foodWeight;

    public FeedingActivity(String id, Habitat habitat, Employee responsible, LocalDate schedulingDate,
            Animal animal, double foodWeight) {
        super(id, habitat, responsible, schedulingDate);
        if (animal == null || !ZooValidation.isPositiveFinite(foodWeight)) {
            throw new IllegalArgumentException("A feeding activity requires an animal and positive food weight.");
        }
        this.animal = animal;
        this.foodWeight = foodWeight;
    }

    public Animal getAnimal() {
        return animal;
    }

    public double getFoodWeight() {
        return foodWeight;
    }

    @Override
    public String getInformation() {
        return "[FEEDING]" + System.lineSeparator() + getCommonInformation() + System.lineSeparator()
                + "Animal: " + animal.getId() + " - " + animal.getName() + System.lineSeparator()
                + "Food weight: " + foodWeight + " kg";
    }

    @Override
    public Role getRequiredRole() {
        return Role.CARETAKER;
    }

    @Override
    public String getAnimalId() {
        return animal.getId();
    }
}
