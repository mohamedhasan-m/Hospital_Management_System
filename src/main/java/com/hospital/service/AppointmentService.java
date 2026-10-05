package com.hospital.service;

import com.hospital.dao.AppointmentDAO;
import com.hospital.dao.DoctorDAO;
import com.hospital.dao.PatientDAO;
import com.hospital.model.Appointment;
import com.hospital.util.Validator;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.List;

/** Business rules for appointments. */
public class AppointmentService {

    private final AppointmentDAO appointmentDAO = new AppointmentDAO();
    private final PatientDAO patientDAO = new PatientDAO();
    private final DoctorDAO doctorDAO = new DoctorDAO();

    public List<Appointment> getAll() throws SQLException {
        return appointmentDAO.findAll();
    }

    /**
     * Rules for booking:
     *  1. patient and doctor must exist
     *  2. date/time must be valid and not in the past
     *  3. the doctor must be free at that date and time
     */
    public void book(String patientId, String doctorId, String date, String time, String reason)
            throws SQLException {

        int pid = Validator.integer(patientId, "Patient", 1, Integer.MAX_VALUE);
        int did = Validator.integer(doctorId, "Doctor", 1, Integer.MAX_VALUE);

        if (patientDAO.findById(pid) == null) {
            throw new IllegalArgumentException("Patient not found");
        }
        if (doctorDAO.findById(did) == null) {
            throw new IllegalArgumentException("Doctor not found");
        }

        LocalDate appointmentDate;
        try {
            appointmentDate = LocalDate.parse(Validator.required(date, "Date", 10));
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Date must be in yyyy-MM-dd format");
        }

        LocalTime appointmentTime;
        try {
            appointmentTime = LocalTime.parse(Validator.required(time, "Time", 8));
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Time must be in HH:mm format");
        }

        if (appointmentDate.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Appointment date cannot be in the past");
        }
        if (appointmentDate.equals(LocalDate.now()) && appointmentTime.isBefore(LocalTime.now())) {
            throw new IllegalArgumentException("Appointment time has already passed");
        }

        String dateText = appointmentDate.toString();                                   // 2026-10-06
        String timeText = String.format("%02d:%02d:00", appointmentTime.getHour(), appointmentTime.getMinute());

        if (appointmentDAO.isSlotTaken(did, dateText, timeText)) {
            throw new IllegalArgumentException("This doctor already has an appointment at that time");
        }

        Appointment a = new Appointment();
        a.setPatientId(pid);
        a.setDoctorId(did);
        a.setAppointmentDate(dateText);
        a.setAppointmentTime(timeText);
        a.setReason(Validator.optional(reason, "Reason", 255));
        appointmentDAO.insert(a);
    }

    public void cancel(String id) throws SQLException {
        int appointmentId = Validator.integer(id, "Appointment ID", 1, Integer.MAX_VALUE);
        if (!appointmentDAO.cancel(appointmentId)) {
            throw new IllegalArgumentException("Appointment not found or already cancelled");
        }
    }
}
