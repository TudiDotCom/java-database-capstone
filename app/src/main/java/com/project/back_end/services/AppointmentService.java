package com.project.back_end.services;

import com.project.back_end.repo.AppointmentRepository;
import com.project.back_end.repo.DoctorRepository;
import com.project.back_end.repo.PatientRepository;
import com.project.back_end.model.Appointment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final TokenService tokenService;
    private final Service sharedService; // Assuming this is some common service you have

    // Constructor injection for all dependencies
    public AppointmentService(AppointmentRepository appointmentRepository,
                              PatientRepository patientRepository,
                              DoctorRepository doctorRepository,
                              TokenService tokenService,
                              Service sharedService) {
        this.appointmentRepository = appointmentRepository;
        this.patientRepository = patientRepository;
        this.doctorRepository = doctorRepository;
        this.tokenService = tokenService;
        this.sharedService = sharedService;
    }

    // 4. Book Appointment
    @Transactional
    public int bookAppointment(Appointment appointment) {
        try {
            appointmentRepository.save(appointment);
            return 1; // success
        } catch (Exception e) {
            // log error if needed
            return 0; // failure
        }
    }

    // 5. Update Appointment
    @Transactional
    public String updateAppointment(Long appointmentId, Appointment updatedAppointment, Long patientId) {
        Optional<Appointment> existingOpt = appointmentRepository.findById(appointmentId);
        if (existingOpt.isEmpty()) {
            return "Appointment not found.";
        }
        Appointment existing = existingOpt.get();

        if (!existing.getPatientId().equals(patientId)) {
            return "Unauthorized update attempt.";
        }

        // Add your logic here to check if appointment is available for update
        // and if the doctor is available at the specified time

        // Example (simplified)
        // if doctor not available:
        // return "Doctor not available at the selected time.";

        // Update fields
        existing.setAppointmentTime(updatedAppointment.getAppointmentTime());
        existing.setStatus(updatedAppointment.getStatus());
        // ... other fields as needed

        appointmentRepository.save(existing);
        return "Appointment updated successfully.";
    }

    // 6. Cancel Appointment
    @Transactional
    public String cancelAppointment(Long appointmentId, Long patientId) {
        Optional<Appointment> existingOpt = appointmentRepository.findById(appointmentId);
        if (existingOpt.isEmpty()) {
            return "Appointment not found.";
        }
        Appointment existing = existingOpt.get();

        if (!existing.getPatientId().equals(patientId)) {
            return "Unauthorized cancel attempt.";
        }

        appointmentRepository.deleteById(appointmentId);
        return "Appointment canceled successfully.";
    }

    // 7. Get Appointments (for doctor, optional patient filter)
    @Transactional(readOnly = true)
    public List<Appointment> getAppointments(Long doctorId, LocalDateTime dayStart, LocalDateTime dayEnd, String patientName) {
        if (patientName == null || patientName.isEmpty()) {
            return appointmentRepository.findByDoctorIdAndAppointmentTimeBetween(doctorId, dayStart, dayEnd);
        } else {
            return appointmentRepository.findByDoctorIdAndPatient_NameContainingIgnoreCaseAndAppointmentTimeBetween(
                    doctorId, patientName, dayStart, dayEnd);
        }
    }

    // 8. Change Status
    @Transactional
    public void changeStatus(int status, Long appointmentId) {
        appointmentRepository.updateStatus(status, appointmentId);
    }
}

// 1. **Add @Service Annotation**:
//    - To indicate that this class is a service layer class for handling business logic.
//    - The `@Service` annotation should be added before the class declaration to mark it as a Spring service component.
//    - Instruction: Add `@Service` above the class definition.

// 2. **Constructor Injection for Dependencies**:
//    - The `AppointmentService` class requires several dependencies like `AppointmentRepository`, `Service`, `TokenService`, `PatientRepository`, and `DoctorRepository`.
//    - These dependencies should be injected through the constructor.
//    - Instruction: Ensure constructor injection is used for proper dependency management in Spring.

// 3. **Add @Transactional Annotation for Methods that Modify Database**:
//    - The methods that modify or update the database should be annotated with `@Transactional` to ensure atomicity and consistency of the operations.
//    - Instruction: Add the `@Transactional` annotation above methods that interact with the database, especially those modifying data.

// 4. **Book Appointment Method**:
//    - Responsible for saving the new appointment to the database.
//    - If the save operation fails, it returns `0`; otherwise, it returns `1`.
//    - Instruction: Ensure that the method handles any exceptions and returns an appropriate result code.

// 5. **Update Appointment Method**:
//    - This method is used to update an existing appointment based on its ID.
//    - It validates whether the patient ID matches, checks if the appointment is available for updating, and ensures that the doctor is available at the specified time.
//    - If the update is successful, it saves the appointment; otherwise, it returns an appropriate error message.
//    - Instruction: Ensure proper validation and error handling is included for appointment updates.

// 6. **Cancel Appointment Method**:
//    - This method cancels an appointment by deleting it from the database.
//    - It ensures the patient who owns the appointment is trying to cancel it and handles possible errors.
//    - Instruction: Make sure that the method checks for the patient ID match before deleting the appointment.

// 7. **Get Appointments Method**:
//    - This method retrieves a list of appointments for a specific doctor on a particular day, optionally filtered by the patient's name.
//    - It uses `@Transactional` to ensure that database operations are consistent and handled in a single transaction.
//    - Instruction: Ensure the correct use of transaction boundaries, especially when querying the database for appointments.

// 8. **Change Status Method**:
//    - This method updates the status of an appointment by changing its value in the database.
//    - It should be annotated with `@Transactional` to ensure the operation is executed in a single transaction.
//    - Instruction: Add `@Transactional` before this method to ensure atomicity when updating appointment status.

