// =====================================================
//  Helpers shared by every page
// =====================================================

/**
 * Calls a servlet and returns the JSON answer.
 * - if the session has expired (HTTP 401) the user is sent to the login page
 * - if the server answers with an error, an Error with the server's message is thrown
 */
async function api(url, options) {
    const response = await fetch(url, options);

    if (response.status === 401 && !location.pathname.endsWith('login.html')) {
        location.href = 'login.html';
        throw new Error('Please login again');
    }

    let data = null;
    try {
        data = await response.json();
    } catch (e) {
        // the answer was not JSON
    }

    if (!response.ok) {
        throw new Error((data && data.message) || 'Request failed (' + response.status + ')');
    }
    return data;
}

/** Sends form fields to a servlet with POST. Example: post('api/patients', { action: 'delete', id: 5 }) */
function post(url, fields) {
    return api(url, {
        method: 'POST',
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
        body: new URLSearchParams(fields)
    });
}

/** Escapes text so data from the database can never inject HTML into the page. */
function esc(value) {
    return String(value == null ? '' : value).replace(/[&<>"']/g, function (c) {
        return { '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#39;' }[c];
    });
}

/** 600 -> "₹600.00" */
function money(value) {
    return '₹' + Number(value).toFixed(2);
}

/** Today's date as yyyy-MM-dd using the computer's local time. */
function today() {
    const d = new Date();
    const month = String(d.getMonth() + 1).padStart(2, '0');
    const day = String(d.getDate()).padStart(2, '0');
    return d.getFullYear() + '-' + month + '-' + day;
}

/** Small message that appears at the bottom right for a few seconds. */
function toast(message, isError) {
    let box = document.getElementById('toast');
    if (!box) {
        box = document.createElement('div');
        box.id = 'toast';
        box.className = 'toast';
        document.body.appendChild(box);
    }
    box.textContent = message;
    box.className = 'toast show' + (isError ? ' error' : '');
    clearTimeout(box.hideTimer);
    box.hideTimer = setTimeout(function () { box.className = 'toast'; }, 3500);
}

/** Search box: hides table rows that do not contain the typed text. Returns a function to re-apply the filter. */
function enableSearch(inputId, tbodyId) {
    const input = document.getElementById(inputId);
    function apply() {
        const query = input.value.toLowerCase();
        document.querySelectorAll('#' + tbodyId + ' tr').forEach(function (row) {
            row.style.display = row.textContent.toLowerCase().includes(query) ? '' : 'none';
        });
    }
    input.addEventListener('input', apply);
    return apply;
}

/**
 * Builds the sidebar, shows the logged-in user and wires the logout button.
 * Every page (except login) calls this once, e.g. setupPage('patients').
 */
function setupPage(activePage) {
    const links = [
        ['dashboard', 'Dashboard', '🏠'],
        ['patients', 'Patients', '🧑'],
        ['doctors', 'Doctors', '🩺'],
        ['appointments', 'Appointments', '📅'],
        ['billing', 'Billing', '💳']
    ];

    let nav = '';
    links.forEach(function (l) {
        nav += '<a href="' + l[0] + '.html" class="' + (l[0] === activePage ? 'active' : '') + '">'
            + '<span class="icon">' + l[2] + '</span>' + l[1] + '</a>';
    });

    document.getElementById('sidebar').innerHTML =
        '<div class="brand"><span class="brand-icon">🏥</span>City Hospital</div>'
        + '<nav class="nav">' + nav + '</nav>'
        + '<div class="sidebar-footer">'
        + '<span class="who" id="userName">...</span>'
        + '<button class="btn btn-outline btn-sm" id="logoutBtn">Logout</button>'
        + '</div>';

    document.getElementById('logoutBtn').addEventListener('click', function () {
        post('api/logout', {}).finally(function () { location.href = 'login.html'; });
    });

    // Ask the server who is logged in (also redirects to login if the session is gone)
    api('api/login').then(function (data) {
        if (!data.loggedIn) {
            location.href = 'login.html';
        } else {
            document.getElementById('userName').textContent = data.name;
        }
    }).catch(function () { /* api() already handled the redirect */ });
}
