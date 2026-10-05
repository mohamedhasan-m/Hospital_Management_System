package com.hospital.model;

/** The three numbers shown on the dashboard cards. */
public class DashboardStats implements Jsonable {

    private final int patients;
    private final int doctors;
    private final int appointments;

    public DashboardStats(int patients, int doctors, int appointments) {
        this.patients = patients;
        this.doctors = doctors;
        this.appointments = appointments;
    }

    public int getPatients() { return patients; }
    public int getDoctors() { return doctors; }
    public int getAppointments() { return appointments; }

    @Override
    public String toJson() {
        return "{\"patients\":" + patients
                + ",\"doctors\":" + doctors
                + ",\"appointments\":" + appointments + "}";
    }
}
