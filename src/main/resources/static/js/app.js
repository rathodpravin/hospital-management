const role = sessionStorage.getItem('role');
if (!sessionStorage.getItem('token')) location.href = '/index.html';
const $ = s => document.querySelector(s);
// Escape everything we put into HTML (prevents XSS from patient-entered text)
const esc = v => String(v ?? '').replace(/[&<>"']/g, c => ({ '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#39;' }[c]));
const dt = v => (v ? String(v).replace('T', ' ').slice(0, 16) : '');
const can = roles => !roles || roles.includes(role);
const label = k => k.replace(/([A-Z])/g, ' $1').replace(/^./, c => c.toUpperCase());

function toast(text, err) {
  const t = $('#toast'); t.textContent = text; t.className = 'toast show' + (err ? ' err' : '');
  setTimeout(() => (t.className = 'toast'), 4000);
}

// Each page = API endpoint + table columns + optional create form + optional row actions.
const badge = s => `<span class="badge ${esc(s)}">${esc(s)}</span>`;
const pages = {
  dashboard: { title: 'Dashboard', custom: renderDashboard },
  doctors: {
    title: 'Doctors', api: '/doctors',
    cols: [['Name', r => r.name], ['Specialization', r => r.specialization], ['Phone', r => r.phone], ['Email', r => r.email], ['Fee', r => r.consultationFee]],
    form: { title: 'Add Doctor', roles: ['ADMIN'], fields: [
      { n: 'name', l: 'Name', t: 'text' }, { n: 'specialization', l: 'Specialization', t: 'text' },
      { n: 'phone', l: 'Phone', t: 'text', req: false }, { n: 'email', l: 'Email', t: 'email', req: false },
      { n: 'consultationFee', l: 'Fee', t: 'number' }, { n: 'username', l: 'Login username', t: 'text' }, { n: 'password', l: 'Login password', t: 'password' }] },
    actions: [{ l: 'Delete', cls: 'danger', roles: ['ADMIN'], confirm: true, run: r => api('/doctors/' + r.id, { method: 'DELETE' }) }]
  },
  patients: {
    title: 'Patients', api: '/patients',
    cols: [['Name', r => r.name], ['DOB', r => r.dateOfBirth], ['Gender', r => r.gender], ['Phone', r => r.phone], ['Blood', r => r.bloodGroup], ['Address', r => r.address]],
    actions: [{ l: 'Delete', cls: 'danger', roles: ['ADMIN'], confirm: true, run: r => api('/patients/' + r.id, { method: 'DELETE' }) }]
  },
  appointments: {
    title: 'Appointments', api: '/appointments',
    cols: [['Patient', r => r.patient.name], ['Doctor', r => r.doctor.name], ['Date & Time', r => dt(r.dateTime)], ['Reason', r => r.reason], ['Status', r => badge(r.status), true]],
    form: { title: 'Book Appointment', roles: ['ADMIN', 'PATIENT'], fields: [
      { n: 'patientId', l: 'Patient', t: 'select', src: '/patients', lab: p => p.name, roles: ['ADMIN'] },
      { n: 'doctorId', l: 'Doctor', t: 'select', src: '/doctors', lab: d => `${d.name} (${d.specialization})` },
      { n: 'dateTime', l: 'Date & Time', t: 'datetime-local' }, { n: 'reason', l: 'Reason', t: 'text', req: false }] },
    actions: [
      { l: 'Complete', cls: 'alt', roles: ['ADMIN', 'DOCTOR'], show: r => r.status === 'SCHEDULED', run: r => api(`/appointments/${r.id}/status`, { method: 'PATCH', body: JSON.stringify({ status: 'COMPLETED' }) }) },
      { l: 'Cancel', cls: 'danger', show: r => r.status === 'SCHEDULED', confirm: true, run: r => api(`/appointments/${r.id}/status`, { method: 'PATCH', body: JSON.stringify({ status: 'CANCELLED' }) }) }]
  },
  records: {
    title: 'Medical Records', api: '/records',
    cols: [['Date', r => r.recordDate], ['Patient', r => r.patient.name], ['Doctor', r => r.doctor.name], ['Diagnosis', r => r.diagnosis], ['Notes', r => r.notes]],
    form: { title: 'Add Medical Record', roles: ['DOCTOR'], fields: [
      { n: 'patientId', l: 'Patient', t: 'select', src: '/patients', lab: p => p.name },
      { n: 'diagnosis', l: 'Diagnosis', t: 'text' }, { n: 'notes', l: 'Notes', t: 'text', req: false }] }
  },
  prescriptions: {
    title: 'Prescriptions', api: '/prescriptions',
    cols: [['Date', r => r.prescribedDate], ['Patient', r => r.medicalRecord.patient.name], ['Doctor', r => r.medicalRecord.doctor.name], ['Medicine', r => r.medicineName], ['Dosage', r => r.dosage], ['Duration', r => r.duration], ['Instructions', r => r.instructions]],
    form: { title: 'Write Prescription', roles: ['DOCTOR'], fields: [
      { n: 'recordId', l: 'Medical record', t: 'select', src: '/records', lab: m => `#${m.id} ${m.patient.name} - ${m.diagnosis}` },
      { n: 'medicineName', l: 'Medicine', t: 'text' }, { n: 'dosage', l: 'Dosage', t: 'text' },
      { n: 'duration', l: 'Duration', t: 'text', req: false }, { n: 'instructions', l: 'Instructions', t: 'text', req: false }] }
  },
  bills: {
    title: 'Billing', api: '/bills',
    cols: [['Date', r => r.billDate], ['Patient', r => r.patient.name], ['Description', r => r.description], ['Amount', r => r.amount], ['Status', r => badge(r.status), true]],
    form: { title: 'Create Bill', roles: ['ADMIN'], fields: [
      { n: 'patientId', l: 'Patient', t: 'select', src: '/patients', lab: p => p.name },
      { n: 'description', l: 'Description', t: 'text' }, { n: 'amount', l: 'Amount', t: 'number', step: '0.01' }] },
    actions: [{ l: 'Mark Paid', roles: ['ADMIN'], show: r => r.status === 'UNPAID', run: r => api(`/bills/${r.id}/pay`, { method: 'PATCH' }) }]
  }
};
const menus = {
  ADMIN: ['dashboard', 'doctors', 'patients', 'appointments', 'records', 'prescriptions', 'bills'],
  DOCTOR: ['dashboard', 'appointments', 'patients', 'records', 'prescriptions'],
  PATIENT: ['dashboard', 'doctors', 'appointments', 'records', 'prescriptions', 'bills']
};

async function renderDashboard(box) {
  const d = await api('/dashboard');
  box.innerHTML = `<div class="stats">${Object.entries(d).map(([k, v]) =>
    `<div class="stat"><b>${esc(v)}</b><span>${esc(label(k))}</span></div>`).join('')}</div>`;
}

async function buildForm(p) {
  let h = '';
  for (const x of p.form.fields) {
    if (!can(x.roles)) continue;
    if (x.t === 'select') {
      const opts = await api(x.src);
      h += `<label>${x.l}<select name="${x.n}" required>${opts.map(o => `<option value="${o.id}">${esc(x.lab(o))}</option>`).join('')}</select></label>`;
    } else h += `<label>${x.l}<input name="${x.n}" type="${x.t}" ${x.step ? `step="${x.step}"` : ''} ${x.req === false ? '' : 'required'}></label>`;
  }
  return `<div class="card"><h3>${p.form.title}</h3><form id="f">${h}<button class="btn">Save</button></form></div>`;
}

async function renderPage(key) {
  const p = pages[key];
  $('#title').textContent = p.title;
  document.querySelectorAll('nav a').forEach(a => a.classList.toggle('active', a.dataset.k === key));
  const box = $('#content');
  try {
    if (p.custom) return await p.custom(box);
    const rows = await api(p.api);
    const acts = (p.actions || []).filter(a => can(a.roles));
    let html = p.form && can(p.form.roles) ? await buildForm(p) : '';
    html += `<div class="card"><div class="table-wrap">`;
    if (!rows.length) html += `<div class="empty">No records yet.</div>`;
    else {
      html += `<table><tr>${p.cols.map(c => `<th>${c[0]}</th>`).join('')}${acts.length ? '<th>Actions</th>' : ''}</tr>`;
      rows.forEach((r, i) => {
        html += `<tr>${p.cols.map(c => `<td>${c[2] ? c[1](r) : esc(c[1](r))}</td>`).join('')}`;
        if (acts.length) html += `<td>${acts.map((a, j) => (!a.show || a.show(r)) ? `<button class="btn sm ${a.cls || ''}" data-i="${i}" data-j="${j}">${a.l}</button>` : '').join(' ')}</td>`;
        html += '</tr>';
      });
      html += '</table>';
    }
    box.innerHTML = html + '</div></div>';
    const form = $('#f');
    if (form) form.onsubmit = async e => {
      e.preventDefault();
      const fd = new FormData(form), body = {};
      for (const x of p.form.fields) {
        if (!can(x.roles)) continue;
        let v = fd.get(x.n);
        if (x.t === 'select' || x.t === 'number') v = v === '' ? null : Number(v);
        else if (x.t === 'datetime-local') v = v + ':00';
        body[x.n] = v;
      }
      try { await api(p.api, { method: 'POST', body: JSON.stringify(body) }); toast('Saved successfully'); renderPage(key); }
      catch (err) { toast(err.message, true); }
    };
    box.querySelectorAll('button[data-i]').forEach(b => b.onclick = async () => {
      const a = acts[b.dataset.j];
      if (a.confirm && !confirm('Are you sure?')) return;
      try { await a.run(rows[b.dataset.i]); toast('Done'); renderPage(key); }
      catch (err) { toast(err.message, true); }
    });
  } catch (err) { box.innerHTML = ''; toast(err.message, true); }
}

$('#who').textContent = `${sessionStorage.getItem('username')} (${role})`;
$('#menu').innerHTML = (menus[role] || []).map(k => `<a data-k="${k}">${pages[k].title}</a>`).join('');
$('#menu').onclick = e => { if (e.target.dataset.k) renderPage(e.target.dataset.k); };
$('#logout').onclick = () => { sessionStorage.clear(); location.href = '/index.html'; };
renderPage('dashboard');
