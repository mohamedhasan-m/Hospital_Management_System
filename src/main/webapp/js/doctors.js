// Doctors page: list, add, edit, delete

let doctors = [];                        // the list received from the server
const dialog = document.getElementById('doctorDialog');
const form = document.getElementById('doctorForm');
const rows = document.getElementById('doctorRows');
const applySearch = enableSearch('search', 'doctorRows');

setupPage('doctors');
loadDoctors();

async function loadDoctors() {
    try {
        doctors = await api('api/doctors');
        renderDoctors();
    } catch (e) {
        toast(e.message, true);
    }
}

function renderDoctors() {
    if (doctors.length === 0) {
        rows.innerHTML = '<tr><td colspan="7" class="empty">No doctors yet. Click "Add Doctor".</td></tr>';
        return;
    }
    rows.innerHTML = doctors.map(function (d) {
        return '<tr>'
            + '<td>' + d.id + '</td>'
            + '<td><strong>' + esc(d.name) + '</strong></td>'
            + '<td>' + esc(d.specialization) + '</td>'
            + '<td>' + esc(d.phone) + '</td>'
            + '<td>' + esc(d.email) + '</td>'
            + '<td class="num">' + money(d.consultationFee) + '</td>'
            + '<td class="actions">'
            + '<button class="btn btn-light btn-sm" data-edit="' + d.id + '">Edit</button>'
            + '<button class="btn btn-danger btn-sm" data-delete="' + d.id + '">Delete</button>'
            + '</td></tr>';
    }).join('');
    applySearch();
}

// Open the popup. doctor = null means "add", otherwise "edit".
function openForm(doctor) {
    form.reset();
    form.elements['id'].value = doctor ? doctor.id : '';
    document.getElementById('dialogTitle').textContent = doctor ? 'Edit Doctor' : 'Add Doctor';

    if (doctor) {
        form.elements['name'].value = doctor.name;
        form.elements['specialization'].value = doctor.specialization;
        form.elements['phone'].value = doctor.phone;
        form.elements['email'].value = doctor.email || '';
        form.elements['fee'].value = doctor.consultationFee;
    }
    dialog.showModal();
}

document.getElementById('addBtn').addEventListener('click', function () { openForm(null); });
document.getElementById('cancelBtn').addEventListener('click', function () { dialog.close(); });

rows.addEventListener('click', function (event) {
    const editId = event.target.dataset.edit;
    const deleteId = event.target.dataset.delete;

    if (editId) {
        openForm(doctors.find(function (d) { return d.id == editId; }));
    }
    if (deleteId) {
        deleteDoctor(deleteId);
    }
});

form.addEventListener('submit', async function (event) {
    event.preventDefault();

    const fields = Object.fromEntries(new FormData(form));     // {id, name, specialization, ...}
    fields.action = fields.id ? 'update' : 'add';

    try {
        const result = await post('api/doctors', fields);
        dialog.close();
        toast(result.message);
        loadDoctors();
    } catch (e) {
        toast(e.message, true);
    }
});

async function deleteDoctor(id) {
    if (!confirm('Delete this doctor? Their appointments will also be deleted.')) {
        return;
    }
    try {
        const result = await post('api/doctors', { action: 'delete', id: id });
        toast(result.message);
        loadDoctors();
    } catch (e) {
        toast(e.message, true);
    }
}
