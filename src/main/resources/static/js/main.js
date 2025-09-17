/**
 * Main Application Module
 * Initializes the application and coordinates between modules
 */

const FlightApp = {
    
    /**
     * Initialize the application
     */
    async init() {
        LoadingManager.showPageLoading();
        
        try {
            // Add a small delay to show the loading animation
            await new Promise(resolve => setTimeout(resolve, 800));
            
            // Initialize UI components
            UIUtils.initializeMenu();
            SubscriptionManager.initializeSubscriptionForm();
            this.initializeEventHandlers();
            this.setDefaultScheduledDate();
            
            // Load initial data
            await SubscriptionManager.loadUserSubscriptions();
            
        } catch (error) {
            console.error("Error during application initialization:", error);
            UIUtils.showMessage("subscriptionsList", `Failed to initialize application: ${error.message}`, true);
        } finally {
            LoadingManager.hidePageLoading();
        }
    },

    /**
     * Initialize event handlers for buttons and other interactions
     */
    initializeEventHandlers() {
        // Refresh button handler
        document.getElementById("refreshSubscriptions").addEventListener("click", () => {
            SubscriptionManager.loadUserSubscriptions();
        });

        // Logout button handler
        document.getElementById("logout").addEventListener("click", this.logoutUser);
    },

    /**
     * Set today's date as default for scheduled date field
     */
    setDefaultScheduledDate() {
        const today = new Date();
        const formattedDate = today.getFullYear() + '-' + 
            String(today.getMonth() + 1).padStart(2, '0') + '-' + 
            String(today.getDate()).padStart(2, '0');
        
        const scheduledDateField = document.getElementById('scheduled_date');
        if (scheduledDateField) {
            scheduledDateField.value = formattedDate;
        }
    },

    /**
     * Logout functionality
     */
    logoutUser() {
        window.location.href = "/logout";
    }
};

// Initialize application when page loads
window.addEventListener("load", () => {
    FlightApp.init();
});