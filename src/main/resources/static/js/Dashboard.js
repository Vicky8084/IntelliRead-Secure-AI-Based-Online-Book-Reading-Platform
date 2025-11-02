// Dashboard.js - Admin Dashboard Functionality

// Global variables
let currentUserPage = 1;
let currentBookPage = 1;
const usersPerPage = 10;
const booksPerPage = 10;
let allUsers = [];
let allBooks = [];

// ===== DASHBOARD INITIALIZATION =====
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

// ===== AUTHENTICATION =====
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

// ===== ENHANCED API CALLS WITH JWT =====
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

// ===== NAVIGATION =====
function showSection(sectionId) {
    // Hide all sections
    const sections = document.querySelectorAll('.section');
    sections.forEach(section => {
        section.classList.remove('active');
    });

    // Remove active class from all menu items
    const menuItems = document.querySelectorAll('.menu-item');
    menuItems.forEach(item => {
        item.classList.remove('active');
    });

    // Show selected section
    const targetSection = document.getElementById(sectionId);
    if (targetSection) {
        targetSection.classList.add('active');
    }

    // Activate corresponding menu item
    const targetMenuItem = document.querySelector(`.menu-item[onclick="showSection('${sectionId}')"]`);
    if (targetMenuItem) {
        targetMenuItem.classList.add('active');
    }

    // Load section-specific data
    switch(sectionId) {
        case 'dashboard':
            loadDashboardSummary();
            break;
        case 'users':
            loadUsers();
            break;
        case 'books':
            loadBooks();
            break;
        case 'analytics':
            loadAnalytics();
            break;
    }
}

// ===== DASHBOARD SUMMARY =====
async function loadDashboardSummary() {
    try {
        // ✅ USE AUTHENTICATED REQUEST
        const response = await makeAuthenticatedRequest('/api/admin/summary');

        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }

        const data = await response.json();

        if (data.success) {
            // Update stats cards
            document.getElementById('totalUsers').textContent = data.totalUsers;
            document.getElementById('totalBooks').textContent = data.totalBooks;
            document.getElementById('pendingBooks').textContent = data.pendingBooks;
            document.getElementById('activeUsers').textContent = data.activeUsers;

            // Update additional stats if elements exist
            const pendingPublishersElement = document.getElementById('pendingPublishers');
            const totalPublishersElement = document.getElementById('totalPublishers');

            if (pendingPublishersElement) {
                pendingPublishersElement.textContent = data.pendingPublishers || 0;
            }
            if (totalPublishersElement) {
                totalPublishersElement.textContent = data.totalPublishers || 0;
            }

            // Load recent activity
            loadRecentActivity();
        } else {
            console.error('Error loading dashboard summary:', data.message);
            showNotification('Error loading dashboard data', 'error');
        }
    } catch (error) {
        console.error('Error loading dashboard summary:', error);
        showNotification('Network error loading dashboard', 'error');
    }
}

// ... (Rest of your existing Dashboard.js functions remain SAME)
// loadUsers(), loadBooks(), etc. - no changes needed

async function loadRecentActivity() {
    try {
        // This would typically fetch from an activity endpoint
        const activityList = document.getElementById('recentActivity');
        if (activityList) {
            activityList.innerHTML = `
                <div class="activity-item">
                    <div class="activity-icon user">
                        <i class='bx bx-user-plus'></i>
                    </div>
                    <div class="activity-info">
                        <p>New user registration</p>
                        <span>Just now</span>
                    </div>
                </div>
                <div class="activity-item">
                    <div class="activity-icon book">
                        <i class='bx bx-book-add'></i>
                    </div>
                    <div class="activity-info">
                        <p>New book pending approval</p>
                        <span>5 minutes ago</span>
                    </div>
                </div>
                <div class="activity-item">
                    <div class="activity-icon system">
                        <i class='bx bx-user-check'></i>
                    </div>
                    <div class="activity-info">
                        <p>Publisher account approved</p>
                        <span>1 hour ago</span>
                    </div>
                </div>
            `;
        }
    } catch (error) {
        console.error('Error loading recent activity:', error);
    }
}

// ===== USER MANAGEMENT =====
async function loadUsers(page = 1) {
    try {
        currentUserPage = page;

        // Show loading state
        const userTableBody = document.getElementById('userTableBody');
        userTableBody.innerHTML = `
            <tr>
                <td colspan="5" class="loading-cell">
                    <div class="loading-spinner"></div>
                    Loading users...
                </td>
            </tr>
        `;

        // ✅ USE AUTHENTICATED REQUEST
        const response = await makeAuthenticatedRequest(`/api/admin/users?page=${page}&limit=${usersPerPage}`);

        if (!response.ok) {
            if (response.status === 401) {
                // Token expired or invalid
                localStorage.removeItem('jwtToken');
                localStorage.removeItem('user');
                window.location.href = '/admin';
                return;
            }
            throw new Error(`HTTP error! status: ${response.status}`);
        }

        const data = await response.json();

        if (data.success) {
            allUsers = data.users;
            userTableBody.innerHTML = '';

            if (data.users.length === 0) {
                userTableBody.innerHTML = `
                    <tr>
                        <td colspan="5" class="no-data">
                            <i class='bx bx-user-x'></i>
                            <p>No users found</p>
                        </td>
                    </tr>
                `;
                return;
            }

            data.users.forEach(user => {
                const row = document.createElement('tr');

                // Ensure user has an _id
                if (!user._id) {
                    console.warn('User missing ID:', user);
                    return;
                }

                // Determine status and badge class
                let statusText, statusClass;
                if (user.role === 'admin') {
                    if (user.isApproved) {
                        statusText = 'Approved Admin';
                        statusClass = 'status-approved';
                    } else {
                        statusText = 'Pending Approval';
                        statusClass = 'status-pending';
                    }
                } else {
                    statusText = user.isActive !== false ? 'Active User' : 'Inactive';
                    statusClass = user.isActive !== false ? 'status-active' : 'status-inactive';
                }

                row.innerHTML = `
                    <td>
                        <div class="user-avatar">
                            <i class='bx bx-user'></i>
                            <span>${user.fullName || 'Unknown User'}</span>
                        </div>
                    </td>
                    <td>${user.email || 'No email'}</td>
                    <td>${user.createdAt ? formatDate(user.createdAt) : 'Unknown'}</td>
                    <td>
                        <span class="status-badge ${statusClass}">${statusText}</span>
                    </td>
                    <td>
                        <div class="action-buttons">
                            <button class="btn-info" onclick="viewUser('${user._id}')" title="View Details">
                                <i class='bx bx-show'></i> View
                            </button>
                            ${user.role === 'admin' && !user.isApproved ?
                                `<button class="btn-success" onclick="approvePublisher('${user._id}')" title="Approve Admin">
                                    <i class='bx bx-check'></i> Approve
                                </button>
                                 <button class="btn-danger" onclick="rejectPublisher('${user._id}')" title="Reject Admin">
                                    <i class='bx bx-x'></i> Reject
                                 </button>` :
                                `<button class="btn-danger" onclick="deleteUser('${user._id}')" title="Delete User">
                                    <i class='bx bx-trash'></i> Delete
                                 </button>`
                            }
                        </div>
                    </td>
                `;
                userTableBody.appendChild(row);
            });

            // Update pagination
            updateUserPagination(data.totalPages, page);
        } else {
            console.error('Error loading users:', data.message);
            showNotification('Error loading users: ' + data.message, 'error');
        }
    } catch (error) {
        console.error('Error loading users:', error);
        showNotification('Network error loading users: ' + error.message, 'error');
    }
}

function updateUserPagination(totalPages, currentPage) {
    const pagination = document.getElementById('userPagination');
    if (!pagination) return;

    let paginationHTML = '';

    if (currentPage > 1) {
        paginationHTML += `<button class="page-btn" onclick="loadUsers(${currentPage - 1})">Previous</button>`;
    }

    for (let i = 1; i <= totalPages; i++) {
        if (i === currentPage) {
            paginationHTML += `<button class="page-btn active">${i}</button>`;
        } else {
            paginationHTML += `<button class="page-btn" onclick="loadUsers(${i})">${i}</button>`;
        }
    }

    if (currentPage < totalPages) {
        paginationHTML += `<button class="page-btn" onclick="loadUsers(${currentPage + 1})">Next</button>`;
    }

    pagination.innerHTML = paginationHTML;
}

// ===== PUBLISHER APPROVAL =====
async function approvePublisher(userId) {
    if (confirm('Are you sure you want to approve this admin?')) {
        try {
            showNotification('Approving admin...', 'info');

            // ✅ USE AUTHENTICATED REQUEST
            const response = await makeAuthenticatedRequest(`/api/admin/publishers/${userId}/approve`, {
                method: 'PUT',
                body: JSON.stringify({ approve: true })
            });

            const data = await response.json();

            if (data.success) {
                showNotification('Admin approved successfully!', 'success');
                loadUsers(currentUserPage);
                loadDashboardSummary();
            } else {
                showNotification('Error: ' + data.message, 'error');
            }
        } catch (error) {
            console.error('Error approving admin:', error);
            showNotification('Error approving admin', 'error');
        }
    }
}

async function rejectPublisher(userId) {
    if (confirm('Are you sure you want to reject this admin? This will delete their account.')) {
        try {
            showNotification('Rejecting admin...', 'info');

            // ✅ USE AUTHENTICATED REQUEST
            const response = await makeAuthenticatedRequest(`/api/admin/publishers/${userId}/approve`, {
                method: 'PUT',
                body: JSON.stringify({ approve: false })
            });

            const data = await response.json();

            if (data.success) {
                showNotification('Admin rejected and account removed!', 'success');
                loadUsers(currentUserPage);
                loadDashboardSummary();
            } else {
                showNotification('Error: ' + data.message, 'error');
            }
        } catch (error) {
            console.error('Error rejecting admin:', error);
            showNotification('Error rejecting admin', 'error');
        }
    }
}

// ===== USER ACTIONS =====
async function viewUser(userId) {
    try {
        console.log('Fetching user details for:', userId);

        // ✅ USE AUTHENTICATED REQUEST
        const response = await makeAuthenticatedRequest(`/api/admin/users/${userId}`);

        if (!response.ok) {
            if (response.status === 401) {
                localStorage.removeItem('jwtToken');
                localStorage.removeItem('user');
                window.location.href = '/admin';
                return;
            }
            throw new Error(`HTTP error! status: ${response.status}`);
        }

        const data = await response.json();

        if (data.success) {
            showUserModal(data.user);
        } else {
            console.error('Error loading user details:', data.message);
            showNotification('Error loading user details: ' + data.message, 'error');
        }
    } catch (error) {
        console.error('Error viewing user:', error);

        // Fallback: Try to find user in already loaded data
        const user = allUsers.find(u => u._id === userId);
        if (user) {
            console.log('Using cached user data for:', userId);
            showUserModal(user);
        } else {
            showNotification('Error loading user details. Please try again.', 'error');
        }
    }
}

function showUserModal(user) {
    const modal = document.getElementById('userModal');
    const userDetails = document.getElementById('userDetails');

    if (modal && userDetails) {
        // Format dates properly
        const joinDate = user.createdAt ? formatDateTime(user.createdAt) : 'Unknown';
        const lastLogin = user.lastLogin ? formatDateTime(user.lastLogin) : 'Never';

        userDetails.innerHTML = `
            <div class="user-detail-grid">
                <div class="user-detail">
                    <label>Full Name:</label>
                    <span>${user.fullName || 'N/A'}</span>
                </div>
                <div class="user-detail">
                    <label>Email:</label>
                    <span>${user.email || 'N/A'}</span>
                </div>
                <div class="user-detail">
                    <label>Role:</label>
                    <span class="role-badge ${user.role || 'user'}">${user.role || 'User'}</span>
                </div>
                <div class="user-detail">
                    <label>Status:</label>
                    <span class="status-badge ${getUserStatusClass(user)}">
                        ${getUserStatusText(user)}
                    </span>
                </div>
                <div class="user-detail">
                    <label>Account Created:</label>
                    <span>${joinDate}</span>
                </div>
                <div class="user-detail">
                    <label>Last Login:</label>
                    <span>${lastLogin}</span>
                </div>
                ${user.role === 'admin' ? `
                <div class="user-detail">
                    <label>Admin Status:</label>
                    <span class="status-badge ${user.isApproved ? 'status-approved' : 'status-pending'}">
                        ${user.isApproved ? 'Approved' : 'Pending Approval'}
                    </span>
                </div>
                ${user.adminSince ? `
                <div class="user-detail">
                    <label>Admin Since:</label>
                    <span>${formatDateTime(user.adminSince)}</span>
                </div>
                ` : ''}
                ` : ''}
                ${user.booksPublished >= 0 ? `
                <div class="user-detail">
                    <label>Books Published:</label>
                    <span>${user.booksPublished}</span>
                </div>
                ` : ''}
            </div>
            <div class="modal-actions">
                ${user.role === 'admin' && !user.isApproved ? `
                    <button class="btn-success" onclick="approvePublisher('${user._id}')">
                        <i class='bx bx-check'></i> Approve Admin
                    </button>
                    <button class="btn-danger" onclick="rejectPublisher('${user._id}')">
                        <i class='bx bx-x'></i> Reject Admin
                    </button>
                ` : `
                    <button class="btn-primary" onclick="editUser('${user._id}')">
                        <i class='bx bx-edit'></i> Edit User
                    </button>
                `}
                <button class="btn-danger" onclick="deleteUser('${user._id}')">
                    <i class='bx bx-trash'></i> Delete User
                </button>
            </div>
        `;

        modal.style.display = 'block';
    } else {
        console.error('Modal elements not found');
        showNotification('Error: Could not open user details', 'error');
    }
}

// Helper functions for user status
function getUserStatusClass(user) {
    if (user.role === 'admin') {
        return user.isApproved ? 'status-approved' : 'status-pending';
    }
    return user.isActive !== false ? 'status-active' : 'status-inactive';
}

function getUserStatusText(user) {
    if (user.role === 'admin') {
        return user.isApproved ? 'Approved Admin' : 'Pending Admin';
    }
    return user.isActive !== false ? 'Active User' : 'Inactive User';
}

async function editUser(userId) {
    showNotification('Edit user functionality coming soon!', 'info');
}

async function deleteUser(userId) {
    const user = allUsers.find(u => u._id === userId);
    if (!user) return;

    if (confirm(`Are you sure you want to delete user "${user.fullName}"? This action cannot be undone.`)) {
        try {
            showNotification('Deleting user...', 'info');

            // ✅ USE AUTHENTICATED REQUEST
            const response = await makeAuthenticatedRequest(`/api/admin/users/${userId}`, {
                method: 'DELETE'
            });

            const data = await response.json();

            if (data.success) {
                showNotification('User deleted successfully!', 'success');
                loadUsers(currentUserPage);
                loadDashboardSummary();
                // Close modal if open
                document.getElementById('userModal').style.display = 'none';
            } else {
                showNotification('Error: ' + data.message, 'error');
            }
        } catch (error) {
            console.error('Error deleting user:', error);
            showNotification('Error deleting user', 'error');
        }
    }
}

// ===== BOOK MANAGEMENT =====
async function loadBooks(page = 1) {
    try {
        currentBookPage = page;

        // Show loading state
        const bookTableBody = document.getElementById('bookTableBody');
        bookTableBody.innerHTML = `
            <tr>
                <td colspan="7" class="loading-cell">
                    <div class="loading-spinner"></div>
                    Loading books...
                </td>
            </tr>
        `;

        // For now, we'll use mock data since book endpoints aren't implemented yet
        // const response = await makeAuthenticatedRequest(`/api/admin/books?page=${page}&limit=${booksPerPage}`);

        // Mock data for books
        const mockBooksData = {
            success: true,
            books: [],
            totalPages: 1
        };

        allBooks = mockBooksData.books;
        bookTableBody.innerHTML = '';

        if (mockBooksData.books.length === 0) {
            bookTableBody.innerHTML = `
                <tr>
                    <td colspan="7" class="no-data">
                        <i class='bx bx-book-open'></i>
                        <p>No books found</p>
                    </td>
                </tr>
            `;
            return;
        }

        // Update pagination
        updateBookPagination(mockBooksData.totalPages, page);

    } catch (error) {
        console.error('Error loading books:', error);
        showNotification('Network error loading books: ' + error.message, 'error');
    }
}

function updateBookPagination(totalPages, currentPage) {
    const pagination = document.getElementById('bookPagination');
    if (!pagination) return;

    let paginationHTML = '';

    if (currentPage > 1) {
        paginationHTML += `<button class="page-btn" onclick="loadBooks(${currentPage - 1})">Previous</button>`;
    }

    for (let i = 1; i <= totalPages; i++) {
        if (i === currentPage) {
            paginationHTML += `<button class="page-btn active">${i}</button>`;
        } else {
            paginationHTML += `<button class="page-btn" onclick="loadBooks(${i})">${i}</button>`;
        }
    }

    if (currentPage < totalPages) {
        paginationHTML += `<button class="page-btn" onclick="loadBooks(${currentPage + 1})">Next</button>`;
    }

    pagination.innerHTML = paginationHTML;
}

// ===== BOOK ACTIONS =====
async function approveBook(bookId) {
    if (confirm('Are you sure you want to approve this book?')) {
        try {
            showNotification('Approving book...', 'info');

            // ✅ USE AUTHENTICATED REQUEST
            const response = await makeAuthenticatedRequest(`/api/admin/books/${bookId}`, {
                method: 'PUT',
                body: JSON.stringify({ status: 'approved' })
            });

            const data = await response.json();

            if (data.success) {
                showNotification('Book approved successfully!', 'success');
                loadBooks(currentBookPage);
                loadDashboardSummary();
            } else {
                showNotification('Error: ' + data.message, 'error');
            }
        } catch (error) {
            console.error('Error approving book:', error);
            showNotification('Error approving book', 'error');
        }
    }
}

async function rejectBook(bookId) {
    if (confirm('Are you sure you want to reject this book?')) {
        try {
            showNotification('Rejecting book...', 'info');

            // ✅ USE AUTHENTICATED REQUEST
            const response = await makeAuthenticatedRequest(`/api/admin/books/${bookId}`, {
                method: 'PUT',
                body: JSON.stringify({ status: 'rejected' })
            });

            const data = await response.json();

            if (data.success) {
                showNotification('Book rejected successfully!', 'success');
                loadBooks(currentBookPage);
            } else {
                showNotification('Error: ' + data.message, 'error');
            }
        } catch (error) {
            console.error('Error rejecting book:', error);
            showNotification('Error rejecting book', 'error');
        }
    }
}

async function viewBook(bookId) {
    showNotification('Book management coming soon!', 'info');
}

function showBookModal(book) {
    // Implementation for book modal
    showNotification('Book details functionality coming soon!', 'info');
}

async function editBook(bookId) {
    showNotification('Edit book functionality coming soon!', 'info');
}

async function featureBook(bookId) {
    showNotification('Feature book functionality coming soon!', 'info');
}

async function unfeatureBook(bookId) {
    showNotification('Unfeature book functionality coming soon!', 'info');
}

async function deleteBook(bookId) {
    showNotification('Delete book functionality coming soon!', 'info');
}

// ===== SEARCH AND FILTER =====
function searchUsers() {
    const searchTerm = document.getElementById('userSearch').value.toLowerCase();
    const rows = document.querySelectorAll('#userTableBody tr');

    rows.forEach(row => {
        const text = row.textContent.toLowerCase();
        row.style.display = text.includes(searchTerm) ? '' : 'none';
    });
}

function searchBooks() {
    const searchTerm = document.getElementById('bookSearch').value.toLowerCase();
    const rows = document.querySelectorAll('#bookTableBody tr');

    rows.forEach(row => {
        const text = row.textContent.toLowerCase();
        row.style.display = text.includes(searchTerm) ? '' : 'none';
    });
}

function filterBooks() {
    const statusFilter = document.getElementById('statusFilter').value;
    const rows = document.querySelectorAll('#bookTableBody tr');

    rows.forEach(row => {
        if (!statusFilter) {
            row.style.display = '';
            return;
        }

        const statusBadge = row.querySelector('.status-badge');
        if (statusBadge && statusBadge.textContent.toLowerCase() === statusFilter.toLowerCase()) {
            row.style.display = '';
        } else {
            row.style.display = 'none';
        }
    });
}

// ===== ANALYTICS =====
async function loadAnalytics() {
    // Implement analytics loading
    console.log('Loading analytics data...');
    showNotification('Analytics data loaded', 'info');
}

// ===== EXPORT FUNCTIONALITY =====
async function exportUsers() {
    try {
        showNotification('Preparing user export...', 'info');

        // ✅ USE AUTHENTICATED REQUEST
        const response = await makeAuthenticatedRequest('/api/admin/users?limit=1000');
        const data = await response.json();

        if (data.success) {
            // Convert to CSV
            const csv = convertToCSV(data.users);

            // Download CSV
            downloadCSV(csv, 'intelliread-users.csv');

            showNotification('Users exported successfully!', 'success');
        } else {
            showNotification('Error exporting users: ' + data.message, 'error');
        }
    } catch (error) {
        console.error('Error exporting users:', error);
        showNotification('Error exporting users', 'error');
    }
}

function convertToCSV(users) {
    const headers = ['Name', 'Email', 'Role', 'Status', 'Joined Date'];
    const rows = users.map(user => [
        user.fullName,
        user.email,
        user.role,
        user.role === 'admin' ? (user.isApproved ? 'Approved Admin' : 'Pending Approval') : 'Active User',
        new Date(user.createdAt).toLocaleDateString()
    ]);

    return [headers, ...rows].map(row => row.map(field => `"${field}"`).join(',')).join('\n');
}

function downloadCSV(csv, filename) {
    const blob = new Blob([csv], { type: 'text/csv' });
    const url = window.URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = filename;
    a.click();
    window.URL.revokeObjectURL(url);
}

// ===== SETTINGS =====
function saveSettings() {
    // Implement settings save functionality
    showNotification('Settings saved successfully!', 'success');
}

// ===== LOGOUT FUNCTIONALITY =====
async function logout() {
    // Show confirmation message
    const confirmLogout = confirm('Are you sure you want to logout? You will need to login again to access the admin dashboard.');

    if (confirmLogout) {
        try {
            // Show logging out message
            showNotification('Logging out... Please wait.', 'info');

            // Clear localStorage
            localStorage.removeItem('jwtToken');
            localStorage.removeItem('user');
            localStorage.removeItem('isLoggedIn');

            console.log('✅ Admin logged out successfully');

            // Show success message before redirect
            showNotification('Logout successful! Redirecting to login page...', 'success');

            // Redirect to admin login page after 2 seconds
            setTimeout(() => {
                window.location.href = '/admin';
            }, 2000);

        } catch (error) {
            console.error('Logout error:', error);

            // Even if there's an error, clear storage and redirect
            localStorage.removeItem('jwtToken');
            localStorage.removeItem('user');
            localStorage.removeItem('isLoggedIn');
            
            showNotification('Logout successful! Redirecting to login page...', 'success');
            
            setTimeout(() => {
                window.location.href = '/admin';
            }, 2000);
        }
    }
}

// ===== NOTIFICATION SYSTEM =====
function showNotification(message, type = 'info') {
    // Remove existing notifications
    const existingNotifications = document.querySelectorAll('.notification');
    existingNotifications.forEach(notification => {
        if (notification.parentNode) {
            notification.parentNode.removeChild(notification);
        }
    });
    
    const notification = document.createElement('div');
    notification.className = `notification ${type}`;
    notification.innerHTML = `
        <div class="notification-content">
            <i class='bx ${getNotificationIcon(type)}'></i>
            <span>${message}</span>
        </div>
    `;
    
    document.body.appendChild(notification);
    
    // Add show class after a delay for animation
    setTimeout(() => {
        notification.classList.add('show');
    }, 100);
    
    // Auto remove after 5 seconds
    setTimeout(() => {
        notification.classList.remove('show');
        setTimeout(() => {
            if (notification.parentNode) {
                notification.parentNode.removeChild(notification);
            }
        }, 300);
    }, 5000);
}

function getNotificationIcon(type) {
    const icons = {
        'success': 'bx-check-circle',
        'error': 'bx-error-circle',
        'warning': 'bx-error',
        'info': 'bx-info-circle'
    };
    return icons[type] || 'bx-info-circle';
}

// Function to show logout messages (kept for backward compatibility)
function showLogoutMessage(message, isError = false) {
    showNotification(message, isError ? 'error' : 'success');
}

// ===== EVENT LISTENERS SETUP =====
function setupEventListeners() {
    // Close modals when clicking X
    const closeButtons = document.querySelectorAll('.close');
    closeButtons.forEach(button => {
        button.addEventListener('click', function() {
            const modal = this.closest('.modal');
            if (modal) {
                modal.style.display = 'none';
            }
        });
    });
    
    // Close modals when clicking outside
    window.addEventListener('click', function(event) {
        const modals = document.querySelectorAll('.modal');
        modals.forEach(modal => {
            if (event.target === modal) {
                modal.style.display = 'none';
            }
        });
    });
    
    // Search debouncing
    let searchTimeout;
    const userSearch = document.getElementById('userSearch');
    const bookSearch = document.getElementById('bookSearch');
    
    if (userSearch) {
        userSearch.addEventListener('input', function() {
            clearTimeout(searchTimeout);
            searchTimeout = setTimeout(searchUsers, 300);
        });
    }
    
    if (bookSearch) {
        bookSearch.addEventListener('input', function() {
            clearTimeout(searchTimeout);
            searchTimeout = setTimeout(searchBooks, 300);
        });
    }
}

// ===== UTILITY FUNCTIONS =====
function formatDate(dateString) {
    if (!dateString) return 'Unknown';
    return new Date(dateString).toLocaleDateString('en-US', {
        year: 'numeric',
        month: 'short',
        day: 'numeric'
    });
}

function formatDateTime(dateString) {
    if (!dateString) return 'Unknown';
    return new Date(dateString).toLocaleString('en-US', {
        year: 'numeric',
        month: 'short',
        day: 'numeric',
        hour: '2-digit',
        minute: '2-digit'
    });
}

// Export functions for global access
window.showSection = showSection;
window.loadUsers = loadUsers;
window.loadBooks = loadBooks;
window.searchUsers = searchUsers;
window.searchBooks = searchBooks;
window.filterBooks = filterBooks;
window.approvePublisher = approvePublisher;
window.rejectPublisher = rejectPublisher;
window.viewUser = viewUser;
window.editUser = editUser;
window.deleteUser = deleteUser;
window.approveBook = approveBook;
window.rejectBook = rejectBook;
window.viewBook = viewBook;
window.editBook = editBook;
window.featureBook = featureBook;
window.unfeatureBook = unfeatureBook;
window.deleteBook = deleteBook;
window.exportUsers = exportUsers;
window.saveSettings = saveSettings;
window.logout = logout;