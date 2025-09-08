/**
 * Utility Functions
 */

window.FlightApp = window.FlightApp || {};

window.FlightApp.utils = {
    /**
     * Format date for display
     * @param {string} dateString - Date string to format
     * @returns {string} Formatted date string
     */
    formatDate(dateString) {
        if (!dateString) return 'N/A';
        const date = new Date(dateString);
        return date.toLocaleDateString('en-US', {
            year: 'numeric',
            month: 'short',
            day: 'numeric',
            hour: '2-digit',
            minute: '2-digit'
        });
    },

    /**
     * Convert scheduled_time to yyyy-mm-dd format for API
     * @param {string} scheduledTime - Scheduled time string
     * @returns {string} Formatted date string (yyyy-mm-dd)
     */
    formatScheduledTimeForAPI(scheduledTime) {
        if (!scheduledTime) return '';
        const date = new Date(scheduledTime);
        // Format as yyyy-mm-dd
        return date.getFullYear() + '-' + 
                String(date.getMonth() + 1).padStart(2, '0') + '-' + 
                String(date.getDate()).padStart(2, '0');
    },

    /**
     * Validate form input
     * @param {Object} formData - Form data to validate
     * @returns {Object} Validation result
     */
    validateSubscriptionForm(formData) {
        const { airlineCode, flightNumber, scheduledDate } = formData;
        
        if (!airlineCode || !flightNumber || !scheduledDate) {
            return {
                isValid: false,
                message: "Please fill in all fields."
            };
        }

        return {
            isValid: true,
            message: null
        };
    }
};
