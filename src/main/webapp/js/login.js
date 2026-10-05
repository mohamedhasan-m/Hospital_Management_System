// Login page

const loginForm = document.getElementById('loginForm');
const errorMsg = document.getElementById('errorMsg');

// Already logged in? Go straight to the dashboard.
api('api/login').then(function (data) {
    if (data.loggedIn) {
        location.href = 'dashboard.html';
    }
}).catch(function () { });

loginForm.addEventListener('submit', async function (event) {
    event.preventDefault();               // do not reload the page
    errorMsg.style.display = 'none';

    const button = document.getElementById('loginBtn');
    button.disabled = true;

    try {
        await post('api/login', {
            username: document.getElementById('username').value,
            password: document.getElementById('password').value
        });
        location.href = 'dashboard.html';
    } catch (e) {
        errorMsg.textContent = e.message;
        errorMsg.style.display = 'block';
        button.disabled = false;
    }
});
