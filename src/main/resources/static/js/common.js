// Common utility functions for all pages

// JWT Token management
function getAuthHeader() {
    const token = localStorage.getItem('jwtToken');
    return token ? { 'Authorization': `Bearer ${token}` } : {};
}

// Check if user is logged in
function isLoggedIn() {
    return localStorage.getItem('isLoggedIn') === 'true';
}

// Get current user
function getCurrentUser() {
    const user = localStorage.getItem('user');
    return user ? JSON.parse(user) : null;
}

// Logout function
function logout() {
    localStorage.removeItem('user');
    localStorage.removeItem('isLoggedIn');
    localStorage.removeItem('jwtToken');
    localStorage.removeItem('isAdmin');
    localStorage.removeItem('admin');
    window.location.href = '/login';
}

// API call with auth
async function apiCall(url, options = {}) {
    const defaultOptions = {
        headers: {
            'Content-Type': 'application/json',
            ...getAuthHeader()
        }
    };

    const finalOptions = { ...defaultOptions, ...options };

    try {
        const response = await fetch(url, finalOptions);
        return await response.json();
    } catch (error) {
        console.error('API call error:', error);
        throw error;
    }
}

// Redirect based on role
function redirectBasedOnRole() {
    const user = getCurrentUser();
    if (!user) return;

    const currentPath = window.location.pathname;

    switch(user.role) {
        case 'ADMIN':
            if (!currentPath.includes('admin')) {
                window.location.href = '/admin-dashboard';
            }
            break;
        case 'PUBLISHER':
            if (!currentPath.includes('publisher')) {
                window.location.href = '/publisher-dashboard';
            }
            break;
        case 'USER':
            if (!currentPath.includes('books')) {
                window.location.href = '/books';
            }
            break;
    }
}

// Check authentication on page load
document.addEventListener('DOMContentLoaded', function() {
    if (!isLoggedIn() && !window.location.pathname.includes('/login') &&
        !window.location.pathname.includes('/signup') &&
        !window.location.pathname.includes('/forgotpassword') &&
        !window.location.pathname.includes('/Home')) {
        window.location.href = '/login';
    } else {
        redirectBasedOnRole();
    }
});