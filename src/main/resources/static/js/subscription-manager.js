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
                id: searchResults[0].id,
                flightId: searchResults[0].flightId,
                airlineCode: searchResults[0].airlineCode,
                flightNumber: searchResults[0].flightNumber,
                scheduledTime: searchResults[0].scheduledTime,
                estimatedTime: searchResults[0].estimatedTime,
                statusEn: searchResults[0].statusEn,
                airlineName: searchResults[0].airlineName,
                airportCode: searchResults[0].airportCode,
                cityEn: searchResults[0].cityEn,
                cityHe: searchResults[0].cityHe,
                countryEn: searchResults[0].countryEn,
                countryHe: searchResults[0].countryHe,
                terminal: searchResults[0].terminal,
                counters: searchResults[0].counters,
                checkinZone: searchResults[0].checkinZone,
                lastUpdated: new Date().toISOString()
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
            
            const airlineCode = document.getElementById("airlineCode").value;
            const flightNumber = document.getElementById("flightNumber").value;
            const scheduledDate = document.getElementById("scheduledDate").value;

            await this.subscribeToFlight(airlineCode, flightNumber, scheduledDate);
        });
    }
};