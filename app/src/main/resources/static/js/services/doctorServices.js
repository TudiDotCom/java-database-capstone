// doctorService.js

import { BASE_API_URL } from '../config/config.js';

const DOCTOR_API = `${BASE_API_URL}/doctor`;

/**
 * Fetch all doctors from the API.
 * @returns {Promise<Array>} - Array of doctor objects or an empty array.
 */
export async function getDoctors() {
  try {
    const response = await fetch(`${DOCTOR_API}/all`);
    const data = await response.json();
    return data.doctors || [];
  } catch (error) {
    console.error('Error fetching doctors:', error);
    return [];
  }
}

/**
 * Delete a doctor by ID.
 * @param {number|string} doctorId - ID of the doctor to delete.
 * @param {string} token - Authentication token.
 * @returns {Promise<{ success: boolean, message: string }>}
 */
export async function deleteDoctor(doctorId, token) {
  try {
    const response = await fetch(`${DOCTOR_API}/delete/${doctorId}/${token}`, {
      method: 'DELETE'
    });
    const result = await response.json();
    return {
      success: response.ok,
      message: result.message || 'Doctor deleted successfully'
    };
  } catch (error) {
    console.error('Error deleting doctor:', error);
    return {
      success: false,
      message: 'Failed to delete doctor'
    };
  }
}

/**
 * Save (create) a new doctor.
 * @param {Object} doctor - Doctor object to save.
 * @param {string} token - Authentication token.
 * @returns {Promise<{ success: boolean, message: string }>}
 */
export async function saveDoctor(doctor, token) {
  try {
    const response = await fetch(`${DOCTOR_API}/save/${token}`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify(doctor)
    });

    const result = await response.json();
    return {
      success: response.ok,
      message: result.message || 'Doctor saved successfully'
    };
  } catch (error) {
    console.error('Error saving doctor:', error);
    return {
      success: false,
      message: 'Failed to save doctor'
    };
  }
}

/**
 * Filter doctors by name, time, and specialty.
 * @param {string|null} name - Name filter or null.
 * @param {string|null} time - Time filter (AM/PM) or null.
 * @param {string|null} specialty - Specialty filter or null.
 * @returns {Promise<Array>} - Filtered list of doctors.
 */
export async function filterDoctors(name, time, specialty) {
  try {
    const url = `${DOCTOR_API}/filter/${name || 'null'}/${time || 'null'}/${specialty || 'null'}`;
    const response = await fetch(url);

    if (!response.ok) {
      console.error('Error filtering doctors:', response.statusText);
      return [];
    }

    const data = await response.json();
    return data.doctors || [];
  } catch (error) {
    console.error('Error filtering doctors:', error);
    alert('Something went wrong while filtering doctors.');
    return [];
  }
}


/*
  Import the base API URL from the config file
  Define a constant DOCTOR_API to hold the full endpoint for doctor-related actions


  Function: getDoctors
  Purpose: Fetch the list of all doctors from the API

   Use fetch() to send a GET request to the DOCTOR_API endpoint
   Convert the response to JSON
   Return the 'doctors' array from the response
   If there's an error (e.g., network issue), log it and return an empty array


  Function: deleteDoctor
  Purpose: Delete a specific doctor using their ID and an authentication token

   Use fetch() with the DELETE method
    - The URL includes the doctor ID and token as path parameters
   Convert the response to JSON
   Return an object with:
    - success: true if deletion was successful
    - message: message from the server
   If an error occurs, log it and return a default failure response


  Function: saveDoctor
  Purpose: Save (create) a new doctor using a POST request

   Use fetch() with the POST method
    - URL includes the token in the path
    - Set headers to specify JSON content type
    - Convert the doctor object to JSON in the request body

   Parse the JSON response and return:
    - success: whether the request succeeded
    - message: from the server

   Catch and log errors
    - Return a failure response if an error occurs


  Function: filterDoctors
  Purpose: Fetch doctors based on filtering criteria (name, time, and specialty)

   Use fetch() with the GET method
    - Include the name, time, and specialty as URL path parameters
   Check if the response is OK
    - If yes, parse and return the doctor data
    - If no, log the error and return an object with an empty 'doctors' array

   Catch any other errors, alert the user, and return a default empty result
*/
