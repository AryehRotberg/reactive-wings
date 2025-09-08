/**
 * Application Configuration
 */

// API Configuration
const API_BASE_URL = "http://34.69.48.95.nip.io:8080/";
// const API_BASE_URL = "http://localhost:8080/";

// Application State
let currentSubscriptions = [];

// Export for use in other modules
window.FlightApp = window.FlightApp || {};
window.FlightApp.config = {
    API_BASE_URL,
    currentSubscriptions
};
