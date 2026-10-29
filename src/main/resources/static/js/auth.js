const $ = s => document.querySelector(s);

// If the user is already logged in, open the dashboard.
// Registration itself will not create a login session.
if (sessionStorage.getItem('token')) {
    location.href = '/app.html';
}


// Display messages
function msg(text, err = true) {
    const m = $('#msg');

    if (!m) return;

    m.textContent = text;
    m.className = 'toast show' + (err ? ' err' : ' success');
}


// Handle login and registration separately
async function submit(path, form) {

    const button = form.querySelector('button[type="submit"]');
    const original = button.innerHTML;

    button.disabled = true;
    button.innerHTML = '<span class="spinner"></span> Please wait…';

    const body = Object.fromEntries(new FormData(form));

    // Convert empty form values to null
    Object.keys(body).forEach(k => {
        if (body[k] === '') {
            body[k] = null;
        }
    });

    try {

        // Call the existing backend API
        const r = await api(path, {
            method: 'POST',
            body: JSON.stringify(body)
        });


        // ==========================================
        // LOGIN
        // ==========================================
        if (path === '/auth/login') {

            if (!r || !r.token) {
                throw new Error(
                    'Login failed. The server did not return a login token.'
                );
            }

            // Save authentication details only after login
            sessionStorage.setItem('token', r.token);
            sessionStorage.setItem('role', r.role);
            sessionStorage.setItem('username', r.username);

            // Open dashboard after successful login
            location.href = '/app.html';

            return;
        }


        // ==========================================
        // PATIENT REGISTRATION
        // ==========================================
        if (path === '/auth/register') {

            // Do not save a token.
            // Do not save a role or username.
            // Do not open the dashboard.

            // Clear any previous session to ensure registration
            // does not leave the browser in an authenticated state.
            sessionStorage.removeItem('token');
            sessionStorage.removeItem('role');
            sessionStorage.removeItem('username');

            // Return to login page with success message.
            location.href = '/index.html?registered=1';

            return;
        }


    } catch (e) {

        msg(
            e.message || 'Unable to complete request. Please try again.',
            true
        );

        button.disabled = false;
        button.innerHTML = original;
    }
}


// ==========================================
// LOGIN FORM
// ==========================================
const loginForm = $('#loginForm');

if (loginForm) {

    loginForm.onsubmit = e => {
        e.preventDefault();

        submit('/auth/login', e.target);
    };
}


// ==========================================
// PATIENT REGISTRATION FORM
// ==========================================
const regForm = $('#regForm');

if (regForm) {

    regForm.onsubmit = e => {
        e.preventDefault();

        submit('/auth/register', e.target);
    };
}