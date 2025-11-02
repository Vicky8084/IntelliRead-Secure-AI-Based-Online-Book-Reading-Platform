// Auth functionality for registration and login

// Registration function
async function handleRegistration(userData) {
    try {
        const response = await fetch('/auth/register', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(userData)
        });

        const data = await response.json();

        if (data.success) {
            return { success: true, message: data.message };
        } else {
            return { success: false, message: data.message };
        }
    } catch (error) {
        console.error('Registration error:', error);
        return { success: false, message: 'Registration failed. Please try again.' };
    }
}

// Password reset request
async function handleForgotPassword(email) {
    try {
        const response = await fetch('/auth/password/forgot', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({ email })
        });

        const data = await response.json();
        return data;
    } catch (error) {
        console.error('Password reset error:', error);
        return { success: false, message: 'Password reset request failed.' };
    }
}

// Password reset confirmation
async function handlePasswordReset(token, newPassword) {
    try {
        const response = await fetch('/auth/password/reset', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({ token, newPassword })
        });

        const data = await response.json();
        return data;
    } catch (error) {
        console.error('Password reset error:', error);
        return { success: false, message: 'Password reset failed.' };
    }
}

// Logout function
async function handleLogout() {
    try {
        await fetch('/auth/logout', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            }
        });
    } catch (error) {
        console.error('Logout error:', error);
    } finally {
        // Always clear local storage
        localStorage.removeItem('jwtToken');
        localStorage.removeItem('user');
        localStorage.removeItem('isLoggedIn');
        window.location.href = '/login';
    }
}