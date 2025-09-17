/**
 * API Service Module
 * Handles all HTTP requests to the backend API
 */

const API = {
    baseUrl: "/",

    /**
     * Fetch user information and subscriptions
     */
    async getUserInfo() {
        const response = await fetch(this.baseUrl + "users/user-info", {
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
     */
    async searchFlights(airlineCode, flightNumber, scheduledTime) {
        const searchUrl = `${this.baseUrl}flights/search?airline_code=${airlineCode}&flight_number=${flightNumber}&scheduled_date=${scheduledTime}`;
        
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
     * Subscribe to flight updates
     */
    async subscribeToFlight(flightData) {
        const response = await fetch(this.baseUrl + "users/subscribe", {
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
     * Unsubscribe from flight updates
     */
    async unsubscribeFromFlight(airlineCode, flightNumber, scheduledTime) {
        const params = new URLSearchParams({
            airline_code: airlineCode,
            flight_number: flightNumber,
            scheduled_date: scheduledTime
        });

        const response = await fetch(this.baseUrl + "users/unsubscribe?" + params.toString(), {
            method: "POST",
            headers: {
                "Content-Type": "application/x-www-form-urlencoded"
            }
        });

        if (!response.ok) {
            throw new Error(`Failed to delete subscription: ${response.status}`);
        }

        return { success: true };
    }
};