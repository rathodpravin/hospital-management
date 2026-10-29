// Shared API helper. Keeps the existing REST endpoints and JWT flow.
const API = '/api';

async function api(path, options = {}) {
    const headers = { 'Content-Type': 'application/json', ...(options.headers || {}) };
    const token = sessionStorage.getItem('token');
    if (token) headers.Authorization = 'Bearer ' + token;

    const res = await fetch(API + path, { ...options, headers });

    if (res.status === 401 && !path.startsWith('/auth')) {
        sessionStorage.clear();
        location.href = '/index.html';
        return;
    }

    const data = res.status === 204 ? null : await res.json().catch(() => null);
    if (!res.ok) throw new Error(data?.message || 'Request failed (' + res.status + ')');
    return data;
}
