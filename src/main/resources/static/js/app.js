const role = sessionStorage.getItem('role');
if (!sessionStorage.getItem('token')) location.href = '/index.html';
const $ = s => document.querySelector(s);

// Escape user-entered values before inserting them into HTML.
const esc = v => String(v ?? '').replace(/[&<>"']/g, c => ({ '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#39;' }[c]));
const dt = v => (v ? String(v).replace('T', ' ').slice(0, 16) : '');
const can = roles => !roles || roles.includes(role);
const label = k => k.replace(/([A-Z])/g, ' $1').replace(/^./, c => c.toUpperCase());

function toast(text, err) {
  const t = $('#toast');
  t.textContent = text;
  t.className = 'toast show' + (err ? ' err' : ' success');
  setTimeout(() => (t.className = 'toast'), 4000);
}
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
  box.innerHTML = `<div class="dashboard-welcome"><div><span class="eyebrow">YOUR WORKSPACE</span><h2>Good to see you, ${esc(sessionStorage.getItem('username') || 'there')}.</h2><p>Here’s the latest overview from your hospital system.</p></div><div class="welcome-symbol">✚</div></div>
  <div class="stats">${Object.entries(d).map(([k, v], i) => `<div class="stat"><div class="stat-top"><span class="stat-icon">${['▦','♙','◷','＋','▤','₹'][i % 6]}</span><span class="stat-tag">LIVE DATA</span></div><b>${esc(v)}</b><span>${esc(label(k))}</span></div>`).join('')}</div>
  <div class="card info-card"><div class="info-icon">✓</div><div><h3>Your workspace is ready</h3><p>Use the navigation menu to manage appointments, patient information, medical records and other hospital operations available to your role.</p></div></div>`;
}

async function buildForm(p) {
  let h = '';
  for (const x of p.form.fields) {
    if (!can(x.roles)) continue;
    const required = x.req === false ? '' : 'required';
    if (x.t === 'select') {
      const opts = await api(x.src);
      h += `<div class="field"><label for="${x.n}">${esc(x.l)}</label><select id="${x.n}" name="${x.n}" ${required}>${opts.map(o => `<option value="${o.id}">${esc(x.lab(o))}</option>`).join('')}</select></div>`;
    } else {
      h += `<div class="field"><label for="${x.n}">${esc(x.l)}</label><input id="${x.n}" name="${x.n}" type="${x.t}" ${x.step ? `step="${x.step}"` : ''} ${required}></div>`;
    }
  }
  return `<section class="card form-card"><div class="card-heading"><div><span class="eyebrow">MANAGE RECORDS</span><h3>${esc(p.form.title)}</h3></div><span class="card-heading-icon">＋</span></div><form id="f" class="data-form">${h}<div class="form-submit"><button class="btn btn-primary" type="submit">Save changes <span aria-hidden="true">→</span></button></div></form></section>`;
}

async function renderPage(key) {
  const p = pages[key];
  if (!p) return;
  $('#title').textContent = p.title;
  const subtitle = document.querySelector('.page-subtitle');
  if (subtitle) subtitle.textContent = ({ dashboard: 'Your hospital activity, all in one place.', doctors: 'View and maintain the hospital care team.', patients: 'Access registered patient information.', appointments: 'Coordinate visits and appointment status.', records: 'Review clinical records available to your role.', prescriptions: 'Manage prescription records.', bills: 'Review billing and payment status.' })[key] || '';
  document.querySelectorAll('#menu a').forEach(a => a.classList.toggle('active', a.dataset.k === key));
  const box = $('#content');
  box.innerHTML = '<div class="loading-state"><span class="spinner"></span> Loading workspace…</div>';
  try {
    if (p.custom) return await p.custom(box);
    const rows = await api(p.api);
    const acts = (p.actions || []).filter(a => can(a.roles));
    let html = p.form && can(p.form.roles) ? await buildForm(p) : '';
    html += `<section class="card table-card"><div class="card-heading"><div><span class="eyebrow">HOSPITAL DATA</span><h3>${esc(p.title)} <span class="record-count">${rows.length}</span></h3></div><span class="card-heading-icon">▤</span></div><div class="table-wrap">`;
    if (!rows.length) html += `<div class="empty-state"><span>▤</span><strong>No records yet</strong><p>Records will appear here when they are available.</p></div>`;
    else {
      html += `<table><thead><tr>${p.cols.map(c => `<th>${esc(c[0])}</th>`).join('')}${acts.length ? '<th>Actions</th>' : ''}</tr></thead><tbody>`;
      rows.forEach((r, i) => {
        html += `<tr>${p.cols.map(c => `<td>${c[2] ? c[1](r) : esc(c[1](r))}</td>`).join('')}`;
        if (acts.length) html += `<td class="actions-cell">${acts.map((a, j) => (!a.show || a.show(r)) ? `<button class="btn btn-sm ${a.cls || 'btn-soft'}" data-i="${i}" data-j="${j}">${esc(a.l)}</button>` : '').join(' ')}</td>`;
        html += '</tr>';
      });
      html += '</tbody></table>';
    }
    box.innerHTML = html + '</div></section>';

    const form = $('#f');
    if (form) form.onsubmit = async e => {
      e.preventDefault();
      const submitBtn = form.querySelector('button[type="submit"]');
      submitBtn.disabled = true;
      const fd = new FormData(form), body = {};
      for (const x of p.form.fields) {
        if (!can(x.roles)) continue;
        let v = fd.get(x.n);
        if (x.t === 'select' || x.t === 'number') v = v === '' ? null : Number(v);
        else if (x.t === 'datetime-local' && v) v = v + ':00';
        body[x.n] = v;
      }
      try { await api(p.api, { method: 'POST', body: JSON.stringify(body) }); toast('Saved successfully.'); await renderPage(key); }
      catch (err) { toast(err.message, true); submitBtn.disabled = false; }
    };
    box.querySelectorAll('button[data-i]').forEach(b => b.onclick = async () => {
      const a = acts[Number(b.dataset.j)];
      if (a.confirm && !confirm('Are you sure you want to continue?')) return;
      b.disabled = true;
      try { await a.run(rows[Number(b.dataset.i)]); toast('Action completed.'); await renderPage(key); }
      catch (err) { toast(err.message, true); b.disabled = false; }
    });
  } catch (err) { box.innerHTML = `<section class="card error-card"><strong>Unable to load this section</strong><p>${esc(err.message)}</p><button class="btn btn-primary" id="retry">Try again</button></section>`; $('#retry').onclick = () => renderPage(key); }
}

const username = sessionStorage.getItem('username') || 'User';
$('#who').textContent = `${username} · ${role || 'USER'}`;
$('#avatar').textContent = username.charAt(0).toUpperCase();
$('#today').textContent = new Intl.DateTimeFormat(undefined, { dateStyle: 'medium' }).format(new Date());
$('#year').textContent = new Date().getFullYear();
$('#menu').innerHTML = (menus[role] || []).map(k => `<a href="#" data-k="${k}"><span class="nav-icon">${({dashboard:'▦',doctors:'✚',patients:'♙',appointments:'◷',records:'▤',prescriptions:'＋',bills:'₹'})[k]}</span><span>${esc(pages[k].title)}</span><span class="nav-arrow">›</span></a>`).join('');
$('#menu').onclick = e => { const a = e.target.closest('a[data-k]'); if (a) { e.preventDefault(); renderPage(a.dataset.k); $('#sidebar').classList.remove('sidebar-open'); } };
$('#logout').onclick = () => { sessionStorage.clear(); location.href = '/index.html'; };
$('#mobileMenu').onclick = () => $('#sidebar').classList.toggle('sidebar-open');
renderPage('dashboard');
