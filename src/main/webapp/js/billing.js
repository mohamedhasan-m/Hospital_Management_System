// Billing page: create a bill (with live total) and list all bills

let doctors = [];
const form = document.getElementById('billForm');
const rows = document.getElementById('billRows');
const applySearch = enableSearch('search', 'billRows');
const chargeFields = ['consultationFee', 'medicineCharges', 'roomCharges', 'otherCharges'];

setupPage('billing');
loadBills();
loadDropdowns();

async function loadBills() {
    try {
        renderBills(await api('api/bills'));
    } catch (e) {
        toast(e.message, true);
    }
}

async function loadDropdowns() {
    try {
        const results = await Promise.all([api('api/patients'), api('api/doctors')]);
        doctors = results[1];

        document.getElementById('patientId').innerHTML = '<option value="">Select patient</option>'
            + results[0].map(function (p) {
                return '<option value="' + p.id + '">' + esc(p.name) + ' (ID ' + p.id + ')</option>';
            }).join('');

        document.getElementById('doctorId').innerHTML = '<option value="">None</option>'
            + doctors.map(function (d) {
                return '<option value="' + d.id + '">' + esc(d.name) + ' - ' + money(d.consultationFee) + '</option>';
            }).join('');
    } catch (e) {
        toast(e.message, true);
    }
}

function renderBills(bills) {
    if (bills.length === 0) {
        rows.innerHTML = '<tr><td colspan="8" class="empty">No bills yet.</td></tr>';
        return;
    }
    rows.innerHTML = bills.map(function (b) {
        return '<tr>'
            + '<td>#' + b.id + '</td>'
            + '<td>' + esc(b.billDate) + '</td>'
            + '<td><strong>' + esc(b.patientName) + '</strong></td>'
            + '<td class="num">' + money(b.consultationFee) + '</td>'
            + '<td class="num">' + money(b.medicineCharges) + '</td>'
            + '<td class="num">' + money(b.roomCharges) + '</td>'
            + '<td class="num">' + money(b.otherCharges) + '</td>'
            + '<td class="num"><strong>' + money(b.totalAmount) + '</strong></td>'
            + '</tr>';
    }).join('');
    applySearch();
}

// Show the total while the user types (the server calculates it again when saving)
function updateTotal() {
    let total = 0;
    chargeFields.forEach(function (id) {
        total += parseFloat(document.getElementById(id).value) || 0;
    });
    document.getElementById('totalAmount').textContent = money(total);
}
chargeFields.forEach(function (id) {
    document.getElementById(id).addEventListener('input', updateTotal);
});

// Choosing a doctor copies that doctor's fee into the consultation fee box
document.getElementById('doctorId').addEventListener('change', function (event) {
    const selectedId = event.target.value;
    const doctor = doctors.find(function (d) { return d.id == selectedId; });
    document.getElementById('consultationFee').value = doctor ? doctor.consultationFee : 0;
    updateTotal();
});

form.addEventListener('submit', async function (event) {
    event.preventDefault();

    const fields = Object.fromEntries(new FormData(form));     // only fields that have a name="" are sent
    fields.action = 'create';

    try {
        const result = await post('api/bills', fields);
        toast(result.message);
        form.reset();
        updateTotal();
        loadBills();
    } catch (e) {
        toast(e.message, true);
    }
});
