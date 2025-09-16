/**
 * Loading States Module
 * Manages loading indicators for different UI elements
 */

const LoadingManager = {
    
    /**
     * Show/hide page loading overlay
     */
    showPageLoading() {
        document.getElementById('pageLoadingOverlay').style.display = 'flex';
    },

    hidePageLoading() {
        document.getElementById('pageLoadingOverlay').style.display = 'none';
    },

    /**
     * Show/hide section loading
     */
    showSectionLoading(sectionId) {
        const section = document.getElementById(sectionId);
        if (section) {
            section.classList.add('section-loading');
        }
    },

    hideSectionLoading(sectionId) {
        const section = document.getElementById(sectionId);
        if (section) {
            section.classList.remove('section-loading');
        }
    },

    /**
     * Show/hide button loading
     */
    showButtonLoading(buttonId) {
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
    },

    hideButtonLoading(buttonId) {
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
};