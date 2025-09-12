const url = "http://34.123.99.78.nip.io:8080/";
// const url = "http://localhost:8080/";
let currentSubscriptions = [];

// Show/hide page loading overlay
function showPageLoading() {
    document.getElementById('pageLoadingOverlay').style.display = 'flex';
}

function hidePageLoading() {
    document.getElementById('pageLoadingOverlay').style.display = 'none';
}

// Show/hide section loading
function showSectionLoading(sectionId) {
    const section = document.getElementById(sectionId);
    if (section) {
    section.classList.add('section-loading');
    }
}

function hideSectionLoading(sectionId) {
    const section = document.getElementById(sectionId);
    if (section) {
    section.classList.remove('section-loading');
    }
}

// Show/hide button loading
function showButtonLoading(buttonId) {
    const button = document.getElementById(buttonId);
    if (button) {
        // Store original styles to prevent any changes
        const originalStyle = button.style.cssText;
        const originalClasses = button.className;
        button.dataset.originalStyle = originalStyle;
        button.dataset.originalClasses = originalClasses;
        
        // Add loading class and disable button
        button.classList.add('loading');
        button.disabled = true;
        
        // Add spinner next to button (not inside)
        const spinner = document.createElement('div');
        spinner.className = 'loading-spinner-beside';
        spinner.id = buttonId + '_spinner';
        button.parentNode.insertBefore(spinner, button.nextSibling);
    }
}

function hideButtonLoading(buttonId) {
    const button = document.getElementById(buttonId);
    if (button) {
        // Remove loading class and re-enable button
        button.classList.remove('loading');
        button.disabled = false;
        
        // Restore original styles if any were stored
        if (button.dataset.originalStyle !== undefined) {
            button.style.cssText = button.dataset.originalStyle;
            delete button.dataset.originalStyle;
        }
        
        // Restore original classes if needed
        if (button.dataset.originalClasses !== undefined) {
            delete button.dataset.originalClasses;
        }
        
        // Remove spinner
        const spinner = document.getElementById(buttonId + '_spinner');
        if (spinner) {
            spinner.remove();
        }
    }
}

// Function to display messages
function showMessage(elementId, message, isError = false) {
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
}

// Function to format date for display
function formatDate(dateString) {
    if (!dateString) return 'N/A';
    const date = new Date(dateString);
    return date.toLocaleDateString('en-US', {
    year: 'numeric',
    month: 'short',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit'
    });
}

// Function to convert scheduled_time to yyyy-mm-dd format
function formatScheduledTimeForAPI(scheduledTime) {
    if (!scheduledTime) return '';
    const date = new Date(scheduledTime);
    // Format as yyyy-mm-dd
    return date.getFullYear() + '-' + 
            String(date.getMonth() + 1).padStart(2, '0') + '-' + 
            String(date.getDate()).padStart(2, '0');
}

// Function to fetch and display user subscriptions
async function loadUserSubscriptions() {
    showSectionLoading('subscriptionsSection');
    showButtonLoading('refreshSubscriptions');
    
    try {
    const response = await fetch(url + "users/user-info", {
        method: "GET",
        headers: {
        "Content-Type": "application/json"
        }
    });

    if (!response.ok) {
        throw new Error(`Failed to fetch user info: ${response.status}`);
    }

    const userInfo = await response.json();
    document.getElementById("userInfo").innerHTML = `
        <div class="user-info">
        <h3>👤 User Information</h3>
        <p><strong>Email:</strong> ${userInfo.email}</p>
        </div>
    `;

    const subscriptionsDiv = document.getElementById("subscriptionsList");
    currentSubscriptions = userInfo.subscriptions || [];
    
    if (currentSubscriptions.length > 0) {
        let subscriptionsHtml = "<h3>✈️ Active Subscriptions</h3>";
        currentSubscriptions.forEach((sub, index) => {
        subscriptionsHtml += `
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
                <span class="detail-value">${formatDate(sub.estimated_time)}</span>
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
            <button class="btn btn-danger" id="deleteBtn_${index}" onclick="deleteSubscription('${sub.airline_code}', '${sub.flight_number}', '${formatScheduledTimeForAPI(sub.scheduled_time)}', ${index})">
                <span class="btn-icon">🗑️</span>
                <span class="btn-text">Remove Subscription</span>
            </button>
            </div>
        `;
        });
        subscriptionsDiv.innerHTML = subscriptionsHtml;
    } else {
        subscriptionsDiv.innerHTML = `
        <div class="empty-state">
            <svg viewBox="0 0 24 24" fill="currentColor">
            <path d="M21 16v-2l-8-5V3.5c0-.83-.67-1.5-1.5-1.5S10 2.67 10 3.5V9l-8 5v2l8-2.5V19l-2 1.5V22l3.5-1 3.5 1v-1.5L13 19v-5.5l8 2.5z"/>
            </svg>
            <h3>No Active Subscriptions</h3>
            <p>Subscribe to your first flight to get started!</p>
        </div>
        `;
    }
    
    } catch (error) {
    console.error("Error loading subscriptions:", error);
    document.getElementById("subscriptionsList").innerHTML = `<div class="message error">❌ Failed to load subscriptions: ${error.message}</div>`;
    currentSubscriptions = [];
    } finally {
    hideSectionLoading('subscriptionsSection');
    hideButtonLoading('refreshSubscriptions');
    }
}

// Function to delete a subscription
async function deleteSubscription(airlineCode, flightNumber, scheduledTime, index) {
    const deleteButtonId = `deleteBtn_${index}`;
    showButtonLoading(deleteButtonId);
    
    try {
    // Create URL with query parameters to match the @RequestParam in the controller
    const params = new URLSearchParams({
        airline_code: airlineCode,
        flight_number: flightNumber,
        scheduled_date: scheduledTime  // Note: parameter name is scheduled_date in the controller
    });

    const response = await fetch(url + "users/unsubscribe?" + params.toString(), {
        method: "POST",
        headers: {
        "Content-Type": "application/x-www-form-urlencoded"
        }
    });

    if (!response.ok) {
        throw new Error(`Failed to delete subscription: ${response.status}`);
    }

    showMessage("subscriptionsList", "Subscription deleted successfully!");
    loadUserSubscriptions(); // Refresh the list
    } catch (error) {
    console.error("Error deleting subscription:", error);
    showMessage("subscriptionsList", `Failed to delete subscription: ${error.message}`, true);
    } finally {
    hideButtonLoading(deleteButtonId);
    }
}

// Subscribe form handler
document.getElementById("subscriptionForm").addEventListener("submit", async function(e) {
    e.preventDefault();
    
    showButtonLoading('subscribeBtn');

    try {
    const airlineCode = document.getElementById("airline_code").value;
    const flightNumber = document.getElementById("flight_number").value;
    const scheduledDate = document.getElementById("scheduled_date").value;

    // Validate input
    if (!airlineCode || !flightNumber || !scheduledDate) {
        showMessage("subscriptionMessage", "Please fill in all fields.", true);
        return;
    }

    // Convert date to the expected format (YYYY-MM-DD)
    const scheduledTime = scheduledDate;

    const searchResponse = await fetch(url + "flights/search?airline_code=" + airlineCode + "&flight_number=" + flightNumber
    + "&scheduled_time=" + scheduledTime, {
        method: "GET",
        headers: {
        "Content-Type": "application/json"
        }
    });

    if (!searchResponse.ok) {
        throw new Error(`Flight search failed: ${searchResponse.status}`);
    }

    const result = await searchResponse.json();
    
    if (!result || result.length === 0) {
        showMessage("subscriptionMessage", "No flights found with the specified criteria.", true);
        return;
    }

    const dataToSend = {
        airline_code: result[0].airline_code,
        flight_number: result[0].flight_number,
        scheduled_time: result[0].scheduled_time,
        estimated_time: result[0].estimated_time,
        last_status: result[0].status_en,
        last_updated: new Date().toISOString(),
        airline_name: result[0].airline_name,
        airport_code: result[0].airport_code,
        city_en: result[0].city_en,
        city_he: result[0].city_he,
        country_en: result[0].country_en,
        country_he: result[0].country_he,
        terminal: result[0].terminal,
        counters: result[0].counters,
        checkin_zone: result[0].checkin_zone
    };

    const response = await fetch(url + "users/subscribe", {
        method: "POST",
        headers: {
        "Content-Type": "application/json"
        },
        body: JSON.stringify(dataToSend)
    });

    if (!response.ok) {
        throw new Error(`Subscription failed: ${response.status}`);
    }

    showMessage("subscriptionMessage", "Flight subscription added successfully!");
    document.getElementById("subscriptionForm").reset();
    loadUserSubscriptions(); // Refresh the list
    } catch (error) {
    console.error("Subscription error:", error);
    showMessage("subscriptionMessage", `Failed to subscribe: ${error.message}`, true);
    } finally {
    hideButtonLoading('subscribeBtn');
    }
});

// Refresh button handler
document.getElementById("refreshSubscriptions").addEventListener("click", loadUserSubscriptions);

// Load subscriptions on page load with loading animation
window.addEventListener("load", async function() {
    showPageLoading();
    
    try {
    // Add a small delay to show the loading animation
    await new Promise(resolve => setTimeout(resolve, 800));
    await loadUserSubscriptions();
    } catch (error) {
    console.error("Error during page load:", error);
    } finally {
    hidePageLoading();
    }
});