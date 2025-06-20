// doctorCard.js

// === Imports ===
import { showBookingOverlay } from '../loggedPatient.js';
import { deleteDoctor } from '../services/doctorService.js';
import { getPatientData } from '../services/patientServices.js';

// === Exported Function ===
export function createDoctorCard(doctor) {
  // Create main card container
  const card = document.createElement("div");
  card.classList.add("doctor-card");

  // Fetch current user role
  const role = localStorage.getItem("userRole");

  // === Create Doctor Info Section ===
  const infoDiv = document.createElement("div");
  infoDiv.classList.add("doctor-info");

  const name = document.createElement("h3");
  name.textContent = doctor.name;

  const specialization = document.createElement("p");
  specialization.textContent = `Specialization: ${doctor.specialty}`;

  const email = document.createElement("p");
  email.textContent = `Email: ${doctor.email}`;

  const availability = document.createElement("p");
  availability.textContent = `Available: ${doctor.availableTimes.join(", ")}`;

  // Append info elements
  infoDiv.appendChild(name);
  infoDiv.appendChild(specialization);
  infoDiv.appendChild(email);
  infoDiv.appendChild(availability);

  // === Create Action Button Container ===
  const actionsDiv = document.createElement("div");
  actionsDiv.classList.add("card-actions");

  // === Admin Role: Show Delete Button ===
  if (role === "admin") {
    const removeBtn = document.createElement("button");
    removeBtn.textContent = "Delete";
    removeBtn.classList.add("delete-btn");

    removeBtn.addEventListener("click", async () => {
      const confirmed = confirm(`Are you sure you want to delete Dr. ${doctor.name}?`);
      if (!confirmed) return;

      const token = localStorage.getItem("token");
      if (!token) {
        alert("Admin session expired. Please log in again.");
        return;
      }

      const result = await deleteDoctor(doctor.id, token);
      if (result.success) {
        alert("Doctor deleted successfully.");
        card.remove();
      } else {
        alert("Failed to delete doctor: " + result.message);
      }
    });

    actionsDiv.appendChild(removeBtn);
  }

  // === Not Logged-in Patient: Show Alert on Book Now ===
  else if (role === "patient") {
    const bookNow = document.createElement("button");
    bookNow.textContent = "Book Now";
    bookNow.classList.add("book-btn");

    bookNow.addEventListener("click", () => {
      alert("Please log in as a patient to book an appointment.");
    });

    actionsDiv.appendChild(bookNow);
  }

  // === Logged-in Patient: Enable Booking Overlay ===
  else if (role === "loggedPatient") {
    const bookNow = document.createElement("button");
    bookNow.textContent = "Book Now";
    bookNow.classList.add("book-btn");

    bookNow.addEventListener("click", async (e) => {
      const token = localStorage.getItem("token");
      if (!token) {
        alert("Session expired. Please log in again.");
        return;
      }

      try {
        const patientData = await getPatientData(token);
        showBookingOverlay(e, doctor, patientData);
      } catch (error) {
        console.error("Error fetching patient data:", error);
        alert("Could not fetch patient data. Try again later.");
      }
    });

    actionsDiv.appendChild(bookNow);
  }

  // === Final Assembly ===
  card.appendChild(infoDiv);
  card.appendChild(actionsDiv);

  return card;
}



/*
Import the overlay function for booking appointments from loggedPatient.js

  Import the deleteDoctor API function to remove doctors (admin role) from docotrServices.js

  Import function to fetch patient details (used during booking) from patientServices.js

  Function to create and return a DOM element for a single doctor card
    Create the main container for the doctor card
    Retrieve the current user role from localStorage
    Create a div to hold doctor information
    Create and set the doctor’s name
    Create and set the doctor's specialization
    Create and set the doctor's email
    Create and list available appointment times
    Append all info elements to the doctor info container
    Create a container for card action buttons
    === ADMIN ROLE ACTIONS ===
      Create a delete button
      Add click handler for delete button
     Get the admin token from localStorage
        Call API to delete the doctor
        Show result and remove card if successful
      Add delete button to actions container
   
    === PATIENT (NOT LOGGED-IN) ROLE ACTIONS ===
      Create a book now button
      Alert patient to log in before booking
      Add button to actions container
  
    === LOGGED-IN PATIENT ROLE ACTIONS === 
      Create a book now button
      Handle booking logic for logged-in patient   
        Redirect if token not available
        Fetch patient data with token
        Show booking overlay UI with doctor and patient info
      Add button to actions container
   
  Append doctor info and action buttons to the car
  Return the complete doctor card element
*/
