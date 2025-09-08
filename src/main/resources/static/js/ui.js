/**
 * UI Management Functions
 */

window.FlightApp = window.FlightApp || {};

window.FlightApp.ui = {
    /**
     * Show page loading overlay
     */
    showPageLoading() {
        document.getElementById('pageLoadingOverlay').style.display = 'flex';
    },

    /**
     * Hide page loading overlay
     */
    hidePageLoading() {
        document.getElementById('pageLoadingOverlay').style.display = 'none';
    },

    /**
     * Show section loading state
     * @param {string} sectionId - ID of the section element
     */
    showSectionLoading(sectionId) {
        const section = document.getElementById(sectionId);
        if (section) {
            section.classList.add('section-loading');
        }
    },

    /**
     * Hide section loading state
     * @param {string} sectionId - ID of the section element
     */
    hideSectionLoading(sectionId) {
        const section = document.getElementById(sectionId);
        if (section) {
            section.classList.remove('section-loading');
        }
    },

    /**
     * Show button loading state
     * @param {string} buttonId - ID of the button element
     */
    showButtonLoading(buttonId) {
        const button = document.getElementById(buttonId);
        if (button) {
            // Store original styles to prevent any changes
            const originalStyle = button.style.cssText;
            button.dataset.originalStyle = originalStyle;
            
            button.classList.add('loading');
            button.disabled = true;
            
            // Add spinner next to button
            const spinner = document.createElement('div');
            spinner.className = 'loading-spinner-beside';
            spinner.id = buttonId + '_spinner';
            button.parentNode.insertBefore(spinner, button.nextSibling);
        }
    },

    /**
     * Hide button loading state
     * @param {string} buttonId - ID of the button element
     */
    hideButtonLoading(buttonId) {
        const button = document.getElementById(buttonId);
        if (button) {
            button.classList.remove('loading');
            button.disabled = false;
            
            // Restore original styles if any were stored
            if (button.dataset.originalStyle !== undefined) {
                button.style.cssText = button.dataset.originalStyle;
                delete button.dataset.originalStyle;
            }
            
            // Remove spinner
            const spinner = document.getElementById(buttonId + '_spinner');
            if (spinner) {
                spinner.remove();
            }
        }
    },

    /**
     * Display success or error messages
     * @param {string} elementId - ID of the element to show message in
     * @param {string} message - Message to display
     * @param {boolean} isError - Whether this is an error message
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
    }
};
