package model.activity;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

import model.Habitat;
import model.Identifiable;
import model.ZooValidation;
import model.employee.Employee;
import model.employee.Role;

/**
 * Common scheduling and assignment data for zoo care activities.
 */
public abstract class Activity implements Serializable, Identifiable {
    private static final long serialVersionUID = 1L;
    private final String id;
    private final Habitat habitat;
    private Employee responsible;
    private final LocalDate schedulingDate;
    private final LocalDate registrationDate;
    private ActivityStatus status;
    private LocalDate executionDate;
    private double cost;

    protected Activity(String id, Habitat habitat, Employee responsible, LocalDate schedulingDate) {
        if (!ZooValidation.isNonBlank(id) || habitat == null || responsible == null || schedulingDate == null) {
            throw new IllegalArgumentException("Activity ID, habitat, responsible employee, and schedule are required.");
        }
        this.id = id;
        this.habitat = habitat;
        this.responsible = responsible;
        this.schedulingDate = schedulingDate;
        this.registrationDate = LocalDate.now();
        this.status = ActivityStatus.ASSIGNED;
    }

    @Override
    public String getId() {
        return id;
    }

    public Habitat getHabitat() {
        return habitat;
    }

    public Employee getResponsible() {
        return responsible;
    }

    public void setResponsible(Employee responsible) {
        if (responsible == null) {
            throw new IllegalArgumentException("Activity responsible employee is required.");
        }
        this.responsible = responsible;
    }

    public LocalDate getSchedulingDate() {
        return schedulingDate;
    }

    public LocalDate getRegistrationDate() {
        return registrationDate;
    }

    public ActivityStatus getStatus() {
        return status;
    }

    public LocalDate getExecutionDate() {
        return executionDate;
    }

    public double getCost() {
        return cost;
    }

    public boolean finish(double cost) {
        if (status != ActivityStatus.ASSIGNED || !Double.isFinite(cost) || cost < 0
                || schedulingDate.isAfter(LocalDate.now())) {
            return false;
        }
        status = ActivityStatus.FINISHED;
        executionDate = LocalDate.now();
        this.cost = cost;
        return true;
    }

    public boolean finish() {
        return finish(0);
    }

    public boolean cancel() {
        if (status != ActivityStatus.ASSIGNED) {
            return false;
        }
        status = ActivityStatus.CANCELED;
        return true;
    }

    protected String getCommonInformation() {
        return "ID: " + id + System.lineSeparator()
                + "Habitat: " + habitat.getId() + " - " + habitat.getName() + System.lineSeparator()
                + "Responsible: " + responsible.getId() + " - " + responsible.getFullName() + System.lineSeparator()
                + "Registered: " + registrationDate + System.lineSeparator()
                + "Scheduled: " + schedulingDate + System.lineSeparator()
                + "Status: " + status.getDescription()
                + System.lineSeparator() + "Cost: " + cost
                + (executionDate == null ? "" : System.lineSeparator() + "Executed: " + executionDate);
    }

    public abstract String getInformation();

    public abstract Role getRequiredRole();

    public String getAnimalId() {
        return null;
    }

    public boolean isMedical() {
        return false;
    }

    @Override
    public String generateId() {
        LocalDateTime now = LocalDateTime.now();
        return "AC" + now.getMinute() + now.getSecond() + now.getNano();
    }
}
