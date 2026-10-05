package com.hospital.service;

import com.hospital.dao.AppointmentDAO;
import com.hospital.dao.DoctorDAO;
import com.hospital.dao.PatientDAO;
import com.hospital.model.DashboardStats;

import java.sql.SQLException;

/** Collects the three totals shown on the dashboard. */
public class DashboardService {

    private final PatientDAO patientDAO = new PatientDAO();
    private final DoctorDAO doctorDAO = new DoctorDAO();
    private final AppointmentDAO appointmentDAO = new AppointmentDAO();

    public DashboardStats getStats() throws SQLException {
        return new DashboardStats(patientDAO.count(), doctorDAO.count(), appointmentDAO.count());
    }
}
