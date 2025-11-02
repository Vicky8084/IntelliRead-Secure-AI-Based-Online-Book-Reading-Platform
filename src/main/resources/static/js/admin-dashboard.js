// Enhanced Admin Dashboard with Backend Connectivity
document.addEventListener('DOMContentLoaded', function() {
    console.log('🏁 Admin Dashboard Initialized');

    // Check if admin is logged in
    checkAdminAuth();

    // Load dashboard data
    loadDashboardSummary();
    loadUsers();
    loadBooks();

    // Set up event listeners
    setupEventListeners();
});

// Enhanced authentication check
async function checkAdminAuth() {
    const token = localStorage.getItem('jwtToken');
    const user = localStorage.getItem('user');

    if (!token || !user) {
        console.log('❌ No admin session found, redirecting to login...');
        window.location.href = '/admin';
        return;
    }

    try {
        const userData = JSON.parse(user);

        // Verify with backend
        const response = await fetch('/auth/check', {
            headers: {
                'Authorization': `Bearer ${token}`
            }
        });

        const authCheck = await response.json();

        if (!authCheck.authenticated || userData.role !== 'ADMIN') {
            console.log('❌ Invalid admin session, redirecting...');
            localStorage.removeItem('jwtToken');
            localStorage.removeItem('user');
            localStorage.removeItem('isLoggedIn');
            window.location.href = '/admin';
            return;
        }

        console.log('✅ Admin authenticated:', userData.email);
    } catch (error) {
        console.log('❌ Auth check failed, redirecting to login...');
        localStorage.removeItem('jwtToken');
        localStorage.removeItem('user');
        localStorage.removeItem('isLoggedIn');
        window.location.href = '/admin';
    }
}

// Enhanced API call with error handling
async function makeAuthenticatedRequest(url, options = {}) {
    const token = localStorage.getItem('jwtToken');

    if (!token) {
        throw new Error('No authentication token found');
    }

    const defaultOptions = {
        headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${token}`
        }
    };

    const finalOptions = { ...defaultOptions, ...options };

    try {
        const response = await fetch(url, finalOptions);

        if (response.status === 401) {
            // Token expired or invalid
            localStorage.removeItem('jwtToken');
            localStorage.removeItem('user');
            localStorage.removeItem('isLoggedIn');
            window.location.href = '/admin';
            throw new Error('Authentication failed');
        }

        return response;
    } catch (error) {
        console.error('API request failed:', error);
        throw error;
    }
}

// Rest of your existing dashboard functions remain the same...
// loadDashboardSummary(), loadUsers(), etc.