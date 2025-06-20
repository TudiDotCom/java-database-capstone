package com.project.back_end.controllers;

import com.project.back_end.models.Login;
import com.project.back_end.models.Patient;
import com.project.back_end.services.PatientService;
import com.project.back_end.services.Service;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/patient")
public class PatientController {

    private final PatientService patientService;
    private final Service service;

    public PatientController(PatientService patientService, Service service) {
        this.patientService = patientService;
        this.service = service;
    }

    // 3. Get patient details by token
    @GetMapping("/details/{token}")
    public ResponseEntity<?> getPatient(@PathVariable String token) {
        if (!service.validateToken(token, "patient")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Invalid or expired token"));
        }

        String email = service.extractEmailFromToken(token);
        Patient patient = patientService.getPatientByEmail(email);
        if (patient == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Patient not found"));
        }

        return ResponseEntity.ok(patient);
    }

    // 4. Register new patient
    @PostMapping("/register")
    public ResponseEntity<?> createPatient(@Validated @RequestBody Patient patient) {
        boolean isValid = service.validatePatient(patient.getEmail(), patient.getPhone());
        if (!isValid) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("message", "Patient with same email or phone already exists"));
        }

        boolean created = patientService.createPatient(patient);
        if (created) {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of("message", "Patient registered successfully"));
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Failed to register patient"));
        }
    }

    // 5. Patient login
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Login login) {
        Map<String, Object> response = service.validatePatientLogin(login);
        HttpStatus status = (Boolean.TRUE.equals(response.get("success"))) ? HttpStatus.OK : HttpStatus.UNAUTHORIZED;
        return ResponseEntity.status(status).body(response);
    }

    // 6. Get patient appointments by patient ID, token, and user role
    @GetMapping("/appointments/{patientId}/{token}/{user}")
    public ResponseEntity<?> getPatientAppointment(
            @PathVariable Long patientId,
            @PathVariable String token,
            @PathVariable String user) {

        if (!service.validateToken(token, user)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Invalid or expired token"));
        }

        List<?> appointments = patientService.getAppointmentsByPatientId(patientId);
        return ResponseEntity.ok(Map.of("appointments", appointments));
    }

    // 7. Filter patient's appointments by condition, doctor name, and token
    @GetMapping("/appointments/filter/{condition}/{name}/{token}")
    public ResponseEntity<?> filterPatientAppointment(
            @PathVariable(required = false) String condition,
            @PathVariable(required = false) String name,
            @PathVariable String token) {

        if (!service.validateToken(token, "patient")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Invalid or expired token"));
        }

        List<?> filteredAppointments = service.filterPatient(condition, name, token);
        return ResponseEntity.ok(Map.of("appointments", filteredAppointments));
    }
}


// 1. Set Up the Controller Class:
//    - Annotate the class with `@RestController` to define it as a REST API controller for patient-related operations.
//    - Use `@RequestMapping("/patient")` to prefix all endpoints with `/patient`, grouping all patient functionalities under a common route.


// 2. Autowire Dependencies:
//    - Inject `PatientService` to handle patient-specific logic such as creation, retrieval, and appointments.
//    - Inject the shared `Service` class for tasks like token validation and login authentication.


// 3. Define the `getPatient` Method:
//    - Handles HTTP GET requests to retrieve patient details using a token.
//    - Validates the token for the `"patient"` role using the shared service.
//    - If the token is valid, returns patient information; otherwise, returns an appropriate error message.


// 4. Define the `createPatient` Method:
//    - Handles HTTP POST requests for patient registration.
//    - Accepts a validated `Patient` object in the request body.
//    - First checks if the patient already exists using the shared service.
//    - If validation passes, attempts to create the patient and returns success or error messages based on the outcome.


// 5. Define the `login` Method:
//    - Handles HTTP POST requests for patient login.
//    - Accepts a `Login` DTO containing email/username and password.
//    - Delegates authentication to the `validatePatientLogin` method in the shared service.
//    - Returns a response with a token or an error message depending on login success.


// 6. Define the `getPatientAppointment` Method:
//    - Handles HTTP GET requests to fetch appointment details for a specific patient.
//    - Requires the patient ID, token, and user role as path variables.
//    - Validates the token using the shared service.
//    - If valid, retrieves the patient's appointment data from `PatientService`; otherwise, returns a validation error.


// 7. Define the `filterPatientAppointment` Method:
//    - Handles HTTP GET requests to filter a patient's appointments based on specific conditions.
//    - Accepts filtering parameters: `condition`, `name`, and a token.
//    - Token must be valid for a `"patient"` role.
//    - If valid, delegates filtering logic to the shared service and returns the filtered result.



