// Patients page: list, add, edit, delete

let patients = [];                       // the list received from the server
const dialog = document.getElementById('patientDialog');
const form = document.getElementById('patientForm');
const rows = document.getElementById('patientRows');
const applySearch = enableSearch('search', 'patientRows');

setupPage('patients');
loadPatients();

async function loadPatients() {
    try {
        patients = await api('api/patients');
        renderPatients();
    } catch (e) {
        toast(e.message, true);
    }
}

function renderPatients() {
    if (patients.length === 0) {
        rows.innerHTML = '<tr><td colspan="8" class="empty">No patients yet. Click "Add Patient".</td></tr>';
        return;
    }
    rows.innerHTML = patients.map(function (p) {
        return '<tr>'
            + '<td>' + p.id + '</td>'
            + '<td><strong>' + esc(p.name) + '</strong></td>'
            + '<td>' + p.age + '</td>'
            + '<td>' + esc(p.gender) + '</td>'
            + '<td>' + esc(p.phone) + '</td>'
            + '<td>' + esc(p.address) + '</td>'
            + '<td>' + esc(p.disease) + '</td>'
            + '<td class="actions">'
            + '<button class="btn btn-light btn-sm" data-edit="' + p.id + '">Edit</button>'
            + '<button class="btn btn-danger btn-sm" data-delete="' + p.id + '">Delete</button>'
            + '</td></tr>';
    }).join('');
    applySearch();
}

// Open the popup. patient = null means "add", otherwise "edit".
function openForm(patient) {
    form.reset();
    form.elements['id'].value = patient ? patient.id : '';
    document.getElementById('dialogTitle').textContent = patient ? 'Edit Patient' : 'Add Patient';

    if (patient) {
        form.elements['name'].value = patient.name;
        form.elements['age'].value = patient.age;
        form.elements['gender'].value = patient.gender;
        form.elements['phone'].value = patient.phone;
        form.elements['address'].value = patient.address || '';
        form.elements['disease'].value = patient.disease || '';
    }
    dialog.showModal();
}

document.getElementById('addBtn').addEventListener('click', function () { openForm(null); });
document.getElementById('cancelBtn').addEventListener('click', function () { dialog.close(); });

// One click listener for all Edit / Delete buttons in the table
rows.addEventListener('click', function (event) {
    const editId = event.target.dataset.edit;
    const deleteId = event.target.dataset.delete;

    if (editId) {
        openForm(patients.find(function (p) { return p.id == editId; }));
    }
    if (deleteId) {
        deletePatient(deleteId);
    }
});

form.addEventListener('submit', async function (event) {
    event.preventDefault();

    const fields = Object.fromEntries(new FormData(form));     // {id, name, age, gender, ...}
    fields.action = fields.id ? 'update' : 'add';

    try {
        const result = await post('api/patients', fields);
        dialog.close();
        toast(result.message);
        loadPatients();
    } catch (e) {
        toast(e.message, true);          // the popup stays open so the user can fix the input
    }
});

async function deletePatient(id) {
    if (!confirm('Delete this patient? Their appointments and bills will also be deleted.')) {
        return;
    }
    try {
        const result = await post('api/patients', { action: 'delete', id: id });
        toast(result.message);
        loadPatients();
    } catch (e) {
        toast(e.message, true);
    }
}
