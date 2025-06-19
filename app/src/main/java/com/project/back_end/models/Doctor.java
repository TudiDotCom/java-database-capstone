package com.project.back_end.models;
import jakarta.persistence.*;
import java.util.List;
import jakarta.validation.constraints.*;
import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
public class Doctor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Please insert your name")
    @Size(min = 3, max = 100)
    private String name;

    @NotNull(message = "Ensures that a specialty must be provided")
    @Size(min = 3, max = 50, message = "the specialty name is between 3 and 50 characters long")
    private String specialty;

    @NotNull
    @Email
    private String email;

    @NotNull(message = "Password required")
    @Size(min = 6, message = "Password must be at least 6 chars")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    @NotNull(message = "Phone is mandatory")
    @Pattern(regexp = "\\d{10}", message = "Phone number must be 10 digits")
    private String phone;

    @ElementCollection
    private List<String> availableTimes;

    // No argument constructor
    public Doctor() {
    }

    // Parameterized constructor
    public Doctor(Long id, String name, String specialty, String email, String password, String phone,
            List<String> availableTimes) {
        this.id = id;
        this.name = name;
        this.specialty = specialty;
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.availableTimes = availableTimes;
    }

    // Getters and setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSpecialty() {
        return specialty;
    }

    public void setSpecialty(String specialty) {
        this.specialty = specialty;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public List<String> getAvailableTimes() {
        return availableTimes;
    }

    public void setAvailableTimes(List<String> availableTimes) {
        this.availableTimes = availableTimes;
    }
    // 'name' field:
    // - Type: private String
    // - Description:
    // - Represents the doctor's name.
    // - The @NotNull annotation ensures that the doctor's name is required.
    // - The @Size(min = 3, max = 100) annotation ensures that the name length is
    // between 3 and 100 characters.
    // - Provides validation for correct input and user experience.

    // 3. 'specialty' field:
    // - Type: private String
    // - Description:
    // - Represents the medical specialty of the doctor.
    // - The @NotNull annotation ensures that a specialty must be provided.
    // - The @Size(min = 3, max = 50) annotation ensures that the specialty name is
    // between 3 and 50 characters long.

    // 4. 'email' field:
    // - Type: private String
    // - Description:
    // - Represents the doctor's email address.
    // - The @NotNull annotation ensures that an email address is required.
    // - The @Email annotation validates that the email address follows a valid
    // email format (e.g., doctor@example.com).

    // 5. 'password' field:
    // - Type: private String
    // - Description:
    // - Represents the doctor's password for login authentication.
    // - The @NotNull annotation ensures that a password must be provided.
    // - The @Size(min = 6) annotation ensures that the password must be at least 6
    // characters long.
    // - The @JsonProperty(access = JsonProperty.Access.WRITE_ONLY) annotation
    // ensures that the password is not serialized in the response (hidden from the
    // frontend).

    // 6. 'phone' field:
    // - Type: private String
    // - Description:
    // - Represents the doctor's phone number.
    // - The @NotNull annotation ensures that a phone number must be provided.
    // - The @Pattern(regexp = "^[0-9]{10}$") annotation validates that the phone
    // number must be exactly 10 digits long.

    // 7. 'availableTimes' field:
    // - Type: private List<String>
    // - Description:
    // - Represents the available times for the doctor in a list of time slots.
    // - Each time slot is represented as a string (e.g., "09:00-10:00",
    // "10:00-11:00").
    // - The @ElementCollection annotation ensures that the list of time slots is
    // stored as a separate collection in the database.

    // 8. Getters and Setters:
    // - Standard getter and setter methods are provided for all fields: id, name,
    // specialty, email, password, phone, and availableTimes.

}
