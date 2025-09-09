/**
 * Subscription Management Functions
 */

window.FlightApp = window.FlightApp || {};

window.FlightApp.subscriptions = {
    /**
     * Load and display user subscriptions
     */
    async loadUserSubscriptions() {
        window.FlightApp.ui.showSectionLoading('subscriptionsSection');
        window.FlightApp.ui.showButtonLoading('refreshSubscriptions');
        
        try {
            const userInfo = await window.FlightApp.api.fetchUserInfo();
            
            // Display user info
            document.getElementById("userInfo").innerHTML = `
                <div class="user-info">
                    <h3>👤 User Information</h3>
                    <p><strong>Email:</strong> ${userInfo.email}</p>
                </div>
            `;

            // Update current subscriptions
            window.FlightApp.config.currentSubscriptions = userInfo.subscriptions || [];
            
            // Display subscriptions
            this.renderSubscriptions(window.FlightApp.config.currentSubscriptions);
            
        } catch (error) {
            console.error("Error loading subscriptions:", error);
            document.getElementById("subscriptionsList").innerHTML = 
                `<div class="message error">❌ Failed to load subscriptions: ${error.message}</div>`;
            window.FlightApp.config.currentSubscriptions = [];
        } finally {
            window.FlightApp.ui.hideSectionLoading('subscriptionsSection');
            window.FlightApp.ui.hideButtonLoading('refreshSubscriptions');
        }
    },

    /**
     * Render subscriptions list
     * @param {Array} subscriptions - Array of subscription objects
     */
    renderSubscriptions(subscriptions) {
        const subscriptionsDiv = document.getElementById("subscriptionsList");
        
        if (subscriptions.length > 0) {
            let subscriptionsHtml = "<h3>✈️ Active Subscriptions</h3>";
            subscriptions.forEach((sub, index) => {
                subscriptionsHtml += this.createSubscriptionHTML(sub, index);
            });
            subscriptionsDiv.innerHTML = subscriptionsHtml;
        } else {
            subscriptionsDiv.innerHTML = this.createEmptyStateHTML();
        }
    },

    /**
     * Create HTML for a single subscription
     * @param {Object} sub - Subscription object
     * @param {number} index - Index of the subscription
     * @returns {string} HTML string
     */
    createSubscriptionHTML(sub, index) {
        return `
            <div class="subscription-item">
                <div class="subscription-details">
                    <div class="detail-item">
                        <span class="detail-label">Flight</span>
                        <span class="detail-value"><span class="flight-icon">✈️</span>${sub.airline_code} ${sub.flight_number}</span>
                    </div>
                    <div class="detail-item">
                        <span class="detail-label">Estimated Time</span>
                        <span class="detail-value">${window.FlightApp.utils.formatDate(sub.estimated_time)}</span>
                    </div>
                    <div class="detail-item">
                        <span class="detail-label">Destination</span>
                        <span class="detail-value">${sub.city_en || 'N/A'} (${sub.country_en || 'N/A'})</span>
                    </div>
                    <div class="detail-item">
                        <span class="detail-label">Status</span>
                        <span class="detail-value">${sub.last_status || 'Unknown'}</span>
                    </div>
                    <div class="detail-item">
                        <span class="detail-label">Terminal</span>
                        <span class="detail-value">${sub.terminal || 'TBD'}</span>
                    </div>
                    <div class="detail-item">
                        <span class="detail-label">Counters</span>
                        <span class="detail-value">${sub.counters || 'TBD'}</span>
                    </div>
                    <div class="detail-item">
                        <span class="detail-label">Check-in Zone</span>
                        <span class="detail-value">${sub.checkin_zone || 'TBD'}</span>
                    </div>
                </div>
                <button class="btn btn-danger" id="deleteBtn_${index}" onclick="window.FlightApp.subscriptions.deleteSubscription('${sub.airline_code}', '${sub.flight_number}', '${window.FlightApp.utils.formatScheduledTimeForAPI(sub.scheduled_time)}', ${index})">
                    <span class="btn-icon">🗑️</span>
                    <span class="btn-text">Remove Subscription</span>
                </button>
            </div>
        `;
    },

    /**
     * Create HTML for empty state
     * @returns {string} HTML string
     */
    createEmptyStateHTML() {
        return `
            <div class="empty-state">
                <svg viewBox="0 0 24 24" fill="currentColor">
                    <path d="M21 16v-2l-8-5V3.5c0-.83-.67-1.5-1.5-1.5S10 2.67 10 3.5V9l-8 5v2l8-2.5V19l-2 1.5V22l3.5-1 3.5 1v-1.5L13 19v-5.5l8 2.5z"/>
                </svg>
                <h3>No Active Subscriptions</h3>
                <p>Subscribe to your first flight to get started!</p>
            </div>
        `;
    },

    /**
     * Delete a subscription
     * @param {string} airlineCode - Airline code
     * @param {string} flightNumber - Flight number
     * @param {string} scheduledTime - Scheduled time
     * @param {number} index - Index of the subscription
     */
    async deleteSubscription(airlineCode, flightNumber, scheduledTime, index) {
        const deleteButtonId = `deleteBtn_${index}`;
        window.FlightApp.ui.showButtonLoading(deleteButtonId);
        
        try {
            await window.FlightApp.api.unsubscribeFromFlight(airlineCode, flightNumber, scheduledTime);
            window.FlightApp.ui.showMessage("subscriptionsList", "Subscription deleted successfully!");
            this.loadUserSubscriptions(); // Refresh the list
        } catch (error) {
            console.error("Error deleting subscription:", error);
            window.FlightApp.ui.showMessage("subscriptionsList", `Failed to delete subscription: ${error.message}`, true);
        } finally {
            window.FlightApp.ui.hideButtonLoading(deleteButtonId);
        }
    },

    /**
     * Handle subscription form submission
     * @param {Event} e - Form submit event
     */
    async handleSubscriptionForm(e) {
        e.preventDefault();
        
        window.FlightApp.ui.showButtonLoading('subscribeBtn');

        try {
            const airlineCode = document.getElementById("airline_code").value;
            const flightNumber = document.getElementById("flight_number").value;
            const scheduledDate = document.getElementById("scheduled_date").value;

            // Validate input
            const validation = window.FlightApp.utils.validateSubscriptionForm({
                airlineCode, flightNumber, scheduledDate
            });

            if (!validation.isValid) {
                window.FlightApp.ui.showMessage("subscriptionMessage", validation.message, true);
                return;
            }

            // Search for flight
            const searchResults = await window.FlightApp.api.searchFlights(airlineCode, flightNumber, scheduledDate);
            
            if (!searchResults || searchResults.length === 0) {
                window.FlightApp.ui.showMessage("subscriptionMessage", "No flights found with the specified criteria.", true);
                return;
            }

            // Prepare subscription data
            const flightData = {
                airline_code: searchResults[0].airline_code,
                flight_number: searchResults[0].flight_number,
                scheduled_time: searchResults[0].scheduled_time,
                estimated_time: searchResults[0].estimated_time,
                last_status: searchResults[0].status_en,
                last_updated: new Date().toISOString(),
                airport_code: searchResults[0].airport_code,
                city_en: searchResults[0].city_en,
                city_he: searchResults[0].city_he,
                country_en: searchResults[0].country_en,
                country_he: searchResults[0].country_he,
                terminal: searchResults[0].terminal,
                counters: searchResults[0].counters,
                checkin_zone: searchResults[0].checkin_zone
            };

            // Subscribe to flight
            await window.FlightApp.api.subscribeToFlight(flightData);

            window.FlightApp.ui.showMessage("subscriptionMessage", "Flight subscription added successfully!");
            document.getElementById("subscriptionForm").reset();
            this.loadUserSubscriptions(); // Refresh the list
        } catch (error) {
            console.error("Subscription error:", error);
            window.FlightApp.ui.showMessage("subscriptionMessage", `Failed to subscribe: ${error.message}`, true);
        } finally {
            window.FlightApp.ui.hideButtonLoading('subscribeBtn');
        }
    }
};
