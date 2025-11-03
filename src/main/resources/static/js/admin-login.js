// Enhanced Admin Login with Better Error Handling
document.addEventListener('DOMContentLoaded', async () => {
    console.log('🔐 Admin Login Page Loaded');

    const adminLoginForm = document.getElementById('adminLoginForm');

    adminLoginForm.addEventListener('submit', async (e) => {
        e.preventDefault();
        e.stopPropagation();

        const email = document.getElementById('email').value.trim();
        const password = document.getElementById('password').value;

        if (!email || !password) {
            alert('Please fill in all fields');
            return;
        }

        const loginBtn = adminLoginForm.querySelector('.login-btn');
        const originalText = loginBtn.textContent;
        loginBtn.textContent = 'Signing In...';
        loginBtn.disabled = true;

        try {
            console.log('📤 Sending ADMIN login request...');
            console.log('📧 Email:', email);

            // ✅ Use ADMIN login endpoint
            const response = await fetch('http://localhost:8035/auth/admin/login', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({
                    email: email,
                    password: password
                })
            });

            console.log('✅ Response status:', response.status);

            // Get response text first
            const responseText = await response.text();
            console.log('📥 Raw response text:', responseText);

            let data;

            // Try to parse as JSON
            try {
                data = JSON.parse(responseText);
                console.log('📥 Parsed JSON data:', data);
            } catch (parseError) {
                console.error('❌ JSON parse error:', parseError);
                throw new Error('Server error. Please try again.');
            }

            if (data.success) {
                // ✅ Store user data in localStorage
                const userData = {
                    email: data.email,
                    name: data.name,
                    role: data.role,
                    userId: data.userId
                };

                localStorage.setItem('user', JSON.stringify(userData));
                localStorage.setItem('isLoggedIn', 'true');
                if (data.token) {
                    localStorage.setItem('jwtToken', data.token);
                }
                localStorage.setItem('loginTime', new Date().toISOString());

                console.log('💾 Admin data stored in localStorage');

                // ✅ ALWAYS redirect to ADMIN DASHBOARD
                console.log('🔄 Redirecting to ADMIN DASHBOARD');
                alert('✅ Admin login successful! Redirecting to admin dashboard...');

                setTimeout(() => {
                    window.location.href = '/admin-dashboard';
                }, 1000);

            } else {
                console.log('❌ Login failed with message:', data.message);
                alert(data.message || 'Login failed. Please try again.');
                loginBtn.textContent = originalText;
                loginBtn.disabled = false;
            }
        } catch (error) {
            console.error('❌ Admin login error:', error);
            alert('Login error: ' + error.message);
            loginBtn.textContent = originalText;
            loginBtn.disabled = false;
        }
    });

    // Add enter key support
    adminLoginForm.addEventListener('keypress', (e) => {
        if (e.key === 'Enter') {
            e.preventDefault();
            adminLoginForm.dispatchEvent(new Event('submit'));
        }
    });
});