package model.activity;

import java.time.LocalDate;

import model.Habitat;
import model.ZooValidation;
import model.animal.Animal;
import model.employee.Employee;
import model.employee.Role;

public class MedicalCheckupActivity extends Activity {
    private static final long serialVersionUID = 1L;

    private final Animal animal;
    private final String reason;
    private double registeredWeight;
    private String observations;
    private String medication;
    private String resultingHealthStatus;
    private LocalDate followUpDate;

    public MedicalCheckupActivity(String id, Habitat habitat, Employee responsible, LocalDate schedulingDate,
            Animal animal, String reason) {
        super(id, habitat, responsible, schedulingDate);
        if (animal == null || !ZooValidation.isNonBlank(reason)) {
            throw new IllegalArgumentException("A medical checkup requires an animal and reason.");
        }
        this.animal = animal;
        this.reason = reason.trim();
    }

    public Animal getAnimal() {
        return animal;
    }

    public String getReason() {
        return reason;
    }

    public boolean canComplete(double treatmentCost, double weight, String healthStatus,
            String observations, LocalDate followUpDate) {
        return getStatus() == ActivityStatus.ASSIGNED
                && ZooValidation.isPositiveFinite(weight) && isValidHealthStatus(healthStatus)
                && ZooValidation.isNonBlank(observations)
                && !Double.isNaN(treatmentCost) && !Double.isInfinite(treatmentCost) && treatmentCost >= 0
                && !getSchedulingDate().isAfter(LocalDate.now())
                && (followUpDate == null || followUpDate.isAfter(LocalDate.now()));
    }

    private boolean isValidHealthStatus(String healthStatus) {
        if (!ZooValidation.isNonBlank(healthStatus)) {
            return false;
        }
        String normalized = healthStatus.trim();
        return "healthy".equalsIgnoreCase(normalized)
                || "quarantine".equalsIgnoreCase(normalized)
                || "recovery".equalsIgnoreCase(normalized)
                || "under observation".equalsIgnoreCase(normalized)
                || "cuarentena".equalsIgnoreCase(normalized)
                || "recuperación".equalsIgnoreCase(normalized)
                || "recuperacion".equalsIgnoreCase(normalized)
                || "en observación".equalsIgnoreCase(normalized)
                || "en observacion".equalsIgnoreCase(normalized);
    }

    public boolean complete(double treatmentCost, double weight, String healthStatus,
            String observations, String medication, LocalDate followUpDate) {
        if (!canComplete(treatmentCost, weight, healthStatus, observations, followUpDate)) {
            return false;
        }
        if (!finish(treatmentCost)) {
            return false;
        }
        registeredWeight = weight;
        resultingHealthStatus = healthStatus.trim();
        this.observations = observations.trim();
        this.medication = medication == null ? "" : medication.trim();
        this.followUpDate = followUpDate;
        animal.setWeight(weight);
        animal.setHealthStatus(resultingHealthStatus);
        return true;
    }

    public double getRegisteredWeight() {
        return registeredWeight;
    }

    public String getObservations() {
        return observations;
    }

    public String getMedication() {
        return medication;
    }

    public String getResultingHealthStatus() {
        return resultingHealthStatus;
    }

    public LocalDate getFollowUpDate() {
        return followUpDate;
    }

    @Override
    public Role getRequiredRole() {
        return Role.VETERINARIAN;
    }

    @Override
    public String getAnimalId() {
        return animal.getId();
    }

    @Override
    public boolean isMedical() {
        return true;
    }

    @Override
    public String getInformation() {
        return "[MEDICAL CHECKUP]" + System.lineSeparator() + getCommonInformation() + System.lineSeparator()
                + "Animal: " + animal.getId() + " - " + animal.getName() + System.lineSeparator()
                + "Reason: " + reason
                + (getStatus() == ActivityStatus.FINISHED
                        ? System.lineSeparator() + "Weight: " + registeredWeight
                        + System.lineSeparator() + "Health: " + resultingHealthStatus
                        + System.lineSeparator() + "Observations: " + observations
                        + System.lineSeparator() + "Medication: " + medication
                        + System.lineSeparator() + "Follow-up: " + followUpDate
                        : "");
    }
}
