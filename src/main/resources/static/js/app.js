/**
 * Main Application Initialization and Event Handlers
 */

window.FlightApp = window.FlightApp || {};

window.FlightApp.app = {
    /**
     * Initialize the application
     */
    init() {
        this.setupEventListeners();
        this.loadInitialData();
    },

    /**
     * Set up event listeners
     */
    setupEventListeners() {
        // Subscription form handler
        const subscriptionForm = document.getElementById("subscriptionForm");
        if (subscriptionForm) {
            subscriptionForm.addEventListener("submit", (e) => {
                window.FlightApp.subscriptions.handleSubscriptionForm(e);
            });
        }

        // Refresh button handler
        const refreshButton = document.getElementById("refreshSubscriptions");
        if (refreshButton) {
            refreshButton.addEventListener("click", () => {
                window.FlightApp.subscriptions.loadUserSubscriptions();
            });
        }

        // Page load handler
        window.addEventListener("load", () => {
            this.handlePageLoad();
        });
    },

    /**
     * Handle page load with loading animation
     */
    async handlePageLoad() {
        window.FlightApp.ui.showPageLoading();
        
        try {
            // Add a small delay to show the loading animation
            await new Promise(resolve => setTimeout(resolve, 800));
            await window.FlightApp.subscriptions.loadUserSubscriptions();
        } catch (error) {
            console.error("Error during page load:", error);
        } finally {
            window.FlightApp.ui.hidePageLoading();
        }
    },

    /**
     * Load initial data
     */
    loadInitialData() {
        // Any initial data loading can go here
        console.log("Flight Manager Application Initialized");
    }
};

// Initialize the application when DOM is ready
document.addEventListener('DOMContentLoaded', () => {
    window.FlightApp.app.init();
});
