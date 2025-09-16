# CSS Organization Documentation

## Overview
The CSS codebase has been modularized into focused, single-responsibility files to improve maintainability, reusability, and development experience. The original 1123-line `styles.css` file has been broken down into 8 specialized CSS modules.

## Directory Structure

```
src/main/resources/static/css/
├── main.css              # Main import file - imports all modules
├── variables.css         # Design tokens and CSS custom properties
├── base.css             # CSS reset, typography, utilities, animations
├── layout.css           # Container, header, sections, grid systems
├── navigation.css       # Top navigation, menu, logo styles
├── components.css       # Buttons, forms, messages, subscriptions
├── loading.css          # Loading states, spinners, overlays
├── responsive.css       # Mobile-first responsive design
└── styles.css           # (DEPRECATED - replaced by modular system)
```

## Module Descriptions

### 1. `variables.css` - Design Tokens
**Purpose**: Centralized design system with CSS custom properties
**Contains**:
- Brand colors (primary-blue, accent-blue, orange, etc.)
- UI colors (success, error, borders, backgrounds)
- Color gradients
- Spacing scale (xs, sm, md, lg, xl, etc.)
- Typography (font sizes, weights, families)
- Border radius values
- Shadow definitions
- Z-index scale
- Breakpoint references
- Transition timing
- Loading animation sizes

**Benefits**:
- Consistent design tokens across the application
- Easy theme customization
- Maintainable color palette
- Scalable spacing system

### 2. `base.css` - Foundation Styles
**Purpose**: CSS reset, typography, and fundamental utilities
**Contains**:
- CSS reset (`* { margin: 0; padding: 0; box-sizing: border-box; }`)
- Body and typography base styles
- Utility classes (text sizes, spacing, flexbox, visibility)
- Core animations (`@keyframes float`, `@keyframes spin`)

**Benefits**:
- Consistent cross-browser rendering
- Reusable utility classes
- Foundation for all other styles

### 3. `layout.css` - Structure & Layout
**Purpose**: Main layout components and containers
**Contains**:
- Main container styles
- Header and content area layouts
- Section styling
- Grid layouts (form-row, form-row-triple)
- User info section
- Empty state styling

**Benefits**:
- Separation of layout concerns
- Reusable layout patterns
- Consistent spacing and structure

### 4. `navigation.css` - Navigation Components
**Purpose**: Top navigation and menu functionality
**Contains**:
- Top navigation bar
- Logo and branding
- Hamburger menu toggle
- Dropdown menu styling
- Menu item interactions

**Benefits**:
- Isolated navigation styles
- Easy to modify menu behavior
- Clean separation from layout

### 5. `components.css` - UI Components
**Purpose**: Reusable UI components and form elements
**Contains**:
- Button styles and variants (primary, danger, refresh, logout)
- Form elements (inputs, labels, validation)
- Message components (success, error)
- Subscription item styling
- Flight icon styling

**Benefits**:
- Component-based styling approach
- Consistent button and form styling
- Reusable UI patterns

### 6. `loading.css` - Loading States
**Purpose**: All loading-related animations and states
**Contains**:
- Basic spinners (small, medium, large)
- Page loading overlay
- Button loading states
- Section loading overlays
- Loading spinner positioning
- Loading state stability rules

**Benefits**:
- Centralized loading behavior
- Consistent loading animations
- Performance optimizations for loading states

### 7. `responsive.css` - Mobile-First Design
**Purpose**: Responsive design with mobile-first approach
**Contains**:
- Mobile breakpoint styles (768px and down)
- Small mobile styles (480px and down)
- Very small mobile (360px and down)
- Landscape orientation adjustments
- Touch-friendly improvements
- Accessibility enhancements

**Benefits**:
- Mobile-first responsive design
- Clear breakpoint organization
- Touch-optimized interface

### 8. `main.css` - Import Coordinator
**Purpose**: Central import file that loads all modules in correct order
**Import Order**:
1. `variables.css` - Design tokens first
2. `base.css` - Foundation styles
3. `layout.css` - Layout structure
4. `navigation.css` - Navigation components
5. `components.css` - UI components
6. `loading.css` - Loading states
7. `responsive.css` - Responsive overrides last

**Benefits**:
- Single entry point for all styles
- Proper cascade order
- Easy to manage dependencies

## Usage

### In HTML
```html
<link rel="stylesheet" href="css/main.css">
```

### For Development
- Modify individual CSS modules as needed
- Variables can be changed in `variables.css` to affect the entire design system
- New components should be added to `components.css`
- Responsive adjustments go in `responsive.css`

## CSS Custom Properties Usage

The design system uses CSS custom properties extensively:

```css
/* Using design tokens */
.my-component {
    background: var(--primary-gradient);
    padding: var(--spacing-lg);
    border-radius: var(--radius-md);
    box-shadow: var(--shadow-md);
    color: var(--white);
}

/* Responsive spacing */
.responsive-element {
    margin: var(--spacing-md);
    font-size: var(--font-size-base);
}
```

## Benefits of This Organization

1. **Maintainability**: Each concern is separated into its own file
2. **Scalability**: Easy to add new components without affecting existing ones
3. **Performance**: Better caching and loading strategies
4. **Collaboration**: Multiple developers can work on different CSS modules
5. **Debugging**: Easier to locate and fix style issues
6. **Consistency**: Design tokens ensure consistent styling
7. **Mobile-First**: Proper responsive design approach
8. **Accessibility**: Dedicated mobile optimizations and touch improvements

## Migration Notes

- The original `styles.css` file is now deprecated
- All functionality has been preserved in the modular system
- CSS custom properties replace hardcoded values
- Responsive design is now mobile-first
- Loading states are more robust and consistent

## Future Enhancements

1. **CSS-in-JS Migration**: Consider migrating to CSS-in-JS for component-scoped styles
2. **Build Process**: Add CSS minification and optimization
3. **Theme System**: Extend variables.css for multiple themes (dark mode, high contrast)
4. **Component Library**: Create a standalone component library documentation
5. **CSS Grid**: Modernize layout system with CSS Grid where appropriate