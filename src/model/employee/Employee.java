package model.employee;

import java.io.Serializable;
import java.time.LocalDateTime;

import model.Identifiable;
import model.ZooValidation;

/**
 * Represents a zoo employee member and stores employee, contact, and employment details.
 */
public class Employee implements Serializable,Identifiable {
    private static final long serialVersionUID = 1L;

    private final String id;
    private String fullName;
    private Role role;
    private String phone;
    private String mail;
    private String state;

    /**
     * Creates an employee with employee and employment information.
     *
     * Postconditions:
     * - Instantiates an Employee with an assigned identifier and field values.
     *
     * @param fullName the employee's full name
     * @param role the employee role
     * @param phone the phone number
     * @param mail the email address
     * @param state the employee's current employment state
     */
    public Employee(String fullName, Role role, String phone, String mail, String state) {
        if (!ZooValidation.isNonBlank(fullName) || role == null
                || !ZooValidation.isNonBlank(phone) || !ZooValidation.isNonBlank(mail)
                || !ZooValidation.isNonBlank(state)) {
            throw new IllegalArgumentException("Employee name, role, phone, email, and state are required.");
        }
        this.id = generateId();
        this.fullName = fullName.trim();
        this.phone = phone.trim();
        this.role = role;
        this.mail = mail.trim();
        this.state = state.trim();
    }

    /**
     * Returns the employee member information in a single formatted string.
     *
     * Postconditions:
     * - Returns a multi-line formatted summary of the employee member's attributes.
     *
     * @return the formatted employee information
     */
    public String getInformation() {
        return "\n========================================\n"
                + "[EMPLOYEE]" + System.lineSeparator()
                + "ID: " + id + System.lineSeparator()
                + "Full name: " + fullName + System.lineSeparator()
                + "Role: " + role.getText() + System.lineSeparator()
                + "Phone: " + phone + System.lineSeparator()
                + "Email: " + mail + System.lineSeparator()
                + "State: " + state + System.lineSeparator()
                + "========================================";
    }

    /**
     * Returns the unique identifier of the employee member.
     *
     * @return the employee member identifier
     */
    @Override 
    public String getId() {
        return id;
    }

    /**
     * Returns the full name of the employee member.
     *
     * @return the full name
     */
    public String getFullName() {
        return fullName;
    }

    /**
     * Returns the role assigned to the employee.
     *
     * @return the employee role
     */
    public Role getRole() {
        return role;
    }

    /**
     * Returns the phone number of the employee member.
     *
     * @return the phone number
     */
    public String getPhone() {
        return phone;
    }

    /**
     * Returns the email address of the employee member.
     *
     * @return the email address
     */
    public String getMail() {
        return mail;
    }

    /**
     * Returns the current employment state of the employee member.
     *
     * @return the state string
     */
    public String getState() {
        return state;
    }

    /**
     * Updates the full name of the employee member.
     *
     * @param fullName the new full name
     */
    public void setFullName(String fullName) {
        if (!ZooValidation.isNonBlank(fullName)) {
            throw new IllegalArgumentException("Employee name cannot be blank.");
        }
        this.fullName = fullName.trim();
    }

    /**
     * Updates the role of the employee member.
     *
     * @param role the new role
     */
    public void setRole(Role role) {
        if (role == null) {
            throw new IllegalArgumentException("Employee role is required.");
        }
        this.role = role;
    }

    /**
     * Updates the phone number of the employee member.
     *
     * @param phone the new phone number
     */
    public void setPhone(String phone) {
        if (!ZooValidation.isNonBlank(phone)) {
            throw new IllegalArgumentException("Employee phone cannot be blank.");
        }
        this.phone = phone.trim();
    }

    /**
     * Updates the email address of the employee member.
     *
     * @param mail the new email address
     */
    public void setMail(String mail) {
        if (!ZooValidation.isNonBlank(mail)) {
            throw new IllegalArgumentException("Employee email cannot be blank.");
        }
        this.mail = mail.trim();
    }

    /**
     * Updates the employment state of the employee member.
     *
     * @param state the new state
     */
    public void setState(String state) {
        if (!ZooValidation.isNonBlank(state)) {
            throw new IllegalArgumentException("Employee state cannot be blank.");
        }
        this.state = state.trim();
    }

    @Override
   
    public String generateId() {
       return "E" + LocalDateTime.now().getMinute() + LocalDateTime.now().getSecond() + LocalDateTime.now().getNano();
    }
}