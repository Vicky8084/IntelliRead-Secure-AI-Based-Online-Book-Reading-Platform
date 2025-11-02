// Admin Login Functionality
document.addEventListener('DOMContentLoaded', function() {
    const loginForm = document.getElementById('adminLoginForm');

    if (loginForm) {
        loginForm.addEventListener('submit', async function(e) {
            e.preventDefault();

            const email = document.getElementById('email').value;
            const password = document.getElementById('password').value;

            await handleAdminLogin(email, password);
        });
    }

    // Check if already logged in
    checkExistingSession();
});

async function checkExistingSession() {
    const token = localStorage.getItem('jwtToken');
    const user = localStorage.getItem('user');

    if (token && user) {
        try {
            const userData = JSON.parse(user);
            if (userData.role === 'ADMIN') {
                // Verify session with backend
                const response = await fetch('/auth/check', {
                    headers: {
                        'Authorization': `Bearer ${token}`
                    }
                });

                const authCheck = await response.json();
                if (authCheck.authenticated) {
                    // Redirect to dashboard
                    window.location.href = '/admin-dashboard';
                }
            }
        } catch (error) {
            // Clear invalid session
            localStorage.removeItem('jwtToken');
            localStorage.removeItem('user');
            localStorage.removeItem('isLoggedIn');
        }
    }
}

async function handleAdminLogin(email, password) {
    try {
        showLoading('Logging in...');

        const response = await fetch('/auth/admin/login', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({ email, password })
        });

        const data = await response.json();

        if (data.success) {
            // Store token and user data in localStorage
            localStorage.setItem('jwtToken', data.token);
            localStorage.setItem('user', JSON.stringify({
                id: data.userId,
                name: data.name,
                email: data.email,
                role: data.role
            }));
            localStorage.setItem('isLoggedIn', 'true');

            showSuccess('Login successful! Redirecting to admin dashboard...');

            // Redirect to admin dashboard after 1 second
            setTimeout(() => {
                window.location.href = '/admin-dashboard';
            }, 1000);
        } else {
            showError(data.message || 'Login failed');
        }
    } catch (error) {
        console.error('Login error:', error);
        showError('Login failed. Please try again.');
    } finally {
        hideLoading();
    }
}

// Utility functions for notifications
function showLoading(message) {
    // You can implement a proper loading indicator here
    const submitBtn = document.querySelector('#adminLoginForm button[type="submit"]');
    if (submitBtn) {
        submitBtn.disabled = true;
        submitBtn.innerHTML = '<i class="bx bx-loader-alt bx-spin"></i> ' + message;
    }
}

function hideLoading() {
    const submitBtn = document.querySelector('#adminLoginForm button[type="submit"]');
    if (submitBtn) {
        submitBtn.disabled = false;
        submitBtn.innerHTML = '<i class="bx bx-log-in"></i> Login';
    }
}

function showSuccess(message) {
    // You can use your existing notification system or a simple alert
    if (typeof showNotification === 'function') {
        showNotification(message, 'success');
    } else {
        alert('✅ ' + message);
    }
}

function showError(message) {
    if (typeof showNotification === 'function') {
        showNotification(message, 'error');
    } else {
        alert('❌ ' + message);
    }
}