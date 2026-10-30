const $ = s => document.querySelector(s);
if (sessionStorage.getItem('token')) location.href = '/app.html';

function msg(text, err = true) {
  const m = $('#msg'); m.textContent = text; m.className = 'toast show' + (err ? ' err' : '');
}
function switchTab(login) {
  $('#loginForm').hidden = !login; $('#regForm').hidden = login;
  $('#tabLogin').classList.toggle('active', login); $('#tabReg').classList.toggle('active', !login);
  $('#msg').className = 'toast';
}
$('#tabLogin').onclick = () => switchTab(true);
$('#tabReg').onclick = () => switchTab(false);

async function submit(path, form) {
  const body = Object.fromEntries(new FormData(form));
  Object.keys(body).forEach(k => { if (body[k] === '') body[k] = null; });
  try {
    const r = await api(path, { method: 'POST', body: JSON.stringify(body) });
    sessionStorage.setItem('token', r.token);
    sessionStorage.setItem('role', r.role);
    sessionStorage.setItem('username', r.username);
    location.href = '/app.html';
  } catch (e) { msg(e.message); }
}
$('#loginForm').onsubmit = e => { e.preventDefault(); submit('/auth/login', e.target); };
$('#regForm').onsubmit = e => { e.preventDefault(); submit('/auth/register', e.target); };
