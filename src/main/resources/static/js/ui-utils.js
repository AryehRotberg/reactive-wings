/**
 * UI Utilities Module
 * Handles UI interactions, messaging, and utility functions
 */

const UIUtils = {
    
    /**
     * Display messages to the user
     */
    showMessage(elementId, message, isError = false) {
        const element = document.getElementById(elementId);
        if (!element) {
            console.warn(`Element with id '${elementId}' not found. Message: ${message}`);
            return;
        }
        element.innerHTML = `<div class="message ${isError ? 'error' : 'success'}">${isError ? '❌' : '✅'} ${message}</div>`;
        setTimeout(() => {
            if (element) {
                element.innerHTML = '';
            }
        }, 5000);
    },

    /**
     * Format date for display
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
     * Generate HTML for subscription item
     */
    generateSubscriptionHTML(sub, index) {
        return `
            <div class="subscription-item">
                <div class="subscription-details">
                    <div class="detail-item">
                        <span class="detail-label">Flight</span>
                        <span class="detail-value"><span class="flight-icon">✈️</span>${sub.airline_code} ${sub.flight_number}</span>
                    </div>
                    <div class="detail-item">
                        <span class="detail-label">Airline Company</span>
                        <span class="detail-value">${sub.airline_name}</span>
                    </div>
                    <div class="detail-item">
                        <span class="detail-label">Estimated Time</span>
                        <span class="detail-value">${this.formatDate(sub.estimated_time)}</span>
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
                        <span class="detail-value">${sub.terminal || 'NOT CONFIRMED'}</span>
                    </div>
                    <div class="detail-item">
                        <span class="detail-label">Counters</span>
                        <span class="detail-value">${sub.counters || 'NOT CONFIRMED'}</span>
                    </div>
                    <div class="detail-item">
                        <span class="detail-label">Check-in Zone</span>
                        <span class="detail-value">${sub.checkin_zone || 'NOT CONFIRMED'}</span>
                    </div>
                </div>
                <button class="btn btn-danger" id="deleteBtn_${index}" onclick="SubscriptionManager.deleteSubscription('${sub.airline_code}', '${sub.flight_number}', '${this.formatScheduledTimeForAPI(sub.scheduled_time)}', ${index})">
                    <span class="btn-icon">🗑️</span>
                    <span class="btn-text">Remove Subscription</span>
                </button>
            </div>
        `;
    },

    /**
     * Generate empty state HTML
     */
    generateEmptyStateHTML() {
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
     * Initialize menu functionality
     */
    initializeMenu() {
        // Menu toggle functionality
        document.getElementById("menuToggle").addEventListener("click", function() {
            const menu = document.getElementById("menuDropdown");
            const toggle = document.getElementById("menuToggle");
            
            menu.classList.toggle("show");
            toggle.classList.toggle("active");
        });

        // Close menu when clicking outside
        document.addEventListener("click", function(event) {
            const menuContainer = document.querySelector(".menu-container");
            const menu = document.getElementById("menuDropdown");
            const toggle = document.getElementById("menuToggle");
            
            if (!menuContainer.contains(event.target)) {
                menu.classList.remove("show");
                toggle.classList.remove("active");
            }
        });
    },

    /**
     * Validate form input
     */
    validateSubscriptionForm(airlineCode, flightNumber, scheduledDate) {
        if (!airlineCode || !flightNumber || !scheduledDate) {
            throw new Error("Please fill in all fields.");
        }
    }
};