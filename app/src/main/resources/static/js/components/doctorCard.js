// doctorCard.js - TO DO

import { showBookingOverlay } from './loggedPatient.js';
import { deleteDoctor } from './doctorService.js';
import { getPatientByToken } from './patientServices.js';

/**
 * Creates and returns a DOM element representing a doctor card.
 * @param {Object} doctor - Doctor object with properties: id, name, email, specialty, availableTimes (array).
 * @returns {HTMLElement} - The complete doctor card element.
 */
export function createDoctorCard(doctor) {
  // Create main card container
  const card = document.createElement('div');
  card.classList.add('doctor-card');
  card.dataset.id = doctor.id;

  // Retrieve current user role from localStorage
  const userRole = localStorage.getItem('role');
  const token = localStorage.getItem('token');

  // Doctor info container
  const infoContainer = document.createElement('div');
  infoContainer.classList.add('doctor-info');

  // Doctor Name
  const nameEl = document.createElement('h3');
  nameEl.textContent = doctor.name;
  infoContainer.appendChild(nameEl);

  // Doctor Specialization
  const specialtyEl = document.createElement('p');
  specialtyEl.textContent = `Specialty: ${doctor.specialty}`;
  infoContainer.appendChild(specialtyEl);

  // Doctor Email
  const emailEl = document.createElement('p');
  emailEl.textContent = `Email: ${doctor.email}`;
  infoContainer.appendChild(emailEl);

  // Available appointment times
  const timesEl = document.createElement('p');
  timesEl.textContent = 'Available Times: ';
  if (doctor.availableTimes && doctor.availableTimes.length > 0) {
    timesEl.textContent += doctor.availableTimes.join(', ');
  } else {
    timesEl.textContent += 'No times available';
  }
  infoContainer.appendChild(timesEl);

  // Action buttons container
  const actionsContainer = document.createElement('div');
  actionsContainer.classList.add('doctor-actions');

  // Admin role actions: Delete button
  if (userRole === 'ADMIN') {
    const deleteBtn = document.createElement('button');
    deleteBtn.classList.add('adminBtn');
    deleteBtn.textContent = 'Delete';

    deleteBtn.addEventListener('click', async () => {
      const adminToken = localStorage.getItem('token');
      if (!adminToken) {
        alert('You must be logged in as an admin to delete doctors.');
        return;
      }
      if (confirm(`Are you sure you want to delete Dr. ${doctor.name}?`)) {
        try {
          const result = await deleteDoctor(doctor.id, adminToken);
          if (result.success) {
            alert(result.message);
            card.remove();
          } else {
            alert(`Failed to delete doctor: ${result.message}`);
          }
        } catch (error) {
          alert('An error occurred while deleting the doctor.');
          console.error(error);
        }
      }
    });

    actionsContainer.appendChild(deleteBtn);
  }

  // Patient (not logged in) actions: Book Now button with alert
  else if (!token) {
    const bookBtn = document.createElement('button');
    bookBtn.classList.add('book-now-btn');
    bookBtn.textContent = 'Book Now';

    bookBtn.addEventListener('click', () => {
      alert('Please log in to book an appointment.');
    });

    actionsContainer.appendChild(bookBtn);
  }

  // Logged-in patient actions: Book Now button with booking logic
  else if (userRole === 'PATIENT' && token) {
    const bookBtn = document.createElement('button');
    bookBtn.classList.add('book-now-btn');
    bookBtn.textContent = 'Book Now';

    bookBtn.addEventListener('click', async () => {
      try {
        // Fetch patient data using token
        const patient = await getPatientByToken(token);
        if (!patient) {
          alert('Could not retrieve patient information. Please log in again.');
          return;
        }

        // Show booking overlay with doctor and patient info
        showBookingOverlay(doctor, patient);
      } catch (error) {
        alert('An error occurred while preparing the booking.');
        console.error(error);
      }
    });

    actionsContainer.appendChild(bookBtn);
  }

  // Append info and actions to card
  card.appendChild(infoContainer);
  card.appendChild(actionsContainer);

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
