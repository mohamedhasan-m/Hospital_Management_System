// Dashboard page

setupPage('dashboard');
loadDashboard();

async function loadDashboard() {
    try {
        // Ask both servlets at the same time
        const results = await Promise.all([api('api/dashboard'), api('api/appointments')]);
        const stats = results[0];
        const appointments = results[1];

        document.getElementById('totalPatients').textContent = stats.patients;
        document.getElementById('totalDoctors').textContent = stats.doctors;
        document.getElementById('totalAppointments').textContent = stats.appointments;

        // Next 5 booked appointments (today or later), earliest first
        const upcoming = appointments
            .filter(function (a) { return a.status === 'Booked' && a.appointmentDate >= today(); })
            .sort(function (a, b) {
                return (a.appointmentDate + a.appointmentTime).localeCompare(b.appointmentDate + b.appointmentTime);
            })
            .slice(0, 5);

        const body = document.getElementById('upcomingRows');
        if (upcoming.length === 0) {
            body.innerHTML = '<tr><td colspan="5" class="empty">No upcoming appointments</td></tr>';
            return;
        }
        body.innerHTML = upcoming.map(function (a) {
            return '<tr>'
                + '<td>' + esc(a.appointmentDate) + '</td>'
                + '<td>' + esc(a.appointmentTime) + '</td>'
                + '<td>' + esc(a.patientName) + '</td>'
                + '<td>' + esc(a.doctorName) + '</td>'
                + '<td>' + esc(a.reason) + '</td>'
                + '</tr>';
        }).join('');
    } catch (e) {
        toast(e.message, true);
    }
}
