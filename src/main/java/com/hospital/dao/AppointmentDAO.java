package com.hospital.dao;

import com.hospital.model.Appointment;
import com.hospital.util.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/** Database access for the appointments table (all queries use PreparedStatement). */
public class AppointmentDAO {

    /** JOINs patients and doctors so we can show names instead of ID numbers. */
    public List<Appointment> findAll() throws SQLException {
        String sql = "SELECT a.id, a.patient_id, a.doctor_id, p.name AS patient_name, "
                + "d.name AS doctor_name, d.specialization, a.appointment_date, a.appointment_time, "
                + "a.reason, a.status "
                + "FROM appointments a "
                + "JOIN patients p ON a.patient_id = p.id "
                + "JOIN doctors d ON a.doctor_id = d.id "
                + "ORDER BY a.appointment_date DESC, a.appointment_time DESC";
        List<Appointment> list = new ArrayList<>();

        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Appointment a = new Appointment();
                a.setId(rs.getInt("id"));
                a.setPatientId(rs.getInt("patient_id"));
                a.setDoctorId(rs.getInt("doctor_id"));
                a.setPatientName(rs.getString("patient_name"));
                a.setDoctorName(rs.getString("doctor_name"));
                a.setSpecialization(rs.getString("specialization"));
                a.setAppointmentDate(rs.getString("appointment_date"));       // 2026-10-06
                a.setAppointmentTime(rs.getString("appointment_time").substring(0, 5)); // 10:30:00 -> 10:30
                a.setReason(rs.getString("reason"));
                a.setStatus(rs.getString("status"));
                list.add(a);
            }
        }
        return list;
    }

    /**
     * Date and time are passed as plain text ('2026-10-06' and '10:30:00').
     * MySQL converts them to DATE / TIME itself, which avoids time-zone surprises.
     */
    public void insert(Appointment a) throws SQLException {
        String sql = "INSERT INTO appointments (patient_id, doctor_id, appointment_date, appointment_time, reason, status) "
                + "VALUES (?, ?, ?, ?, ?, 'Booked')";

        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, a.getPatientId());
            ps.setInt(2, a.getDoctorId());
            ps.setString(3, a.getAppointmentDate());
            ps.setString(4, a.getAppointmentTime());
            ps.setString(5, a.getReason());
            ps.executeUpdate();
        }
    }

    /** True if this doctor already has a booked (not cancelled) appointment at that date and time. */
    public boolean isSlotTaken(int doctorId, String date, String time) throws SQLException {
        String sql = "SELECT COUNT(*) FROM appointments "
                + "WHERE doctor_id = ? AND appointment_date = ? AND appointment_time = ? AND status = 'Booked'";

        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, doctorId);
            ps.setString(2, date);
            ps.setString(3, time);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1) > 0;
            }
        }
    }

    /** Cancelling keeps the record and only changes its status. Returns false if nothing was cancelled. */
    public boolean cancel(int id) throws SQLException {
        String sql = "UPDATE appointments SET status = 'Cancelled' WHERE id = ? AND status = 'Booked'";

        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    public int count() throws SQLException {
        String sql = "SELECT COUNT(*) FROM appointments";

        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            rs.next();
            return rs.getInt(1);
        }
    }
}
