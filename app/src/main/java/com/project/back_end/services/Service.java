
package com.project.back_end.services;

import com.project.back_end.models.Admin;

import com.project.back_end.models.Doctor;
import com.project.back_end.models.Patient;
import com.project.back_end.repo.AdminRepository;
import com.project.back_end.repo.DoctorRepository;
import com.project.back_end.repo.PatientRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.logging.Logger;

@Service
public class Service {

    private final TokenService tokenService;
    private final AdminRepository adminRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final AppointmentService appointmentService;
    private final PatientService patientService;
    private final Logger logger = Logger.getLogger(Service.class.getName());

    // Constructor injection for all dependencies
    public Service(TokenService tokenService,
                   AdminRepository adminRepository,
                   DoctorRepository doctorRepository,
                   PatientRepository patientRepository,
                   AppointmentService appointmentService,
                   PatientService patientService) {
        this.tokenService = tokenService;
        this.adminRepository = adminRepository;
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
        this.appointmentService = appointmentService;
        this.patientService = patientService;
    }

    // 3. validateToken Method
    public ResponseEntity<String> validateToken(String token, String userEmail) {
        try {
            if (token == null || !tokenService.validateToken(token, userEmail)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                     .body("Invalid or expired token.");
            }
            return ResponseEntity.ok("Token is valid.");
        } catch (Exception e) {
            logger.severe("Error validating token: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("Internal error during token validation.");
        }
    }

    public boolean isTokenValid(String token, String userRole) {
        try {
            return token != null && tokenService.validateToken(token, userRole);
        } catch (Exception e) {
            logger.severe("Error validating token: " + e.getMessage());
            return false;
        }
    }

    // 4. validateAdmin Method
    public ResponseEntity<String> validateAdmin(String username, String password) {
        try {
            Admin admin = adminRepository.findByUsername(username);
            if (admin == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Admin not found.");
            }
            if (!admin.getPassword().equals(password)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid password.");
            }
            String token = tokenService.generateToken(admin.getUsername());
            return ResponseEntity.ok(token);
        } catch (Exception e) {
            logger.severe("Error validating admin login: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("Internal server error during admin validation.");
        }
    }

    // 5. filterDoctor Method
    @Transactional
    public List<Doctor> filterDoctor(String name, String specialty, String timePeriod) {
        // Logic to handle various combinations of filters:
        // If none provided, return all doctors.
        // Otherwise filter based on the parameters.
        // Delegation to doctorService is recommended for modularity.
        if ((name == null || name.isEmpty()) &&
            (specialty == null || specialty.isEmpty()) &&
            (timePeriod == null || timePeriod.isEmpty())) {
            return doctorRepository.findAll();
        }
        // Assume doctorService has a method like filterDoctorsByNameSpecialtyAndTime
        return doctorRepository.filterDoctorsByNameSpecialtyAndTime(name, specialty, timePeriod);
    }

    // 6. validateAppointment Method
    public int validateAppointment(Long doctorId, String appointmentDate, String appointmentTime) {
        try {
            if (!doctorRepository.existsById(doctorId)) {
                return -1; // Doctor does not exist
            }
            List<String> availableSlots = appointmentService.getDoctorAvailability(doctorId, appointmentDate);
            for (String slot : availableSlots) {
                if (slot.equals(appointmentTime)) {
                    return 1; // Valid appointment time
                }
            }
            return 0; // Time slot not available
        } catch (Exception e) {
            logger.severe("Error validating appointment: " + e.getMessage());
            return 0;
        }
    }

    // 7. validatePatient Method
    public boolean validatePatient(String email, String phone) {
        try {
            Patient patient = patientRepository.findByEmailOrPhone(email, phone);
            return patient == null; // True if no matching patient found
        } catch (Exception e) {
            logger.severe("Error validating patient: " + e.getMessage());
            return false;
        }
    }

    // 8. validatePatientLogin Method
    public ResponseEntity<String> validatePatientLogin(String email, String password) {
        try {
            Patient patient = patientRepository.findByEmail(email);
            if (patient == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Patient not found.");
            }
            if (!patient.getPassword().equals(password)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid password.");
            }
            String token = tokenService.generateToken(patient.getEmail());
            return ResponseEntity.ok(token);
        } catch (Exception e) {
            logger.severe("Error validating patient login: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("Internal server error during patient login.");
        }
    }

    // 9. filterPatient Method
    public ResponseEntity<?> filterPatient(String token, String condition, String doctorName) {
        try {
            String email = tokenService.extractEmail(token);
            if (email == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token.");
            }
            if ((condition == null || condition.isEmpty()) && (doctorName == null || doctorName.isEmpty())) {
                // No filters, return all appointments
                return ResponseEntity.ok(patientService.getPatientAppointment(email));
            } else if (condition != null && !condition.isEmpty() && (doctorName == null || doctorName.isEmpty())) {
                return ResponseEntity.ok(patientService.filterByCondition(email, condition));
            } else if ((condition == null || condition.isEmpty()) && doctorName != null && !doctorName.isEmpty()) {
                return ResponseEntity.ok(patientService.filterByDoctor(email, doctorName));
            } else {
                return ResponseEntity.ok(patientService.filterByDoctorAndCondition(email, doctorName, condition));
            }
        } catch (Exception e) {
            logger.severe("Error filtering patient appointments: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("Internal server error while filtering appointments.");
        }
    }
}


// 1. **@Service Annotation**
// The @Service annotation marks this class as a service component in Spring. This allows Spring to automatically detect it through component scanning
// and manage its lifecycle, enabling it to be injected into controllers or other services using @Autowired or constructor injection.

// 2. **Constructor Injection for Dependencies**
// The constructor injects all required dependencies (TokenService, Repositories, and other Services). This approach promotes loose coupling, improves testability,
// and ensures that all required dependencies are provided at object creation time.

// 3. **validateToken Method**
// This method checks if the provided JWT token is valid for a specific user. It uses the TokenService to perform the validation.
// If the token is invalid or expired, it returns a 401 Unauthorized response with an appropriate error message. This ensures security by preventing
// unauthorized access to protected resources.

// 4. **validateAdmin Method**
// This method validates the login credentials for an admin user.
// - It first searches the admin repository using the provided username.
// - If an admin is found, it checks if the password matches.
// - If the password is correct, it generates and returns a JWT token (using the admin’s username) with a 200 OK status.
// - If the password is incorrect, it returns a 401 Unauthorized status with an error message.
// - If no admin is found, it also returns a 401 Unauthorized.
// - If any unexpected error occurs during the process, a 500 Internal Server Error response is returned.
// This method ensures that only valid admin users can access secured parts of the system.

// 5. **filterDoctor Method**
// This method provides filtering functionality for doctors based on name, specialty, and available time slots.
// - It supports various combinations of the three filters.
// - If none of the filters are provided, it returns all available doctors.
// This flexible filtering mechanism allows the frontend or consumers of the API to search and narrow down doctors based on user criteria.

// 6. **validateAppointment Method**
// This method validates if the requested appointment time for a doctor is available.
// - It first checks if the doctor exists in the repository.
// - Then, it retrieves the list of available time slots for the doctor on the specified date.
// - It compares the requested appointment time with the start times of these slots.
// - If a match is found, it returns 1 (valid appointment time).
// - If no matching time slot is found, it returns 0 (invalid).
// - If the doctor doesn’t exist, it returns -1.
// This logic prevents overlapping or invalid appointment bookings.

// 7. **validatePatient Method**
// This method checks whether a patient with the same email or phone number already exists in the system.
// - If a match is found, it returns false (indicating the patient is not valid for new registration).
// - If no match is found, it returns true.
// This helps enforce uniqueness constraints on patient records and prevent duplicate entries.

// 8. **validatePatientLogin Method**
// This method handles login validation for patient users.
// - It looks up the patient by email.
// - If found, it checks whether the provided password matches the stored one.
// - On successful validation, it generates a JWT token and returns it with a 200 OK status.
// - If the password is incorrect or the patient doesn't exist, it returns a 401 Unauthorized with a relevant error.
// - If an exception occurs, it returns a 500 Internal Server Error.
// This method ensures only legitimate patients can log in and access their data securely.

// 9. **filterPatient Method**
// This method filters a patient's appointment history based on condition and doctor name.
// - It extracts the email from the JWT token to identify the patient.
// - Depending on which filters (condition, doctor name) are provided, it delegates the filtering logic to PatientService.
// - If no filters are provided, it retrieves all appointments for the patient.
// This flexible method supports patient-specific querying and enhances user experience on the client side.
