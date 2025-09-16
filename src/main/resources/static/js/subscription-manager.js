/**
 * Subscription Manager Module
 * Handles subscription-related functionality
 */

const SubscriptionManager = {
    currentSubscriptions: [],

    /**
     * Load and display user subscriptions
     */
    async loadUserSubscriptions() {
        LoadingManager.showSectionLoading('subscriptionsSection');
        LoadingManager.showButtonLoading('refreshSubscriptions');
        
        try {
            const userInfo = await API.getUserInfo();
            
            // Display user info
            document.getElementById("userInfo").innerHTML = `
                <div class="user-info">
                    <h3>👤 User Information</h3>
                    <p><strong>Email:</strong> ${userInfo.email}</p>
                </div>
            `;

            // Display subscriptions
            const subscriptionsDiv = document.getElementById("subscriptionsList");
            this.currentSubscriptions = userInfo.subscriptions || [];
            
            if (this.currentSubscriptions.length > 0) {
                let subscriptionsHtml = "<h3>✈️ Active Subscriptions</h3>";
                this.currentSubscriptions.forEach((sub, index) => {
                    subscriptionsHtml += UIUtils.generateSubscriptionHTML(sub, index);
                });
                subscriptionsDiv.innerHTML = subscriptionsHtml;
            } else {
                subscriptionsDiv.innerHTML = UIUtils.generateEmptyStateHTML();
            }
            
        } catch (error) {
            console.error("Error loading subscriptions:", error);
            document.getElementById("subscriptionsList").innerHTML = `<div class="message error">❌ Failed to load subscriptions: ${error.message}</div>`;
            this.currentSubscriptions = [];
        } finally {
            LoadingManager.hideSectionLoading('subscriptionsSection');
            LoadingManager.hideButtonLoading('refreshSubscriptions');
        }
    },

    /**
     * Delete a subscription
     */
    async deleteSubscription(airlineCode, flightNumber, scheduledTime, index) {
        const deleteButtonId = `deleteBtn_${index}`;
        LoadingManager.showButtonLoading(deleteButtonId);
        
        try {
            await API.unsubscribeFromFlight(airlineCode, flightNumber, scheduledTime);
            UIUtils.showMessage("subscriptionsList", "Subscription deleted successfully!");
            this.loadUserSubscriptions(); // Refresh the list
        } catch (error) {
            console.error("Error deleting subscription:", error);
            UIUtils.showMessage("subscriptionsList", `Failed to delete subscription: ${error.message}`, true);
        } finally {
            LoadingManager.hideButtonLoading(deleteButtonId);
        }
    },

    /**
     * Subscribe to a flight
     */
    async subscribeToFlight(airlineCode, flightNumber, scheduledDate) {
        LoadingManager.showButtonLoading('subscribeBtn');

        try {
            // Validate input
            UIUtils.validateSubscriptionForm(airlineCode, flightNumber, scheduledDate);

            // Search for the flight
            const searchResults = await API.searchFlights(airlineCode, flightNumber, scheduledDate);
            
            if (!searchResults || searchResults.length === 0) {
                UIUtils.showMessage("subscriptionMessage", "No flights found with the specified criteria.", true);
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
                airline_name: searchResults[0].airline_name,
                airport_code: searchResults[0].airport_code,
                city_en: searchResults[0].city_en,
                city_he: searchResults[0].city_he,
                country_en: searchResults[0].country_en,
                country_he: searchResults[0].country_he,
                terminal: searchResults[0].terminal,
                counters: searchResults[0].counters,
                checkin_zone: searchResults[0].checkin_zone
            };

            // Subscribe to the flight
            await API.subscribeToFlight(flightData);

            UIUtils.showMessage("subscriptionMessage", "Flight subscription added successfully!");
            document.getElementById("subscriptionForm").reset();
            this.loadUserSubscriptions(); // Refresh the list
            
        } catch (error) {
            console.error("Subscription error:", error);
            UIUtils.showMessage("subscriptionMessage", `Failed to subscribe: ${error.message}`, true);
        } finally {
            LoadingManager.hideButtonLoading('subscribeBtn');
        }
    },

    /**
     * Initialize subscription form handler
     */
    initializeSubscriptionForm() {
        document.getElementById("subscriptionForm").addEventListener("submit", async (e) => {
            e.preventDefault();
            
            const airlineCode = document.getElementById("airline_code").value;
            const flightNumber = document.getElementById("flight_number").value;
            const scheduledDate = document.getElementById("scheduled_date").value;

            await this.subscribeToFlight(airlineCode, flightNumber, scheduledDate);
        });
    }
};