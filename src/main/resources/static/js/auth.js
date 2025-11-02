// auth.js - Authentication related functions
document.addEventListener('DOMContentLoaded', function() {
    // Login Form
    const loginForm = document.getElementById('loginForm');
    if (loginForm) {
        loginForm.addEventListener('submit', handleLogin);
    }

    // SignUp Form
    const signupForm = document.getElementById('signupForm');
    if (signupForm) {
        signupForm.addEventListener('submit', handleSignup);
    }

    // Forgot Password Form
    const forgotPassForm = document.getElementById('forgotPassForm');
    if (forgotPassForm) {
        forgotPassForm.addEventListener('submit', handleForgotPassword);
    }
});

async function handleLogin(e) {
    e.preventDefault();
    const formData = {
        email: document.getElementById('email').value,
        password: document.getElementById('password').value
    };

    try {
        const response = await fetch('/auth/login', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(formData)
        });

        const data = await response.json();
        if (data.success) {
            localStorage.setItem('token', data.token);
            window.location.href = '/dashboard';
        } else {
            alert('Login failed: ' + data.message);
        }
    } catch (error) {
        alert('Login error: ' + error.message);
    }
}

async function handleSignup(e) {
    e.preventDefault();
    // Signup logic here
}

async function handleForgotPassword(e) {
    e.preventDefault();
    // Forgot password logic here
}