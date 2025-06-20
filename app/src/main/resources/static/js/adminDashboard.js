import { getDoctors, filterDoctors, saveDoctor } from '../services/adminService.js';
import { createDoctorCard } from '../components/doctorCard.js';
import { openModal, closeModal } from '../util.js';

const contentDiv = document.getElementById('content');
const searchBar = document.getElementById('searchBar');
const timeFilter = document.getElementById('timeFilter');
const specialtyFilter = document.getElementById('specialtyFilter');
const addDoctorBtn = document.getElementById('addDoctorBtn');

// === OPEN ADD DOCTOR MODAL ===
if (addDoctorBtn) {
  addDoctorBtn.addEventListener('click', () => {
    openModal('addDoctor');
  });
}

// === INITIAL LOAD ===
document.addEventListener('DOMContentLoaded', async () => {
  await loadDoctorCards();
});

// === LOAD DOCTOR CARDS FUNCTION ===
async function loadDoctorCards() {
  try {
    const doctors = await getDoctors();
    renderDoctorCards(doctors);
  } catch (error) {
    console.error('Failed to load doctors:', error);
    contentDiv.innerHTML = `<p style="color: red;">Failed to fetch doctors. Try again later.</p>`;
  }
}

// === FILTER LOGIC ===
if (searchBar) {
  searchBar.addEventListener('input', filterDoctorsOnChange);
}
if (timeFilter) {
  timeFilter.addEventListener('change', filterDoctorsOnChange);
}
if (specialtyFilter) {
  specialtyFilter.addEventListener('change', filterDoctorsOnChange);
}

async function filterDoctorsOnChange() {
  const name = searchBar?.value.trim() || null;
  const time = timeFilter?.value || null;
  const specialty = specialtyFilter?.value || null;

  try {
    const doctors = await filterDoctors(name, time, specialty);
    if (doctors.length > 0) {
      renderDoctorCards(doctors);
    } else {
      contentDiv.innerHTML = `<p>No doctors found with the given filters.</p>`;
    }
  } catch (error) {
    alert('Error filtering doctors. Please try again.');
    console.error(error);
  }
}

// === RENDER DOCTOR CARDS ===
function renderDoctorCards(doctors) {
  contentDiv.innerHTML = '';
  doctors.forEach(doctor => {
    const card = createDoctorCard(doctor);
    contentDiv.appendChild(card);
  });
}

// === ADD DOCTOR HANDLER ===
window.adminAddDoctor = async function () {
  const name = document.getElementById('doctorName')?.value.trim();
  const email = document.getElementById('doctorEmail')?.value.trim();
  const phone = document.getElementById('doctorPhone')?.value.trim();
  const password = document.getElementById('doctorPassword')?.value.trim();
  const specialty = document.getElementById('doctorSpecialty')?.value.trim();
  const availableTimes = document.getElementById('doctorTimes')?.value.trim();

  if (!name || !email || !phone || !password || !specialty || !availableTimes) {
    alert('Please fill in all fields.');
    return;
  }

  const token = localStorage.getItem('token');
  if (!token) {
    alert('Unauthorized. Please log in again.');
    return;
  }

  const doctor = {
    name,
    email,
    phone,
    password,
    specialty,
    availableTimes
  };

  try {
    const result = await saveDoctor(doctor, token);
    alert('Doctor added successfully!');
    closeModal();
    await loadDoctorCards();
  } catch (error) {
    alert('Failed to add doctor. Please check your data and try again.');
    console.error(error);
  }
};



/*
  This script handles the admin dashboard functionality for managing doctors:
  - Loads all doctor cards
  - Filters doctors by name, time, or specialty
  - Adds a new doctor via modal form


  Attach a click listener to the "Add Doctor" button
  When clicked, it opens a modal form using openModal('addDoctor')


  When the DOM is fully loaded:
    - Call loadDoctorCards() to fetch and display all doctors


  Function: loadDoctorCards
  Purpose: Fetch all doctors and display them as cards

    Call getDoctors() from the service layer
    Clear the current content area
    For each doctor returned:
    - Create a doctor card using createDoctorCard()
    - Append it to the content div

    Handle any fetch errors by logging them


  Attach 'input' and 'change' event listeners to the search bar and filter dropdowns
  On any input change, call filterDoctorsOnChange()


  Function: filterDoctorsOnChange
  Purpose: Filter doctors based on name, available time, and specialty

    Read values from the search bar and filters
    Normalize empty values to null
    Call filterDoctors(name, time, specialty) from the service

    If doctors are found:
    - Render them using createDoctorCard()
    If no doctors match the filter:
    - Show a message: "No doctors found with the given filters."

    Catch and display any errors with an alert


  Function: renderDoctorCards
  Purpose: A helper function to render a list of doctors passed to it

    Clear the content area
    Loop through the doctors and append each card to the content area


  Function: adminAddDoctor
  Purpose: Collect form data and add a new doctor to the system

    Collect input values from the modal form
    - Includes name, email, phone, password, specialty, and available times

    Retrieve the authentication token from localStorage
    - If no token is found, show an alert and stop execution

    Build a doctor object with the form values

    Call saveDoctor(doctor, token) from the service

    If save is successful:
    - Show a success message
    - Close the modal and reload the page

    If saving fails, show an error message
*/
