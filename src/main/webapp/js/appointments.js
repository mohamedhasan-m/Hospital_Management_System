// Appointments page: list, book, cancel

let appointments = [];
const dialog = document.getElementById('appointmentDialog');
const form = document.getElementById('appointmentForm');
const rows = document.getElementById('appointmentRows');
const applySearch = enableSearch('search', 'appointmentRows');

setupPage('appointments');
loadAppointments();
loadDropdowns();

async function loadAppointments() {
    try {
        appointments = await api('api/appointments');
        renderAppointments();
    } catch (e) {
        toast(e.message, true);
    }
}

// Fill the Patient and Doctor drop-down lists of the booking form
async function loadDropdowns() {
    try {
        const results = await Promise.all([api('api/patients'), api('api/doctors')]);

        document.getElementById('patientId').innerHTML = '<option value="">Select patient</option>'
            + results[0].map(function (p) {
                return '<option value="' + p.id + '">' + esc(p.name) + ' (ID ' + p.id + ')</option>';
            }).join('');

        document.getElementById('doctorId').innerHTML = '<option value="">Select doctor</option>'
            + results[1].map(function (d) {
                return '<option value="' + d.id + '">' + esc(d.name) + ' - ' + esc(d.specialization) + '</option>';
            }).join('');
    } catch (e) {
        toast(e.message, true);
    }
}

function renderAppointments() {
    if (appointments.length === 0) {
        rows.innerHTML = '<tr><td colspan="8" class="empty">No appointments yet. Click "Book Appointment".</td></tr>';
        return;
    }
    rows.innerHTML = appointments.map(function (a) {
        const booked = a.status === 'Booked';
        return '<tr>'
            + '<td>' + a.id + '</td>'
            + '<td><strong>' + esc(a.patientName) + '</strong></td>'
            + '<td>' + esc(a.doctorName) + '<br><small style="color:#64748b">' + esc(a.specialization) + '</small></td>'
            + '<td>' + esc(a.appointmentDate) + '</td>'
            + '<td>' + esc(a.appointmentTime) + '</td>'
            + '<td>' + esc(a.reason) + '</td>'
            + '<td><span class="badge ' + (booked ? 'badge-booked' : 'badge-cancelled') + '">' + esc(a.status) + '</span></td>'
            + '<td>' + (booked ? '<button class="btn btn-danger btn-sm" data-cancel="' + a.id + '">Cancel</button>' : '') + '</td>'
            + '</tr>';
    }).join('');
    applySearch();
}

document.getElementById('addBtn').addEventListener('click', function () {
    form.reset();
    document.getElementById('date').min = today();       // cannot pick a past date
    dialog.showModal();
});
document.getElementById('cancelBtn').addEventListener('click', function () { dialog.close(); });

rows.addEventListener('click', function (event) {
    const cancelId = event.target.dataset.cancel;
    if (cancelId) {
        cancelAppointment(cancelId);
    }
});

form.addEventListener('submit', async function (event) {
    event.preventDefault();

    const fields = Object.fromEntries(new FormData(form));     // {patientId, doctorId, date, time, reason}
    fields.action = 'book';

    try {
        const result = await post('api/appointments', fields);
        dialog.close();
        toast(result.message);
        loadAppointments();
    } catch (e) {
        toast(e.message, true);          // for example: "This doctor already has an appointment at that time"
    }
});

async function cancelAppointment(id) {
    if (!confirm('Cancel this appointment?')) {
        return;
    }
    try {
        const result = await post('api/appointments', { action: 'cancel', id: id });
        toast(result.message);
        loadAppointments();
    } catch (e) {
        toast(e.message, true);
    }
}
