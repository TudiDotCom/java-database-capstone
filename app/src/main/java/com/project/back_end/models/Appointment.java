package com.project.back_end.models;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import jakarta.validation.constraints.*;

import jakarta.persistence.*;

@Entity
public class Appointment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne
    private Doctor doctor;
    
    @NotNull
    @ManyToOne
    private Patient patient;

    @Future(message = "Appointment time must be in the future")
    private LocalDateTime appointmentTime;
    
    @NotNull
    private int status;

    @Transient
    //Returns the end time of the appointment (1 hour after start time)
    private LocalDateTime getEndTime(){
        return appointmentTime.plusHours(1L);
    } 

    @Transient
    //Returns only the date portion of the appointment
    private LocalDate getAppointmentDate() {
        return appointmentTime.toLocalDate();
    } 

    @Transient
    private LocalTime getAppointmentTimeOnly() {
        return appointmentTime.toLocalTime();
    }

     // No-argument constructor
    public Appointment() {
    
    }

    public Appointment(Long id, Doctor doctor, Patient patient, LocalDateTime appointmentTime, int status) {
        this.id = id;
        this.doctor = doctor;
        this.patient = patient;
        this.appointmentTime = appointmentTime;
        this.status = status;
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Doctor getDoctor() {
        return doctor;
    }

    public void setDoctor(Doctor doctor) {
        this.doctor = doctor;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public LocalDateTime getAppointmentTime() {
        return appointmentTime;
    }

    public void setAppointmentTime(LocalDateTime appointmentTime) {
        this.appointmentTime = appointmentTime;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
// 5. 'status' field:
//    - Type: private int
//    - Description:
//      - Represents the current status of the appointment. It is an integer where:
//        - 0 means the appointment is scheduled.
//        - 1 means the appointment has been completed.
//      - The @NotNull annotation ensures that the status field is not null.

// 6. 'getEndTime' method:
//    - Type: private LocalDateTime
//    - Description:
//      - This method is a transient field (not persisted in the database).
//      - It calculates the end time of the appointment by adding one hour to the start time (appointmentTime).
//      - It is used to get an estimated appointment end time for display purposes.

// 7. 'getAppointmentDate' method:
//    - Type: private LocalDate
//    - Description:
//      - This method extracts only the date part from the appointmentTime field.
//      - It returns a LocalDate object representing just the date (without the time) of the scheduled appointment.

// 8. 'getAppointmentTimeOnly' method:
//    - Type: private LocalTime
//    - Description:
//      - This method extracts only the time part from the appointmentTime field.
//      - It returns a LocalTime object representing just the time (without the date) of the scheduled appointment.