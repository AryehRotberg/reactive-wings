# Static Files Organization

This document describes the organized structure of the static web files for the Flight Subscription Manager application.

## Directory Structure

```
src/main/resources/static/
├── index.html                 # Main HTML file
├── css/                       # Stylesheets directory
│   └── styles.css            # Main application styles
├── js/                        # JavaScript modules directory
│   ├── api.js                # API service module - handles HTTP requests
│   ├── loading.js            # Loading states manager - controls loading indicators  
│   ├── ui-utils.js           # UI utilities - messaging, formatting, validation
│   ├── subscription-manager.js # Subscription functionality - core business logic
│   └── main.js               # Main application - initialization and coordination
└── assets/                   # Assets directory (for images, fonts, etc.)
```

## JavaScript Module Organization

The JavaScript code has been modularized into focused, single-responsibility modules:

### 1. `api.js` - API Service Module
- Handles all HTTP requests to the backend API
- Contains methods for user info, flight search, subscription management
- Centralizes API endpoint management

### 2. `loading.js` - Loading States Manager  
- Manages loading indicators for different UI elements
- Page loading overlay, section loading, button loading states
- Maintains UI consistency during async operations

### 3. `ui-utils.js` - UI Utilities Module
- Message handling and display
- Date formatting utilities
- HTML generation for UI components
- Form validation
- Menu initialization

### 4. `subscription-manager.js` - Subscription Manager
- Core subscription functionality
- User subscription loading and display
- Flight subscription and unsubscription logic
- Form handling for subscription creation

### 5. `main.js` - Main Application Module
- Application initialization and coordination
- Event handler setup
- Module orchestration
- Entry point for the application

## Loading Order

The JavaScript modules are loaded in the following dependency order in `index.html`:

1. `api.js` - Base API functionality
2. `loading.js` - Loading state management
3. `ui-utils.js` - UI utilities and helpers
4. `subscription-manager.js` - Business logic (depends on API, Loading, UI Utils)
5. `main.js` - Application coordination (depends on all previous modules)

## Benefits of This Organization

1. **Modularity**: Each file has a single, clear responsibility
2. **Maintainability**: Easier to locate and modify specific functionality
3. **Reusability**: Modules can be reused across different parts of the application
4. **Debugging**: Easier to isolate and fix issues in specific modules
5. **Performance**: Better caching and loading strategies possible
6. **Collaboration**: Multiple developers can work on different modules simultaneously
7. **Testing**: Individual modules can be tested in isolation

## CSS Organization

The CSS has been modularized into focused, single-responsibility files:

```
css/
├── main.css              # Central import file
├── variables.css         # Design tokens & CSS custom properties
├── base.css             # Reset, typography, utilities
├── layout.css           # Container, header, sections
├── navigation.css       # Top nav, menu, logo
├── components.css       # Buttons, forms, messages
├── loading.css          # Loading states & spinners
└── responsive.css       # Mobile-first responsive design
```

**Benefits**:
- **Maintainable**: Each file has a single, clear responsibility
- **Scalable**: Easy to extend without affecting other modules
- **Consistent**: Design tokens ensure uniform styling
- **Performance**: Better caching and loading strategies
- **Mobile-First**: Proper responsive design approach

See `css/CSS-ORGANIZATION.md` for detailed documentation.

## Future Improvements

Consider these enhancements for further organization:

1. **CSS Modularization**: Split CSS into component-specific files
2. **Asset Management**: Add images, icons, and fonts to the `assets/` directory
3. **Build Process**: Implement minification and bundling for production
4. **Type Safety**: Consider migrating to TypeScript for better development experience
5. **Documentation**: Add JSDoc comments to all JavaScript functions