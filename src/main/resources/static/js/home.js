// home.js
document.addEventListener('DOMContentLoaded', function() {
    const loginButton = document.querySelector('.btn-outline');

    if (loginButton) {
        loginButton.addEventListener('click', function() {
            console.log('Login button clicked - going to /Login');
            window.location.href = '/Login';
        });
    }

    // Explore button functionality
    const exploreBtn = document.querySelector('.explore-btn');
    if (exploreBtn) {
        exploreBtn.addEventListener('click', function(e) {
            e.preventDefault();
            console.log('Explore button clicked - going to /books');
            window.location.href = '/books';
        });
    }
});