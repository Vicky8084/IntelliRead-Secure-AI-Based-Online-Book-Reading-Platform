// admin.js - Admin functionality
document.addEventListener('DOMContentLoaded', function() {
    // Admin specific functions
    loadAdminData();
});

async function loadAdminData() {
    try {
        const response = await fetch('/admin/data', {
            headers: {
                'Authorization': 'Bearer ' + localStorage.getItem('token')
            }
        });
        // Admin data load karo
    } catch (error) {
        console.error('Admin data load error:', error);
    }
}