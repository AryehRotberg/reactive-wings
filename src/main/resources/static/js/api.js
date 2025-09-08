/**
 * API Functions
 */

window.FlightApp = window.FlightApp || {};

window.FlightApp.api = {
    /**
     * Fetch user information and subscriptions
     * @returns {Promise<Object>} User information object
     */
    async fetchUserInfo() {
        const response = await fetch(window.FlightApp.config.API_BASE_URL + "users/user-info", {
            method: "GET",
            headers: {
                "Content-Type": "application/json"
            }
        });

        if (!response.ok) {
            throw new Error(`Failed to fetch user info: ${response.status}`);
        }

        return await response.json();
    },

    /**
     * Search for flights
     * @param {string} airlineCode - Airline code
     * @param {string} flightNumber - Flight number
     * @param {string} scheduledTime - Scheduled time
     * @returns {Promise<Array>} Flight search results
     */
    async searchFlights(airlineCode, flightNumber, scheduledTime) {
        const searchUrl = window.FlightApp.config.API_BASE_URL + 
            `flights/search?airline_code=${airlineCode}&flight_number=${flightNumber}&scheduled_time=${scheduledTime}`;
        
        const response = await fetch(searchUrl, {
            method: "GET",
            headers: {
                "Content-Type": "application/json"
            }
        });

        if (!response.ok) {
            throw new Error(`Flight search failed: ${response.status}`);
        }

        return await response.json();
    },

    /**
     * Subscribe to a flight
     * @param {Object} flightData - Flight data to subscribe to
     * @returns {Promise<Object>} Subscription response
     */
    async subscribeToFlight(flightData) {
        const response = await fetch(window.FlightApp.config.API_BASE_URL + "users/subscribe", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(flightData)
        });

        if (!response.ok) {
            throw new Error(`Subscription failed: ${response.status}`);
        }

        return await response.json();
    },

    /**
     * Unsubscribe from a flight
     * @param {string} airlineCode - Airline code
     * @param {string} flightNumber - Flight number
     * @param {string} scheduledDate - Scheduled date
     * @returns {Promise<Object>} Unsubscribe response
     */
    async unsubscribeFromFlight(airlineCode, flightNumber, scheduledDate) {
        const params = new URLSearchParams({
            airline_code: airlineCode,
            flight_number: flightNumber,
            scheduled_date: scheduledDate
        });

        const response = await fetch(window.FlightApp.config.API_BASE_URL + "users/unsubscribe?" + params.toString(), {
            method: "POST",
            headers: {
                "Content-Type": "application/x-www-form-urlencoded"
            }
        });

        if (!response.ok) {
            throw new Error(`Failed to delete subscription: ${response.status}`);
        }

        return await response.json();
    }
};
