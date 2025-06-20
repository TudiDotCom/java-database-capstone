// doctorDashboard.js

import { getAllAppointments } from './services/appointmentRecordService.js';
import { createPatientRow } from './components/patientRows.js';

// DOM Elements
const patientTableBody = document.getElementById('patientTableBody');
const searchBar = document.getElementById('searchBar');
const todayButton = document.getElementById('todayButton');
const datePicker = document.getElementById('datePicker');

// Initialize filter state
let selectedDate = new Date().toISOString().split('T')[0]; // 'YYYY-MM-DD'
let patientName = null;

// Retrieve token for authenticated calls
const token = localStorage.getItem('token');

// Set datePicker default value to today
datePicker.value = selectedDate;

// Event: Search bar input
searchBar.addEventListener('input', () => {
    const input = searchBar.value.trim();
    patientName = input.length > 0 ? input : null;
    loadAppointments();
});

// Event: Today button click
todayButton.addEventListener('click', () => {
    selectedDate = new Date().toISOString().split('T')[0];
    datePicker.value = selectedDate;
    loadAppointments();
});

// Event: Date picker change
datePicker.addEventListener('change', () => {
    selectedDate = datePicker.value;
    loadAppointments();
});

// Function: Load and render appointments based on filters
async function loadAppointments() {
    try {
        // Clear existing rows
        patientTableBody.innerHTML = '';

        // Fetch appointments from backend
        const appointments = await getAllAppointments(selectedDate, patientName, token);

        if (!appointments || appointments.length === 0) {
            // No appointments message row
            const noDataRow = document.createElement('tr');
            noDataRow.innerHTML = `<td colspan="5" class="noPatientRecord">No Appointments found for today.</td>`;
            patientTableBody.appendChild(noDataRow);
            return;
        }

        // Render each appointment as a patient row
        appointments.forEach(app => {
            const patient = {
                id: app.patientId,
                name: app.patientName,
                phone: app.patientPhone,
                email: app.patientEmail
            };

            //const row = createPatientRecordRow(patient, app);
            const row = createPatientRow(patient, app.appoimentId, app.doctorId);
            patientTableBody.appendChild(row);
        });
    } catch (error) {
        console.error('Error loading appointments:', error);
        const errorRow = document.createElement('tr');
        errorRow.innerHTML = `<td colspan="5" class="noPatientRecord">Error loading appointments. Try again later.</td>`;
        patientTableBody.appendChild(errorRow);
    }
}

// On page load, render UI and load today's appointments
document.addEventListener('DOMContentLoaded', () => {
    if (typeof renderContent === 'function') {
        renderContent();
    }
    loadAppointments();
});


/*
  Import getAllAppointments to fetch appointments from the backend
  Import createPatientRow to generate a table row for each patient appointment


  Get the table body where patient rows will be added
  Initialize selectedDate with today's date in 'YYYY-MM-DD' format
  Get the saved token from localStorage (used for authenticated API calls)
  Initialize patientName to null (used for filtering by name)


  Add an 'input' event listener to the search bar
  On each keystroke:
    - Trim and check the input value
    - If not empty, use it as the patientName for filtering
    - Else, reset patientName to "null" (as expected by backend)
    - Reload the appointments list with the updated filter


  Add a click listener to the "Today" button
  When clicked:
    - Set selectedDate to today's date
    - Update the date picker UI to match
    - Reload the appointments for today


  Add a change event listener to the date picker
  When the date changes:
    - Update selectedDate with the new value
    - Reload the appointments for that specific date


  Function: loadAppointments
  Purpose: Fetch and display appointments based on selected date and optional patient name

  Step 1: Call getAllAppointments with selectedDate, patientName, and token
  Step 2: Clear the table body content before rendering new rows

  Step 3: If no appointments are returned:
    - Display a message row: "No Appointments found for today."

  Step 4: If appointments exist:
    - Loop through each appointment and construct a 'patient' object with id, name, phone, and email
    - Call createPatientRow to generate a table row for the appointment
    - Append each row to the table body

  Step 5: Catch and handle any errors during fetch:
    - Show a message row: "Error loading appointments. Try again later."


  When the page is fully loaded (DOMContentLoaded):
    - Call renderContent() (assumes it sets up the UI layout)
    - Call loadAppointments() to display today's appointments by default
*/
